package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.Workplane;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;

import java.util.Collection;

public class DesignToolV3Renderer extends Pane implements DesignRenderer {

	private final Pane world;

	// The geometry in this pane should be configured by the workplane but
	// managed by an internal class that can optimize the use of the FX geometry.
	private final Pane grid;

	private final Pane design;

	// NEXT Apply lessons learned to create a new design renderer

	DesignToolV3Renderer() {
		super();
		getStyleClass().add( "tool-renderer" );

		// The grid pane is the bottom most layer
		grid = new Pane();
		grid.getStyleClass().add( "tool-renderer-grid" );

		// The design pane contains all the design layers
		design = new Pane();
		design.getStyleClass().add( "tool-renderer-design" );

		// What other specific panes should be here
		// Reference pane?
		// Preview pane?

		// The world pane contains all the world-scale panes
		world = new Pane();
		world.getStyleClass().add( "tool-renderer-world" );
		world.getTransforms().add( Transform.scale( 1, -1 ) );
		world.getChildren().addAll( grid, design );

		getChildren().addAll( world );
	}

	@Override
	public void setDesign( Design design ) {
		this.design.getChildren().clear();
	}

	public void setWorkplane( Workplane workplane ) {
		//gridGeometryManager.updateGridGeometry( workplane );
	}

	public void setLayer( DesignLayer layer ) {
		design.getChildren().clear();
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
