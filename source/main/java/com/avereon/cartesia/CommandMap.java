package com.avereon.cartesia;

import com.avereon.cartesia.command.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommandMap {

	private static final Map<String, Class<? extends Command>> defaultCommands;

	static {
		Map<String, Class<? extends Command>> map = new HashMap<>();

		// High level letters
		// a - arc
		// c - circle
		// e - ellipse
		// g - grid
		// l - line
		// p - point
		// s - snap
		// t - text
		// v - curve
		// y - layer
		// z - zoom

		map.put( "aa", ArcCommand.class );
		map.put( "ll", LineCommand.class );
		map.put( "pp", PointCommand.class );
		map.put( "vv", CurveCommand.class );
		map.put( "ww", PathCommand.class );
		map.put( "yy", LayerCommand.class );
		//map.put( "sy", SubLayerCommand.class );

		map.put( "pa", PanCommand.class );

		// gg - grid toggle
		// sn - snap nearest
		// sg - toggle snap to grid

		defaultCommands = Collections.unmodifiableMap( map );
	}

	public static boolean hasCommand( String id ) {
		return defaultCommands.containsKey( id );
	}

	@SuppressWarnings( "unchecked" )
	public static <T extends Command> Class<T> get( String id ) {
		return (Class<T>)defaultCommands.get( id );
	}

}
