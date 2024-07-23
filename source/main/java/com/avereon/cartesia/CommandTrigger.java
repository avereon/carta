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

	public enum Modifier {
		CONTROL, SHIFT, ALT, META, DIRECT, INERTIA, MOVING
	}

	private final EventType<?> type;

	private boolean isControl;

	private boolean isShift;

	private boolean isAlt;

	private boolean isMeta;

	private boolean isDirect;

	private boolean isInertia;

	private MouseButton mouseButton;

	// TODO Can this constructor be deprecated?
	private CommandTrigger( InputEvent event ) {
		this.type = event.getEventType();
		if( event instanceof MouseEvent mouse ) {
			this.mouseButton = mouse.getButton();
			this.isControl = mouse.isControlDown();
			this.isShift = mouse.isShiftDown();
			this.isAlt = mouse.isAltDown();
			this.isMeta = mouse.isMetaDown();

			// TODO Should not-drag automatically set the moving flag to false?
			// TODO Should mouse drag automatically set the moving flag to true?
			//this.isMoving = !mouse.isStillSincePress();
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

	// TODO Can this constructor be improved by using mouse and gesture flags?
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
	 * This matches all the attributes of the specified event with this trigger
	 * except for the event type which it matches with the specified type.
	 *
	 * @param event The event to match with this trigger
	 * @param type The specific event type to match
	 * @return true if this trigger matches, false otherwise
	 */
	public boolean matchesWithType( InputEvent event, EventType<?> type ) {
		boolean typeMatches = event.getEventType().equals( type );
		if( event instanceof MouseEvent mouseEvent) {
			boolean buttonMatches = mouseEvent.getButton() == mouseButton;
			boolean controlMatches = mouseEvent.isControlDown() == isControl;
			boolean shiftMatches = mouseEvent.isShiftDown() == isShift;
			boolean altMatches = mouseEvent.isAltDown() == isAlt;
			boolean metaMatches = mouseEvent.isMetaDown() == isMeta;
			return typeMatches && buttonMatches && controlMatches && shiftMatches && altMatches && metaMatches;
		}
		return false;
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
