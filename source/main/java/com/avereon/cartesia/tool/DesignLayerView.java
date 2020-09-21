package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignLayer;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;

public class DesignLayerView extends DesignDrawableView {

	private DesignPane.Layer layer;

	private EventHandler<NodeEvent> layerVisibleHandler;

	public DesignLayerView( DesignPane pane, DesignLayer layer ) {
		super( pane, layer );
		generate();
	}

	public DesignLayerView( DesignPane pane, DesignLayer designLayer, DesignPane.Layer layer ) {
		super( pane, designLayer );
		this.layer = layer;
	}

	protected DesignLayer getDesignLayer() {
		return (DesignLayer)getDesignNode();
	}

	protected DesignPane.Layer getLayer() {
		return layer;
	}

	void addLayerGeometry() {
		getPane().addLayerGeometry( this );
		registerListeners();
	}

	void removeLayerGeometry() {
		unregisterListeners();
		getPane().removeLayerGeometry( this );
	}

	@Override
	public void registerListeners() {
		super.registerListeners();
		getDesignLayer().register( DesignLayer.VISIBLE, layerVisibleHandler = e -> Fx.run( () -> getLayer().setVisible( e.getNewValue() ) ) );
	}

	@Override
	public void unregisterListeners() {
		getDesignLayer().unregister( DesignLayer.VISIBLE, layerVisibleHandler );
		super.unregisterListeners();
	}

	private void generate() {
		layer = new DesignPane.Layer();
	}
}
