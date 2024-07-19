package com.avereon.cartesia;

import javafx.event.EventType;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.Objects;

/**
 * The CommandTrigger class is used to define the user input combination to
 * trigger a specific command. The trigger can be a mouse event or a key event.
 * <p>
 * The trigger should be defined by the latest EventType that would trigger the
 * command. For example, if the command is triggered by a mouse event, the
 * trigger should be defined by the MouseEvent.MOUSE_CLICKED event type, instead
 * of the MouseEvent.MOUSE_PRESSED event type.
 */
public class CommandTrigger {

	private final EventType<?> type;

	private boolean isControl;

	private boolean isShift;

	private boolean isAlt;

	private boolean isMeta;

	private boolean isDirect;

	private boolean isInertia;

	private MouseButton mouseButton;

	private CommandTrigger( InputEvent event ) {
		this.type = event.getEventType();
		if( event instanceof MouseEvent mouse ) {
			this.isControl = mouse.isControlDown();
			this.isShift = mouse.isShiftDown();
			this.isAlt = mouse.isAltDown();
			this.isMeta = mouse.isMetaDown();
			this.mouseButton = mouse.getButton();
		}
		if( event instanceof GestureEvent gestureEvent ) {
			this.isControl = gestureEvent.isControlDown();
			this.isShift = gestureEvent.isShiftDown();
			this.isAlt = gestureEvent.isAltDown();
			this.isMeta = gestureEvent.isMetaDown();
			this.isDirect = gestureEvent.isDirect();
			this.isInertia = gestureEvent.isInertia();
		}
	}

	public CommandTrigger( EventType<?> type ) {
		this( type, false, false, false, false );
	}

	public CommandTrigger( EventType<?> type, boolean isControl, boolean isShift, boolean isAlt, boolean isMeta ) {
		this( type, null, isControl, isShift, isAlt, isMeta );
	}

	public CommandTrigger( EventType<?> type, MouseButton button ) {
		this( type, button, false, false, false, false );
	}

	public CommandTrigger( EventType<?> type, MouseButton button, boolean isControl, boolean isShift, boolean isAlt, boolean isMeta ) {
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
	public boolean matches( CommandTrigger eventKey, EventType<?> type ) {
		return isControl == eventKey.isControl && isShift == eventKey.isShift && isAlt == eventKey.isAlt && isMeta == eventKey.isMeta && isDirect == eventKey.isDirect && isInertia == eventKey.isInertia && this.type
			.equals( type ) && mouseButton == eventKey.mouseButton;
	}

	@Override
	public boolean equals( Object other ) {
		if( this == other ) return true;
		if( other == null || getClass() != other.getClass() ) return false;
		CommandTrigger eventKey = (CommandTrigger)other;
		return isControl == eventKey.isControl && isShift == eventKey.isShift && isAlt == eventKey.isAlt && isMeta == eventKey.isMeta && isDirect == eventKey.isDirect && isInertia == eventKey.isInertia && type
			.equals( eventKey.type ) && mouseButton == eventKey.mouseButton;
	}

	@Override
	public int hashCode() {
		return Objects.hash( type, isControl, isShift, isAlt, isMeta, isDirect, isInertia, mouseButton );
	}

	public static CommandTrigger of( InputEvent event ) {
		return new CommandTrigger( event );
	}

}
