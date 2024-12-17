package com.avereon.cartesia;

import com.avereon.cartesia.command.Command;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Getter
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

	private final List<String> tags;

	public CommandMetadata( String action, String name, String command, String shortcut, List<String> tags, Class<? extends Command> type, Object... parameters ) {
		this.action = Objects.requireNonNull( action );
		this.name = Objects.requireNonNull( name );
		this.command = command;
		this.shortcut = shortcut;
		this.type = Objects.requireNonNull( type );
		this.parameters = Objects.requireNonNull( parameters );
		this.tags = tags;
	}

	public CommandMetadata cloneWithParameters( Object... parameters ) {
		return new CommandMetadata( action, name, command, shortcut, null, type, parameters );
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
