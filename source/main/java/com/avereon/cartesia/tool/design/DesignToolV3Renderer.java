package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.tool.Workplane;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import lombok.CustomLog;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@CustomLog
public class DesignToolV3Renderer extends DesignRenderer {

	public static final String FX_SHAPE = "fx-shape";

	/**
	 * The primary container for all visual elements that are not part of the design
	 * in the renderer. Examples include the orientation indicator.
	 * <p>
	 * This field is immutable and is used internally to manage the rendering system's
	 * screen-level components.
	 */
	private final Pane screen;

	/**
	 * Represents the primary rendering pane for the design in the renderer.
	 * This pane serves as the container for all graphical components and sublayers
	 * that are part of the design. It acts as the central element around which
	 * other panes or layers may be structured to compose the complete design visualization.
	 * <p>
	 * This field is immutable and is used internally to manage the rendering system's
	 * design-level components.
	 */
	private final Pane world;

	// The geometry in this pane should be configured by the workplane but
	// managed by an internal class that can optimize the use of the FX geometry.
	private final Pane grid;

	// The design pane contains all the design layers.
	private final Pane layers;

	private final Pane reference;

	private final Pane preview;

	private Design design;

	/**
	 * A flag indicating whether the FX geometry is currently being updated.
	 * This variable is primarily used to prevent redundant or recursive updates
	 * during the rendering process, ensuring the update operations are executed
	 * efficiently and without conflicts.
	 */
	private boolean updatingFxGeometry;

	// NEXT Apply lessons learned to create a new design renderer

	DesignToolV3Renderer() {
		super();
		getStyleClass().add( "tool-renderer" );

		grid = new Pane();
		grid.getStyleClass().add( "tool-renderer-grid" );

		layers = new Pane();
		layers.getStyleClass().add( "tool-renderer-design" );

		preview = new Pane();
		preview.getStyleClass().add( "tool-renderer-preview" );

		reference = new Pane();
		reference.getStyleClass().add( "tool-renderer-reference" );

		// The world scale container
		// Contains the grid, design, preview, and reference panes
		world = new Pane();
		world.getTransforms().add( Transform.scale( 1, -1 ) );
		world.getChildren().addAll( grid, layers, preview, reference );

		// The screen scale container
		// Contains the orientation indicator
		screen = new Pane();

		getChildren().addAll( world, screen );

		// Update the geometry when the DPI changes
		dpiXProperty().addListener( ( _, _, _ ) -> this.updateFxGeometry() );
		dpiYProperty().addListener( ( _, _, _ ) -> this.updateFxGeometry() );
	}

	@Override
	public void setDesign( Design design ) {
		this.design = design;

		// Grid geometry
		this.grid.getChildren().clear();
		this.grid.getChildren().add( new Line( -10, 0, 10, 0 ) ); // Horizontal line
		this.grid.getChildren().add( new Line( 0, -10, 0, 10 ) ); // Vertical line

		// Update the geometry when the design unit changes
		design.register( this, Design.UNIT, _ -> this.updateFxGeometry() );
	}

	/**
	 * Determines whether the specified design layer is visible within the renderer.
	 *
	 * @param layer The design layer whose visibility is to be checked.
	 * @return True if the layer is visible, false otherwise.
	 */
	@Override
	public boolean isLayerVisible( DesignLayer layer ) {
		Pane pane = layer.getValue( FX_SHAPE );
		return layers.getChildren().contains( pane );
	}

	/**
	 * Sets the visibility of a specific design layer in the renderer. When a layer
	 * is made visible, its geometry is created and added to the rendering system.
	 * Conversely, when a layer is hidden, its geometry is removed to optimize
	 * rendering performance.
	 *
	 * @param layer The design layer whose visibility is being set.
	 * @param visible True to make the layer visible, false to make it hidden.
	 */
	@Override
	public void setLayerVisible( DesignLayer layer, boolean visible ) {
		// This method has a very important implementation, it is more than just
		// setting a flag, it participates in the performance of the renderer by
		// creating and destroying geometry. Since most layers are not visible in
		// most designs, layer geometry is only created when needed, and that is
		// most often when the layer is made visible. The same happens in reverse;
		// when the layer is hidden, the geometry is usually not needed anymore.
		if( visible ) {
			// Add the FX layer to the renderer
			Pane pane = mapDesignLayer( layer, true );
			layer.setValue( FX_SHAPE, pane );
			layers.getChildren().add( determineLayerIndex( layer ), pane );
		} else {
			// Remove the FX layer from the renderer
			Pane pane = layer.getValue( FX_SHAPE );
			if( pane != null ) layers.getChildren().remove( pane );
			layer.setValue( FX_SHAPE, null );
		}
	}

