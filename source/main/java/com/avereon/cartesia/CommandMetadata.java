package com.avereon.cartesia;

import com.avereon.cartesia.command.Command;

import java.util.Comparator;
import java.util.Objects;

public class CommandMetadata implements Comparable<CommandMetadata> {

	public static final Comparator<CommandMetadata> BY_ACTION = new CommandActionComparator();

	public static final Comparator<CommandMetadata> BY_NAME = new CommandNameComparator();

	public static final Comparator<CommandMetadata> BY_SHORTCUT = new CommandShortcutComparator();

	private final String action;

	private final String name;

	private final String command;

	private final String shortcut;

	private final Class<? extends Command> type;

	private final Object[] parameters;

	private final String[] tags;

	public CommandMetadata( String action, String name, String command, String shortcut, String[] tags, Class<? extends Command> type, Object... parameters ) {
		this.action = Objects.requireNonNull( action );
		this.name = Objects.requireNonNull( name );
		this.command = command;
		this.shortcut = shortcut;
		this.type = Objects.requireNonNull( type );
		this.parameters = Objects.requireNonNull( parameters );
		this.tags = tags;
	}

	public String getAction() {
		return action;
	}

	public String getName() {
		return name;
	}

	public String getCommand() {
		return command;
	}

	public String getShortcut() {
		return shortcut;
	}

	public Class<? extends Command> getType() {
		return type;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public String[] getTags() {
		return tags;
	}

	@Override
	public int compareTo( CommandMetadata that ) {
		return BY_SHORTCUT.compare( this, that );
	}

	private static class CommandShortcutComparator implements Comparator<CommandMetadata> {

		@Override
		public int compare( CommandMetadata o1, CommandMetadata o2 ) {
			String s1 = o1.getCommand();
			String s2 = o2.getCommand();
			if( s1 != null && s2 != null ) return s1.compareTo( s2 );

			return o1.getAction().compareTo( o2.getAction() );
		}

	}

	private static class CommandActionComparator implements Comparator<CommandMetadata> {

		@Override
		public int compare( CommandMetadata o1, CommandMetadata o2 ) {
			return o1.getAction().compareTo( o2.getAction() );
		}

	}

	private static class CommandNameComparator implements Comparator<CommandMetadata> {

		@Override
		public int compare( CommandMetadata o1, CommandMetadata o2 ) {
			return o1.getName().compareTo( o2.getName() );
		}

	}

}
