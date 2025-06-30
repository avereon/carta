package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.*;
import com.avereon.cartesia.tool.Workplane;
import com.avereon.zerra.color.Colors;
import com.avereon.zerra.javafx.FxUtil;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import lombok.CustomLog;

import java.util.Collection;

@CustomLog
public class DesignToolV3Renderer extends DesignRenderer {

	// NEXT Implement an internal scale value that is used to scale the world
	// geometry to the renderer. This should allow the geometry to be rendered
	// accurately as well as bounding rectangles to be calculated accurately.
	// The scale should be at least as large as the media DPI, which can reach
	// as high as 9600 x 2400 DPI with high resolution printers.
	//
 	// The text renderer dies at 100,000,000 scale, so we need to keep the
	// scale below that value.

	static final double ATOMIC_SCALE = 10000;

	static final double ATOMIC_ISCALE = 1.0 / ATOMIC_SCALE;

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

		dpiXProperty().addListener( ( _, _, n ) -> this.updateWorldScale( n.doubleValue(), getDpiY() ) );
		dpiYProperty().addListener( ( _, _, n ) -> this.updateWorldScale( getDpiX(), n.doubleValue() ) );

		// Initialize the internal scale
		this.updateWorldScale( getDpiX(), getDpiY() );
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

		// Add boundary rectangles for each shape in the design
		design.getLayers().getAllLayers().forEach( designLayer -> {
			designLayer.getShapes().forEach( designShape -> {
				Shape shape = mapDesignShape( designShape );
				if( shape != null ) {
					Rectangle bounds = FxUtil.toRectangle( getVisualBounds( shape ) );
					bounds.setStroke( Colors.parse("#C0A000") );
					bounds.setStrokeWidth( 0.02 * ATOMIC_SCALE );
					bounds.setFill( null );
					reference.getChildren().add( bounds );
				}
			} );
		} );

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
		// TODO Use a weak reference to the layer to avoid memory leaks
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
		Shape fxShape = shape.getValue( "fx-shape" );
		if( fxShape != null ) return fxShape;

		fxShape = switch( shape.getType() ) {
			case LINE -> {
				DesignLine designLine = (DesignLine)shape;
				yield new Line(
					designLine.getOrigin().getX() * ATOMIC_SCALE,
					designLine.getOrigin().getY() * ATOMIC_SCALE,
					designLine.getPoint().getX() * ATOMIC_SCALE,
					designLine.getPoint().getY() * ATOMIC_SCALE
				);
			}
			case TEXT -> {
				DesignText designText = (DesignText)shape;
				Text text = new Text( designText.getOrigin().getX() * ATOMIC_SCALE, -designText.getOrigin().getY() * ATOMIC_SCALE, designText.getText() );
				text.setFont( Font.font( designText.calcFontName(), designText.calcFontWeight(), designText.calcFontPosture(), designText.calcTextSize() * ATOMIC_SCALE ) );
				text.getTransforms().add( Transform.rotate( designText.calcRotate(), designText.getOrigin().getX() * ATOMIC_SCALE, designText.getOrigin().getY() * ATOMIC_SCALE ) );
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
		fxShape.setStrokeWidth( shape.calcDrawWidth() * ATOMIC_SCALE );
		fxShape.setStrokeLineCap( shape.calcDrawCap() );
		//fxShape.setStrokeLineJoin( shape.calcDrawJoin() );
		//fxShape.setStrokeType( shape.calcDrawType() );

		fxShape.getStrokeDashArray().setAll( shape.calcDrawPattern() );
		//fxShape.setStrokeDashOffset( shape.calcDrawDashOffset() );
		//fxShape.setStrokeMiterLimit( shape.calcDrawMiterLimit() );
		fxShape.setFill( shape.calcFillPaint() );

		return fxShape;
	}

	private Bounds getVisualBounds( Node node ) {
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

	private void updateWorldScale( double dpiX, double dpiY ) {
		if( worldScaleTransform != null ) world.getTransforms().remove( worldScaleTransform );
		worldScaleTransform = Transform.scale( dpiX* ATOMIC_ISCALE, -dpiY* ATOMIC_ISCALE );
		world.getTransforms().add( worldScaleTransform );
	}

}
