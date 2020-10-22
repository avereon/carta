package com.avereon.cartesia;

import com.avereon.cartesia.command.CameraZoomCommand;
import com.avereon.cartesia.command.CameraZoomInCommand;
import com.avereon.cartesia.command.CameraZoomOutCommand;
import com.avereon.cartesia.command.MeasureDistanceCommand;
import com.avereon.util.Log;
import com.avereon.util.TextUtil;
import com.avereon.xenon.Action;
import com.avereon.xenon.ProgramProduct;
import javafx.event.EventType;
import javafx.scene.input.InputEvent;
import javafx.scene.input.ScrollEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CommandMap {

	public static final EventType<ScrollEvent> SCROLL_WHEEL_UP = new EventType<>( ScrollEvent.SCROLL, "SCROLL_WHEEL_UP" );

	public static final EventType<ScrollEvent> SCROLL_WHEEL_DOWN = new EventType<>( ScrollEvent.SCROLL, "SCROLL_WHEEL_DOWN" );

	private static final System.Logger log = Log.get();

	private static final Map<String, Class<? extends Command>> commands = new ConcurrentHashMap<>();

	private static final Map<String, String> shortcutActions = new ConcurrentHashMap<>();

	private static final Map<String, CommandMapping> actionCommands = new ConcurrentHashMap<>();

	private static final Map<EventType<? extends InputEvent>, String> inputTypeActions = new ConcurrentHashMap<>();

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

		// Input type actions
		add( SCROLL_WHEEL_UP, "camera-zoom-in" );
		add( SCROLL_WHEEL_DOWN, "camera-zoom-out" );

		//mouseActionKeys.put( MouseEvent.MOUSE_PRESSED + MouseEvent.BUTTON1_DOWN_MASK, "select" );
		//mouseActionKeys.put( MouseEvent.MOUSE_PRESSED + MouseEvent.BUTTON3_DOWN_MASK, "snap-auto-nearest" );
		//mouseActionKeys.put( MouseEvent.MOUSE_PRESSED + MouseEvent.BUTTON1_DOWN_MASK + MouseEvent.CTRL_DOWN_MASK, "camera-spin" );
		//mouseActionKeys.put( MouseEvent.MOUSE_PRESSED + MouseEvent.BUTTON1_DOWN_MASK + MouseEvent.SHIFT_DOWN_MASK, "camera-move" );
		//mouseActionKeys.put( MouseEvent.MOUSE_WHEEL, "camera-zoom-wheel" );
		//mouseActionKeys.put( MouseEvent.MOUSE_WHEEL + MouseEvent.CTRL_DOWN_MASK, "camera-walk-wheel" );

		// View commands
		//		add( product, "camera-view-pan", PanCommand.class );
		//		add( product, "camera-view-point", ViewPointCommand.class );
		add( product, "camera-zoom", CameraZoomCommand.class );
		add( product, "camera-zoom-in", CameraZoomInCommand.class );
		add( product, "camera-zoom-out", CameraZoomOutCommand.class );
		//add( product, "camera-zoom-window", ZoomWindowCommand.class );

		// Measure commands
		//add( product, "measure-angle", MeasureAngleCommand.class );
		add( product, "measure-distance", MeasureDistanceCommand.class );

		// Shape commands
		//		add( product, "draw-arc-2", ArcCommand.class ); // center-endpoint-endpoint
		//add( "draw-arc-3", Arc2Command.class ); // endpoint-midpoint-endpoint
		//add( "draw-circle-2", CircleCommand.class ); // center-radius
		//add( "draw-circle-3", Circle3Command.class ); // point-point-point
		//add( "draw-ellipse-3", EllipseCommand.class ); // center-radius-radius
		//add( "draw-ellipse-5", Ellipse5Command.class ); // point-point-point-point-point
		//add( "draw-ellipse-arc-5", EllipseArc5Command.class ); // center-radius-radius-endpoint-endpoint
		//		add( product, "draw-line-2", LineCommand.class ); // endpoint-endpoint
		//add( product, "draw-line-perpendicular", LineCommand.class ); // shape-endpoint-endpoint
		//		add( product, "draw-point", PointCommand.class ); // point
		//		add( product, "draw-curve-4", CurveCommand.class ); // endpoint-midpoint-midpoint-endpoint
		//		add( product, "draw-path", PathCommand.class );

		// gg - grid toggle
		// sn - snap nearest
		// sg - toggle snap to grid

		// Layer commands
		//		add( product, "layer-create", LayerCreateCommand.class );
		//		add( product, "layer-show", LayerShowCommand.class );
		//		add( product, "layer-hide", LayerHideCommand.class );
		//		add( product, "layer-sublayer", LayerSubLayerCommand.class );
		//		add( product, "layer-delete", LayerDeleteCommand.class );
		//		add( product, "layer-toggle", LayerToggleCommand.class );
	}

	public static Set<CommandMapping> getMappings() {
		return new HashSet<>( actionCommands.values() );
	}

	public static boolean hasCommand( String shortcut ) {
		return shortcutActions.containsKey( shortcut );
	}

	@SuppressWarnings( "unchecked" )
	public static <T extends Command> Class<T> get( String shortcut ) {
		CommandMapping mapping = actionCommands.get( shortcutActions.getOrDefault( shortcut, TextUtil.EMPTY ) );
		return mapping == null ? null : (Class<T>)mapping.getCommand();
	}

	@SuppressWarnings( "unchecked" )
	public static <T extends Command> Class<T> get( EventType<? extends InputEvent> type ) {
		CommandMapping mapping = actionCommands.get( inputTypeActions.getOrDefault( type, TextUtil.EMPTY ) );
		return mapping == null ? null : (Class<T>)mapping.getCommand();
	}

	private static void add( EventType<? extends InputEvent> type, String action ) {
		inputTypeActions.put( type, action );
	}

	private static void add( ProgramProduct product, String action, Class<? extends Command> command, Object... parameters ) {
		String shortcut = product.rb().textOr( BundleKey.ACTION, action + Action.SHORTCUT_SUFFIX, "" ).toLowerCase();

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
