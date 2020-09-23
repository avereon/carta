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

		// Shape commands
		add( "aa", ArcCommand.class ); // center-endpoint-endpoint
		//add( "a3", Arc2Command.class ); // endpoint-midpoint-endpoint
		//add( "cc", CircleCommand.class ); // center-radius
		//add( "c3", Circle3Command.class ); // point-point-point
		//add( "ee", EllipseCommand.class ); // center-radius-radius
		//add( "e5", Ellipse5Command.class ); // point-point-point-point-point
		add( "ll", LineCommand.class );
		add( "pp", PointCommand.class );
		add( "vv", CurveCommand.class );
		add( "ww", PathCommand.class );

		// gg - grid toggle
		// sn - snap nearest
		// sg - toggle snap to grid

		// Layer commands
		add( "yc", LayerCommand.class );
		add( "ys", SubLayerCommand.class );
		add( "yh", LayerHideCommand.class );
		add( "yd", LayerShowCommand.class );
		add( "yy", LayerToggleCommand.class );

		// View commands
		add( "pa", PanCommand.class );
		add( "vp", ViewPointCommand.class );
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
			log.log( Log.ERROR, "Command already in use: command={0} existing={1} conflict={2}", key, existing, command );
		}
	}
}
