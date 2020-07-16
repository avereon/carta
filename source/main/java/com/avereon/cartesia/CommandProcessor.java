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

	private Stack<?> parameterStack;

}
