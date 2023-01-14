package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.DesignLayer;
import com.avereon.zarra.javafx.Fx;

public class DesignLayerView extends DesignDrawableView {

	private final DesignLayerPane layerPane;

	public DesignLayerView( DesignPane pane, DesignLayer layerPane ) {
		this( pane, layerPane, new DesignLayerPane() );
	}

	// Special handing of the root layer pane
	DesignLayerView( DesignPane pane, DesignLayer layer, DesignLayerPane layerPane ) {
		super( pane, layer );
		this.layerPane = layerPane;
		DesignShapeView.setDesignData( layerPane, layer );
		layerPane.setVisible( true );
	}

	public DesignLayer getDesignLayer() {
		return (DesignLayer)getDesignNode();
	}

	public DesignLayerPane getLayerPane() {
		return layerPane;
	}

	public boolean isEnabled() {
		return this.layerPane.isEnabled();
	}

	public void setEnabled( boolean enabled ) {
		this.layerPane.setEnabled( enabled );
	}

	public boolean isVisible() {
		return this.layerPane.isVisible();
	}

	public void setVisible( boolean visible ) {
		this.layerPane.setVisible( visible );
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
