package com.avereon.cartesia.tool.design;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class DesignToolEvent extends Event {

	public static final EventType<DesignToolEvent> DESIGN_TOOL = new EventType<>( Event.ANY, "DESIGN_TOOL" );

	public static final EventType<DesignToolEvent> ANY = DESIGN_TOOL;

	public static final EventType<DesignToolEvent> DESIGN_READY = new EventType<>( DesignToolEvent.DESIGN_TOOL, "DESIGN_READY" );

	public DesignToolEvent( EventType<? extends Event> eventType ) {
		this( null, null, eventType );
	}

	public DesignToolEvent( Object source, EventType<? extends Event> eventType ) {
		this( source, null, eventType );
	}

	public DesignToolEvent( Object source, EventTarget target, EventType<? extends Event> eventType ) {
		super( source, target, eventType );
	}

}
