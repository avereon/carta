package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.DesignPane;
import com.avereon.cartesia.tool.DesignPaneLayer;

public class DesignLayerView extends DesignDrawableView {

	private final DesignPaneLayer layer;

	public DesignLayerView( DesignPane pane, DesignLayer layer ) {
		this( pane, layer, new DesignPaneLayer() );
	}

	// Special handing of the root layer pane
	public DesignLayerView( DesignPane pane, DesignLayer designLayer, DesignPaneLayer layer ) {
		super( pane, designLayer );
		this.layer = layer;
		DesignShapeView.setDesignData( layer, designLayer );
	}

	public  DesignLayer getDesignLayer() {
		return (DesignLayer)getDesignNode();
	}

	public DesignPaneLayer getLayer() {
		return layer;
	}

	public boolean isVisible() {
		return this.layer.isVisible();
	}

	public void setVisible( boolean visible ) {
		this.layer.setVisible( visible );
	}

	public void addLayerGeometry() {
		getPane().addLayerGeometry( this );
		registerListeners();
	}

	public void removeLayerGeometry() {
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

}
