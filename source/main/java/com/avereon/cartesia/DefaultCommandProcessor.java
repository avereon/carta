package com.avereon.cartesia;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.xenon.task.Task;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;

import java.util.Stack;

/**
 * The command processor handles processing commands for a design. It is common
 * for multiple tools on the same asset to work with the command processor
 * through the course of a command. The command processor holds the state for
 * commands "in progress".
 * <p>
 * The way this should work is a command pushes param commands onto the stack
 * for anything that is not available. Those commands are evaluated until there
 * are enough parameters for the original command.
 */
@Deprecated
public class DefaultCommandProcessor implements CommandProcessor {

	private static final System.Logger log = Log.get();

	private final Stack<OldCommand> commandStack;

	private final Stack<Object> valueStack;

	private Point3D anchor;

	private boolean isAutoCommandSafe;

	private String priorCommand;

	public DefaultCommandProcessor() {
		commandStack = new Stack<>();
		valueStack = new Stack<>();
		anchor = new Point3D( 0, 0, 0 );
		isAutoCommandSafe = true;
		priorCommand = "";
	}

	@Override
	public String getPriorCommand() {
		return priorCommand;
	}

	@Override
	public void cancel( DesignTool tool ) {
		Fx.run( () -> tool.setCursor( Cursor.DEFAULT ) );
		tool.getCommandPrompt().setPrompt( null );
		isAutoCommandSafe = true;
		commandStack.clear();
		valueStack.clear();
	}

	@Override
	public void mouse( Point3D point ) {
		commandStack.stream().findFirst().ifPresent( c -> c.mouse( point ) );
	}

	/**
	 * Convenience method to evaluate known points
	 *
	 * @param tool The design tool
	 * @param point The selected point
	 */
	@Override
	public void evaluate( DesignTool tool, Point3D point ) {
		pushValue( tool, point );
	}

	@Override
	public void evaluate( DesignTool tool, String input ) throws CommandException {
//		if( TextUtil.isEmpty( input ) ) return;
//
//		//Class<OldCommand> commandClass = CommandMap.get( input );
//		Class<OldCommand> commandClass = null;
//		String text = input.trim();
//		Point3D point = parsePoint( text );
//
//		if( commandClass != null ) {
//			try {
//				priorCommand = text;
//
//				log.log( Log.DEBUG, "Command found {0}", commandClass.getName() );
//				OldCommand command = commandClass.getConstructor().newInstance();
//
//				// Push the command itself
//				pushCommand( command );
//
//				// Push all the command pre steps
//				List<OldCommand> preSteps = new ArrayList<>( command.getPreSteps( tool ) );
//				Collections.reverse( preSteps );
//				preSteps.forEach( this::pushCommand );
//
//				// Start the command
//				tool.getProgram().getTaskManager().submit( new CommandTask( this, tool, pullCommand( tool ) ) );
//			} catch( Exception exception ) {
//				throw new CommandException( exception );
//			}
//		} else if( point != null ) {
//			pushValue( tool, point );
//		} else if( !TextUtil.isEmpty( text ) ) {
//			pushValue( tool, text );
//		}
	}

	@Override
	public void pushValue( DesignTool tool, Object object ) {
		valueStack.push( object );
		nextCommand( tool );
	}

	@Override
	public Object pullValue() {
		return valueStack.isEmpty() ? null : valueStack.pop();
	}

	@Override
	public boolean isSelecting() {
		return commandStack.isEmpty();
	}

	@Override
	public boolean isAutoCommandSafe() {
		return isAutoCommandSafe;
	}

	void nextCommand( DesignTool tool ) {
		try {
			// If there are no more commands but there is a shape on the value stack
			if( commandStack.isEmpty() ) {
				Fx.run( () -> tool.setCursor( Cursor.DEFAULT ) );
				if( !valueStack.isEmpty() ) {
					Object value = valueStack.pop();
					if( value instanceof DesignShape ) {
						// FIXME This should use the current layer of the starting tool
						tool.getCurrentLayer().addShape( (DesignShape)value );
					}
				}
			} else {
				tool.getProgram().getTaskManager().submit( new CommandTask( this, tool, pullCommand( tool ) ) );
			}
		} finally {
			// Don't set the cursor back to default here
			isAutoCommandSafe = true;
		}
	}

	OldCommand peekCommand() {
		return commandStack.peek();
	}

	void pushCommand( OldCommand command ) {
		commandStack.push( command );
	}

	OldCommand pullCommand( DesignTool tool ) {
		OldCommand command = commandStack.pop();
		isAutoCommandSafe = command.isAutoCommandSafe();
		if( commandStack.isEmpty() ) tool.getCommandPrompt().setPrompt( null );
		return command;
	}

	private static class CommandTask extends Task<Void> {

		private final CommandProcessor processor;

		private final DesignTool tool;

		private final OldCommand command;

		public CommandTask( CommandProcessor processor, DesignTool tool, OldCommand command ) {
			this.processor = processor;
			this.tool = tool;
			this.command = command;
		}

		@Override
		public Void call() throws Exception {
			command.evaluate( processor, tool );
			return null;
		}

	}

}
