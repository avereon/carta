package com.avereon.cartesia.tool;

import com.avereon.cartesia.CartesiaMod;
import com.avereon.cartesia.CommandMetadata;
import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.command.*;
import com.avereon.cartesia.command.base.Value;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.error.UnknownCommand;
import com.avereon.log.LazyEval;
import com.avereon.product.Rb;
import com.avereon.util.TextUtil;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.notice.Notice;
import com.avereon.zerra.javafx.Fx;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.input.*;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;

import static com.avereon.cartesia.CommandMap.NONE;
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
		SHAPE,
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

	public final XenonProgramProduct getProduct() {
		return product;
	}

	public final Xenon getProgram() {
		return product.getProgram();
	}

	public final CartesiaMod getMod() {
		return (CartesiaMod)product;
	}

	public CommandPrompt getCommandPrompt() {
		if( commandPrompt == null ) this.commandPrompt = new CommandPrompt( this );
		return commandPrompt;
	}

	public CommandTask getCommand( int index ) {
		return new ArrayList<>( commandStack ).get( index );
	}

	public Command submit( DesignTool tool, String command ) {
		String[] parts = command.split( " " );

		String shortcut = parts[ 0 ];

		CommandMetadata metadata = getMod().getCommandMap().getCommandByShortcut( shortcut );
		if( metadata == NONE ) return null;

		// Create a new metadata object with the parameters
		metadata = metadata.cloneWithParameters( (Object[])Arrays.copyOfRange( parts, 1, parts.length ) );

		return submitCommand( metadata );
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
		return submitCommand( new CommandTask( this, tool, null, null, command, parameters ) );
	}

	public Command submit( CommandTask request ) {
		return submitCommand( request );
	}

	public void cancelAllCommands( KeyEvent event ) {
		event.consume();
		cancelAllCommands();
	}

	private void cancelAllCommands() {
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
			submitCommand( new SelectByPoint(), mouseEvent );
		} else {
			// Process text calls doCommand
			processText( input, true );
		}
		reset();
	}

	public void repeat( KeyEvent event ) {
		event.consume();
		if( TextUtil.isEmpty( getCommandPrompt().getCommand() ) ) {
			submitCommand( mapCommand( getPriorCommand() ) );
			reset();
		}
	}

	Command processText( String input, boolean strict ) {
		boolean isTextInput = getInputMode() == DesignCommandContext.Input.TEXT;
		if( strict ) {
			return switch( getInputMode() ) {
				case NUMBER, POINT, TEXT -> submitCommand( new Value(), input );
				default -> submitCommand( mapCommand( input ) );
			};
		} else if( !isTextInput && isAutoCommandEnabled() && getMod().getCommandMap().hasCommand( input ) ) {
			return submitCommand( mapCommand( input ) );
		}
		return null;
	}

	public void command( String input ) {
		submitCommand( mapCommand( input ) );
	}

	public boolean isPenMode() {
		return commandStack.size() > 1;
	}

	public boolean isSelectMode() {
		return commandStack.size() < 2;
	}

	public boolean isEmptyMode() {
		return commandStack.isEmpty();
	}

	public boolean isInteractive() {
		// TODO Turn off when running a script
		return true;
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
				case ESCAPE -> cancelAllCommands( event );
				case ENTER -> enter( event );
				case SPACE -> repeat( event );
			}
		}

		forwardCommandToCommandStack( event );

		// If the event is not consumed here, it will bubble up to the event
		// handling of the scene which should trigger the appropriate action.
	}

	void forwardCommandToCommandStack( KeyEvent event ) {
		Iterator<CommandTask> iterator = commandStack.iterator();
		while( iterator.hasNext() && !event.isConsumed() ) {
			CommandTask task = iterator.next();
			task.getCommand().handle( task, event );
		}
		// Do not consume key events here, let them bubble up
	}

	public void handle( MouseEvent event ) {
		// If the event does not trigger a command, forward it to the command stack
		if( !submitEventCommand( event ) ) forwardCommandToCommandStack( event );
	}

	void forwardCommandToCommandStack( MouseEvent event ) {
		Iterator<CommandTask> iterator = commandStack.iterator();
		while( iterator.hasNext() && !event.isConsumed() ) {
			CommandTask task = iterator.next();
			task.getCommand().handle( task, event );
		}
		event.consume();
	}

	public void handle( ScrollEvent event ) {
		submitEventCommand( event );
	}

	public void handle( ZoomEvent event ) {
		submitEventCommand( event );
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

	// For test purposes only
	int getCommandStackDepth() {
		return commandStack.size();
	}

	// THREAD Task Thread
	Object doProcessCommands() throws Exception {
		if( Fx.isFxThread() ) {
			log.atSevere().log( "Command processing should not be run on the FX thread" );
			return FAILURE;
		}

		CommandTask task = null;
		Object result = SUCCESS;

		try {
			// This loop tries to execute as many steps as possible on the command
			// stack. It is possible that the command stack will grow, if user input
			// is required, or shrink as commands are completed.
			while( !commandStack.isEmpty() ) {
				task = commandStack.peek();
				logCommandStack( "exec" );
				setInputMode( task.getCommand().getInputMode() );

				// Run the next task step
				Object stepResult = task.runTaskStep();

				// Don't pass incomplete results to the next task and
				// allow the calling thread to exit with the INCOMPLETE result.
				if( stepResult == INCOMPLETE ) return stepResult;

				// Remove the command if it has completed
				commandStack.remove( task );

				// Pass the task result to the next task
				passParameter( commandStack.peek(), stepResult );

				logCommandStack( "rslt" );

				result = stepResult;
			}
		} catch( InvalidInputException exception ) {
			commandStack.remove( task );
			String title = Rb.text( RbKey.NOTICE, "invalid-input" );
			String message = Rb.text( RbKey.PROMPT, exception.getInputRbKey() ) + " " + exception.getValue();
			if( task.getContext().isInteractive() ) {
				getProgram().getNoticeManager().addNotice( new Notice( title, message ).setType( Notice.Type.WARN ) );
			} else {
				log.atWarn( exception ).log( "Invalid input=%s", task );
			}
		} catch( Exception exception ) {
			cancelAllCommands();
			throw exception;
		}

		return result;
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

		CommandMetadata mapping = getMod().getCommandMap().getCommandByShortcut( input );
		if( mapping == NONE ) throw new UnknownCommand( input );

		return mapping;
	}

	private boolean submitEventCommand( InputEvent event ) {
		// NOTE This method does not handle key events,
		//  those are handled by the action infrastructure
		CommandMetadata metadata = getMod().getCommandMap().getCommandByEvent( event );
		if( metadata == NONE ) return false;

		submitCommand( (DesignTool)event.getSource(), event, metadata.getType(), metadata.getParameters() );
		return true;
	}

	private Command submitCommand( CommandMetadata metadata ) {
		if( metadata == NONE ) return null;
		priorCommand = metadata.getCommand();
		return submitCommand( getLastActiveDesignTool(), null, metadata.getType(), metadata.getParameters() );
	}

	private Command submitCommand( Command command, Object... parameters ) {
		return submitCommand( new CommandTask( this, getLastActiveDesignTool(), null, null, command, parameters ) );
	}

	private Command submitCommand( DesignTool tool, InputEvent event, Class<? extends Command> commandClass, Object... parameters ) {
		Objects.requireNonNull( commandClass, "Command class cannot be null" );
		try {
			return submitCommand( new CommandTask( this, tool, CommandTrigger.from( event ), event, commandClass.getConstructor().newInstance(), parameters ) );
		} catch( Exception exception ) {
			log.atSevere().withCause( exception ).log();
		}
		return null;
	}

	Command submitCommand( CommandTask request ) {
		commandStack.removeIf( r -> r.getCommand() == request.getCommand() );

		checkForCommonProblems( request );

		// Clear the prompt before executing the command, because
		// one of the commands could be setting a new prompt
		if( Fx.isRunning() ) getCommandPrompt().clear();

		request.setPrior( commandStack.peek() );
		commandStack.push( request );
		log.atTrace().log( "Command submitted %s", request );

		// Run the processing on a task thread
		getProduct().task( "process-commands", this::doProcessCommands );

		// TODO Consider returning the command request
		return request.getCommand();
	}

	private void checkForCommonProblems( CommandTask request ) {
		if( request.getCommand() instanceof Value && commandStack.isEmpty() ) {
			log.atWarning().log( "There is not a command waiting for the value: %s", LazyEval.of( () -> Arrays.toString( request.getParameters() ) ) );
		}
	}

	private void logCommandStack( String prefix ) {
		if( !log.at( COMMAND_STACK_LOG_LEVEL ).isEnabled() ) return;
		log.at( COMMAND_STACK_LOG_LEVEL ).log( "%s tasks=%s", prefix, commandStack.reversed() );
	}

	private void passParameter( CommandTask task, Object parameter ) throws InvalidInputException {
		if( task == null ) return;
		if( parameter == null ) throw new InvalidInputException( task.getCommand(), "step-result", "null" );
		task.addParameter( parameter );
	}

	private String getPriorCommand() {
		return priorCommand;
	}

}
