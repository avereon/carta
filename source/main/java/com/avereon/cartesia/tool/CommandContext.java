package com.avereon.cartesia.tool;

import com.avereon.cartesia.CommandMap;
import com.avereon.cartesia.CommandMetadata;
import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.Select;
import com.avereon.cartesia.command.Value;
import com.avereon.cartesia.error.UnknownCommand;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.log.LazyEval;
import com.avereon.util.ArrayUtil;
import com.avereon.util.TextUtil;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.zerra.javafx.Fx;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.input.*;
import lombok.CustomLog;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;

@CustomLog
public class CommandContext implements EventHandler<KeyEvent> {

	public enum Input {
		NONE,
		NUMBER,
		POINT,
		TEXT
	}

	private static final boolean DEFAULT_AUTO_COMMAND = true;

	private static final Level COMMAND_STACK_LOG_LEVEL = Level.FINE;

	private final ProgramProduct product;

	private final BlockingDeque<CommandExecuteRequest> commandStack;

	private CommandPrompt commandPrompt;

	private String priorCommand;

	private DesignTool lastActiveDesignTool;

	private Input inputMode;

	private Point3D screenMouse;

	private Point3D worldMouse;

	private Point3D anchor;

	private DesignTool tool;

	public CommandContext( ProgramProduct product ) {
		this.product = product;
		this.commandStack = new LinkedBlockingDeque<>();
		this.priorCommand = TextUtil.EMPTY;
		this.inputMode = CommandContext.Input.NONE;
	}

	public final Program getProgram() {
		return product.getProgram();
	}

	public final ProgramProduct getProduct() {
		return product;
	}

	public CommandPrompt getCommandPrompt() {
		if( commandPrompt == null ) this.commandPrompt = new CommandPrompt( this );
		return commandPrompt;
	}

	public Command submit( DesignTool tool, Command command, Object... parameters ) {
		return doCommand( tool, command, parameters );
	}

	public Command resubmit( DesignTool tool, Command command, Object... parameters ) {
		commandStack.removeIf( r -> r.getCommand() == command );
		return submit( tool, command, parameters );
	}

	public void cancel( KeyEvent event ) {
		event.consume();
		cancel();
	}

	private void cancel() {
		commandStack.forEach( CommandExecuteRequest::cancel );
		commandStack.clear();
		reset();
	}

