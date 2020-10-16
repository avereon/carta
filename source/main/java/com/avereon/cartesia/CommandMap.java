package com.avereon.cartesia;

import com.avereon.cartesia.command.*;
import com.avereon.util.Log;
import com.avereon.xenon.Action;
import com.avereon.xenon.ProgramProduct;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CommandMap {

	private static final System.Logger log = Log.get();

	private static final Map<String, Class<? extends Command>> commands = new ConcurrentHashMap<>();

	private static final Map<String, String> shortcutActions = new ConcurrentHashMap<>();

	private static final Map<String, CommandMapping> shortcutCommands = new ConcurrentHashMap<>();

	public static void load( ProgramProduct product ) {
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
		add( product, "draw-arc-2", ArcCommand.class ); // center-endpoint-endpoint
		//add( "draw-arc-3", Arc2Command.class ); // endpoint-midpoint-endpoint
		//add( "draw-circle-2", CircleCommand.class ); // center-radius
		//add( "draw-circle-3", Circle3Command.class ); // point-point-point
		//add( "draw-ellipse-3", EllipseCommand.class ); // center-radius-radius
		//add( "draw-ellipse-5", Ellipse5Command.class ); // point-point-point-point-point
		//add( "draw-ellipse-arc-5", EllipseArc5Command.class ); // center-radius-radius-endpoint-endpoint
		add( product, "draw-line-2", LineCommand.class ); // endpoint-endpoint
		//add( product, "draw-line-perpendicular", LineCommand.class ); // shape-endpoint-endpoint
		add( product, "draw-point", PointCommand.class ); // point
		add( product, "draw-curve-4", CurveCommand.class ); // endpoint-midpoint-midpoint-endpoint
		add( product, "draw-path", PathCommand.class );

		// gg - grid toggle
		// sn - snap nearest
		// sg - toggle snap to grid

		// Layer commands
		add( product, "layer-create", LayerCreateCommand.class );
		add( product, "layer-show", LayerShowCommand.class );
		add( product, "layer-hide", LayerHideCommand.class );
		add( product, "layer-sublayer", LayerSubLayerCommand.class );
		add( product, "layer-delete", LayerDeleteCommand.class );
		add( product, "layer-toggle", LayerToggleCommand.class );

		// View commands
		add( product, "view-pan", PanCommand.class );
		add( product, "view-point", ViewPointCommand.class );
		add( product, "zoom", ZoomCommand.class );
		//add( product, "zoom-in", ZoomInCommand.class );
		//add( product, "zoom-out", ZoomOutCommand.class );
		//add( product, "zoom-window", ZoomWindowCommand.class );
	}

	public static Set<CommandMapping> getMappings() {
		return new HashSet<>( shortcutCommands.values() );
	}

	public static boolean hasCommand( String shortcut ) {
		return shortcutCommands.containsKey( shortcut );
	}

	@SuppressWarnings( "unchecked" )
	public static <T extends Command> Class<T> get( String shortcut ) {
		return (Class<T>)shortcutCommands.get( shortcut ).getCommand();
	}

	private static void add( ProgramProduct product, String key, Class<? extends Command> command, Object... parameters ) {
		String shortcut = product.rb().textOr( BundleKey.ACTION, key + Action.SHORTCUT_SUFFIX, "" ).toLowerCase();

		if( !shortcutCommands.containsKey( shortcut ) ) {
			shortcutCommands.put( shortcut, new CommandMapping( key, shortcut, command, parameters ) );
		} else {
			CommandMapping existing = shortcutCommands.get( shortcut );
			log.log(
				Log.ERROR,
				"Shortcut already used: shortcut={0} action={1} conflict={2} existing={3}",
				shortcut,
				key,
				command.getName(),
				existing.getCommand().getName()
			);
		}
	}

}
