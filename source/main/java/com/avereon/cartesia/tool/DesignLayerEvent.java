package com.avereon.cartesia.tool;

import com.avereon.cartesia.tool.view.DesignPane;
import javafx.event.Event;
import javafx.event.EventType;

public class DesignLayerEvent extends javafx.event.Event  {

	public static final EventType<DesignLayerEvent> LAYER_ADDED = new EventType<>("LAYER_ADDED");

	public static final EventType<DesignLayerEvent> LAYER_REMOVED = new EventType<>("LAYER_REMOVED");

	private final DesignPaneLayer layer;

	public DesignLayerEvent( DesignPane source, EventType<? extends Event> eventType, DesignPaneLayer layer ) {
		super( source, null, eventType );
		this.layer = layer;
	}

	public DesignPane getDesignPane() {
		return (DesignPane)getSource();
	}

	public DesignPaneLayer getLayer() {
		return layer;
	}

}
