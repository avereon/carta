package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.tool.Workplane;
import com.avereon.data.NodeEvent;
import com.avereon.xenon.util.DragCapability;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import lombok.CustomLog;

import java.lang.ref.WeakReference;
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

	private final DoubleProperty gzX;

	private final DoubleProperty gzY;

	private final DoubleProperty unitScale;

	private Design design;

	private Workplane workplane;

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

		gzX = new SimpleDoubleProperty( 1.0 );
		gzY = new SimpleDoubleProperty( 1.0 );
		unitScale = new SimpleDoubleProperty( 1.0 );

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
		world.getChildren().addAll( grid, layers, preview, reference );

		// TODO DEVELOPMENT
		DragCapability.add( world );

		// The screen scale container
		// Contains the orientation indicator
		screen = new Pane();

		getChildren().addAll( world, screen );

		// Add a listener to the unit scale property to update the global scale
		unitScaleProperty().addListener( ( _, _, n ) -> updateGz( n.doubleValue(), getDpiX(), getDpiY(), getOutputScaleX(), getOutputScaleY() ) );

		// Update the global scale when the DPI or output scale changes
		dpiXProperty().addListener( ( _, _, n ) -> this.updateGz( getUnitScale(), n.doubleValue(), getDpiY(), getOutputScaleX(), getOutputScaleY() ) );
		dpiYProperty().addListener( ( _, _, n ) -> this.updateGz( getUnitScale(), getDpiX(), n.doubleValue(), getOutputScaleX(), getOutputScaleY() ) );
		outputScaleXProperty().addListener( ( _, _, n ) -> this.updateGz( getUnitScale(), getDpiX(), getDpiY(), n.doubleValue(), getOutputScaleY() ) );
		outputScaleYProperty().addListener( ( _, _, n ) -> this.updateGz( getUnitScale(), getDpiX(), getDpiY(), getOutputScaleX(), n.doubleValue() ) );

		// Update the design geometry when the global scale changes
		gzXProperty().addListener( ( _, _, _ ) -> this.updateGridFxGeometry() );
		gzYProperty().addListener( ( _, _, _ ) -> this.updateGridFxGeometry() );
		gzXProperty().addListener( ( _, _, _ ) -> this.updateDesignFxGeometry() );
		gzYProperty().addListener( ( _, _, _ ) -> this.updateDesignFxGeometry() );
		gzXProperty().addListener( ( _, _, n ) -> this.updateWorldOrientation(
			getWidth(),
			getHeight(),
			getViewCenterX(),
			getViewCenterY(),
			getViewZoomX(),
			getViewZoomY(),
			getViewRotate(),
			n.doubleValue(),
			getGzY()
		) );
		gzYProperty().addListener( ( _, _, n ) -> this.updateWorldOrientation(
			getWidth(),
			getHeight(),
			getViewCenterX(),
			getViewCenterY(),
			getViewZoomX(),
			getViewZoomY(),
			getViewRotate(),
			getGzX(),
			n.doubleValue()
		) );

		// Update the world orientation when view settings change
		widthProperty().addListener( ( _, _, n ) -> updateWorldOrientation(
			n.doubleValue(),
			getHeight(),
			getViewCenterX(),
			getViewCenterY(),
			getViewZoomX(),
			getViewZoomY(),
			getViewRotate(),
			getGzX(),
			getGzY()
		) );
		heightProperty().addListener( ( _, _, n ) -> updateWorldOrientation(
			getWidth(),
			n.doubleValue(),
			getViewCenterX(),
			getViewCenterY(),
			getViewZoomX(),
			getViewZoomY(),
			getViewRotate(),
			getGzX(),
			getGzY()
		) );
		viewCenterXProperty().addListener( ( _, _, n ) -> updateWorldOrientation(
			getWidth(),
			getHeight(),
			n.doubleValue(),
			getViewCenterY(),
			getViewZoomX(),
			getViewZoomY(),
			getViewRotate(),
			getGzX(),
			getGzY()
		) );
		viewCenterYProperty().addListener( ( _, _, n ) -> updateWorldOrientation(
			getWidth(),
			getHeight(),
			getViewCenterX(),
			n.doubleValue(),
			getViewZoomX(),
			getViewZoomY(),
			getViewRotate(),
			getGzX(),
			getGzY()
		) );
		viewZoomXProperty().addListener( ( _, _, n ) -> updateWorldOrientation(
			getWidth(),
			getHeight(),
			getViewCenterX(),
			getViewCenterY(),
			n.doubleValue(),
			getViewZoomY(),
			getViewRotate(),
			getGzX(),
			getGzY()
		) );
		viewZoomYProperty().addListener( ( _, _, n ) -> updateWorldOrientation(
			getWidth(),
			getHeight(),
			getViewCenterX(),
			getViewCenterY(),
			getViewZoomX(),
			n.doubleValue(),
			getViewRotate(),
			getGzX(),
			getGzY()
		) );
		viewRotateProperty().addListener( ( _, _, n ) -> updateWorldOrientation(
			getWidth(),
			getHeight(),
			getViewCenterX(),
			getViewCenterY(),
			getViewZoomX(),
			getViewZoomY(),
			n.doubleValue(),
			getGzX(),
			getGzY()
		) );
	}

	@Override
	public Workplane getWorkplane() {
		return workplane;
	}

	@Override
	public void setWorkplane( Workplane workplane ) {
		this.workplane = workplane;

		// Update the grid geometry when the grid parameters change
		workplane.register( this, NodeEvent.ANY, _ -> this.updateGridFxGeometry() );

		updateGz( getUnitScale(), getDpiX(), getDpiY(), getOutputScaleX(), getOutputScaleY() );
	}

	public boolean isGridVisible() {
		return grid.isVisible();
	}

	public void setGridVisible( boolean visible ) {
		// This method has a very important implementation, it is more than just
		// setting a flag, it participates in the performance of the renderer by
		// creating and destroying geometry. Grid geometry is only created when
		// needed, and that is when the grid is made visible. The same happens in
		// reverse; when the grid is hidden, the geometry is not needed anymore.
		if( visible ) {
			updateGridFxGeometry();
			grid.setVisible( true );
		} else {
			grid.setVisible( false );
			grid.getChildren().clear();
		}
	}

	@Override
	public Design getDesign() {
		return design;
	}

	@Override
	public void setDesign( Design design ) {
		this.design = design;

		// Update the design geometry when the design unit changes
		design.register( this, Design.UNIT, _ -> setDesignUnit( design.calcDesignUnit() ) );

		setDesignUnit( design.calcDesignUnit() );
	}

	/**
	 * Determines whether the specified design layer is visible within the renderer.
	 *
	 * @param layer The design layer whose visibility is to be checked.
	 * @return True if the layer is visible, false otherwise.
	 */
	@Override
	public boolean isLayerVisible( DesignLayer layer ) {
		WeakReference<Pane> layerRef = layer.getValue( FX_SHAPE );
		Pane pane = layerRef == null ? null : layerRef.get();
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
			layer.setValue( FX_SHAPE, new WeakReference<>( pane ) );
			layers.getChildren().add( determineLayerIndex( layer ), pane );
		} else {
			// Remove the FX layer from the renderer
			WeakReference<Pane> layerRef = layer.getValue( FX_SHAPE );
			Pane pane = layerRef == null ? null : layerRef.get();
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

	@Override
	public void render() {

	}

	@Override
	public void print( double factor ) {

	}

	public Bounds screenToWorld( Bounds bounds ) {
		Bounds worldBounds = world.parentToLocal( bounds );

		double minX = worldBounds.getMinX() / getGzX();
		double minY = worldBounds.getMinY() / getGzY();
		double maxX = worldBounds.getMaxX() / getGzX();
		double maxY = worldBounds.getMaxY() / getGzY();

		return new BoundingBox( minX, minY, maxX - minX, maxY - minY );
	}

	public Bounds worldToScreen( Bounds bounds ) {
		Bounds screenBounds = world.localToParent( bounds );

		double minX = screenBounds.getMinX() * getGzX();
		double minY = screenBounds.getMinY() * getGzY();
		double maxX = screenBounds.getMaxX() * getGzX();
		double maxY = screenBounds.getMaxY() * getGzY();

		return new BoundingBox( minX, minY, maxX - minX, maxY - minY );
	}

	final Pane layersPane() {
		return layers;
	}

	Bounds getVisualBounds( Node node ) {
		return node.getBoundsInParent();
	}

	private void updateGz( double unitScale, double dpiX, double dpiY, double outputScaleX, double outputScaleY ) {
		setGzX( unitScale * dpiX * outputScaleX );
		setGzY( unitScale * dpiY * outputScaleY );
	}

	private double getGzX() {
		return gzX.get();
	}

	private void setGzX( double gzX ) {
		this.gzX.set( gzX );
	}

	private DoubleProperty gzXProperty() {
		return gzX;
	}

	private double getGzY() {
		return gzY.get();
	}

	private void setGzY( double gzY ) {
		this.gzY.set( gzY );
	}

	private DoubleProperty gzYProperty() {
		return gzY;
	}

	private double getUnitScale() {
		return unitScale.get();
	}

	private void setUnitScale( double unitScale ) {
		this.unitScale.set( unitScale );
	}

	private DoubleProperty unitScaleProperty() {
		return unitScale;
	}

	private void setDesignUnit( DesignUnit unit ) {
		setUnitScale( unit.to( 1, DesignUnit.IN ) );
	}

	private void updateWorldOrientation( double width, double height, double centerX, double centerY, double zoomX, double zoomY, double rotate, double gzx, double gzy ) {
		world.setTranslateX( -centerX * gzx + (0.5 * width) );
		world.setTranslateY( centerY * gzy + (0.5 * height) );
		world.setScaleX( zoomX );
		world.setScaleY( zoomY );
		world.setRotate( rotate );

		double outputRescaleX = 1.0 / getOutputScaleX();
		double outputRescaleY = 1.0 / getOutputScaleY();

		world.getTransforms().setAll( Transform.scale( outputRescaleX, -outputRescaleY ) );
	}

	void updateGridFxGeometry() {
		if( workplane == null ) return;
		workplane.getGridSystem().updateFxGeometryGrid( workplane, getGzX(), grid.getChildren() );
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
			WeakReference<Pane> layerRef = checkLayer.getValue( FX_SHAPE );
			Pane fxLayer = layerRef == null ? null : layerRef.get();
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
		designLayer.setValue( FX_SHAPE, new WeakReference<>( layer ) );

		if( includeShapes ) {
			designLayer.getShapes().forEach( designShape -> {
				Shape shape = mapDesignShape( designShape );
				if( shape != null ) layer.getChildren().add( shape );

				// TODO Handlers need to be attached with the layer as owner
				// i.e. designLayer.register(layer, "order", e -> changeLayerOrder() );
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
	private void updateDesignFxGeometry() {
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
		WeakReference<Shape> shapeRef = designShape.getValue( FX_SHAPE );
		Shape fxShape = shapeRef == null ? null : shapeRef.get();
		if( !forceUpdate && fxShape != null ) return fxShape;

		double gzX = getGzX();
		double gzY = getGzY();

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
		WeakReference<Line> lineRef = designLine.getValue( FX_SHAPE );
		Line line = lineRef == null ? null : lineRef.get();
		if( line == null ) {
			line = new Line();
			designLine.setValue( FX_SHAPE, new WeakReference<>( line ) );
		}

		line.setStartX( designLine.getOrigin().getX() * gzX );
		line.setStartY( designLine.getOrigin().getY() * gzY );
		line.setEndX( designLine.getPoint().getX() * gzX );
		line.setEndY( designLine.getPoint().getY() * gzY );

		return updateCommonShapeGeometry( designLine, line, gzX, gzY );
	}

	// TODO Finish building the update methods for the remaining design shapes

	private Shape updateTextGeometry( DesignText designText, double gzX, double gzY ) {
		WeakReference<Text> textRef = designText.getValue( FX_SHAPE );
		Text text = textRef == null ? null : textRef.get();
		if( text == null ) {
			text = new Text();
			designText.setValue( FX_SHAPE, new WeakReference<>( text ) );
		}

		double x = designText.getOrigin().getX() * gzX;
		double y = designText.getOrigin().getY() * gzY;

		text.setX( x );
		text.setY( -y );
		text.setText( designText.getText() );
		text.setFont( Font.font( designText.calcFontName(), designText.calcFontWeight(), designText.calcFontPosture(), designText.calcTextSize() * gzY ) );

		// Rotate must be before scale
		text.getTransforms().setAll( Transform.rotate( designText.calcRotate(), x, y ), Transform.scale( 1, -1 ) );

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
