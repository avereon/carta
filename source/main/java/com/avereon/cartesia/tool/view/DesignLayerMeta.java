package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.DesignLayer;

@Deprecated
public class DesignLayerMeta extends DesignDrawableMeta {

	//private final DesignLayerPane layerPane;

	private boolean enabled;

	private boolean visible;

	public DesignLayerMeta( DesignPane pane, DesignLayer layerPane ) {
		this( pane, layerPane, new DesignLayerPane() );
	}

	// Special handing of the root layer pane
	DesignLayerMeta( DesignPane pane, DesignLayer layer, DesignLayerPane layerPane ) {
		super( pane, layer );
		//this.layerPane = layerPane;
		DesignShapeView.setDesignData( layerPane, layer );
		layerPane.setVisible( true );
	}

	public  DesignLayer getDesignLayer() {
		return (DesignLayer)getDesignNode();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled( boolean enabled ) {
		this.enabled = enabled;
		// TODO Notify listeners
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible( boolean visible ) {
		this.visible = visible;
		// TODO Notify listeners
	}

	void addLayerGeometry() {
		//Fx.run( () -> getPane().addLayerGeometry( this ) );
		registerListeners();
	}

	void removeLayerGeometry() {
		unregisterListeners();
		//Fx.run( () -> getPane().removeLayerGeometry( this ) );
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
