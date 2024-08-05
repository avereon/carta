package com.avereon.cartesia.tool;

import com.avereon.cartesia.CommandMap;
import com.avereon.cartesia.CommandMetadata;
import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.SelectByPoint;
import com.avereon.cartesia.command.Value;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.error.UnknownCommand;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.log.LazyEval;
import com.avereon.util.TextUtil;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.zarra.javafx.Fx;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.input.*;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;

import static com.avereon.cartesia.command.Command.Result.*;

/**
 * The CommandContext class is a container for command specific information.
 * <pre>
 * {@link DesignTool} -> {@link Design} -> {@link DesignContext} -> {@link DesignCommandContext}
 * </pre>
 */
@CustomLog
public class DesignCommandContext implements EventHandler<KeyEvent> {

	public enum Input {
		NONE,
		NUMBER,
		POINT,
		TEXT
	}

	private static final boolean DEFAULT_AUTO_COMMAND = true;

	private static final Level COMMAND_STACK_LOG_LEVEL = Level.FINE;

	private final XenonProgramProduct product;

	private final BlockingDeque<CommandTask> commandStack;

	private CommandPrompt commandPrompt;

	private String priorCommand;

	private DesignTool lastActiveDesignTool;

	@Setter
	@Getter
	private Input inputMode;

	@Getter
	@Setter
	private Point3D screenAnchor;

	@Getter
	@Setter
	private Point3D screenMouse;

	@Getter
	@Setter
	private Point3D worldAnchor;

	@Getter
	@Setter
	private Point3D worldMouse;

	private DesignTool tool;

	public DesignCommandContext( XenonProgramProduct product ) {
		this.product = product;
		this.commandStack = new LinkedBlockingDeque<>();
		this.priorCommand = TextUtil.EMPTY;
		this.inputMode = DesignCommandContext.Input.NONE;
	}

	public final Xenon getProgram() {
		return product.getProgram();
	}

	public final XenonProgramProduct getProduct() {
		return product;
	}

	public CommandPrompt getCommandPrompt() {
		if( commandPrompt == null ) this.commandPrompt = new CommandPrompt( this );
		return commandPrompt;
	}

	public int getCommandStackDepth() {
		return commandStack.size();
	}

	/**
	 * This is the script entrypoint for running commands.
	 *
	 * @param tool The tool that is running the command
	 * @param command The command to run
	 * @param parameters The command parameters
	 * @return The command that was run
	 */
	public Command submit( DesignTool tool, Command command, Object... parameters ) {
		return submit( tool, null, null, command, parameters );
	}

	/**
	 * @param tool
	 * @param trigger
	 * @param event
	 * @param command
	 * @param parameters
	 * @return
	 * @deprecated In favor of {@link #submit(CommandTask)}
	 */
	@Deprecated
	public Command submit( DesignTool tool, CommandTrigger trigger, InputEvent event, Command command, Object... parameters ) {
		commandStack.removeIf( r -> r.getCommand() == command );
		return pushCommand( new CommandTask( this, tool, trigger, event, command, parameters ) );
	}

	public Command submit( CommandTask request ) {
		commandStack.removeIf( r -> r.getCommand() == request.getCommand() );
		return pushCommand( request );
	}

	//	/**
	//	 * When a command needs to resubmit itself, it should use this method. This
	//	 * method is generally called from the event handler of a command when an
	//	 * event value need to be passed to the command.
	//	 *
	//	 * @param tool The tool that is running the command
	//	 * @param command The command to run
	//	 * @param parameters The command parameters
	//	 * @return The command that was run
	//	 */
	//	public Command resubmit( DesignTool tool, InputEvent event, Command command, Object... parameters ) {
	//		return submit( tool, null, event, command, parameters );
	//	}

	public void cancel( KeyEvent event ) {
		event.consume();
		cancel();
	}

	private void cancel() {
		commandStack.forEach( CommandTask::cancel );
		commandStack.clear();
		logCommandStack( "cncl" );
		reset();
	}

	public void enter( KeyEvent event ) {
		event.consume();
		String input = getCommandPrompt().getCommand();
		if( input.isEmpty() ) {
			DesignTool tool = getLastActiveDesignTool();
			Point3D mouse = tool.worldToScreen( getWorldMouse() );
			Point3D screen = tool.worldToScreen( mouse );
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
			pushCommand( new SelectByPoint(), mouseEvent );
		} else {
			// Process text calls doCommand
			processText( input, true );
		}
		reset();
	}

	public void repeat( KeyEvent event ) {
		event.consume();
		if( TextUtil.isEmpty( getCommandPrompt().getCommand() ) ) {
			pushCommand( mapCommand( getPriorCommand() ) );
			reset();
		}
	}

