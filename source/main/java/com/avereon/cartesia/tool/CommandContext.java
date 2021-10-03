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

	private static final Level COMMAND_STACK_LOG_LEVEL = Level.CONFIG;

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
		return pushCommand( tool, command, parameters );
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
			pushCommand( new Select(), mouseEvent );
		} else {
			// Process text calls doCommand
			processText( input, true );
		}
		reset();
	}

	public void repeat( KeyEvent event ) {
		event.consume();
		if( TextUtil.isEmpty( getCommandPrompt().getText() ) ) {
			pushCommand( mapCommand( getPriorCommand() ) );
			reset();
		}
	}

	Command processText( String input, boolean force ) {
		boolean isTextInput = getInputMode() == CommandContext.Input.TEXT;
		if( force ) {
			return switch( getInputMode() ) {
				case NUMBER -> pushCommand( new Value(), CadShapes.parsePoint( input ).getX() );
				case POINT -> pushCommand( new Value(), CadShapes.parsePoint( input, getAnchor() ) );
				case TEXT -> pushCommand( new Value(), input );
				default -> pushCommand( mapCommand( input ) );
			};
		} else if( !isTextInput && isAutoCommandEnabled() && CommandMap.hasCommand( input ) ) {
			return pushCommand( mapCommand( input ) );
		}
		return null;
	}

	public void command( String input ) {
		pushCommand( mapCommand( input ) );
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

		// If the event is not consumed here, it will bubble up to the event
		// handling of the scene which should trigger the appropriate action.
	}

	void handle( MouseEvent event ) {
		// FIXME In the case of select, the mouse released event is sometimes handled before the select command is even on the stack
		//if( event.getEventType() != MouseEvent.MOUSE_MOVED ) log.atConfig().log( "pass mouse event to command: event=%s", event );
		//log.atConfig().log("source event type=%s", event.getEventType());

		if( event.getEventType() == MouseEvent.MOUSE_RELEASED && commandStack.isEmpty() ) log.atWarn().log( "Command stack is empty on mouse release!" );
		doEventCommand( event );
		if( event.getEventType() == MouseEvent.MOUSE_PRESSED && commandStack.isEmpty() ) log.atWarn().log( "Command stack is empty on mouse pressed!" );

		// NOTE Synchronizing on the command stack here will cause the FX thread to hang
		commandStack.forEach( r -> r.getCommand().handle( event ) );
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

	public final DesignTool getTool() {
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
		// NOTE This method does not handle key events, those are handled by the action infrastructure
		CommandMetadata metadata = CommandMap.get( event );
		if( metadata != CommandMap.NONE ) pushCommand( event, metadata.getType(), metadata.getParameters() );
	}

	private CommandMetadata mapCommand( String input ) {
		if( TextUtil.isEmpty( input ) ) return null;

		CommandMetadata mapping = CommandMap.get( input );
		if( mapping == CommandMap.NONE ) throw new UnknownCommand( input );

		return mapping;
	}

	private Command pushCommand( CommandMetadata metadata ) {
		if( metadata == CommandMap.NONE ) return null;
		priorCommand = metadata.getCommand();
		return pushCommand( getLastActiveDesignTool(), metadata.getType(), metadata.getParameters() );
	}

	private Command pushCommand( InputEvent event, Class<? extends Command> commandClass, Object... parameters ) {
		DesignTool tool = (DesignTool)event.getSource();
		return pushCommand( tool, commandClass, ArrayUtil.concat( parameters, event ) );
	}

	private Command pushCommand( Command command, Object... parameters ) {
		return pushCommand( getLastActiveDesignTool(), command, parameters );
	}

	private Command pushCommand( DesignTool tool, Class<? extends Command> commandClass, Object... parameters ) {
		Objects.requireNonNull( commandClass, "Command class cannot be null" );
		try {
			return pushCommand( tool, commandClass.getConstructor().newInstance(), parameters );
		} catch( Exception exception ) {
			log.atSevere().withCause( exception ).log();
		}
		return null;
	}

	private Command pushCommand( DesignTool tool, Command command, Object... parameters ) {
		checkForCommonProblems( tool, command, parameters );

		// Clear the prompt before executing the command, because one of the commands could be setting a new prompt
		if( Fx.isRunning() ) getCommandPrompt().clear();

		synchronized( commandStack ) {
			CommandExecuteRequest request = new CommandExecuteRequest( this, tool, command, parameters );
			log.atTrace().log( "Command submitted %s", request );

			commandStack.push( request );
			logCommandStack();
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
		if( !log.at( COMMAND_STACK_LOG_LEVEL ).isEnabled() ) return;

		List<CommandExecuteRequest> invertedCommandStack = new ArrayList<>( commandStack );
		Collections.reverse( invertedCommandStack );
		if( commandStack.size() != 0 ) log.at( COMMAND_STACK_LOG_LEVEL ).log( "commands=%s", invertedCommandStack );
	}

	private Object doProcessCommands() throws Exception {
		Object result = Command.COMPLETE;
		synchronized( commandStack ) {
			try {
				List<CommandExecuteRequest> requests = new ArrayList<>( commandStack );
				for( CommandExecuteRequest request : requests ) {
					//logCommandStack();
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

		private Object result;

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

			if( result == Command.FAIL ) return Command.FAIL;
			if( priorResult == null ) log.atWarning().log( "A prior result of null was passed to execute" );
			if( priorResult == Command.INCOMPLETE ) log.atWarning().log( "A prior result of INCOMPLETE was passed to execute" );
			if( priorResult != null && priorResult != Command.COMPLETE ) parameters = ArrayUtil.append( parameters, priorResult );

			Object result = Command.INVALID;
			try {
				context.setTool( tool );
				result = command.execute( context, parameters );
				if( result != Command.INVALID ) command.incrementStep();
			} catch( Throwable throwable ) {
				log.atWarn( throwable ).log( "Unhandled error executing command=%s", command );
			} finally {
				if( result == Command.COMPLETE || result == Command.INVALID ) doComplete();
				command.setStepExecuted();
			}

			this.result = result;

			return getResult();
		}

		public Object getResult() {
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
			return command + "{s=" + command.getStep() + " p=" + parameters.length + "}";
		}
	}

}
