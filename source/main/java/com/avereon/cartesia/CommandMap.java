package com.avereon.cartesia;

import com.avereon.cartesia.command.*;
import com.avereon.cartesia.snap.SnapGrid;
import com.avereon.cartesia.snap.SnapNearest;
import com.avereon.util.Log;
import com.avereon.util.TextUtil;
import com.avereon.xenon.ActionLibrary;
import com.avereon.xenon.ActionProxy;
import com.avereon.xenon.ProgramProduct;
import javafx.scene.input.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandMap {

	private static final System.Logger log = Log.get();

	private static final Map<String, CommandMetadata> actionCommands = new ConcurrentHashMap<>();

	private static final Map<String, String> commandActions = new ConcurrentHashMap<>();

	private static final Map<CommandEventKey, String> eventActions = new ConcurrentHashMap<>();

	public static void load( ProgramProduct product ) {
		// High level letters
		// a - arc
		// c - circle
		// d - draw (size, paint, pattern, etc.)
		// e - ellipse
		// f - fill (paint, pattern, pattern offset, etc.)
		// g - grid
		// k - color/paint
		// l - line
		// p - point
		// s - snap
		// t - text
		// v - curve
		// w - path?
		// y - layer
		// z - zoom

		// Basic command
		add( product, "select", SelectCommand.class );

		// View commands
		add( product, "camera-move", CameraMoveCommand.class );
		//add( product, "camera-spin", CameraSpinCommand.class );
		add( product, "camera-view-point", ViewPointCommand.class );
		//add( product, "camera-walk", CameraWalkCommand.class );
		add( product, "camera-zoom", CameraZoomCommand.class );
		//add( product, "camera-zoom-all", CameraZoomAllCommand.class );
		add( product, "camera-zoom-in", CameraZoomInCommand.class );
		add( product, "camera-zoom-out", CameraZoomOutCommand.class );
		//add( product, "camera-zoom-window", ZoomWindowCommand.class );

		// Grid commands
		// gg - grid toggle
		add( product, "grid-toggle", GridToggleCommand.class );

		// Measure commands
		//add( product, "measure-angle", MeasureAngleCommand.class );
		add( product, "measure-distance", MeasureDistanceCommand.class );

		// Draw setting commands
		// In GCAD these were initially managed with simple commands. Later on you could tell the
		// pattern changed to use grouped settings. There are multiple options here:
		// 1. Use simple commands for each setting. This could end up using a lot of commands
		//    unless three-letter commands (or two-letter with extra) are allowed.
		// 2. Use a single command to show the properties/settings for a context. Such as,
		//    DS for design draw/fill settings,
		//    YS for current layer draw/fill settings and
		//    LS or PS for new shape draw/fill settings.
		// 3. A combination of both ideas above where simple commands are defined for common actions.
		//
		// There are several settings each for draw and fill:
		//   Draw has size/width, paint/color, pattern, pattern offset, cap, join
		//   Fill has paint/color, pattern/image, pattern offset
		// design draw width
		// design draw paint
		// design fill paint
		// layer draw width
		// layer draw paint
		// layer fill paint
		// shape draw width
		// shape draw paint
		// shape fill paint

		// Shape commands
		add( product, "draw-arc-2", DrawArcCommand.class ); // center-endpoint-endpoint
		//add( "draw-arc-3", Arc3Command.class ); // endpoint-midpoint-endpoint
		add( product, "draw-circle-2", DrawCircleCommand.class ); // center-radius
		//add( "draw-circle-3", Circle3Command.class ); // point-point-point
		//add( "draw-ellipse-3", EllipseCommand.class ); // center-radius/start-extent
		//add( "draw-ellipse-5", Ellipse5Command.class ); // point-point-point-point-point
		//add( "draw-ellipse-arc-5", EllipseArc5Command.class ); // center-radius-radius-start-extent
		add( product, "draw-line-2", DrawLineCommand.class ); // endpoint-endpoint
		add( product, "draw-line-perpendicular", DrawLinePerpendicular.class ); // shape-endpoint-endpoint
		add( product, "draw-point", DrawPointCommand.class ); // point
		add( product, "draw-curve-4", CurveCommand.class ); // endpoint-midpoint-midpoint-endpoint
		add( product, "draw-path", PathCommand.class );

		// Layer commands
		add( product, "layer-create", LayerCreateCommand.class );
		add( product, "layer-show", LayerShowCommand.class );
		add( product, "layer-hide", LayerHideCommand.class );
		add( product, "layer-sublayer", LayerSubLayerCommand.class );
		add( product, "layer-delete", LayerDeleteCommand.class );
		add( product, "layer-toggle", LayerToggleCommand.class );

		// Snap commands
		add( product, "snap-auto-grid", SnapAutoCommand.class, new SnapGrid() );
		add( product, "snap-auto-nearest", SnapAutoCommand.class, new SnapNearest() );
		add( product, "snap-grid-toggle", SnapGridToggle.class );

		add( product, "trim-single", ExtendTrimCommand.class );
		add( product, "trim-multi", MultiTrimCommand.class );

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

		printCommandMapByCommand();
		//printCommandMapByName();
	}

	private static void printCommandMapByCommand() {
		actionCommands.values().stream().sorted().forEach( k -> {
			System.out.println( k.getShortcut() + " -> " + k.getName() + " [" + k.getAction() + "]" );
		} );
	}

	public static boolean hasCommand( String shortcut ) {
		return commandActions.containsKey( shortcut );
	}

	public static CommandMetadata get( String shortcut ) {
		return getActionCommand( commandActions.getOrDefault( shortcut, TextUtil.EMPTY ) );
	}

	public static CommandMetadata get( InputEvent event ) {
		return getActionCommand( eventActions.getOrDefault( CommandEventKey.of( event ), TextUtil.EMPTY ) );
	}

	private static CommandMetadata getActionCommand( String action ) {
		if( TextUtil.isEmpty( action ) ) return null;
		CommandMetadata mapping = actionCommands.get( action );
		if( mapping == null ) log.log( Log.WARN, "No command for action: " + action );
		return mapping;
	}

	private static void add( CommandEventKey key, String action ) {
		eventActions.put( key, action );
	}

	private static void add( ProgramProduct product, String action, Class<? extends Command> type, Object... parameters ) {
		ActionLibrary library = product.getProgram().getActionLibrary();
		library.register( product.rb(), action );
		ActionProxy proxy = library.getAction( action );

		String name = proxy.getName();
		String command = proxy.getCommand().toLowerCase();

		if( !actionCommands.containsKey( action ) ) {
			commandActions.put( command, action );
			actionCommands.put( action, new CommandMetadata( action, name, command, type, parameters ) );
		} else {
			CommandMetadata existing = actionCommands.get( action );
			log.log( Log.ERROR, "Shortcut already used: shortcut={0} action={1} conflict={2} existing={3}", command, action, type.getName(), existing.getType().getName() );
		}
	}

}
