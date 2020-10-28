package com.avereon.cartesia;

import com.avereon.cartesia.command.*;
import com.avereon.util.Log;
import com.avereon.util.TextUtil;
import com.avereon.xenon.Action;
import com.avereon.xenon.ProgramProduct;
import javafx.event.EventType;
import javafx.scene.input.*;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CommandMap {

	private static final System.Logger log = Log.get();

	private static final Map<String, CommandMapping> actionCommands = new ConcurrentHashMap<>();

	private static final Map<String, String> shortcutActions = new ConcurrentHashMap<>();

	private static final Map<EventKey, String> eventActions = new ConcurrentHashMap<>();

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

		// Event type actions
		add( new EventKey( MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY ), "pen-down" );
		add( new EventKey( ScrollEvent.SCROLL ), "camera-zoom-gesture" );
		add( new EventKey( ZoomEvent.ZOOM ), "camera-zoom-gesture" );

		//mouseActionKeys.put( MouseEvent.MOUSE_PRESSED + MouseEvent.BUTTON1_DOWN_MASK, "select" );
		//mouseActionKeys.put( MouseEvent.MOUSE_PRESSED + MouseEvent.BUTTON3_DOWN_MASK, "snap-auto-nearest" );
		//mouseActionKeys.put( MouseEvent.MOUSE_PRESSED + MouseEvent.BUTTON1_DOWN_MASK + MouseEvent.CTRL_DOWN_MASK, "camera-spin" );
		//mouseActionKeys.put( MouseEvent.MOUSE_PRESSED + MouseEvent.BUTTON1_DOWN_MASK + MouseEvent.SHIFT_DOWN_MASK, "camera-move" );
		//mouseActionKeys.put( MouseEvent.MOUSE_WHEEL, "camera-zoom-wheel" );
		//mouseActionKeys.put( MouseEvent.MOUSE_WHEEL + MouseEvent.CTRL_DOWN_MASK, "camera-walk-wheel" );

		// Basic command
		add( product, "pen-down", PenDownCommand.class );

		// View commands
		add( product, "camera-view-pan", PanCommand.class );
		add( product, "camera-view-point", ViewPointCommand.class );
		add( product, "camera-zoom", CameraZoomCommand.class );
		add( product, "camera-zoom-in", CameraZoomInCommand.class );
		add( product, "camera-zoom-out", CameraZoomOutCommand.class );
		//add( product, "camera-zoom-window", ZoomWindowCommand.class );
		add( product, "camera-zoom-gesture", CameraZoomGestureCommand.class );

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

	public static <T extends Command> Class<T> get( String shortcut ) {
		return getActionCommand( shortcutActions.getOrDefault( shortcut, TextUtil.EMPTY ) );
	}

	public static <T extends Command> Class<T> get( InputEvent event ) {
		return getActionCommand( eventActions.getOrDefault( new EventKey( event ), TextUtil.EMPTY ) );
	}

	@SuppressWarnings( "unchecked" )
	private static <T extends Command> Class<T> getActionCommand( String action ) {
		if( TextUtil.isEmpty( action ) ) return null;
		CommandMapping mapping = actionCommands.get( action );
		if( mapping == null ) log.log( Log.WARN, "No command for action: " + action );
		return mapping == null ? null : (Class<T>)mapping.getCommand();
	}

	private static void add( EventKey key, String action ) {
		eventActions.put( key, action );
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

	private static class EventKey {

		private final EventType<?> type;

		private boolean isControl;

		private boolean isShift;

		private boolean isAlt;

		private boolean isMeta;

		private boolean isDirect;

		private boolean isInertia;

		private MouseButton mouseButton;

		public EventKey( InputEvent event ) {
			this.type = event.getEventType();
			if( event instanceof MouseEvent ) {
				MouseEvent mouse = (MouseEvent)event;
				this.isControl = mouse.isControlDown();
				this.isShift = mouse.isShiftDown();
				this.isAlt = mouse.isAltDown();
				this.isMeta = mouse.isMetaDown();
				this.mouseButton = mouse.getButton();
			}
			if( event instanceof GestureEvent ) {
				GestureEvent gestureEvent = (GestureEvent)event;
				this.isControl = gestureEvent.isControlDown();
				this.isShift = gestureEvent.isShiftDown();
				this.isAlt = gestureEvent.isAltDown();
				this.isMeta = gestureEvent.isMetaDown();
				this.isDirect = gestureEvent.isDirect();
				this.isInertia = gestureEvent.isInertia();
			}
		}

		public EventKey( EventType<?> type ) {
			this.type = type;
		}

		public EventKey( EventType<?> type, MouseButton button ) {
			this.type = type;
			this.mouseButton = button;
		}

		@Override
		public boolean equals( Object other ) {
			if( this == other ) return true;
			if( other == null || getClass() != other.getClass() ) return false;
			EventKey eventKey = (EventKey)other;
			return isControl == eventKey.isControl && isShift == eventKey.isShift && isAlt == eventKey.isAlt && isMeta == eventKey.isMeta && isDirect == eventKey.isDirect && isInertia == eventKey.isInertia && type
				.equals( eventKey.type ) && mouseButton == eventKey.mouseButton;
		}

		@Override
		public int hashCode() {
			return Objects.hash( type, isControl, isShift, isAlt, isMeta, isDirect, isInertia, mouseButton );
		}

	}

}
