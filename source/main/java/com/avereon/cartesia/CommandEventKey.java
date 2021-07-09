package com.avereon.cartesia;

import javafx.event.EventType;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.Objects;

public class CommandEventKey {

	private final EventType<?> type;

	private boolean isControl;

	private boolean isShift;

	private boolean isAlt;

	private boolean isMeta;

	private boolean isDirect;

	private boolean isInertia;

	private MouseButton mouseButton;

	private CommandEventKey( InputEvent event ) {
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

	public CommandEventKey( EventType<?> type ) {
		this( type, false, false, false, false );
	}

	public CommandEventKey( EventType<?> type, boolean isControl, boolean isShift, boolean isAlt, boolean isMeta ) {
		this( type, null, isControl, isShift, isAlt, isMeta );
	}

	public CommandEventKey( EventType<?> type, MouseButton button ) {
		this( type, button, false, false, false, false );
	}

	public CommandEventKey( EventType<?> type, MouseButton button, boolean isControl, boolean isShift, boolean isAlt, boolean isMeta ) {
		this.type = type;
		this.mouseButton = button;
		this.isControl = isControl;
		this.isShift = isShift;
		this.isAlt = isAlt;
		this.isMeta = isMeta;
	}

	public MouseButton getButton() {
		return mouseButton;
	}

	/**
	 * This matches all the attributes of this event key with the specified event
	 * key except for the event type which it matches with the specified type.
	 *
	 * @param eventKey The event key to match against
	 * @param type The specific event type to match
	 * @return True if this event key matches, false otherwise
	 */
	public boolean matches( CommandEventKey eventKey, EventType<?> type ) {
		return isControl == eventKey.isControl && isShift == eventKey.isShift && isAlt == eventKey.isAlt && isMeta == eventKey.isMeta && isDirect == eventKey.isDirect && isInertia == eventKey.isInertia && this.type
			.equals( type ) && mouseButton == eventKey.mouseButton;
	}

	@Override
	public boolean equals( Object other ) {
		if( this == other ) return true;
		if( other == null || getClass() != other.getClass() ) return false;
		CommandEventKey eventKey = (CommandEventKey)other;
		return isControl == eventKey.isControl && isShift == eventKey.isShift && isAlt == eventKey.isAlt && isMeta == eventKey.isMeta && isDirect == eventKey.isDirect && isInertia == eventKey.isInertia && type
			.equals( eventKey.type ) && mouseButton == eventKey.mouseButton;
	}

	@Override
	public int hashCode() {
		return Objects.hash( type, isControl, isShift, isAlt, isMeta, isDirect, isInertia, mouseButton );
	}

	public static CommandEventKey of( InputEvent event ) {
		return new CommandEventKey( event );
	}

}
