package com.avereon.cartesia;

import com.avereon.cartesia.command.*;
import com.avereon.cartesia.snap.SnapCenter;
import com.avereon.cartesia.snap.SnapIntersection;
import com.avereon.cartesia.snap.SnapMidpoint;
import com.avereon.cartesia.snap.SnapNearestCp;
import com.avereon.log.LazyEval;
import com.avereon.util.TextUtil;
import com.avereon.xenon.ActionLibrary;
import com.avereon.xenon.ActionProxy;
import com.avereon.xenon.ProgramProduct;
import javafx.scene.input.*;
import lombok.CustomLog;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CustomLog
public class CommandMap {

	public static final CommandMetadata NONE = new CommandMetadata( "", "", "", "", new String[]{}, Noop.class );

	private static final Map<String, String> commandActions = new ConcurrentHashMap<>();

	private static final Map<String, CommandMetadata> actionCommands = new ConcurrentHashMap<>();

	private static final Map<CommandEventKey, String> eventActions = new ConcurrentHashMap<>();

	public static void load( ProgramProduct product ) {
		// High level letters
		// a - arc
		// c - circle
		// d - draw [verb] (size, paint, pattern, etc.)
		// e - ellipse
		// f - fill [verb] (paint, pattern, pattern offset, etc.)
		// g - grid
		// k - color/paint [verb]
		// l - line
		// m - marker
		// p - print
		// r - reference
		// s - snap
		// t - text
		// v - curve
		// w - view
		// y - layer
		// z - zoom

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

		// Basic commands
		add( product, "select", Select.class );

		// View commands
		add( product, "camera-move", CameraMove.class );
		//add( product, "camera-spin", CameraSpinCommand.class );
		add( product, "camera-view-point", CameraViewPoint.class );
		//add( product, "camera-view-previous", CameraViewPrevious.class );
		add( product, "camera-view-rotate-left", CameraViewRotateLeft.class );
		add( product, "camera-view-rotate-right", CameraViewRotateRight.class );
		//add( product, "camera-walk", CameraWalkCommand.class );
		add( product, "camera-zoom", CameraZoom.class );
		add( product, "camera-zoom-all", CameraZoomAll.class );
		add( product, "camera-zoom-in", CameraZoomIn.class );
		add( product, "camera-zoom-out", CameraZoomOut.class );
		add( product, "camera-zoom-selected", CameraZoomSelected.class );
		add( product, "camera-zoom-window", CameraZoomWindow.class );

		// Toggle commands
		add( product, "grid-toggle", GridToggle.class );
		add( product, "snap-grid-toggle", SnapGridToggle.class );

		// Draw commands
		add( product, "draw-arc-2", DrawArc2.class ); // center-radius/start-extent
		add( product, "draw-arc-3", DrawArc3.class ); // endpoint-midpoint-endpoint
		add( product, "draw-circle-2", DrawCircle2.class ); // center-radius
		add( product, "draw-circle-3", DrawCircle3.class ); // point-point-point
		add( product, "draw-curve-4", DrawCurve4.class ); // endpoint-midpoint-midpoint-endpoint
		add( product, "draw-ellipse-3", DrawEllipse3.class ); // center-radius-radius
		//add( product, "draw-conic-5", DrawConic5.class ); // point-point-point-point-point
		add( product, "draw-ellipse-arc-5", DrawEllipseArc5.class ); // center-radius-radius-start-extent
		//add( product, "draw-conic-arc-5", DrawConicArc5.class ); // endpoint-point-point-point-endpoint
		add( product, "draw-line-2", DrawLine2.class ); // endpoint-endpoint
		add( product, "draw-line-perpendicular", DrawLinePerpendicular.class ); // shape-endpoint-endpoint
		add( product, "draw-marker", DrawMarker.class ); // point
		add( product, "draw-path", Path.class );

		// Measure commands
		add( product, "measure-angle", MeasureAngle.class );
		add( product, "measure-distance", MeasureDistance.class );
		add( product, "measure-length", MeasureLength.class );
		add( product, "measure-point", MeasurePoint.class );
		add( product, "shape-information", ShapeInformation.class );

		// Modify commands
		add( product, "copy", Copy.class );
		add( product, "delete", Delete.class );
		add( product, "extend", Trim.class );
		add( product, "flip", Flip.class );
		add( product, "join", Join.class );
		add( product, "mirror", Mirror.class );
		add( product, "move", Move.class );
		add( product, "radial-copy", RadialCopy.class );
		//add( product, "multi-copy", Copy.class );
		//add( product, "multi-radial-copy", RadialCopy.class );
		//add( product, "multi-trim", TrimExtendMultiple.class );
		add( product, "redo", Redo.class );
		add( product, "rotate", Rotate.class );
		add( product, "scale", Scale.class );
		add( product, "split", Split.class );
		add( product, "stretch", Stretch.class );
		add( product, "trim", Trim.class );
		add( product, "undo", Undo.class );

		// Layer commands
		add( product, "layer-create", LayerCreate.class );
		add( product, "layer-show", LayerShow.class );
		add( product, "layer-hide", LayerHide.class );
		add( product, "layer-sublayer", LayerSubLayer.class );
		add( product, "layer-delete", LayerDelete.class );
		add( product, "layer-toggle", LayerToggle.class );

		// View Commands
		add( product, "view-create", ViewCreate.class );
		add( product, "view-delete", ViewDelete.class );
		add( product, "view-update", ViewUpdate.class );

		// Reference Commands
		add( product, "reference-toggle", ReferencePointsToggle.class );

		// Snap commands
		add( product, "snap-center", SnapSelect.class, new SnapCenter() );
		add( product, "snap-midpoint", SnapSelect.class, new SnapMidpoint() );
		add( product, "snap-nearest", SnapSelect.class, new SnapNearestCp() );
		add( product, "snap-intersection", SnapSelect.class, new SnapIntersection() );
		//add( product, "snap-grid", SnapSelectCommand.class, new SnapGrid() ); // No one really does this

		// Snap auto commands
		add( product, "snap-auto-center", SnapAuto.class, new SnapCenter() );
		add( product, "snap-auto-midpoint", SnapAuto.class, new SnapMidpoint() );
		add( product, "snap-auto-nearest", SnapAuto.class, new SnapNearestCp() );
		add( product, "snap-auto-intersection", SnapAuto.class, new SnapIntersection() );
		//add( product, "snap-auto-grid", SnapAutoCommand.class, new SnapGrid() ); // No one really does this

		// Single select
		add( new CommandEventKey( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY ), "select" );
		// Add/remove select
		add( new CommandEventKey( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, true, false, false, false ), "select" );
		// Snap nearest
		add( new CommandEventKey( MouseEvent.MOUSE_PRESSED, MouseButton.SECONDARY ), "snap-auto-nearest" );
		// Camera move
		add( new CommandEventKey( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, false, true, false, false ), "camera-move" );
		// Camera spin
		add( new CommandEventKey( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, true, true, false, false ), "camera-spin" );
		// Camera zoom
		add( new CommandEventKey( ScrollEvent.SCROLL ), "camera-zoom" );
		add( new CommandEventKey( ZoomEvent.ZOOM ), "camera-zoom" );
		// Camera walk
		add( new CommandEventKey( ScrollEvent.SCROLL, true, false, false, false ), "camera-walk" );
		add( new CommandEventKey( ZoomEvent.ZOOM, true, false, false, false ), "camera-walk" );

		//printCommandMapByCommand();
		//printCommandMapByName();
	}

