package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignUnit;
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
import javafx.scene.transform.Transform;
import lombok.CustomLog;

import java.util.Collection;

@CustomLog
public class DesignToolV3Renderer extends DesignRenderer {

	public static final String FX_SHAPE = "fx-shape";

	// The geometry in this pane should be configured by the workplane but
	// managed by an internal class that can optimize the use of the FX geometry.
	private final Pane grid;

	// The design pane contains all the design layers.
	private final Pane design;

	private final Pane reference;

	private final Pane preview;

	private final Pane world;

	private final Pane screen;

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
		world.getTransforms().add( Transform.scale( 1, -1 ) );
		world.getChildren().addAll( grid, design, preview, reference );

		// The screen scale container
		// Contains the orientation indicator
		screen = new Pane();

		getChildren().addAll( world, screen );

		// Update the geometry when the DPI changes
		//		dpiXProperty().addListener( ( _, _, n ) -> this.updateFxGeometry( n.doubleValue(), getDpiY() ) );
		//		dpiYProperty().addListener( ( _, _, n ) -> this.updateFxGeometry( getDpiX(), n.doubleValue() ) );
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

		design.register( this, Design.UNIT, e -> {
			// NEXT Design unit changed, update the geometry scale
		} );

		// Add boundary rectangles for each shape in the design
		design.getLayers().getAllLayers().forEach( designLayer -> {
			designLayer.getShapes().forEach( designShape -> {
				Shape shape = mapDesignShape( designShape );
				if( shape != null ) {
					Rectangle bounds = FxUtil.toRectangle( getVisualBounds( shape ) );
					bounds.setStroke( Colors.parse( "#C0A000" ) );
					bounds.setStrokeWidth( 1 );
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

	private Shape mapDesignShape( DesignShape designShape ) {
		Shape fxShape = designShape.getValue( FX_SHAPE );
		if( fxShape != null ) return fxShape;

		Design design = designShape.getDesign().orElse(null);
		if( design == null ) return null;

		DesignUnit unit = design.calcDesignUnit();
		double unitScale = unit.to( 1, DesignUnit.IN );
		double gzX = getDpiX() * unitScale;
		double gzY = getDpiY() * unitScale;

		fxShape = switch( designShape.getType() ) {
			case LINE -> updateFxGeometry( (DesignLine)designShape, gzX, gzY );
			case TEXT -> updateFxGeometry( (DesignText)designShape, gzX, gzY );
			default -> null;
		};

		if( fxShape == null ) {
			log.atWarn().log( "Unable to map design shape: %s", designShape );
			return null;
		}

		fxShape.setUserData( designShape );
		fxShape.setManaged( false );

		fxShape.setStroke( designShape.calcDrawPaint() );
		fxShape.setStrokeLineCap( designShape.calcDrawCap() );
		fxShape.setStrokeLineJoin( designShape.calcDrawJoin() );
		//fxShape.setStrokeType( designShape.calcDrawType() );

		fxShape.setFill( designShape.calcFillPaint() );

		return fxShape;
	}

	private Bounds getVisualBounds( Node node ) {
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

	private Shape updateFxGeometry( DesignLine designLine, double gzX, double gzY ) {
		Line line = designLine.getValue( FX_SHAPE );
		if( line == null ) line = designLine.setValue( FX_SHAPE, new Line() );

		line.setStartX( designLine.getOrigin().getX() * gzX );
		line.setStartY( designLine.getOrigin().getY() * gzY );
		line.setEndX( designLine.getPoint().getX() * gzX );
		line.setEndY( designLine.getPoint().getY() * gzY );

		return updateFxGeometry( designLine, line, gzX, gzY );
	}

	// TODO Finish building the update methods for the remaining design shapes

	private Shape updateFxGeometry( DesignText designText, double gzX, double gzY ) {
		Text text = designText.getValue( FX_SHAPE );
		if( text == null ) text = designText.setValue( FX_SHAPE, new Text() );

		double x = designText.getOrigin().getX() * gzX;
		double y = designText.getOrigin().getY() * gzY;

		text.setX( x );
		text.setY( -y );
		text.setText( designText.getText() );
		text.setFont( Font.font( designText.calcFontName(), designText.calcFontWeight(), designText.calcFontPosture(), designText.calcTextSize() * gzY ) );
		text.getTransforms().add( Transform.rotate( designText.calcRotate(), x, y ) );
		text.getTransforms().add( Transform.scale( 1, -1 ) );

		return updateFxGeometry( designText, text, gzX, gzY );
	}

	/**
	 * Update the common geometry properties of the shape. This method is used to
	 * common shape properties that are dependent on the rendering scale.
	 *
	 * @param designShape The source design shape
	 * @param shape The target FX shape
	 * @param gzX The pre-calculated geometry scale factor for the X axis
	 * @param gzY The pre-calculated geometry scale factor for the Y axis
	 * @return The updated FX shape
	 */
	private Shape updateFxGeometry( DesignShape designShape, Shape shape, double gzX, double gzY ) {
		shape.setStrokeWidth( designShape.calcDrawWidth() * gzX );
		shape.setStrokeDashOffset( designShape.calcDashOffset() * gzX );
		shape.getStrokeDashArray().setAll( designShape.calcDashPattern().stream().map( d -> d * gzX ).toList() );
		//shape.setStrokeMiterLimit( designShape.calcDrawMiterLimit()* gzX );
		return shape;
	}

}
