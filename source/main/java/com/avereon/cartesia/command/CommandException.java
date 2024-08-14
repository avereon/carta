package com.avereon.cartesia.command;

import lombok.Getter;

@Getter
public abstract class CommandException extends Exception {

	private final Command command;

	public CommandException( Command command ) {
		super();
		this.command = command;
	}

	public CommandException( Command command, Throwable cause ) {
		super( cause );
		this.command = command;
	}

}
