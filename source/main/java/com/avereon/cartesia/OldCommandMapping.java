package com.avereon.cartesia;

import java.util.Objects;

public class OldCommandMapping {

	private final String action;

	private final String shortcut;

	private final Class<? extends OldCommand> command;

	private final Object[] parameters;

	public OldCommandMapping( String action, String shortcut, Class<? extends OldCommand> command, Object... parameters ) {
		this.action = Objects.requireNonNull( action );
		this.shortcut = Objects.requireNonNull( shortcut );
		this.command = Objects.requireNonNull( command );
		this.parameters = Objects.requireNonNull( parameters );
	}

	public String getAction() {
		return action;
	}

	public String getShortcut() {
		return shortcut;
	}

	public Class<? extends OldCommand> getCommand() {
		return command;
	}

	public Object[] getParameters() {
		return parameters;
	}
}