	public void enter( KeyEvent event ) {
		event.consume();
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
			// Process text calls doCommand
			processText( input, true );
		}
		reset();
	}

	public void repeat( KeyEvent event ) {
		event.consume();
		if( TextUtil.isEmpty( getCommandPrompt().getText() ) ) {
			doCommand( mapCommand( getPriorCommand() ) );
			reset();
		}
	}

	Command processText( String input, boolean force ) {
		boolean isTextInput = getInputMode() == CommandContext.Input.TEXT;
		if( force ) {
			return switch( getInputMode() ) {
				case NUMBER -> doCommand( new Value(), CadShapes.parsePoint( input ).getX() );
				case POINT -> doCommand( new Value(), CadShapes.parsePoint( input, getAnchor() ) );
				case TEXT -> doCommand( new Value(), input );
				default -> doCommand( mapCommand( input ) );
			};
		} else if( !isTextInput && isAutoCommandEnabled() && CommandMap.hasCommand( input ) ) {
			return doCommand( mapCommand( input ) );
		}
		return null;
	}

	public void command( String input ) {
		doCommand( mapCommand( input ) );
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

	public void handle( KeyEvent event ) {
		// This method handles key events from both the workpane and the command
		// prompt. The command prompt consumes some key events that correspond to
		// the text field, while the workpane will forward pretty much every event.

		// If the event comes from the workpane a copy should be sent to the command
		// prompt so that the command prompt displays the typed keys.
		if( event.getSource() == getTool().getWorkpane() ) getCommandPrompt().fireEvent( event );

		// On each key event the situation needs to be evaluated...
		// If ESC was pressed, then the whole command stack should be cancelled
		// If ENTER was pressed, then an attempt to process the text should be forced
		// If SPACE was pressed, then the last command should be repeated
		if( event.getEventType() == KeyEvent.KEY_PRESSED ) {
			switch( event.getCode() ) {
				case ESCAPE -> cancel( event );
				case ENTER -> enter( event );
				case SPACE -> repeat( event );
			}
		}

		// If the event is not consumed here, it will pass to the event handling of
		// the scene which should trigger the appropriate program action.
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
		lastActiveDesignTool = Objects.requireNonNull( tool );
	}

	public DesignTool getTool() {
		return tool;
	}

	void setTool( DesignTool tool ) {
		this.tool = Objects.requireNonNull( tool );
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
		// This method does not handle the key events, those are handled by the action infrastructure

		CommandMetadata metadata = CommandMap.get( event );
		if( metadata != CommandMap.NONE ) {
			doCommand( event, metadata.getType(), metadata.getParameters() );
			event.consume();
		}
	}

	private CommandMetadata mapCommand( String input ) {
		if( TextUtil.isEmpty( input ) ) return null;

		CommandMetadata mapping = CommandMap.get( input );
		if( mapping == CommandMap.NONE ) throw new UnknownCommand( input );

		return mapping;
	}

	private Command doCommand( CommandMetadata metadata ) {
		if( metadata == CommandMap.NONE ) return null;
		priorCommand = metadata.getCommand();
		return doCommand( getLastActiveDesignTool(), metadata.getType(), metadata.getParameters() );
	}

	private Command doCommand( InputEvent event, Class<? extends Command> commandClass, Object... parameters ) {
		DesignTool tool = (DesignTool)event.getSource();
		return doCommand( tool, commandClass, ArrayUtil.concat( parameters, event ) );
	}

	private Command doCommand( Command command, Object... parameters ) {
		return doCommand( getLastActiveDesignTool(), command, parameters );
	}

	private Command doCommand( DesignTool tool, Class<? extends Command> commandClass, Object... parameters ) {
		Objects.requireNonNull( commandClass, "Command class cannot be null" );
		try {
			return doCommand( tool, commandClass.getConstructor().newInstance(), parameters );
		} catch( Exception exception ) {
			log.atSevere().withCause( exception ).log();
		}
		return null;
	}

	private Command doCommand( DesignTool tool, Command command, Object... parameters ) {
		checkForCommonProblems( tool, command, parameters );

		// Clear the prompt before executing the command, because one of the commands could be setting a new prompt
		if( Fx.isRunning() ) getCommandPrompt().clear();

		// TODO Is parameter data leaking to the next command stack???

		synchronized( commandStack ) {
			CommandExecuteRequest request = new CommandExecuteRequest( this, tool, command, parameters );
			log.atDebug().log( "Command submitted %s", request );

			commandStack.push( request );
			getProduct().task( "process-commands", this::doProcessCommands );
		}

		return command;
	}

	private void checkForCommonProblems( DesignTool tool, Command command, Object... parameters ) {
		if( command instanceof Value && commandStack.isEmpty() ) {
			log.atWarning().log( "There is not a command waiting for the value: %s", LazyEval.of( () -> Arrays.toString( parameters ) ) );
		}
	}

	private void logCommandStack() {
		if( log.at( COMMAND_STACK_LOG_LEVEL ).isEnabled() ) {
			List<CommandExecuteRequest> invertedCommandStack = new ArrayList<>( commandStack );
			Collections.reverse( invertedCommandStack );
			if( commandStack.size() != 0 ) log.at( COMMAND_STACK_LOG_LEVEL ).log( "commands=%s", invertedCommandStack );
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

	Command getCurrentCommand() {
		CommandExecuteRequest request = commandStack.peek();
		return request == null ? null : request.getCommand();
	}

	private String getPriorCommand() {
		return priorCommand;
	}

	private static class CommandExecuteRequest {

		private final CommandContext context;

		private final DesignTool tool;

		private final Command command;

		private Object[] parameters;

		public CommandExecuteRequest( CommandContext context, DesignTool tool, Command command, Object... parameters ) {
			this.context = Objects.requireNonNull( context );
			this.tool = Objects.requireNonNull( tool );
			this.command = Objects.requireNonNull( command );
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
			// NOTE Be judicious adding logic in this method, it is called for every step in a command

			if( priorResult == Command.INCOMPLETE ) log.atWarning().log( "A prior result of INCOMPLETE was passed to execute" );
			if( priorResult != Command.COMPLETE ) parameters = ArrayUtil.append( parameters, priorResult );

			Object result = Command.INVALID;
			try {
				context.setTool( tool );
				result = command.execute( context, parameters );
				if( result != Command.INVALID ) command.incrementStep();
			} finally {
				if( result != Command.INCOMPLETE ) doComplete();
				command.setExecuted();
			}

			return result;
		}

		private void doComplete() {
			if( command.clearSelectionWhenComplete() ) tool.clearSelected();
		}

		public void cancel() {
			try {
				command.cancel( context );
			} catch( Exception exception ) {
				log.atSevere().withCause( exception ).log();
			}
		}

		@Override
		public String toString() {
			return command + "{step=" + command.getStep() + " parameters.length=" + parameters.length;
		}
	}

}
