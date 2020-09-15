package com.avereon.cartesia;

import com.avereon.cartesia.data.CsaShape;
import com.avereon.cartesia.math.MathEx;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.util.TextUtil;
import com.avereon.xenon.task.Task;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;

import java.text.ParseException;
import java.util.*;

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
public class CommandProcessor {

	private static final System.Logger log = Log.get();

	private final Stack<Command> commandStack;

	private final Stack<Object> valueStack;

	private Point3D anchor;

	public CommandProcessor() {
		commandStack = new Stack<>();
		valueStack = new Stack<>();
		anchor = new Point3D( 0, 0, 0 );
	}

	public void cancel( DesignTool tool ) {
		Platform.runLater( () -> tool.setCursor( Cursor.DEFAULT ) );
		tool.getCommandPrompt().setPrompt( null );
		commandStack.clear();
		valueStack.clear();
	}

	/**
	 * Convenience method to evaluate known points
	 *
	 * @param tool
	 * @param point
	 */
	public void evaluate( DesignTool tool, Point3D point ) {
		pushValue( tool, point );
	}

	public void evaluate( DesignTool tool, String input ) throws CommandException {
		if( TextUtil.isEmpty( input ) ) return;

		Class<Command> commandClass = CommandMap.get( input );
		Point3D point = parsePoint( input );
		String text = input.trim();

		if( commandClass != null ) {
			try {
				log.log( Log.WARN, "Command found {0}", commandClass.getName() );
				Command command = commandClass.getConstructor().newInstance();

				// Push the command itself
				pushCommand( command );

				// Push all the command pre steps
				List<Command> preSteps = new ArrayList<>( command.getPreSteps( tool ) );
				Collections.reverse( preSteps );
				preSteps.forEach( this::pushCommand );

				// Start the next task
				tool.getProgram().getTaskManager().submit( new CommandTask( this, tool, pullCommand( tool ) ) );
			} catch( Exception exception ) {
				throw new CommandException( exception );
			}
		} else if( point != null ) {
			pushValue( tool, point );
		} else if( !TextUtil.isEmpty( text ) ) {
			pushValue( tool, text );
		}
	}

	public void pushValue( DesignTool tool, Object object ) {
		valueStack.push( object );
		nextCommand( tool );
	}

	public Object pullValue() {
		return valueStack.isEmpty() ? null : valueStack.pop();
	}

	public boolean isSelecting() {
		return commandStack.isEmpty();
	}

	void nextCommand( DesignTool tool ) {
		try {
			// If there are no more commands but there is a shape on the value stack
			if( commandStack.isEmpty() ) {
				if( !valueStack.isEmpty() ) {
					Object value = valueStack.pop();
					if( value instanceof CsaShape ) {
						CsaShape shape = (CsaShape)value;
						tool.getDesign().getCurrentLayer().addShape( shape );
					}
				}
			} else {
				tool.getProgram().getTaskManager().submit( new CommandTask( this, tool, pullCommand( tool ) ) );
			}
		} finally {
			Platform.runLater( () -> tool.setCursor( Cursor.DEFAULT ) );
		}
	}

	void pushCommand( Command command ) {
		commandStack.push( command );
	}

	Command pullCommand( DesignTool tool ) {
		Command command = commandStack.pop();
		if( commandStack.isEmpty() ) tool.getCommandPrompt().setPrompt( null );
		return command;
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
			if( relative ) point = anchor.add( point );
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

	private static class CommandTask extends Task<Void> {

		private final CommandProcessor processor;

		private final DesignTool tool;

		private final Command command;

		public CommandTask( CommandProcessor processor, DesignTool tool, Command command ) {
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
