package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.Collection;

public class DesignToolV3Renderer extends Pane implements DesignRenderer {

	// NEXT Apply lessons learned to create a new design renderer

	@Override
	public void setDesign( Design design ) {

	}

	@Override
	public void setVisibleLayers( Collection<DesignLayer> layers ) {

	}

	@Override
	public void setDpi( double dpi ) {

	}

//	@Override
//	public void setPrefWidth( double width ) {
//		super.setPrefWidth( width );
//	}
//
//	@Override
//	public void setPrefHeight( double height ) {
//
//	}

	@Override
	public void setViewCenter( Point3D center ) {

	}

	@Override
	public void setViewRotate( double rotate ) {

	}

	@Override
	public void setViewZoom( double zoom ) {

	}

	@Override
	public void render() {

	}

	@Override
	public void print( double factor ) {

	}

	@Override
	public Node getNode() {
		return this;
	}

}
