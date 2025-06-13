package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import javafx.geometry.Point3D;
import javafx.scene.Node;

import java.util.Collection;

public interface DesignRenderer {

	void setDesign( Design design );

	void setVisibleLayers( Collection<DesignLayer> layers );

	void setDpi( double dpi );

	void setPrefWidth( double width );

	void setPrefHeight( double height );

	void setViewCenter( Point3D center );

	void setViewRotate( double rotate );

	void setViewZoom( double zoom );

	/**
	 * Called to request the design be rendered.
	 */
	void render();

	/**
	 * Called to request the design be printed.
	 *
	 * @param factor The scale factor to apply to the design when printing.
	 */
	void print( double factor );

	Node getNode();
}
