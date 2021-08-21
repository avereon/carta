package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.DesignLayer;
import com.avereon.zerra.javafx.Fx;

public class DesignLayerView extends DesignDrawableView {

	private final DesignLayerPane layer;

	public DesignLayerView( DesignPane pane, DesignLayer layer ) {
		this( pane, layer, new DesignLayerPane() );
	}

	// Special handing of the root layer pane
	DesignLayerView( DesignPane pane, DesignLayer layer, DesignLayerPane layerPane ) {
		super( pane, layer );
		this.layer = layerPane;
		layerPane.setVisible( true );
		DesignShapeView.setDesignData( layerPane, layer );

		// Update the design layer when the layer pane is updated
		layerPane.visibleProperty().addListener( (p,o,n) -> layer.setVisible( n ) );
	}

	public  DesignLayer getDesignLayer() {
		return (DesignLayer)getDesignNode();
	}

	public DesignLayerPane getLayer() {
		return layer;
	}

	public boolean isVisible() {
		return this.layer.isVisible();
	}

	public void setVisible( boolean visible ) {
		this.layer.setVisible( visible );
	}

	public void addLayerGeometry() {
		Fx.run( () -> getPane().addLayerGeometry( this ) );
		registerListeners();
	}

	public void removeLayerGeometry() {
		unregisterListeners();
		Fx.run( () -> getPane().removeLayerGeometry( this ) );
	}

	@Override
	void registerListeners() {
		super.registerListeners();
	}

	@Override
	void unregisterListeners() {
		super.unregisterListeners();
	}

}
