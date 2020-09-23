package com.avereon.cartesia.tool;

import javafx.event.Event;
import javafx.event.EventType;

public class DesignLayerEvent extends javafx.event.Event  {

	public static final EventType<DesignLayerEvent> LAYER_ADDED = new EventType<>("LAYER_ADDED");

	public static final EventType<DesignLayerEvent> LAYER_REMOVED = new EventType<>("LAYER_REMOVED");

	private final DesignPane.Layer layer;

	public DesignLayerEvent( DesignPane source, EventType<? extends Event> eventType, DesignPane.Layer layer ) {
		super( source, null, eventType );
		this.layer = layer;
	}

	public DesignPane getDesignPane() {
		return (DesignPane)getSource();
	}

	public DesignPane.Layer getLayer() {
		return layer;
	}

}
