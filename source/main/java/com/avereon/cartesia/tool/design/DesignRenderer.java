package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.Collection;

public abstract class DesignRenderer extends Pane {

	public abstract void setDesign( Design design );

	public abstract void setVisibleLayers( Collection<DesignLayer> layers );

	public abstract void setDpi( double dpi );

	public abstract void setViewCenter( Point3D center );

	public abstract void setViewRotate( double rotate );

	public abstract void setViewZoom( double zoom );

	/**
	 * Called to request the design be rendered.
	 */
	public abstract void render();

	/**
	 * Called to request the design be printed.
	 *
	 * @param factor The scale factor to apply to the design when printing.
	 */
	public abstract void print( double factor );

	public abstract Node getNode();
}
