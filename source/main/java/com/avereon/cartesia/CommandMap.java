package com.avereon.cartesia;

import com.avereon.cartesia.command.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommandMap {

	private static final Map<String, Class<? extends Command<?>>> defaultCommands;

	static {
		Map<String, Class<? extends Command<?>>> map = new HashMap<>();

		map.put( "aa", ArcCommand.class );
		map.put( "ll", LineCommand.class );
		map.put( "pp", PathCommand.class );
		map.put( "vv", CurveCommand.class );
		map.put( "ps", PointCommand.class );

		defaultCommands = Collections.unmodifiableMap( map );
	}

	public static boolean hasCommand( String id ) {
		return defaultCommands.containsKey( id );
	}

	@SuppressWarnings( "unchecked" )
	public static <T extends Command<?>> Class<T> get( String id ) {
		return (Class<T>)defaultCommands.get( id );
	}

}