	@Override
	public List<DesignLayer> getVisibleLayers() {
		return List.of();
	}

	@Override
	public void setVisibleLayers( Collection<DesignLayer> layers ) {
		// Convenience method, to set multiple layers visible and hidden at the same time
	}

	public void addWorkplane( Workplane workplane ) {
		//gridGeometryManager.updateGridGeometry( workplane );
	}

	public void removeWorkplane( Workplane workplane ) {
		//gridGeometryManager.removeGridGeometry( workplane );
	}

	@Override
	public void render() {

	}

	@Override
	public void print( double factor ) {

	}

	final Pane layersPane() {
		return layers;
	}

	Bounds getVisualBounds( Node node ) {
		return node.getBoundsInParent();
	}

	/**
	 * Determines the appropriate index for placing a design layer among the existing
	 * FX layers based on the order of the design layers in the design.
	 *
	 * @param designLayer The design layer to determine the index for.
	 * @return The computed index where the design layer should be inserted among FX layers.
	 */
	private int determineLayerIndex( DesignLayer designLayer ) {
		List<DesignLayer> designLayers = design.getLayers().getAllLayers();
		Collections.reverse( designLayers );
		List<Node> fxLayers = layers.getChildren();

		// Determine the appropriate index in the FX layers
		int index = -1;
		for( DesignLayer checkLayer : designLayers ) {
			if( checkLayer == designLayer ) break;
			Pane fxLayer = checkLayer.getValue( FX_SHAPE );
			if( fxLayer != null ) index = fxLayers.indexOf( fxLayer );
		}

		return index + 1;
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
				// TODO Handlers need to be attached with the layer as owner
				if( shape != null ) layer.getChildren().add( shape );
			} );
		}

		if( includeSubLayers ) {
			designLayer.getLayers().forEach( subLayer -> layer.getChildren().add( mapDesignLayer( subLayer ) ) );
		}

		return layer;
	}

	/**
	 * Called when all the FX geometry needs to be updated due to a change in
	 * renderer or design settings such as DPI or design unit.
	 */
	private void updateFxGeometry() {
		if( updatingFxGeometry || design == null ) return;
		try {
			updatingFxGeometry = true;
			design.getLayers().getAllLayers().forEach( layer -> {
				layer.getShapes().forEach( shape -> mapDesignShape( shape, true ) );
			} );
		} finally {
			updatingFxGeometry = false;
		}
	}

	private Shape mapDesignShape( DesignShape designShape ) {
		return mapDesignShape( designShape, false );
	}

	private Shape mapDesignShape( DesignShape designShape, boolean forceUpdate ) {
		Shape fxShape = designShape.getValue( FX_SHAPE );
		if( !forceUpdate && fxShape != null ) return fxShape;

		Design design = designShape.getDesign().orElse( null );
		if( design == null ) return null;

		DesignUnit unit = design.calcDesignUnit();
		double unitScale = unit.to( 1, DesignUnit.IN );
		double gzX = getDpiX() * unitScale;
		double gzY = getDpiY() * unitScale;

		fxShape = switch( designShape.getType() ) {
			case LINE -> updateLineGeometry( (DesignLine)designShape, gzX, gzY );
			case TEXT -> updateTextGeometry( (DesignText)designShape, gzX, gzY );
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

	private Shape updateLineGeometry( DesignLine designLine, double gzX, double gzY ) {
		Line line = designLine.getValue( FX_SHAPE );
		if( line == null ) line = designLine.setValue( FX_SHAPE, new Line() );

		line.setStartX( designLine.getOrigin().getX() * gzX );
		line.setStartY( designLine.getOrigin().getY() * gzY );
		line.setEndX( designLine.getPoint().getX() * gzX );
		line.setEndY( designLine.getPoint().getY() * gzY );

		return updateCommonShapeGeometry( designLine, line, gzX, gzY );
	}

	// TODO Finish building the update methods for the remaining design shapes

	private Shape updateTextGeometry( DesignText designText, double gzX, double gzY ) {
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

		return updateCommonShapeGeometry( designText, text, gzX, gzY );
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
	private Shape updateCommonShapeGeometry( DesignShape designShape, Shape shape, double gzX, double gzY ) {
		shape.setStrokeWidth( designShape.calcDrawWidth() * gzX );
		shape.setStrokeDashOffset( designShape.calcDashOffset() * gzX );
		shape.getStrokeDashArray().setAll( designShape.calcDashPattern().stream().map( d -> d * gzX ).toList() );
		//shape.setStrokeMiterLimit( designShape.calcDrawMiterLimit()* gzX );
		return shape;
	}

}
