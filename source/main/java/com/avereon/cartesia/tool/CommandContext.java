package com.avereon.cartesia.tool;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandMap;
import com.avereon.cartesia.command.ValueCommand;
import com.avereon.cartesia.math.MathEx;
import com.avereon.settings.Settings;
import com.avereon.util.ArrayUtil;
import com.avereon.util.Log;
import com.avereon.util.TextUtil;
import com.avereon.xenon.ProgramProduct;
import com.avereon.zerra.javafx.Fx;
import javafx.event.EventType;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class CommandContext {

	private static final System.Logger log = Log.get();

	private static final boolean DEFAULT_AUTO_COMMAND = true;

	private final ProgramProduct product;

	private final DesignContext designContext;

	private final BlockingDeque<CommandExecuteRequest> commandStack;

	private CommandPrompt commandPrompt;

	private boolean autoCommandEnabled;

	private String priorCommand;

	private DesignTool lastActiveDesignTool;

	private boolean inputMode;

	private Point3D mouse;

	private Point3D anchor;

	public CommandContext( ProgramProduct product, DesignContext designContext ) {
		this.product = product;
		this.designContext = designContext;
		this.commandStack = new LinkedBlockingDeque<>();
		this.priorCommand = TextUtil.EMPTY;

		Settings productSettings = product.getSettings();
		autoCommandEnabled = productSettings.get( "command-auto-start", Boolean.class, DEFAULT_AUTO_COMMAND );
		productSettings.register( "command-auto-start", e -> setAutoCommandEnabled( Boolean.parseBoolean( String.valueOf( e.getNewValue() ) ) ) );
	}

	public ProgramProduct getProduct() {
		return product;
	}

	public DesignContext getDesignContext() {
		return designContext;
	}

	public CommandPrompt getCommandPrompt() {
		if( commandPrompt == null ) this.commandPrompt = new CommandPrompt( product, this );
		return commandPrompt;
	}

	public void submit( DesignTool tool, Command command, Object... parameters ) {
		doCommand( tool, command, parameters );
	}

	public void cancel() {
		// Cancel the command stack
		commandStack.forEach( c -> Fx.run( () -> c.getTool().setCursor( Cursor.DEFAULT ) ) );
		getCommandPrompt().setPrompt( TextUtil.EMPTY );
		//isAutoCommandSafe = true;
		commandStack.clear();
		//valueStack.clear();

		// Clear the design selection
		getDesignContext().getDesign().clearSelected();
		// Clear the command prompt
		getCommandPrompt().clear();
	}

	public void enter() {
		String input = getCommandPrompt().getText();
		if( input.isEmpty() ) {
			doCommand( new ValueCommand(), getMouse() );
		} else if( isInputMode() ) {
			doCommand( new ValueCommand(), input );
		} else {
			doCommand( input );
		}
		getCommandPrompt().clear();
	}

	public void repeat() {
		if( TextUtil.isEmpty( getCommandPrompt().getText() ) ) {
			doCommand( getPriorCommand() );
			getCommandPrompt().clear();
		}
	}

	public boolean isAutoCommandEnabled() {
		return autoCommandEnabled;
	}

	public void setAutoCommandEnabled( boolean autoCommandEnabled ) {
		this.autoCommandEnabled = autoCommandEnabled;
	}

	void text( String text ) {
		if( isAutoCommandEnabled() && CommandMap.hasCommand( text ) ) {
			doCommand( text );
			getCommandPrompt().clear();
		}
	}

	void handle( KeyEvent event ) {
		doProcessKeyEvent( event );
	}

	void handle( MouseEvent event ) {
		// TODO Implement CommandContext.handle( MouseEvent )
		DesignTool tool = (DesignTool)event.getSource();
		Point3D point = tool.mouseToWorld( event.getX(), event.getY(), event.getZ() );
		if( event.getEventType() == MouseEvent.MOUSE_PRESSED ) setAnchor( point );
		setMouse( point );
	}

	void handle( MouseDragEvent event ) {
		// TODO Implement CommandContext.handle( MouseDragEvent )
	}

	void handle( ScrollEvent event ) {
		// Zoom in is the equivalent of moving forward (positive delta y)
		// Zoom out is the equivalent of moving backward (negative delta y)
		double deltaY = event.getDeltaY();

		EventType<ScrollEvent> type = event.getEventType();

		if( type == ScrollEvent.SCROLL && deltaY != 0.0 ) {
			type = deltaY > 0 ? CommandMap.SCROLL_WHEEL_UP : CommandMap.SCROLL_WHEEL_DOWN;
			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.mouseToWorld( event.getX(), event.getY(), 0 );
			Class<? extends Command> command = CommandMap.get( type );
			if( command != null ) doCommand( tool, command, point.getX(), point.getY() );
		}
	}

	DesignTool getLastActiveDesignTool() {
		return lastActiveDesignTool;
	}

	void setLastActiveDesignTool( DesignTool tool ) {
		lastActiveDesignTool = tool;
	}

	Point3D getMouse() {
		return mouse;
	}

	void setMouse( Point3D point ) {
		mouse = point;
	}

	Point3D getAnchor() {
		return anchor;
	}

	void setAnchor( Point3D anchor ) {
		this.anchor = anchor;
	}

	Point3D parsePoint( String input ) {
		input = Objects.requireNonNull( input ).trim();

		try {
			boolean relative = false;
			boolean polar = false;

			if( input.charAt( 0 ) == '@' ) {
				input = input.substring( 1 ).trim();
				relative = true;
			}
			if( input.charAt( 0 ) == '>' || input.charAt( 0 ) == '<' ) {
				// Modifier > is for polar coords
				input = input.substring( 1 ).trim();
				polar = true;
			}

			String[] coords = input.split( "," );
			Point3D point = switch( coords.length ) {
				case 1 -> new Point3D( MathEx.parse( coords[ 0 ] ), 0, 0 );
				case 2 -> new Point3D( MathEx.parse( coords[ 0 ] ), MathEx.parse( coords[ 1 ] ), 0 );
				case 3 -> new Point3D( MathEx.parse( coords[ 0 ] ), MathEx.parse( coords[ 1 ] ), MathEx.parse( coords[ 2 ] ) );
				default -> null;
			};

			if( point == null ) return null;
			if( polar ) point = fromPolar( point );
			if( relative ) point = getAnchor().add( point );
			return point;
		} catch( ParseException exception ) {
			return null;
		}
	}

	Point3D fromPolar( Point3D point ) {
		double angle = point.getX();
		double radius = point.getY();
		return new Point3D( radius * Math.cos( angle ), radius * Math.sin( angle ), 0 );
	}

	private boolean isInputMode() {
		return inputMode;
	}

	private void setInputMode( boolean mode ) {
		this.inputMode = mode;
	}

	private void doCommand( String input ) {
		if( TextUtil.isEmpty( input ) ) return;

		Class<? extends Command> commandClass = CommandMap.get( input );

		if( commandClass != null ) {
			priorCommand = input;
			doCommand( getLastActiveDesignTool(), commandClass );
		} else {
			log.log( Log.WARN, "Unknown command=" + input );
		}
	}

	private void doCommand( Command command, Object... parameters ) {
		doCommand( getLastActiveDesignTool(), command, parameters );
	}

	private void doCommand( DesignTool tool, Class<? extends Command> commandClass, Object... parameters ) {
		Objects.requireNonNull( commandClass, "Command class cannot be null" );
		try {
			doCommand( tool, commandClass.getConstructor().newInstance(), parameters );
		} catch( Exception exception ) {
			log.log( Log.ERROR, exception );
		}
	}

	private void doCommand( DesignTool tool, Command command, Object... parameters ) {
		synchronized( commandStack ) {
			log.log( Log.TRACE, "Command submitted " + command.getClass().getSimpleName() );
			commandStack.push( new CommandExecuteRequest( this, tool, command, parameters ) );
			getProduct().task( "process-commands", this::doProcessCommands );
		}
	}

	private void doProcessKeyEvent( KeyEvent event ) {
		// This prevents double events
		event.consume();

		// On each key event the situation needs to be evaluated...
		// If ESC was pressed, then the whole command stack should be cancelled
		// If ENTER was pressed, then an attempt to process the text should be forced
		// If SPACE was pressed, then the last command should be repeated

		if( event.getEventType() == KeyEvent.KEY_PRESSED ) {
			switch( event.getCode() ) {
				case ESCAPE -> cancel();
				case ENTER -> enter();
				case SPACE -> repeat();
			}
		}
	}

	private String getPriorCommand() {
		return priorCommand;
	}

	private Object doProcessCommands() throws Exception {
		Object result = null;
		log.log( Log.WARN, "Command stack size=" + commandStack.size() );
		for( CommandExecuteRequest request : new ArrayList<>( commandStack ) ) {
			setInputMode( request.getCommand().isInputCommand() );
			result = request.execute( result );
			if( result == Command.INCOMPLETE ) break;
			commandStack.remove( request );
			setInputMode( false );
		}
		return result;
	}

	private static class CommandExecuteRequest {

		private final CommandContext context;

		private final DesignTool tool;

		private final Command command;

		private final Object[] parameters;

		public CommandExecuteRequest( CommandContext context, DesignTool tool, Command command, Object... parameters ) {
			this.context = context;
			this.tool = tool;
			this.command = command;
			this.parameters = parameters;
		}

		public DesignTool getTool() {
			return tool;
		}

		public Command getCommand() {
			return command;
		}

		public Object[] getParameters() {
			return parameters;
		}

		public Object execute( Object priorResult ) throws Exception {
			// No extra parameters
			if( priorResult == null ) return command.execute( context, tool, parameters );

			// Append prior result
			return command.execute( context, tool, ArrayUtil.append( parameters, priorResult ) );
		}

	}

}