	Command processText( String input, boolean strict ) {
		boolean isTextInput = getInputMode() == DesignCommandContext.Input.TEXT;
		if( strict ) {
			return switch( getInputMode() ) {
				case NUMBER -> pushCommand( new Value(), CadShapes.parsePoint( input ).getX() );
				case POINT -> pushCommand( new Value(), CadShapes.parsePoint( input, getWorldAnchor() ) );
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

		// If the event comes from the tool, a copy should be sent to the command
		// prompt so that the command prompt displays the typed keys.
		if( event.getSource() == getTool() ) getCommandPrompt().fireEvent( event );

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

		commandStack.forEach( r -> r.getCommand().handle( this, event ) );

		// If the event is not consumed here, it will bubble up to the event
		// handling of the scene which should trigger the appropriate action.
	}

	public void handle( MouseEvent event ) {
		doEventCommand( event );
		forwardCommandToCommandStack( event );
	}

	private void forwardCommandToCommandStack( MouseEvent event ) {
		// Forward the mouse event to the other commands in the stack
		Iterator<CommandTask> iterator = commandStack.iterator();
		while( !event.isConsumed() && iterator.hasNext() ) {
			iterator.next().getCommand().handle( this, event );
		}
	}

	public void handle( ScrollEvent event ) {
		doEventCommand( event );
	}

	public void handle( ZoomEvent event ) {
		doEventCommand( event );
	}

	DesignTool getLastActiveDesignTool() {
		return lastActiveDesignTool;
	}

	// FIXME What is the difference between this method and setTool()?
	//  Because these are both set at the same time, they are the same value
	public void setLastActiveDesignTool( DesignTool tool ) {
		lastActiveDesignTool = Objects.requireNonNull( tool );
	}

	public final DesignTool getTool() {
		return tool;
	}

	// FIXME What is the difference between this method and setLastActiveDesignTool()?
	//  Because these are both set at the same time, they are the same value
	public void setTool( DesignTool tool ) {
		this.tool = Objects.requireNonNull( tool );
	}

	private void reset() {
		Fx.run( () -> {
			getLastActiveDesignTool().setSelectAperture( null, null );
			getLastActiveDesignTool().clearSelectedShapes();
			setInputMode( DesignCommandContext.Input.NONE );
			getCommandPrompt().clear();
		} );
	}

	private CommandMetadata mapCommand( String input ) {
		if( TextUtil.isEmpty( input ) ) return null;

		CommandMetadata mapping = CommandMap.getCommandByShortcut( input );
		if( mapping == CommandMap.NONE ) throw new UnknownCommand( input );

		return mapping;
	}

	private void doEventCommand( InputEvent event ) {
		// NOTE This method does not handle key events,
		//  those are handled by the action infrastructure
		CommandMetadata metadata = CommandMap.getCommandByEvent( event );
		if( metadata == CommandMap.NONE ) return;

		pushCommand( (DesignTool)event.getSource(), event, metadata.getType(), metadata.getParameters() );
	}

	private Command pushCommand( CommandMetadata metadata ) {
		if( metadata == CommandMap.NONE ) return null;
		priorCommand = metadata.getCommand();
		return pushCommand( getLastActiveDesignTool(), null, metadata.getType(), metadata.getParameters() );
	}

	private Command pushCommand( Command command, Object... parameters ) {
		return pushCommand( new CommandTask( this, getLastActiveDesignTool(), null, null, command, parameters ) );
	}

	private Command pushCommand( DesignTool tool, InputEvent event, Class<? extends Command> commandClass, Object... parameters ) {
		Objects.requireNonNull( commandClass, "Command class cannot be null" );
		try {
			return pushCommand( new CommandTask( this, tool, CommandTrigger.from( event ), event, commandClass.getConstructor().newInstance(), parameters ) );
		} catch( Exception exception ) {
			log.atSevere().withCause( exception ).log();
		}
		return null;
	}

	private Command pushCommand( CommandTask request ) {
		checkForCommonProblems( request );

		// Clear the prompt before executing the command, because
		// one of the commands could be setting a new prompt
		if( Fx.isRunning() ) getCommandPrompt().clear();

		commandStack.push( request );
		log.atTrace().log( "Command submitted %s", request );
		getProduct().task( "process-commands", this::doProcessCommands );

		// TODO Consider returning the command request
		return request.getCommand();
	}

	private void checkForCommonProblems( CommandTask request ) {
		if( request.getCommand() instanceof Value && commandStack.isEmpty() ) {
			log.atWarning().log( "There is not a command waiting for the value: %s", LazyEval.of( () -> Arrays.toString( request.getParameters() ) ) );
		}
	}

	private void logCommandStack() {
		logCommandStack( "" );
	}

	private void logCommandStack( String prefix ) {
		if( !log.at( COMMAND_STACK_LOG_LEVEL ).isEnabled() ) return;
		log.at( COMMAND_STACK_LOG_LEVEL ).log( "%s tasks=%s", prefix, commandStack.reversed() );
	}

	private Object doProcessCommands() throws Exception {
		if( Fx.isFxThread() ) {
			log.atSevere().log( "Command processing should not be run on the FX thread" );
			return FAILURE;
		}

		Object priorResult = SUCCESS;
		Object thisResult;

		try {
			List<CommandTask> tasks = new ArrayList<>( commandStack );
			for( CommandTask task : tasks ) {
				try {
					setInputMode( task.getCommand().getInputMode() );

					logCommandStack( "push" );
					thisResult = task.runTaskStep();
					if( thisResult == INCOMPLETE ) break;
					if( thisResult == INVALID ) break;

					// Add the task result to the next task
					if( commandStack.remove( task ) && !commandStack.isEmpty() ) commandStack.peek().addParameter( thisResult );

					logCommandStack( "pull" );

					priorResult = thisResult;
				} catch( Exception exception ) {
					log.atWarn( exception ).log( "Unhandled error executing command=%s", task );
					throw exception;
				}
			}
		} catch( Exception exception ) {
			cancel();
			throw exception;
		}

		return priorResult;
	}

	CommandTask getCurrentCommandTask() {
		return commandStack.peek();
	}

	private String getPriorCommand() {
		return priorCommand;
	}

}
