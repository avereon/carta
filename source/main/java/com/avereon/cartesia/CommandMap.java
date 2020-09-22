package com.avereon.cartesia;

import com.avereon.cartesia.command.*;
import com.avereon.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandMap {

	private static final System.Logger log = Log.get();

	private static final Map<String, Class<? extends Command>> commands;

	static {
		commands = new ConcurrentHashMap<>();

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

		add( "aa", ArcCommand.class );
		add( "ll", LineCommand.class );
		add( "pp", PointCommand.class );
		add( "vv", CurveCommand.class );
		add( "ww", PathCommand.class );

		add( "pa", PanCommand.class );
		add( "vp", ViewPointCommand.class );

		// gg - grid toggle
		// sn - snap nearest
		// sg - toggle snap to grid

		add( "yc", LayerCommand.class );
		//add( "ys", SubLayerCommand.class );
		add( "yh", LayerHideCommand.class );
		add( "ys", LayerShowCommand.class );
		add( "yy", LayerToggleCommand.class );

		add( "zm", ZoomCommand.class );
	}

	public static void verify() {}

	public static boolean hasCommand( String id ) {
		return commands.containsKey( id );
	}

	@SuppressWarnings( "unchecked" )
	public static <T extends Command> Class<T> get( String id ) {
		return (Class<T>)commands.get( id );
	}

	private static void add( String key, Class<? extends Command> command ) {
		Class<? extends Command> existing = commands.get( key );
		if( existing == null ) {
			commands.put( key, command );
		} else {
			log.log( Log.WARN, "Command already in use: command={0} existing={1} conflict={2}", key, existing, command );
		}
	}
}
