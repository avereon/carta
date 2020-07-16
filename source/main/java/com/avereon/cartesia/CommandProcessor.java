package com.avereon.cartesia;

import java.util.Stack;

/**
 * The command processor handles processing commands for a design. It is common
 * for multiple tools on the same asset to work with the command processor
 * through the course of a command. The command processor holds the state for
 * commands "in progress".
 */
public class CommandProcessor {

	private Stack<Command<?>> commandStack;

	private Stack<Object> valueStack;

	public void evaluate( String text ) {
		Class<Command<?>> commandClass = CommandMap.get( text );
		try {
			Command<?> command = commandClass.getConstructor(  ).newInstance(  );
		} catch( Exception exception ) {
			exception.printStackTrace();
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
