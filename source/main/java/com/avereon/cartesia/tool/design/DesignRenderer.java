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

	void setViewpoint( Point3D center );

	void setRotate( double rotate );

	default void setZoom( double zoom ) {
		setZoom( zoom, zoom );
	}

	void setZoom( double zoomX, double zoomY );

	/**
	 * Called to request the design be rendered.
	 */
	void render();

	/**
	 * Called to request the design be printed.
	 *
	 * @param factor The scale factor to apply to the design when printing.
	 */
	// TODO Rename to print()
	void printRender( double factor );

	Node getNode();
}
