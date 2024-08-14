package com.avereon.cartesia.command;

import lombok.CustomLog;
import lombok.Getter;

@Getter
@CustomLog
public class InvalidInputException extends CommandException {

	private final String inputRbKey;

	private final Object value;

	public InvalidInputException( Command command, String inputRbKey, Object value ) {
		this(command, inputRbKey, value, null);
	}

	public InvalidInputException( Command command, String inputRbKey, Object value, Throwable cause ) {
		super( command, cause );
		this.inputRbKey = inputRbKey;
		this.value = value;
	}

}
