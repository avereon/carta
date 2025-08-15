package com.avereon.cartesia.tool.design;

import com.avereon.annotation.CommonNote;
import com.avereon.annotation.Note;
import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.tool.Workplane;
import com.avereon.cartesia.tool.design.binding.DesignBinding;
import com.avereon.cartesia.tool.design.binding.DesignDoubleBinding;
import com.avereon.cartesia.tool.design.binding.PathElementMapper;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.*;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.mapstruct.factory.Mappers;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@CustomLog
public class DesignToolV3Renderer extends BaseDesignRenderer {

	public static final String FX_SHAPE = "fx-shape";

	private static PathElementMapper pathElementMapper = Mappers.getMapper( PathElementMapper.class );

	private Design design;

	private Workplane workplane;

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

	@Getter( AccessLevel.PACKAGE )
	private final DoubleProperty rendererCenterX;

	@Getter( AccessLevel.PACKAGE )
	private final DoubleProperty rendererCenterY;

	@Getter( AccessLevel.PACKAGE )
	private final Scale viewZoomTransform;

	@Getter( AccessLevel.PACKAGE )
	private final Rotate viewRotateTransform;

	@Getter( AccessLevel.PACKAGE )
	private final Translate viewCenterTransform;

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

	/**
	 * Create a new renderer. This class is intended to only be used by {@link
	 * DesignToolV3} and should not be instantiated directly otherwise except for
	 * testing purposes.
	 */
	DesignToolV3Renderer() {
		super();

		shapeScaleX = new SimpleDoubleProperty( 1.0 );
		shapeScaleY = new SimpleDoubleProperty( 1.0 );
		unitScale = new SimpleDoubleProperty( 1.0 );
		rendererCenterX = new SimpleDoubleProperty( 0.0 );
		rendererCenterY = new SimpleDoubleProperty( 0.0 );

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
		// shapeScale = unitScale * dpi * outputScale
		shapeScaleX.bind( unitScaleProperty().multiply( dpiXProperty() ).multiply( outputScaleXProperty() ) );
		shapeScaleY.bind( unitScaleProperty().multiply( dpiYProperty() ).multiply( outputScaleYProperty() ) );

		// Create and set the world transforms
		viewZoomTransform = new Scale( 1, -1 );
		viewRotateTransform = new Rotate( 0, 0, 0 );
		viewCenterTransform = new Translate( 0, 0 );
		world.getTransforms().setAll( viewZoomTransform, viewRotateTransform, viewCenterTransform );

		// Configure the renderer center definition. The renderer center maintains
		// the center point in the parent coordinate system regardless of the parent
		// size, view zoom or output scale. This is important when converting
		// between screen and world coordinates.
		rendererCenterX.bind( widthProperty().multiply( 0.5 ).multiply( outputScaleXProperty() ).divide( viewZoomXProperty() ) );
		rendererCenterY.bind( heightProperty().multiply( -0.5 ).multiply( outputScaleYProperty() ).divide( viewZoomYProperty() ) );

		// The rotation transform needs to include the rotation angle and the pivot
		// point. The pivot point is always in parent coordinates and is bound to
		// the renderer center.
		viewRotateTransform.angleProperty().bind( viewRotateProperty() );
		viewRotateTransform.pivotXProperty().bind( getRendererCenterX() );
		viewRotateTransform.pivotYProperty().bind( getRendererCenterY() );

		// The zoom transform does not include the DPI property because the geometry
		// values already include the DPI. What is interesting here is that we divide
		// out the output scale at the same time. This allows JavaFX to render the
		// geometry at the highest resolution, regardless of the output scale set by
		// the user. Someday this may need to be tied to a HiDPI setting, but we'll
		// leave it here to understand how the technique works.
		// viewZoomTransform = viewZoom / outputScale;
		viewZoomTransform.xProperty().bind( viewZoomXProperty().divide( outputScaleXProperty() ) );
		viewZoomTransform.yProperty().bind( viewZoomYProperty().divide( outputScaleYProperty() ).negate() );

		// The translate properties do not include the output scale property because
		// these are parent coordinates and not local coordinates, and the parent
		// transforms have already incorporated the output scale. The translate
		// properties also have to compensate for the scale acting at the center of
		// the pane and not at the origin.
		viewCenterTransform.xProperty().bind( getRendererCenterX().subtract( viewCenterXProperty().multiply( shapeScaleXProperty() ) ) );
		viewCenterTransform.yProperty().bind( getRendererCenterY().subtract( viewCenterYProperty().multiply( shapeScaleYProperty() ) ) );

		// FIXME Consider changing the grid geometry to bound properties
		// Update the design geometry when the global scale changes
		shapeScaleXProperty().addListener( ( _, _, _ ) -> this.updateGridFxGeometry() );
		shapeScaleYProperty().addListener( ( _, _, _ ) -> this.updateGridFxGeometry() );
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
	@Override
	public boolean isGridVisible() {
		return grid.isVisible();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
		// Return the list of design layers that currently have an FX pane in the renderer,
		// in the same order as they appear visually (top to bottom) in the layers pane.
		return this.layers.getChildren().stream().filter( p -> p instanceof Pane ).map( p -> (DesignLayer)p.getUserData() ).toList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisibleLayers( @NonNull Collection<DesignLayer> layers ) {
		// Convenience: show only the specified layers; hide all others currently visible
		if( this.design == null ) return;

		// Hide layers that are currently visible but not in the target collection
		for( Node node : List.copyOf( this.layers.getChildren() ) ) {
			if( !(node instanceof Pane pane) ) continue;
			Object userData = pane.getUserData();
			if( userData instanceof DesignLayer existing && !layers.contains( existing ) ) {
				setLayerVisible( existing, false );
			}
		}

		// Show any requested layers that are not already visible
		for( DesignLayer layer : layers ) {
			if( !isLayerVisible( layer ) ) setLayerVisible( layer, true );
		}
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
	@Override
	public Transform getScreenToWorldTransform() {
		try {
			return getWorldToScreenTransform().createInverse();
		} catch( NonInvertibleTransformException exception ) {
			// This should never happen since the world-to-screen transform should always be invertible
			throw new RuntimeException( exception );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point2D screenToWorld( double x, double y ) {
		return screenToWorld( new Point2D( x, y ) );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point2D screenToWorld( Point2D point ) {
		return getScreenToWorldTransform().transform( point );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point3D screenToWorld( double x, double y, double z ) {
		return screenToWorld( new Point3D( x, y, z ) );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point3D screenToWorld( Point3D point ) {
		return getScreenToWorldTransform().transform( point );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bounds screenToWorld( Bounds bounds ) {
		return getScreenToWorldTransform().transform( bounds );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Transform getWorldToScreenTransform() {
		return world.getLocalToParentTransform().createConcatenation( Transform.scale( getShapeScaleX(), getShapeScaleY() ) );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point2D worldToScreen( double x, double y ) {
		return worldToScreen( new Point2D( x, y ) );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point2D worldToScreen( Point2D point ) {
		return getWorldToScreenTransform().transform( point );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point3D worldToScreen( double x, double y, double z ) {
		return worldToScreen( new Point3D( x, y, z ) );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point3D worldToScreen( Point3D point ) {
		return getWorldToScreenTransform().transform( point );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bounds worldToScreen( Bounds bounds ) {
		return getWorldToScreenTransform().transform( bounds );
	}

	final Pane layersPane() {
		return layers;
	}

	Bounds getVisualBounds( Node node ) {
		return node.getBoundsInParent();
	}

	double getShapeScaleX() {
		return shapeScaleX.get();
	}

	private DoubleProperty shapeScaleXProperty() {
		return shapeScaleX;
	}

	double getShapeScaleY() {
		return shapeScaleY.get();
	}

	private DoubleProperty shapeScaleYProperty() {
		return shapeScaleY;
	}

	double getUnitScale() {
		return unitScale.get();
	}

	void setUnitScale( double unitScale ) {
		this.unitScale.set( unitScale );
	}

	private DoubleProperty unitScaleProperty() {
		return unitScale;
	}

	void setDesignUnit( DesignUnit unit ) {
		setUnitScale( unit.to( 1, DesignUnit.IN ) );
	}

	@Note( CommonNote.ANY_THREAD )
	void updateGridFxGeometry() {
		if( System.nanoTime() < nextGridUpdate ) return;

		Fx.onFxOrCurrent( () -> {
			if( workplane == null ) {
				grid.getChildren().clear();
			} else {
				workplane.getGridSystem().updateFxGeometryGrid( workplane, getShapeScaleX(), grid.getChildren() );
			}
		} );

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
		List<DesignLayer> designLayers = new ArrayList<>( design.getLayers().getAllLayers() );
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

	private Shape mapDesignShape( DesignShape designShape ) {
		return mapDesignShape( designShape, false );
	}

	private Shape mapDesignShape( DesignShape designShape, boolean forceUpdate ) {
		WeakReference<Shape> shapeRef = designShape.getValue( FX_SHAPE );
		Shape fxShape = shapeRef == null ? null : shapeRef.get();
		if( !forceUpdate && fxShape != null ) return fxShape;

		fxShape = switch( designShape.getType() ) {
			case ARC -> bindArcGeometry( (DesignArc)designShape );
			case BOX -> bindBoxGeometry( (DesignBox)designShape );
			case CUBIC -> bindCubicGeometry( (DesignCubic)designShape );
			case ELLIPSE -> bindEllipseGeometry( (DesignEllipse)designShape );
			case LINE -> bindLineGeometry( (DesignLine)designShape );
			case MARKER -> bindMarkerGeometry( (DesignMarker)designShape );
			case PATH -> bindPathGeometry( (DesignPath)designShape );
			case QUAD -> bindQuadGeometry( (DesignQuad)designShape );
			case TEXT -> bindTextGeometry( (DesignText)designShape );
		};

		if( fxShape == null ) {
			log.atWarn().log( "Unable to map design shape: %s", designShape );
			return null;
		}

		fxShape.setManaged( false );
		fxShape.setUserData( designShape );

		return fxShape;
	}

	// TODO Finish building the bind methods for the remaining design shapes

	private Arc bindArcGeometry( DesignArc designArc ) {
		return null;
	}

	private Rectangle bindBoxGeometry( DesignBox designBox ) {
		return null;
	}

	private CubicCurve bindCubicGeometry( DesignCubic designCubic ) {
		return null;
	}

	private Ellipse bindEllipseGeometry( DesignEllipse designEllipse ) {
		return null;
	}

	private Line bindLineGeometry( DesignLine designLine ) {
		WeakReference<Line> reference = designLine.getValue( FX_SHAPE );
		Line line = reference == null ? null : reference.get();
		if( line == null ) {
			line = new Line();
			designLine.setValue( FX_SHAPE, new WeakReference<>( line ) );

			bindCommonShapeGeometry( designLine, line );

			DesignDoubleBinding startXProperty = new DesignDoubleBinding( designLine, DesignLine.ORIGIN, v -> v.getOrigin().getX() );
			DesignDoubleBinding startYProperty = new DesignDoubleBinding( designLine, DesignLine.ORIGIN, v -> v.getOrigin().getY() );
			DesignDoubleBinding pointXProperty = new DesignDoubleBinding( designLine, DesignLine.POINT, v -> v.getPoint().getX() );
			DesignDoubleBinding pointYProperty = new DesignDoubleBinding( designLine, DesignLine.POINT, v -> v.getPoint().getY() );

			line.startXProperty().bind( shapeScaleXProperty().multiply( startXProperty ) );
			line.startYProperty().bind( shapeScaleYProperty().multiply( startYProperty ) );
			line.endXProperty().bind( shapeScaleXProperty().multiply( pointXProperty ) );
			line.endYProperty().bind( shapeScaleYProperty().multiply( pointYProperty ) );
		}

		return line;
	}

	private Path bindMarkerGeometry( DesignMarker designMarker ) {
		Path path = new Path();
		designMarker.setValue( FX_SHAPE, new WeakReference<>( path ) );

		bindCommonShapeGeometry( designMarker, path );
		path.setFillRule( FillRule.EVEN_ODD );

		// Bind on steps and update the path geometry
		DesignBinding<List<DesignPath.Step>> stepsBinding = new DesignBinding<>( designMarker, DesignPath.STEPS, DesignMarker::getSteps );
		ObjectBinding<List<PathElement>> elementsBinding = Bindings.createObjectBinding(
			() -> {
				List<PathElement> elements = new ArrayList<>();
				double shapeScaleX = shapeScaleXProperty().get();
				double shapeScaleY = shapeScaleYProperty().get();
				for( DesignPath.Step step : stepsBinding.get() ) {
					elements.add( pathElementMapper.map( step, shapeScaleX, shapeScaleY ) );
				}
				return elements;
			}, stepsBinding
		);
		path.getElements().setAll( elementsBinding.get() );
		elementsBinding.addListener( ( _, _, n ) -> path.getElements().setAll( n ) );

		return path;
	}

	private Path bindPathGeometry( DesignPath designPath ) {
		Path path = new Path();
		designPath.setValue( FX_SHAPE, new WeakReference<>( path ) );

		bindCommonShapeGeometry( designPath, path );

		// Bind on steps and update the path geometry
		DesignBinding<List<DesignPath.Step>> stepsBinding = new DesignBinding<>( designPath, DesignPath.STEPS, DesignPath::getSteps );
		ObjectBinding<List<PathElement>> elementsBinding = Bindings.createObjectBinding(
			() -> {
				List<PathElement> elements = new ArrayList<>();
				double shapeScaleX = shapeScaleXProperty().get();
				double shapeScaleY = shapeScaleYProperty().get();
				for( DesignPath.Step step : stepsBinding.get() ) {
					elements.add( pathElementMapper.map( step, shapeScaleX, shapeScaleY ) );
				}
				return elements;
			}, stepsBinding
		);
		path.getElements().setAll( elementsBinding.get() );
		elementsBinding.addListener( ( _, _, n ) -> path.getElements().setAll( n ) );

		return path;
	}

	private QuadCurve bindQuadGeometry( DesignQuad designQuad ) {
		WeakReference<QuadCurve> reference = designQuad.getValue( FX_SHAPE );
		QuadCurve quad = reference == null ? null : reference.get();
		if( quad == null ) {
			quad = new QuadCurve();
			designQuad.setValue( FX_SHAPE, new WeakReference<>( quad ) );

			bindCommonShapeGeometry( designQuad, quad );

			DesignDoubleBinding startXProperty = new DesignDoubleBinding( designQuad, DesignQuad.ORIGIN, v -> v.getOrigin().getX() );
			DesignDoubleBinding startYProperty = new DesignDoubleBinding( designQuad, DesignQuad.ORIGIN, v -> v.getOrigin().getY() );
			DesignDoubleBinding controlXProperty = new DesignDoubleBinding( designQuad, DesignQuad.CONTROL, v -> v.getControl().getX() );
			DesignDoubleBinding controlYProperty = new DesignDoubleBinding( designQuad, DesignQuad.CONTROL, v -> v.getControl().getY() );
			DesignDoubleBinding pointXProperty = new DesignDoubleBinding( designQuad, DesignQuad.POINT, v -> v.getPoint().getX() );
			DesignDoubleBinding pointYProperty = new DesignDoubleBinding( designQuad, DesignQuad.POINT, v -> v.getPoint().getY() );

			quad.startXProperty().bind( shapeScaleXProperty().multiply( startXProperty ) );
			quad.startYProperty().bind( shapeScaleYProperty().multiply( startYProperty ) );
			quad.controlXProperty().bind( shapeScaleXProperty().multiply( controlXProperty ) );
			quad.controlYProperty().bind( shapeScaleYProperty().multiply( controlYProperty ) );
			quad.endXProperty().bind( shapeScaleXProperty().multiply( pointXProperty ) );
			quad.endYProperty().bind( shapeScaleYProperty().multiply( pointYProperty ) );
		}

		return quad;
	}

	// Eventually this should only have to be called once per design shape
	private Text bindTextGeometry( DesignText designText ) {
		WeakReference<Text> reference = designText.getValue( FX_SHAPE );
		Text text = reference == null ? null : reference.get();
		if( text == null ) {
			text = new Text();
			designText.setValue( FX_SHAPE, new WeakReference<>( text ) );

			bindCommonShapeGeometry( designText, text );

			DesignDoubleBinding originXProperty = new DesignDoubleBinding( designText, DesignText.ORIGIN, v -> v.getOrigin().getX() );
			DesignDoubleBinding originYProperty = new DesignDoubleBinding( designText, DesignText.ORIGIN, v -> v.getOrigin().getY() );
			DesignDoubleBinding rotateProperty = new DesignDoubleBinding( designText, DesignText.ORIGIN, DesignShape::calcRotate );
			DesignBinding<String> textProperty = new DesignBinding<>( designText, DesignText.TEXT, DesignText::getText );
			DesignBinding<String> fontNameProperty = new DesignBinding<>( designText, DesignText.FONT_NAME, DesignText::getFontName );
			DesignBinding<FontWeight> fontWeightProperty = new DesignBinding<>( designText, DesignText.FONT_WEIGHT, DesignText::calcFontWeight );
			DesignBinding<FontPosture> fontPostureProperty = new DesignBinding<>( designText, DesignText.FONT_POSTURE, DesignText::calcFontPosture );
			DesignDoubleBinding textSizeProperty = new DesignDoubleBinding( designText, DesignText.TEXT_SIZE, DesignText::calcTextSize );

			text.textProperty().bind( textProperty );

			text.xProperty().bind( shapeScaleXProperty().multiply( originXProperty ) );
			text.yProperty().bind( shapeScaleYProperty().multiply( originYProperty ).negate() );

			text.fontProperty().bind( Bindings.createObjectBinding(
				() -> Font.font( fontNameProperty.get(), designText.calcFontWeight(), designText.calcFontPosture(), textSizeProperty.get() * shapeScaleYProperty().get() ),
				fontNameProperty,
				fontWeightProperty,
				fontPostureProperty,
				textSizeProperty,
				shapeScaleYProperty()
			) );

			Rotate rotate = new Rotate();
			rotate.angleProperty().bind( rotateProperty );
			rotate.pivotXProperty().bind( shapeScaleXProperty().multiply( originXProperty ) );
			rotate.pivotYProperty().bind( shapeScaleYProperty().multiply( originYProperty ) );

			// Rotate must be before scale
			text.getTransforms().setAll( rotate, Transform.scale( 1, -1 ) );
		}

		return text;
	}

	/**
	 * Bind the common geometry properties of the shape. This method is used to
	 * bind the common shape properties to their dependent properties, whether
	 * they be FX properties or design properties.
	 *
	 * @param designShape The source design shape
	 * @param shape The target FX shape
	 */
	private void bindCommonShapeGeometry( DesignShape designShape, Shape shape ) {
		shape.fillProperty().bind( new DesignBinding<>( designShape, DesignShape.FILL_PAINT, DesignShape::calcFillPaint ) );
		shape.strokeProperty().bind( new DesignBinding<>( designShape, DesignShape.DRAW_PAINT, DesignShape::calcDrawPaint ) );
		shape.strokeWidthProperty().bind( shapeScaleXProperty().multiply( new DesignDoubleBinding( designShape, DesignShape.DRAW_WIDTH, DesignShape::calcDrawWidth ) ) );
		shape.strokeLineCapProperty().bind( new DesignBinding<>( designShape, DesignShape.DRAW_CAP, DesignShape::calcDrawCap ) );
		shape.strokeLineJoinProperty().bind( new DesignBinding<>( designShape, DesignShape.DRAW_JOIN, DesignShape::calcDrawJoin ) );
		//shape.strokeTypeProperty().bind( new DesignBinding<>( designShape, DesignShape.DRAW_TYPE, DesignShape::calcDrawType ) );
		//shape.strokeMiterLimitProperty().bind( shapeScaleXProperty().multiply( new DesignDoubleBinding( designShape, DesignShape.DRAW_MITER_LIMIT, DesignShape::calcDrawMiterLimit ) ) );

		// Dash offset
		shape.strokeDashOffsetProperty().bind( shapeScaleXProperty().multiply( new DesignDoubleBinding( designShape, DesignShape.DASH_OFFSET, DesignShape::calcDashOffset ) ) );
		// Dash pattern
		DesignBinding<List<Double>> patternBinding = new DesignBinding<>( designShape, DesignShape.DASH_PATTERN, DesignShape::calcDashPattern );
		ObjectBinding<List<Double>> dashBinding = Bindings.createObjectBinding(
			() -> patternBinding.get().stream().map( d -> d * shapeScaleXProperty().get() ).toList(),
			shapeScaleXProperty(),
			patternBinding
		);
		shape.getStrokeDashArray().setAll( dashBinding.get() );
		dashBinding.addListener( ( _, _, n ) -> shape.getStrokeDashArray().setAll( n ) );
	}

}
