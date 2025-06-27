package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.Workplane;
import com.avereon.zerra.javafx.FxUtil;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;

import java.util.Collection;

public class DesignToolV3Renderer extends DesignRenderer {

	// The geometry in this pane should be configured by the workplane but
	// managed by an internal class that can optimize the use of the FX geometry.
	private final Pane grid;

	// The design pane contains all the design layers.
	private final Pane design;

	private final Pane world;

	private final Pane screen;

	private Scale worldScaleTransform;

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

		world = new Pane();
		world.getChildren().addAll( grid, design );

		screen = new Pane();

		//getTransforms().add( Transform.scale( 1, -1 ) );
		getChildren().addAll( world, screen );

		unitProperty().addListener( ( _, _, n ) -> this.updateWorldScale( n, getDpiX(), getDpiY() ) );
		dpiXProperty().addListener( ( _, _, n ) -> this.updateWorldScale( getUnit(), n.doubleValue(), getDpiY() ) );
		dpiYProperty().addListener( ( _, _, n ) -> this.updateWorldScale( getUnit(), getDpiX(), n.doubleValue() ) );

		// Initialize the internal scale
		this.updateWorldScale( getUnit(), getDpiX(), getDpiY() );
	}

	@Override
	public void setDesign( Design design ) {
		// Grid geometry
		this.grid.getChildren().clear();
		this.grid.getChildren().add( new Line( -10, 0, 10, 0 ) ); // Horizontal line
		this.grid.getChildren().add( new Line( 0, -10, 0, 10 ) ); // Vertical line

		// Test geometry
		this.design.getChildren().clear();
		// Green line goes up and to the right
		Line greenLine = new Line( -3, -3, 3, 3 );
		greenLine.setStroke( javafx.scene.paint.Color.GREEN );
		greenLine.setStrokeWidth( 1 );
		greenLine.setStrokeLineCap( StrokeLineCap.ROUND );
		// Red line goes down and to the right
		Line redLine = new Line( -4, 4, 4, -4 );
		redLine.setStroke( javafx.scene.paint.Color.RED.darker().darker() );
		redLine.setStrokeWidth( 0.2 );
		redLine.setStrokeLineCap( StrokeLineCap.SQUARE );

		Rectangle greenBounds = FxUtil.toRectangle( getVisibleBounds( greenLine ) );
		greenBounds.setFill( null );
		greenBounds.setStrokeWidth( 0.01 );
		greenBounds.setStroke( javafx.scene.paint.Color.GREEN );

		Rectangle redBounds = FxUtil.toRectangle( getVisibleBounds( redLine ) );
		redBounds.setFill( null );
		redBounds.setStrokeWidth( 0.01 );
		redBounds.setStroke( javafx.scene.paint.Color.RED );

		this.design.getChildren().addAll( redLine, greenLine, greenBounds, redBounds );
	}

	private Bounds getVisibleBounds( Node node) {
		// There are two ways to approach this:
		// 1. Use the bounds of the world to determine the visible area.
		// 2. Use the bounds of the renderer to determine the visible area.
		return node.getBoundsInParent();
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

	/*
	For testing purposes only! This method is not part of the public API.
  */
	Scale getWorldScale() {
		return worldScaleTransform;
	}

	private void updateWorldScale( DesignUnit unit, double dpiX, double dpiY ) {
		double scaleFactorX = unit.to( dpiX, DesignUnit.INCH );
		double scaleFactorY = unit.to( dpiY, DesignUnit.INCH );
		if( worldScaleTransform != null ) world.getTransforms().remove( worldScaleTransform );
		worldScaleTransform = Transform.scale( scaleFactorX, -scaleFactorY );
		world.getTransforms().add( worldScaleTransform );
	}

}
