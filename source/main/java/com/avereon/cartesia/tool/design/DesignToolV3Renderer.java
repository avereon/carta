package com.avereon.cartesia.tool.design;

import com.avereon.annotation.CommonNote;
import com.avereon.annotation.Note;
import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.tool.Workplane;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import lombok.CustomLog;
import lombok.Getter;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@CustomLog
public class DesignToolV3Renderer extends BaseDesignRenderer {

	public static final String FX_SHAPE = "fx-shape";

	/**
	 * The primary container for all visual elements that are not part of the design
	 * in the renderer. Examples include the orientation indicator.
	 * <p>
	 * This field is immutable and is used internally to manage the rendering system's
	 * screen-level components.
	 */
	@Getter
	final Pane screen;

	/**
	 * Represents the primary rendering pane for the design in the renderer.
	 * This pane serves as the container for all graphical components and sublayers
	 * that are part of the design. It acts as the central element around which
	 * other panes or layers may be structured to compose the complete design visualization.
	 * <p>
	 * This field is immutable and is used internally to manage the rendering system's
	 * design-level components.
	 */
	@Getter
	final Pane world;

	// The geometry in this pane should be configured by the workplane but
	// managed internally so that it can be optimized the use of the FX geometry.
	@Getter
	final Pane grid;

	// The design pane contains all the design layers.
	@Getter
	final Pane layers;

	@Getter
	final Pane reference;

	@Getter
	final Pane preview;

	private final DoubleProperty shapeScaleX;

	private final DoubleProperty shapeScaleY;

	private final DoubleProperty unitScale;

	private Design design;

	private Workplane workplane;

	// Cacheable - meaning always use getScreenToWorldTransform()
	private Transform screenToWorldTransform;

	// Cacheable - meaning always use getWorldToScreenTransform()
	private Transform worldToScreenTransform;

	/**
	 * A flag indicating whether the FX geometry is currently being updated.
	 * This variable is primarily used to prevent redundant or recursive updates
	 * during the rendering process, ensuring the update operations are executed
	 * efficiently and without conflicts.
	 */
	private boolean updatingFxGeometry;

	/**
	 * A timing flag indicating when to allow the next grid update.
	 */
	private long nextGridUpdate;

	private final EventHandler<NodeEvent> workflowChangeHandler = _ -> updateGridFxGeometry();

	private final EventHandler<NodeEvent> designUnitChangeHandler = _ -> setDesignUnit( design.calcDesignUnit() );

	// NEXT Apply lessons learned to create a new design renderer