	private static void printCommandMapByCommand() {
		actionCommands.values().stream().sorted().forEach( k -> {
			if( TextUtil.isEmpty( k.getCommand() ) ) return;
			StringBuilder builder = new StringBuilder();
			builder.append( k.getCommand() == null ? "  " : k.getCommand() ).append( " -> " ).append( k.getName() );
			builder.append( " [" ).append( k.getAction() ).append( "]" );
			if( k.getShortcut() != null ) builder.append( " <" ).append( k.getShortcut() ).append( ">" );
			System.out.println( builder );
		} );

		// Event actions???
	}

	public static Map<String,CommandMetadata> getAll() {
		return Collections.unmodifiableMap(actionCommands);
	}

	public static boolean hasCommand( String shortcut ) {
		return get( shortcut ) != NONE;
	}

	public static CommandMetadata get( String shortcut ) {
		return getActionCommand( commandActions.getOrDefault( shortcut.toLowerCase(), TextUtil.EMPTY ) );
	}

	public static CommandMetadata get( InputEvent event ) {
		return getActionCommand( eventActions.getOrDefault( CommandEventKey.of( event ), TextUtil.EMPTY ) );
	}

	public static CommandMetadata getActionCommand( String action ) {
		if( TextUtil.isEmpty( action ) ) return NONE;
		CommandMetadata mapping = actionCommands.get( action );
		if( mapping == null ) {
			log.atWarning().log( "No command for action: %s", action );
			mapping = NONE;
		}
		return mapping;
	}

	private static void add( CommandEventKey key, String action ) {
		eventActions.put( key, action );
	}

	private static void add( ProgramProduct product, String action, Class<? extends Command> type, Object... parameters ) {
		ActionLibrary library = product.getProgram().getActionLibrary();
		library.register( product, action );
		ActionProxy proxy = library.getAction( action );

		String name = proxy.getName();
		String command = null;
		if( proxy.getCommand() != null ) command = proxy.getCommand().toLowerCase();
		String shortcut = proxy.getShortcut();
		String[] tags = proxy.getTags();

		add( action, type, name, command, shortcut, tags, parameters );
	}

	public static void add( String action, Class<? extends Command> type, String name, String command, String shortcut, Object... parameters ) {
		add( action, type, name, command, shortcut, null, parameters );
	}

	private static void add( String action, Class<? extends Command> type, String name, String command, String shortcut, String[] tags, Object... parameters ) {
		if( command != null && commandActions.containsKey( command ) ) {
			CommandMetadata existing = actionCommands.get( commandActions.get( command ) );
			log.atSevere().log( "Shortcut already used [%s]: %s %s", command, LazyEval.of( existing::getAction ), action );
			return;
		}

		if( !actionCommands.containsKey( action ) ) {
			if( command != null ) commandActions.put( command, action );
			actionCommands.put( action, new CommandMetadata( action, name, command, shortcut, tags, type, parameters ) );
		}
	}

	public static final class Noop extends Command {}

}
