package com.avereon.cartesia.tool;

import com.avereon.cartesia.CommandMap;
import com.avereon.cartesia.CommandMetadata;
import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.Select;
import com.avereon.cartesia.command.Value;
import com.avereon.cartesia.error.UnknownCommand;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.util.ArrayUtil;
import com.avereon.util.Log;
import com.avereon.util.TextUtil;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.input.*;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class CommandContext {

	public enum Input {
		NONE,
		NUMBER,
		POINT,
		TEXT
	}

	private static final System.Logger log = Log.get();

	private static final boolean DEFAULT_AUTO_COMMAND = true;

	private final ProgramProduct product;

	private final BlockingDeque<CommandExecuteRequest> commandStack;

	private CommandPrompt commandPrompt;

	private String priorShortcut;

	private DesignTool lastActiveDesignTool;

	private Input inputMode;

	private Point3D screenMouse;

	private Point3D worldMouse;

	private Point3D anchor;

	private DesignTool tool;

	public CommandContext( ProgramProduct product ) {
		this.product = product;
		this.commandStack = new LinkedBlockingDeque<>();
		this.priorShortcut = TextUtil.EMPTY;
		this.inputMode = CommandContext.Input.NONE;
	}

	public final Program getProgram() {
		return product.getProgram();
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

	public void resubmit( DesignTool tool, Command command, Object... parameters ) {
		commandStack.removeIf( r -> r.getCommand() == command );
		submit( tool, command, parameters );
	}

	public void cancel() {
		commandStack.forEach( CommandExecuteRequest::cancel );
		commandStack.clear();
		reset();
	}

	public void enter( KeyEvent event ) {
		String input = getCommandPrompt().getText();
		if( input.isEmpty() ) {
			DesignTool tool = getLastActiveDesignTool();
			Point3D mouse = tool.worldToScreen( getWorldMouse() );
			Point2D screen = tool.localToScreen( mouse );
			MouseEvent mouseEvent = new MouseEvent(
				tool,
				null,
				MouseEvent.MOUSE_RELEASED,
				mouse.getX(),
				mouse.getY(),
				screen.getX(),
				screen.getY(),
				MouseButton.PRIMARY,
				1,
				event.isShiftDown(),
				event.isControlDown(),
				event.isAltDown(),
				event.isMetaDown(),
				true,
				false,
				false,
				true,
				false,
				true,
				null
			);
			doCommand( new Select(), mouseEvent );
		} else {
			text( input, true );
		}
		reset();
	}

	public void repeat() {
		if( TextUtil.isEmpty( getCommandPrompt().getText() ) ) {
			doCommand( getPriorShortcut() );
			reset();
		}
	}

	void text( String input ) {
		text( input, false );
	}

	void text( String input, boolean hard ) {
		if( hard ) {
			if( getInputMode() == CommandContext.Input.NUMBER ) {
				doCommand( new Value(), CadShapes.parsePoint( input ).getX() );
			} else if( getInputMode() == CommandContext.Input.POINT ) {
				doCommand( new Value(), CadShapes.parsePoint( input, getAnchor() ) );
			} else if( getInputMode() == CommandContext.Input.TEXT ) {
				doCommand( new Value(), input );
			} else {
				doCommand( input );
			}
		} else if( isAutoCommandEnabled() && CommandMap.hasCommand( input ) ) {
			doCommand( input );
		}
	}

	public void command( String input ) {
		doCommand( input );
	}

	public boolean isPenMode() {
		return commandStack.size() > 1;
	}

	public boolean isInteractive() {
		// TODO Turn off when running a script
		return true;
	}

	public boolean isSelectMode() {
		return commandStack.size() == 1;
	}

	public boolean isAutoCommandEnabled() {
		return getProduct().getSettings().get( "command-auto-start", Boolean.class, DEFAULT_AUTO_COMMAND );
	}

	void handle( KeyEvent event ) {
		event.consume();
		doProcessKeyEvent( event );
	}

	void handle( MouseEvent event ) {
		if( !event.isConsumed() ) commandStack.stream().map( CommandExecuteRequest::getCommand ).forEach( c -> c.handle( event ) );
		doEventCommand( event );
	}

	void handle( ScrollEvent event ) {
		doEventCommand( event );
	}

	void handle( ZoomEvent event ) {
		doEventCommand( event );
	}

	DesignTool getLastActiveDesignTool() {
		return lastActiveDesignTool;
	}

	void setLastActiveDesignTool( DesignTool tool ) {
		lastActiveDesignTool = tool;
	}

	public DesignTool getTool() {
		return tool;
	}

	void setTool( DesignTool tool ) {
		this.tool = tool;
	}

	public Point3D getScreenMouse() {
		return screenMouse;
	}

	public void setScreenMouse( Point3D screenMouse ) {
		this.screenMouse = screenMouse;
	}

	public Point3D getWorldMouse() {
		return worldMouse;
	}

	void setWorldMouse( Point3D point ) {
		worldMouse = point;
	}

	public Point3D getAnchor() {
		return anchor;
	}

	void setAnchor( Point3D anchor ) {
		this.anchor = anchor;
	}

	private void reset() {
		setInputMode( CommandContext.Input.NONE );
		Fx.run( () -> {
			getLastActiveDesignTool().clearSelected();
			getCommandPrompt().clear();
		} );
	}

	public Input getInputMode() {
		return inputMode;
	}

	public void setInputMode( Input inputMode ) {
		this.inputMode = inputMode;
	}

	private void doEventCommand( InputEvent event ) {
		event.consume();
		CommandMetadata mapping = CommandMap.get( event );
		if( mapping != null ) doCommand( event, mapping.getType(), mapping.getParameters() );
	}

	private void doCommand( String input ) {
		if( TextUtil.isEmpty( input ) ) return;

		CommandMetadata mapping = CommandMap.get( input );
		if( mapping != null ) {
			priorShortcut = input;
			doCommand( getLastActiveDesignTool(), mapping.getType(), mapping.getParameters() );
		} else {
			throw new UnknownCommand( input );
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

		// Clear the prompt before executing the command, because one of the commands could be setting a new prompt
		getCommandPrompt().clear();

		synchronized( commandStack ) {
			log.log( Log.TRACE, "Command submitted " + command.getClass().getSimpleName() );
			commandStack.push( new CommandExecuteRequest( this, tool, command, parameters ) );
			getProduct().task( "process-commands", this::doProcessCommands );
		}
	}

	private void checkForCommonProblems( DesignTool tool, Command command, Object... parameters ) {
		if( command instanceof Value && commandStack.isEmpty() ) {
			log.log( Log.WARN, "There is not a command waiting for the value: " + Arrays.toString( parameters ) );
		}
	}

	private void logCommandStack() {
		if( log.isLoggable( Log.DEBUG ) ) {
			List<CommandExecuteRequest> invertedCommandStack = new ArrayList<>( commandStack );
			Collections.reverse( invertedCommandStack );
			if( commandStack.size() != 0 ) log.log( Log.DEBUG, "commands=" + invertedCommandStack );
		}
	}

	private Object doProcessCommands() throws Exception {
		Object result = Command.COMPLETE;
		synchronized( commandStack ) {
			try {
				List<CommandExecuteRequest> requests = new ArrayList<>( commandStack );
				for( CommandExecuteRequest request : requests ) {
					logCommandStack();
					setInputMode( request.getCommand().getInputMode() );
					result = request.executeCommandStep( result );
					if( result == Command.INVALID ) break;
					if( result instanceof Point3D ) setAnchor( (Point3D)result );
					if( result == Command.INCOMPLETE ) break;
					commandStack.remove( request );
				}
			} catch( Exception exception ) {
				cancel();
				throw exception;
			}
		}
		return result;
	}

	private Command getCurrentCommand() {
		CommandExecuteRequest request = commandStack.peek();
		return request == null ? null : request.getCommand();
	}

	private void doProcessKeyEvent( KeyEvent event ) {
		// On each key event the situation needs to be evaluated...
		// If ESC was pressed, then the whole command stack should be cancelled
		// If ENTER was pressed, then an attempt to process the text should be forced
		// If SPACE was pressed, then the last command should be repeated

		if( event.getEventType() == KeyEvent.KEY_PRESSED ) {
			switch( event.getCode() ) {
				case ESCAPE -> cancel();
				case ENTER -> enter( event );
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

		public Object executeCommandStep( Object priorResult ) throws Exception {
			// NOTE Be judicious adding logic in this method.
			// It is called for every step in a command

			if( priorResult == Command.INCOMPLETE ) log.log( Log.WARN, "A prior result of INCOMPLETE was passed to execute" );
			if( priorResult != Command.COMPLETE ) parameters = ArrayUtil.append( parameters, priorResult );

			Object result = Command.INVALID;
			try {
				context.setTool( tool );
				result = command.execute( context, parameters );
				if( result != Command.INVALID ) command.incrementStep();
			} finally {
				if( result != Command.INCOMPLETE ) doComplete();
			}

			return result;
		}

		private void doComplete() {
			if( tool != null ) {
				// FIXME Merged changes not working quite right
				//tool.getAsset().getUndoManager().preventMerge();
				if( command.clearSelectionWhenComplete() ) tool.clearSelected();
			}
		}

		public void cancel() {
			try {
				command.cancel( context );
			} catch( Exception exception ) {
				log.log( Log.ERROR, exception );
			}
		}

		@Override
		public String toString() {
			return command.toString() + ":" + command.getStep();
		}
	}

}