	DesignToolV3Renderer() {
		super();

		shapeScaleX = new SimpleDoubleProperty( 1.0 );
		shapeScaleY = new SimpleDoubleProperty( 1.0 );
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
		world = new StackPane();
		world.getChildren().addAll( grid, layers, preview, reference );

		// The screen scale container
		// Contains the orientation indicator
		screen = new Pane();

		getChildren().addAll( world, screen );

		// Configure the shape scale definition. The shape scale includes the unit
		// scale, DPI and the output scale and is used to modify the shape geometry.
		shapeScaleX.bind( unitScaleProperty().multiply( dpiXProperty() ).multiply( outputScaleXProperty() ) );
		shapeScaleY.bind( unitScaleProperty().multiply( dpiYProperty() ).multiply( outputScaleYProperty() ) );

		// The translate properties do not include the output scale property because
		// these are parent coordinates and not local coordinates, and the parent
		// transforms have already incorporated the output scale. The translate
		// properties also have to compensate for the scale acting at the center of
		// the pane and not at the origin.
		world
			.translateXProperty()
			.bind( viewCenterXProperty()
				.multiply( viewZoomXProperty() )
				.multiply( -1 )
				.multiply( unitScaleProperty() )
				.multiply( dpiXProperty() )
				.add( widthProperty().multiply( 0.5 ).multiply( viewZoomXProperty() ).divide( outputScaleXProperty() ) ) );
		world
			.translateYProperty()
			.bind( viewCenterYProperty()
				.multiply( viewZoomXProperty() )
				.multiply( unitScaleProperty() )
				.multiply( dpiYProperty() )
				.add( heightProperty().multiply( -0.5 ).multiply( viewZoomYProperty() ).divide( outputScaleXProperty() ) ) );

		// The scale properties do not include the DPI property because the geometry
		// values already include the DPI. What is interesting here is that we divide
		// out the output scale at the same time. This allows JavaFX to render the
		// geometry at the highest resolution, regardless of the output scale set by
		// the user. Someday this may need to be tied to a HiDPI setting, but we'll
		// leave it here to understand how the technique works.
		world.scaleXProperty().bind( viewZoomXProperty().divide( outputScaleXProperty() ) );
		world.scaleYProperty().bind( viewZoomYProperty().divide( outputScaleYProperty() ).multiply( -1 ) );

		// The rotate property is pretty straightforward, after the other two groups
		world.rotateProperty().bind( viewRotateProperty() );

		// Update the design geometry when the global scale changes
		shapeScaleXProperty().addListener( ( _, _, _ ) -> this.updateGridFxGeometry() );
		shapeScaleYProperty().addListener( ( _, _, _ ) -> this.updateGridFxGeometry() );
		shapeScaleXProperty().addListener( ( _, _, _ ) -> this.updateDesignFxGeometry() );
		shapeScaleYProperty().addListener( ( _, _, _ ) -> this.updateDesignFxGeometry() );
		shapeScaleXProperty().addListener( ( _, _, _ ) -> this.clearCachedTransforms() );
		shapeScaleYProperty().addListener( ( _, _, _ ) -> this.clearCachedTransforms() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Design getDesign() {
		return design;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDesign( Design design ) {
		if( this.design != null ) {
			this.design.unregister( this, Design.UNIT, designUnitChangeHandler );
		}

		this.design = design;

		if( this.design != null ) {
			design.register( this, Design.UNIT, designUnitChangeHandler );
			setDesignUnit( design.calcDesignUnit() );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Workplane getWorkplane() {
		return workplane;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWorkplane( Workplane workplane ) {
		if( this.workplane != null ) {
			this.workplane.unregister( this, NodeEvent.ANY, workflowChangeHandler );
		}

		this.workplane = workplane;

		if( this.workplane != null ) {
			this.workplane.register( this, NodeEvent.ANY, workflowChangeHandler );
		}

		updateGridFxGeometry();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isGridVisible() {
		return grid.isVisible();
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLayerVisible( DesignLayer layer ) {
		WeakReference<Pane> layerRef = layer.getValue( FX_SHAPE );
		Pane pane = layerRef == null ? null : layerRef.get();
		return layers.getChildren().contains( pane );
	}

	/**
	 * {@inheritDoc}
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<DesignLayer> getVisibleLayers() {
		return List.of();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisibleLayers( Collection<DesignLayer> layers ) {
		// Convenience method, to set multiple layers visible and hidden at the same time
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print( double factor ) {

	}

	/**
	 * {@inheritDoc}
	 */
	public Transform getScreenToWorldTransform() {
		// FIXME This causes stale transforms :-(
		//if( screenToWorldTransform == null ) {
		try {
			screenToWorldTransform = getWorldToScreenTransform().createInverse();
		} catch( NonInvertibleTransformException exception ) {
			// This should never happen since the world-to-screen transform should always be invertible
			throw new RuntimeException( exception );
		}
		//}
		return screenToWorldTransform;
	}

	/**
	 * {@inheritDoc}
	 */
	public Point2D screenToWorld( double x, double y ) {
		return screenToWorld( new Point2D( x, y ) );
	}

	/**
	 * {@inheritDoc}
	 */
	public Point2D screenToWorld( Point2D point ) {
		return getScreenToWorldTransform().transform( point );
	}

	/**
	 * {@inheritDoc}
	 */
	public Point3D screenToWorld( double x, double y, double z ) {
		return screenToWorld( new Point3D( x, y, z ) );
	}

	/**
	 * {@inheritDoc}
	 */
	public Point3D screenToWorld( Point3D point ) {
		return getScreenToWorldTransform().transform( point );
	}

	/**
	 * {@inheritDoc}
	 */
	public Bounds screenToWorld( Bounds bounds ) {
		return getScreenToWorldTransform().transform( bounds );
	}

	/**
	 * {@inheritDoc}
	 */
	public Transform getWorldToScreenTransform() {
		// FIXME This causes stale transforms :-(
		//if( worldToScreenTransform == null ) {
		Transform scale = Transform.scale( getShapeScaleX(), getShapeScaleY() );
		worldToScreenTransform = world.getLocalToParentTransform().createConcatenation( scale );
		//}
		return worldToScreenTransform;
	}

	/**
	 * {@inheritDoc}
	 */
	public Point2D worldToScreen( double x, double y ) {
		return worldToScreen( new Point2D( x, y ) );
	}

	/**
	 * {@inheritDoc}
	 */
	public Point2D worldToScreen( Point2D point ) {
		return getWorldToScreenTransform().transform( point );
	}

	/**
	 * {@inheritDoc}
	 */
	public Point3D worldToScreen( double x, double y, double z ) {
		return worldToScreen( new Point3D( x, y, z ) );
	}

	/**
	 * {@inheritDoc}
	 */
	public Point3D worldToScreen( Point3D point ) {
		return getWorldToScreenTransform().transform( point );
	}

	/**
	 * {@inheritDoc}
	 */
	public Bounds worldToScreen( Bounds bounds ) {
		return getWorldToScreenTransform().transform( bounds );
	}

	final Pane layersPane() {
		return layers;
	}

	Bounds getVisualBounds( Node node ) {
		return node.getBoundsInParent();
	}

	//	private void updateGz( double unitScale, double dpiX, double dpiY, double outputScaleX, double outputScaleY ) {
	//		setShapeScaleX( unitScale * dpiX * outputScaleX );
	//		setShapeScaleY( unitScale * dpiY * outputScaleY );
	//	}

	private double getShapeScaleX() {
		return shapeScaleX.get();
	}

	//	private void setShapeScaleX( double shapeScaleX ) {
	//		this.shapeScaleX.set( shapeScaleX );
	//	}

	private DoubleProperty shapeScaleXProperty() {
		return shapeScaleX;
	}

	private double getShapeScaleY() {
		return shapeScaleY.get();
	}

	//	private void setShapeScaleY( double shapeScaleY ) {
	//		this.shapeScaleY.set( shapeScaleY );
	//	}

	private DoubleProperty shapeScaleYProperty() {
		return shapeScaleY;
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

	private void clearCachedTransforms() {
		// Clear the cached transforms
		screenToWorldTransform = null;
		worldToScreenTransform = null;
	}

	@Note( CommonNote.ANY_THREAD )
	void updateGridFxGeometry() {
		if( System.nanoTime() < nextGridUpdate ) return;
		if( workplane == null ) {
			grid.getChildren().clear();
		} else {
			Fx.onFxOrCurrent( () -> workplane.getGridSystem().updateFxGeometryGrid( workplane, getShapeScaleX(), grid.getChildren() ) );
		}
		nextGridUpdate = System.nanoTime() + DEFAULT_REFRESH_TIME_NANOS;
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

		double shapeScaleX = getShapeScaleX();
		double shapeScaleY = getShapeScaleY();

		fxShape = switch( designShape.getType() ) {
			case LINE -> updateLineGeometry( (DesignLine)designShape, shapeScaleX, shapeScaleY );
			case TEXT -> updateTextGeometry( (DesignText)designShape, shapeScaleX, shapeScaleY );
			default -> null;
		};

		if( fxShape == null ) {
			log.atWarn().log( "Unable to map design shape: %s", designShape );
			return null;
		}

		fxShape.setManaged( false );
		fxShape.setUserData( designShape );

		fxShape.setFill( designShape.calcFillPaint() );
		fxShape.setStroke( designShape.calcDrawPaint() );
		fxShape.setStrokeLineCap( designShape.calcDrawCap() );
		fxShape.setStrokeLineJoin( designShape.calcDrawJoin() );
		//fxShape.setStrokeType( designShape.calcDrawType() );

		return updateCommonShapeGeometry( designShape, fxShape, shapeScaleX, shapeScaleY );
	}

	private Shape updateLineGeometry( DesignLine designLine, double shapeScaleX, double shapeScaleY ) {
		WeakReference<Line> lineRef = designLine.getValue( FX_SHAPE );
		Line line = lineRef == null ? null : lineRef.get();
		if( line == null ) {
			line = new Line();
			designLine.setValue( FX_SHAPE, new WeakReference<>( line ) );
		}

		line.setStartX( designLine.getOrigin().getX() * shapeScaleX );
		line.setStartY( designLine.getOrigin().getY() * shapeScaleY );
		line.setEndX( designLine.getPoint().getX() * shapeScaleX );
		line.setEndY( designLine.getPoint().getY() * shapeScaleY );

		return line;
	}

	// TODO Finish building the update methods for the remaining design shapes

	private Shape updateTextGeometry( DesignText designText, double shapeScaleX, double shapeScaleY ) {
		WeakReference<Text> textRef = designText.getValue( FX_SHAPE );
		Text text = textRef == null ? null : textRef.get();
		if( text == null ) {
			text = new Text();
			designText.setValue( FX_SHAPE, new WeakReference<>( text ) );
		}

		double x = designText.getOrigin().getX() * shapeScaleX;
		double y = designText.getOrigin().getY() * shapeScaleY;

		text.setX( x );
		text.setY( -y );
		text.setText( designText.getText() );
		text.setFont( Font.font( designText.calcFontName(), designText.calcFontWeight(), designText.calcFontPosture(), designText.calcTextSize() * shapeScaleY ) );

		// Rotate must be before scale
		text.getTransforms().setAll( Transform.rotate( designText.calcRotate(), x, y ), Transform.scale( 1, -1 ) );

		return text;
	}

	/**
	 * Update the common geometry properties of the shape. This method is used to
	 * common shape properties that are dependent on the rendering scale.
	 *
	 * @param designShape The source design shape
	 * @param shape The target FX shape
	 * @param shapeScaleX The pre-calculated geometry scale factor for the X axis
	 * @param shapeScaleY The pre-calculated geometry scale factor for the Y axis
	 * @return The updated FX shape
	 */
	private Shape updateCommonShapeGeometry( DesignShape designShape, Shape shape, double shapeScaleX, double shapeScaleY ) {
		shape.setStrokeWidth( designShape.calcDrawWidth() * shapeScaleX );
		shape.setStrokeDashOffset( designShape.calcDashOffset() * shapeScaleX );
		shape.getStrokeDashArray().setAll( designShape.calcDashPattern().stream().map( d -> d * shapeScaleX ).toList() );
		//shape.setStrokeMiterLimit( designShape.calcDrawMiterLimit() * shapeScaleX );
		return shape;
	}

}
