package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.Workplane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

import java.util.Collection;

public class DesignToolV3Renderer extends DesignRenderer {

	// The geometry in this pane should be configured by the workplane but
	// managed by an internal class that can optimize the use of the FX geometry.
	private final Pane grid;

	// The design pane contains all the design layers.
	private final Pane design;

	// NEXT Apply lessons learned to create a new design renderer

	DesignToolV3Renderer() {
		super();
		getStyleClass().add( "tool-renderer" );

		grid = new Pane();
		grid.getStyleClass().add( "tool-renderer-grid" );

		design = new Pane();
		design.getStyleClass().add( "tool-renderer-design" );

		// What other specific panes should be here
		// Reference pane?
		// Preview pane?

		//getTransforms().add( Transform.scale( 1, -1 ) );
		getChildren().addAll( grid, design );
	}

	@Override
	public void setDesign( Design design ) {
		// Grid geometry
		this.grid.getChildren().clear();
		this.grid.getChildren().add(new Line( -10, 0, 10, 0 ) ); // Horizontal line
		this.grid.getChildren().add(new Line( 0, -10, 0, 10 ) ); // Vertical line

		// Test geometry
		this.design.getChildren().clear();
		// Green line goes up and to the right
		Line greenLine = new Line( -2, -2, 2, 2 );
		greenLine.setStroke( javafx.scene.paint.Color.GREEN );
		greenLine.setStrokeWidth( 1 );
		greenLine.setStrokeLineCap( StrokeLineCap.ROUND );
		// Red line goes down and to the right
		Line redLine = new Line( -2, 2, 2, -2 );
		redLine.setStroke( javafx.scene.paint.Color.RED.darker().darker() );
		redLine.setStrokeWidth( 1 );
		redLine.setStrokeLineCap( StrokeLineCap.ROUND );
		this.design.getChildren().addAll( redLine, greenLine );
	}

	public void addWorkplane( Workplane workplane ) {
		//gridGeometryManager.updateGridGeometry( workplane );
	}

	public void removeWorkplane( Workplane workplane ) {
		//gridGeometryManager.removeGridGeometry( workplane );
	}

	public void setLayer( DesignLayer layer ) {
		design.getChildren().clear();
	}

	@Override
	public void setVisibleLayers( Collection<DesignLayer> layers ) {

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
	public void render() {

	}

	@Override
	public void print( double factor ) {

	}

}
