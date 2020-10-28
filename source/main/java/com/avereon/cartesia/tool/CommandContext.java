package com.avereon.cartesia.tool;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandMap;
import com.avereon.cartesia.CommandMapping;
import com.avereon.cartesia.command.ValueCommand;
import com.avereon.util.ArrayUtil;
import com.avereon.util.Log;
import com.avereon.util.TextUtil;
import com.avereon.xenon.ProgramProduct;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.input.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class CommandContext {

	private static final System.Logger log = Log.get();

	private static final boolean DEFAULT_AUTO_COMMAND = true;

	private final ProgramProduct product;

	private final BlockingDeque<CommandExecuteRequest> commandStack;

	private CommandPrompt commandPrompt;

	private String priorShortcut;

	private DesignTool lastActiveDesignTool;

	private boolean inputMode;

	private Point3D mouse;

	private Point3D anchor;

	public CommandContext( ProgramProduct product ) {
		this.product = product;
		this.commandStack = new LinkedBlockingDeque<>();
		this.priorShortcut = TextUtil.EMPTY;
	}

	public final ProgramProduct getProduct() {
		return product;
	}

	public CommandPrompt getCommandPrompt() {
		if( commandPrompt == null ) this.commandPrompt = new CommandPrompt( getProduct(), this );
		return commandPrompt;
	}

	public void submit( DesignTool tool, Command command, Object... parameters ) {
		doCommand( tool, command, parameters );
	}

	public void cancel() {
		commandStack.stream().filter( r -> r.getTool() != null ).map( CommandExecuteRequest::getTool ).forEach( t -> {
			t.setCursor( Cursor.DEFAULT );
			t.getDesign().clearSelected();
			t.clearPreview();
		} );
		commandStack.clear();
		reset();
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
		reset();
	}

	public void repeat() {
		if( TextUtil.isEmpty( getCommandPrompt().getText() ) ) doCommand( getPriorShortcut() );
	}

	public boolean isSelectMode() {
		return commandStack.isEmpty();
	}

	public boolean isAutoCommandEnabled() {
		return getProduct().getSettings().get( "command-auto-start", Boolean.class, DEFAULT_AUTO_COMMAND );
	}

	void text( String text ) {
		if( isAutoCommandEnabled() && CommandMap.hasCommand( text ) ) {
			// Clear the prompt before executing the command, because one of the commands could be setting a new prompt
			getCommandPrompt().clear();
			doCommand( text );
		}
	}

	void handle( KeyEvent event ) {
		doProcessKeyEvent( event );
	}

	void handle( InputEvent event ) {
		CommandMapping mapping = CommandMap.get( event );
		if( mapping != null ) doCommand( event, mapping.getCommand(), mapping.getParameters() );
	}

	void handle( MouseEvent event ) {
		DesignTool tool = (DesignTool)event.getSource();
		setMouse( tool.mouseToWorld( event.getX(), event.getY(), event.getZ() ) );
		commandStack.stream().map( CommandExecuteRequest::getCommand ).forEach( c -> c.handle( event ) );
		handle( (InputEvent)event );
	}

	DesignTool getLastActiveDesignTool() {
		return lastActiveDesignTool;
	}

	void setLastActiveDesignTool( DesignTool tool ) {
		lastActiveDesignTool = tool;
	}

	public Point3D getMouse() {
		return mouse;
	}

	void setMouse( Point3D point ) {
		mouse = point;
	}

	public Point3D getAnchor() {
		return anchor;
	}

	public void setAnchor( Point3D anchor ) {
		this.anchor = anchor;
	}

	private void reset() {
		getCommandPrompt().clear();
		setInputMode( false );
	}

	private boolean isInputMode() {
		return inputMode;
	}

	private void setInputMode( boolean mode ) {
		this.inputMode = mode;
	}

	private void doCommand( String input ) {
		if( TextUtil.isEmpty( input ) ) return;

		CommandMapping mapping = CommandMap.get( input );
		if( mapping != null ) {
			priorShortcut = input;
			doCommand( getLastActiveDesignTool(), mapping.getCommand(), mapping.getParameters() );
		} else {
			log.log( Log.WARN, "Unknown command=" + input );
		}
	}

	private void doCommand( InputEvent event, Class<? extends Command> commandClass, Object... parameters ) {
		DesignTool tool = (DesignTool)event.getSource();
		doCommand( tool, commandClass, ArrayUtil.concat( parameters, event ) );
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
		checkForCommonProblems( tool, command, parameters );
		synchronized( commandStack ) {
			log.log( Log.TRACE, "Command submitted " + command.getClass().getSimpleName() );
			commandStack.push( new CommandExecuteRequest( this, tool, command, parameters ) );
			getProduct().task( "process-commands", this::doProcessCommands );
		}
	}

	private void checkForCommonProblems( DesignTool tool, Command command, Object... parameters ) {
		if( command instanceof ValueCommand && commandStack.isEmpty() ) {
			log.log( Log.WARN, "There is not a command waiting for the value: " + Arrays.toString( parameters ) );
		}
	}

	private Object doProcessCommands() throws Exception {
		Object result = null;
		try {
			for( CommandExecuteRequest request : new ArrayList<>( commandStack ) ) {
				setInputMode( request.getCommand().isInputCommand() );
				result = request.execute( result );
				if( result == Command.INCOMPLETE ) break;
				commandStack.remove( request );

				// FIXME This breaks the unit tests
				//reset();
			}
		} catch( Exception exception ) {
			cancel();
			throw exception;
		}
		return result;
	}

	private Command getCurrentCommand() {
		CommandExecuteRequest request = commandStack.peek();
		return request == null ? null : request.getCommand();
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

	private String getPriorShortcut() {
		return priorShortcut;
	}

	private static class CommandExecuteRequest {

		private final CommandContext context;

		private final DesignTool tool;

		private final Command command;

		private Object[] parameters;

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
			if( priorResult != null ) parameters = ArrayUtil.append( parameters, priorResult );
			return command.execute( context, tool, parameters );
		}

	}

}
