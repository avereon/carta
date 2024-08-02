package com.avereon.cartesia;

import javafx.event.EventType;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The CommandTrigger class is used to define the user input combination to
 * trigger a specific command. The trigger can be a mouse event or a key event.
 * <p>
 * The trigger should be defined by the latest EventType that would trigger the
 * command. For example, if the command is triggered by a mouse event, the
 * trigger should be defined by the MouseEvent.MOUSE_CLICKED event type, instead
 * of the MouseEvent.MOUSE_PRESSED event type.
 */
@CustomLog
public class CommandTrigger {

	public enum Modifier {
		ANY,
		CONTROL,
		SHIFT,
		ALT,
		META,
		DIRECT,
		INERTIA,
		MOVED
	}

	private final EventType<?> type;

	// There can only be one mouse button
	private MouseButton mouseButton;

	// There can be many modifiers
	private final Set<Modifier> modifiers;

	public CommandTrigger( EventType<?> type, Modifier... modifiers ) {
		this( type, null, modifiers );
	}

	public CommandTrigger( EventType<?> type, MouseButton button, Modifier... modifiers ) {
		this.type = type;
		this.mouseButton = button;
		this.modifiers = new HashSet<>( Arrays.asList( modifiers ) );

		//		// If comparing against mouse drag events, always enable the MOVING modifier
		//		if( type == MouseEvent.MOUSE_DRAGGED ) {
		//			this.modifiers.add( Modifier.MOVING );
		//		}
	}

	public EventType<?> getEventType() {
		return type;
	}

	public MouseButton getButton() {
		return mouseButton;
	}

	public Set<Modifier> getModifiers() {
		return new HashSet<>( modifiers );
	}

	public boolean hasButton( MouseButton button ) {
		return mouseButton == button;
	}

	public boolean hasModifier( Modifier modifier ) {
		return modifiers != null && modifiers.contains( modifier );
	}

	@Override
	public String toString() {
		return "CommandTrigger{" + "type=" + type + ", mouseButton=" + mouseButton + ", modifiers=" + modifiers + '}';
	}

	@Override
	public boolean equals( Object object ) {
		if( this == object ) return true;
		if( object == null || getClass() != object.getClass() ) return false;
		CommandTrigger that = (CommandTrigger)object;
		return type.equals( that.type ) && mouseButton == that.mouseButton && modifiers.equals( that.modifiers );
	}

	@Override
	public int hashCode() {
		return Objects.hash( type, mouseButton, modifiers );
	}

	public boolean matches(InputEvent event ) {
		return this.equals( from( event ) );
	}

	public static CommandTrigger from( InputEvent event ) {
		// This method creates a command trigger from an input event for the purpose
		// of using the event trigger to look up a command in the command map.

		if( event == null ) return null;

		CommandTrigger trigger = new CommandTrigger( event.getEventType() );
		if( event instanceof MouseEvent mouseEvent ) {
			trigger.mouseButton = mouseEvent.getButton();

			if( mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED ) {
				// Special handling for MOUSE_PRESSED events
				trigger.modifiers.add( Modifier.ANY );
			} else {
				if( mouseEvent.isControlDown() ) trigger.modifiers.add( Modifier.CONTROL );
				if( mouseEvent.isShiftDown() ) trigger.modifiers.add( Modifier.SHIFT );
				if( mouseEvent.isAltDown() ) trigger.modifiers.add( Modifier.ALT );
				if( mouseEvent.isMetaDown() ) trigger.modifiers.add( Modifier.META );
				if( !mouseEvent.isStillSincePress() ) trigger.modifiers.add( Modifier.MOVED );
			}
		} else if( event instanceof GestureEvent gestureEvent ) {
			if( gestureEvent.isControlDown() ) trigger.modifiers.add( Modifier.CONTROL );
			if( gestureEvent.isShiftDown() ) trigger.modifiers.add( Modifier.SHIFT );
			if( gestureEvent.isAltDown() ) trigger.modifiers.add( Modifier.ALT );
			if( gestureEvent.isMetaDown() ) trigger.modifiers.add( Modifier.META );
			if( gestureEvent.isDirect() ) trigger.modifiers.add( Modifier.DIRECT );
			if( gestureEvent.isInertia() ) trigger.modifiers.add( Modifier.INERTIA );
		} else {
			log.atWarn().log( "Unhandled event type" );
			return null;
		}

		return trigger;
	}

}
