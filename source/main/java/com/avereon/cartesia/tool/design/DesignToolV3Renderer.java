package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.tool.Workplane;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import lombok.CustomLog;

import java.util.Collection;

@CustomLog
public class DesignToolV3Renderer extends DesignRenderer {

	// The geometry in this pane should be configured by the workplane but
	// managed by an internal class that can optimize the use of the FX geometry.
	private final Pane grid;

	// The design pane contains all the design layers.
	private final Pane design;

	private final Pane reference;

	private final Pane preview;

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

		preview = new Pane();
		preview.getStyleClass().add( "tool-renderer-preview" );

		reference = new Pane();
		reference.getStyleClass().add( "tool-renderer-reference" );

		// The world scale container
		// Contains the grid, design, preview, and reference panes
		world = new Pane();
		world.getChildren().addAll( grid, design, preview, reference );

		// The screen scale container
		// Contains the orientation indicator
		screen = new Pane();

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

		// FIXME This is inefficient since it builds geometry for everything in the design.
		// Consider only generating geometry for the visible layers and shapes.
		DesignLayer rootDesignLayer = design.getLayers();
		rootDesignLayer.getLayers().forEach( designLayer -> this.design.getChildren().add( mapDesignLayer( designLayer ) ) );

		//		// Test geometry
		//		this.design.getChildren().clear();
		//		// Green line goes up and to the right
		//		Line greenLine = new Line( -3, -3, 3, 3 );
		//		greenLine.setStroke( javafx.scene.paint.Color.GREEN );
		//		greenLine.setStrokeWidth( 1 );
		//		greenLine.setStrokeLineCap( StrokeLineCap.ROUND );
		//		// Red line goes down and to the right
		//		Line redLine = new Line( -4, 4, 4, -4 );
		//		redLine.setStroke( javafx.scene.paint.Color.RED.darker().darker() );
		//		redLine.setStrokeWidth( 0.2 );
		//		redLine.setStrokeLineCap( StrokeLineCap.SQUARE );
		//
		//		this.design.getChildren().addAll( redLine, greenLine );
		//
		//		Rectangle greenBounds = FxUtil.toRectangle( getVisibleBounds( greenLine ) );
		//		greenBounds.setFill( null );
		//		greenBounds.setStrokeWidth( 0.01 );
		//		greenBounds.setStroke( javafx.scene.paint.Color.GREEN );
		//
		//		Rectangle redBounds = FxUtil.toRectangle( getVisibleBounds( redLine ) );
		//		redBounds.setFill( null );
		//		redBounds.setStrokeWidth( 0.01 );
		//		redBounds.setStroke( javafx.scene.paint.Color.RED );
		//
		//		this.design.getChildren().addAll( greenBounds, redBounds );
	}

	private Pane mapDesignLayer( DesignLayer designLayer ) {
		return mapDesignLayer( designLayer, true, true );
	}

	private Pane mapDesignLayer( DesignLayer designLayer, boolean includeShapes ) {
		return mapDesignLayer( designLayer, includeShapes, false );
	}

	private Pane mapDesignLayer( DesignLayer designLayer, boolean includeShapes, boolean includeSubLayers ) {
		Pane layer = new Pane();
		layer.setUserData( designLayer );
		designLayer.setValue( "fx-pane", layer );
		log.atConfig().log( "Created a pane for layer: %s", designLayer.getName() );

		if( includeShapes ) {
			designLayer.getShapes().forEach( designShape -> {
				Shape shape = mapDesignShape( designShape );
				if( shape != null ) layer.getChildren().add( shape );
			} );
		}

		if( includeSubLayers ) {
			designLayer.getLayers().forEach( subLayer -> layer.getChildren().add( mapDesignLayer( subLayer ) ) );
		}

		return layer;
	}

	private Shape mapDesignShape( DesignShape shape ) {

		Shape fxShape = switch( shape.getType() ) {
			case LINE -> {
				DesignLine designLine = (DesignLine)shape;
				yield new Line( designLine.getOrigin().getX(), designLine.getOrigin().getY(), designLine.getPoint().getX(), designLine.getPoint().getY() );
			}
			case TEXT -> {
				DesignText designText = (DesignText)shape;
				Text text = new Text( designText.getOrigin().getX(), -designText.getOrigin().getY(), designText.getText() );
				text.setFont( designText.calcFont() );
				text.setRotate( designText.calcRotate() );
				text.getTransforms().add( Transform.scale( 1, -1 ) );
				yield text;
			}
			default -> null;
		};

		if( fxShape == null ) {
			log.atWarn().log( "Unable to map design shape: %s", shape );
			return null;
		}

		fxShape.setUserData( shape );
		fxShape.setManaged( false );

		fxShape.setStroke( shape.calcDrawPaint() );
		fxShape.setStrokeWidth( shape.calcDrawWidth() );
		fxShape.setStrokeLineCap( shape.calcDrawCap() );
		//fxShape.setStrokeLineJoin( shape.calcDrawJoin() );
		//fxShape.setStrokeType( shape.calcDrawType() );

		fxShape.getStrokeDashArray().setAll( shape.calcDrawPattern() );
		//fxShape.setStrokeDashOffset( shape.calcDrawDashOffset() );
		//fxShape.setStrokeMiterLimit( shape.calcDrawMiterLimit() );
		fxShape.setFill( shape.calcFillPaint() );

		return fxShape;
	}

	private Bounds getVisibleBounds( Node node ) {
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
