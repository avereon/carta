package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignLayer;

public class DesignLayerView extends DesignDrawableView {

	private DesignPane.Layer layer;

	public DesignLayerView( DesignPane pane, DesignLayer layer ) {
		super( pane, layer );
		generate();
	}

	// Special handing of the root layer pane
	DesignLayerView( DesignPane pane, DesignLayer designLayer, DesignPane.Layer layer ) {
		super( pane, designLayer );
		this.layer = layer;
	}

	protected DesignLayer getDesignLayer() {
		return (DesignLayer)getDesignNode();
	}

	protected DesignPane.Layer getLayer() {
		return layer;
	}

	boolean isVisible() {
		return this.layer.isVisible();
	}

	void setVisible( boolean visible ) {
		this.layer.setVisible( visible );
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
	void registerListeners() {
		super.registerListeners();
		//getDesignLayer().register( DesignLayer.VISIBLE, layerVisibleHandler = e -> Fx.run( () -> getLayer().setVisible( e.getNewValue() ) ) );
	}

	@Override
	void unregisterListeners() {
		//getDesignLayer().unregister( DesignLayer.VISIBLE, layerVisibleHandler );
		super.unregisterListeners();
	}

	private void generate() {
		layer = new DesignPane.Layer();
		layer.getProperties().put( DesignShapeView.DESIGN_DATA, getDesignLayer() );
	}

}
