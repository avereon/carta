package com.avereon.cartesia;

import com.avereon.util.Log;

import java.util.Stack;

/**
 * The command processor handles processing commands for a design. It is common
 * for multiple tools on the same asset to work with the command processor
 * through the course of a command. The command processor holds the state for
 * commands "in progress".
 *
 * The way this should work is a command pushes param commands onto the stack
 * for anything that is not available. Those commands are evaluated until there
 * are enough parameters for the original command.
 */
public class CommandProcessor {

	private static final System.Logger log = Log.get();

	private Stack<Command<?>> commandStack;

	private Stack<Object> valueStack;

	public void cancel() {
		commandStack.clear();
		valueStack.clear();
	}

	public void evaluate( String text ) {
		Class<Command<?>> commandClass = CommandMap.get( text );

		if( commandClass != null ) {
			try {
				Command<?> command = commandClass.getConstructor().newInstance();
				log.log( Log.WARN, "Command found {0}", command.getClass().getName() );
			} catch( Exception exception ) {
				exception.printStackTrace();
			}
		}

		// The text needs to be turned into a command
		// The text can be a lettered command to start one
		// ...or it can be a value expression for a point
		// ...or it can be a text value (but maybe text should be wysiwyg)
	}

	public void pushValue( Object value ) {
		valueStack.push( value );

		// NEXT Check the command on the stack if there are enough parameters to process
		// The command can ask for another value...
		// or cancel
		// or evaluate and return a value
	}

}
