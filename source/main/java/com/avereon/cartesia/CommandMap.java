package com.avereon.cartesia;

import com.avereon.cartesia.command.*;
import com.avereon.cartesia.snap.SnapGrid;
import com.avereon.cartesia.snap.SnapNearest;
import com.avereon.util.Log;
import com.avereon.util.TextUtil;
import com.avereon.xenon.ProgramProduct;
import javafx.scene.input.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandMap {

	private static final System.Logger log = Log.get();

	public static final String COMMAND_SUFFIX = ".command";

	private static final Map<String, CommandMapping> actionCommands = new ConcurrentHashMap<>();

	private static final Map<String, String> shortcutActions = new ConcurrentHashMap<>();

	private static final Map<CommandEventKey, String> eventActions = new ConcurrentHashMap<>();

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
		// w - path?
		// y - layer
		// z - zoom

		// Event type actions
		add( new CommandEventKey( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY ), "select" );
		add( new CommandEventKey( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, true, false, false, false ), "select" );
		add( new CommandEventKey( MouseEvent.MOUSE_PRESSED, MouseButton.SECONDARY ), "snap-auto-nearest" );

		// FIXME Conflicts with select
		//add( new CommandEventKey( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, true, false, false, false ), "camera-spin" );
		add( new CommandEventKey( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, true, false, false ), "camera-move" );
		add( new CommandEventKey( ScrollEvent.SCROLL ), "camera-zoom" );
		add( new CommandEventKey( ZoomEvent.ZOOM ), "camera-zoom" );
		add( new CommandEventKey( ScrollEvent.SCROLL, true, false, false, false ), "camera-walk" );
		add( new CommandEventKey( ZoomEvent.ZOOM, true, false, false, false ), "camera-walk" );

		// Basic command
		add( product, "select", SelectCommand.class );

		// View commands
		add( product, "camera-move", CameraMoveCommand.class );
		//add( product, "camera-spin", CameraSpinCommand.class );
		add( product, "camera-view-point", ViewPointCommand.class );
		//add( product, "camera-walk", CameraWalkCommand.class );
		add( product, "camera-zoom", CameraZoomCommand.class );
		add( product, "camera-zoom-in", CameraZoomInCommand.class );
		add( product, "camera-zoom-out", CameraZoomOutCommand.class );
		//add( product, "camera-zoom-window", ZoomWindowCommand.class );

		// Grid commands
		add( product, "grid-toggle", GridToggleCommand.class );

		// Measure commands
		//add( product, "measure-angle", MeasureAngleCommand.class );
		add( product, "measure-distance", MeasureDistanceCommand.class );

		// Shape commands
		add( product, "draw-arc-2", ArcCommand.class ); // center-endpoint-endpoint
		//add( "draw-arc-3", Arc3Command.class ); // endpoint-midpoint-endpoint
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
		add( product, "snap-auto-grid", SnapAutoCommand.class, new SnapGrid() );
		add( product, "snap-auto-nearest", SnapAutoCommand.class, new SnapNearest() );
		add( product, "snap-grid-toggle", SnapGridToggle.class );

		// Layer commands
		add( product, "layer-create", LayerCreateCommand.class );
		add( product, "layer-show", LayerShowCommand.class );
		add( product, "layer-hide", LayerHideCommand.class );
		add( product, "layer-sublayer", LayerSubLayerCommand.class );
		add( product, "layer-delete", LayerDeleteCommand.class );
		add( product, "layer-toggle", LayerToggleCommand.class );
	}

	public static boolean hasCommand( String shortcut ) {
		return shortcutActions.containsKey( shortcut );
	}

	public static CommandMapping get( String shortcut ) {
		return getActionCommand( shortcutActions.getOrDefault( shortcut, TextUtil.EMPTY ) );
	}

	public static CommandMapping get( InputEvent event ) {
		return getActionCommand( eventActions.getOrDefault( CommandEventKey.of( event ), TextUtil.EMPTY ) );
	}

	private static CommandMapping getActionCommand( String action ) {
		if( TextUtil.isEmpty( action ) ) return null;
		CommandMapping mapping = actionCommands.get( action );
		if( mapping == null ) log.log( Log.WARN, "No command for action: " + action );
		return mapping;
	}

	private static void add( CommandEventKey key, String action ) {
		eventActions.put( key, action );
	}

	private static void add( ProgramProduct product, String action, Class<? extends Command> command, Object... parameters ) {
		String shortcut = product.rb().textOr( BundleKey.ACTION, action + COMMAND_SUFFIX, "" ).toLowerCase();

		if( !actionCommands.containsKey( action ) ) {
			shortcutActions.put( shortcut, action );
			actionCommands.put( action, new CommandMapping( action, shortcut, command, parameters ) );
		} else {
			CommandMapping existing = actionCommands.get( action );
			log.log(
				Log.ERROR,
				"Shortcut already used: shortcut={0} action={1} conflict={2} existing={3}",
				shortcut,
				action,
				command.getName(),
				existing.getCommand().getName()
			);
		}
	}

}
