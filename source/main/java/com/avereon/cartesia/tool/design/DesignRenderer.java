package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.cartesia.tool.DesignContext;
import com.avereon.cartesia.tool.DesignWorkplane;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.marea.Font;
import com.avereon.marea.LineCap;
import com.avereon.marea.LineJoin;
import com.avereon.marea.RenderUnit;
import com.avereon.marea.fx.FxRenderer2d;
import com.avereon.marea.geom.Path;
import com.avereon.util.ThreadUtil;
import com.avereon.zarra.color.Colors;
import com.avereon.zarra.javafx.Fx;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;
import lombok.CustomLog;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CustomLog
public class DesignRenderer extends Pane {

	private Design design;

	@Getter
	private DesignWorkplane workplane;

	private final FxRenderer2d renderer;

	private final SimpleObjectProperty<DesignShape> selectAperture;

	private final ObservableList<DesignLayer> enabledLayers;

	private final ObservableList<DesignLayer> visibleLayers;

	@Getter
	private final DesignLayer previewLayer;

	@Getter
	private final DesignLayer referenceLayer;

	// Properties ----------------------------------------------------------------

	private SimpleBooleanProperty gridVisible;

	private SimpleBooleanProperty referencePointsVisible;

	private SimpleBooleanProperty constructionPointsVisible;

	private final SimpleStringProperty apertureDrawPaint;

	private final SimpleStringProperty apertureFillPaint;

	private final SimpleStringProperty selectedDrawPaint;

	private final SimpleStringProperty selectedFillPaint;

	private final SimpleStringProperty previewDrawPaint;

	private final SimpleStringProperty previewFillPaint;

	private final SimpleStringProperty referenceDrawPaint;

	private final SimpleStringProperty referenceFillPaint;

	// Cached values

	private Paint cachedSelectedFillPaint;

	private Paint cachedSelectedDrawPaint;

	// Listeners -----------------------------------------------------------------

	private final EventHandler<NodeEvent> designWatcher = e -> {
		render();
	};

	private final EventHandler<NodeEvent> unitValueWatcher = e -> setLengthUnit( e.getNewValue() );

	public DesignRenderer() {
		// Ensure the minimum layout size can go to zero
		// This fixes a problem where the parentToLocal and localToParent methods
		// did not work correctly.
		setMinSize( 0, 0 );

		selectAperture = new SimpleObjectProperty<>();
		apertureDrawPaint = new SimpleStringProperty( Colors.toString( Colors.translucent( Color.YELLOW, 0.8 ) ) );
		apertureFillPaint = new SimpleStringProperty( Colors.toString( Colors.translucent( Color.YELLOW, 0.2 ) ) );
		selectedDrawPaint = new SimpleStringProperty( Colors.toString( Colors.translucent( Color.MAGENTA, 0.8 ) ) );
		selectedFillPaint = new SimpleStringProperty( Colors.toString( Colors.translucent( Color.MAGENTA, 0.2 ) ) );

		enabledLayers = FXCollections.observableArrayList();
		visibleLayers = FXCollections.observableArrayList();

		previewDrawPaint = new SimpleStringProperty( Colors.toString( Colors.translucent( Color.MAGENTA, 0.8 ) ) );
		previewFillPaint = new SimpleStringProperty( Colors.toString( Colors.translucent( Color.MAGENTA, 0.2 ) ) );
		previewLayer = new DesignLayer();
		previewLayer.setDrawPaint( getPreviewDrawPaint() );
		previewLayer.setFillPaint( getPreviewFillPaint() );

		referenceDrawPaint = new SimpleStringProperty( Colors.toString( Colors.translucent( Color.MAGENTA, 0.8 ) ) );
		referenceFillPaint = new SimpleStringProperty( Colors.toString( Colors.translucent( Color.MAGENTA, 0.2 ) ) );
		referenceLayer = new DesignLayer();
		referenceLayer.setDrawPaint( getReferenceDrawPaint() );
		referenceLayer.setFillPaint( getReferenceFillPaint() );

		workplane = new DesignWorkplane();

		// Create and add the renderer to the center
		renderer = new FxRenderer2d();
		getChildren().add( renderer );

		// Disable the default renderer mouse actions
		renderer.setOnMousePressed( null );
		renderer.setOnMouseDragged( null );
		renderer.setOnMouseReleased( null );
		renderer.setOnScroll( null );

		// Bind the underlying renderer width and height
		renderer.widthProperty().bind( this.widthProperty() );
		renderer.heightProperty().bind( this.heightProperty() );

		// TEMPORARY
		// Add listeners for properties that should update the render
		renderer.zoomXProperty().addListener( ( p, o, n ) -> render() );
		renderer.zoomYProperty().addListener( ( p, o, n ) -> render() );
		renderer.viewpointXProperty().addListener( ( p, o, n ) -> render() );
		renderer.viewpointYProperty().addListener( ( p, o, n ) -> render() );
		renderer.viewRotateProperty().addListener( ( p, o, n ) -> render() );
		renderer.widthProperty().addListener( ( p, o, n ) -> render() );
		renderer.heightProperty().addListener( ( p, o, n ) -> render() );

		selectAperture.addListener( (ChangeListener<? super DesignShape>)( p, o, n ) -> render() );
		visibleLayers.addListener( (ListChangeListener<? super DesignLayer>)( c ) -> render() );
		enabledLayers.addListener( (ListChangeListener<? super DesignLayer>)( c ) -> render() );

		previewLayer.register( NodeEvent.ANY, e -> render() );
		referenceLayer.register( NodeEvent.ANY, e -> render() );
	}

	public void setDesign( Design design ) {
		if( this.design != null ) {
			this.design.unregister( NodeEvent.ANY, designWatcher );
			this.design.unregister( Design.UNIT, unitValueWatcher );
		}

		this.design = design;

		if( this.design != null ) {
			// Configure the rendering unit
			renderer.setLengthUnit( RenderUnit.valueOf( design.getDesignUnit().toUpperCase() ) );

			// Add listeners
			this.design.register( Design.UNIT, unitValueWatcher );
			this.design.register( NodeEvent.ANY, designWatcher );

			visibleLayers.addAll( design.getAllLayers() );
		}
	}

	public DesignContext getDesignContext() {
		return design.getDesignContext();
	}

	public void setWorkplane( DesignWorkplane workplane ) {
		// TODO Disconnect listeners

		this.workplane = workplane;

		// TODO Connect listeners

		// Temporary listener for testing
		workplane.register( NodeEvent.VALUE_CHANGED, e -> {
			//log.atConfig().log( "Something changed in the workplane: " + e.getKey() );
			render();
		} );
	}

	/**
	 * Get the DPI for the renderer. This method returns the DPI for the X and Y axes.
	 *
	 * @return The DPI for the renderer
	 */
	public Point2D getDpi() {
		return new Point2D( renderer.getDpiX(), renderer.getDpiY() );
	}

	public double getDpiX() {
		return renderer.getDpiX();
	}

	public double getDpiY() {
		return renderer.getDpiY();
	}

	/**
	 * Set the DPI for the renderer. This method sets the DPI for both the X and Y
	 * axes to the same value.
	 *
	 * @param dpi The DPI to set
	 */
	public void setDpi( double dpi ) {
		setDpi( dpi, dpi );
	}

	/**
	 * Set the DPI for the renderer. This method sets the DPI for both the X and Y
	 * axes.
	 *
	 * @param dpiX The DPI to set for the X axis
	 * @param dpiY The DPI to set for the Y axis
	 */
	public void setDpi( double dpiX, double dpiY ) {
		renderer.setDpi( dpiX, dpiY );
	}

	public void setDpi( Point2D dpi ) {
		renderer.setDpi( dpi.getX(), dpi.getY() );
	}

	public boolean isGridVisible() {
		return gridVisible != null && gridVisible().get();
	}

	public void setGridVisible( boolean visible ) {
		gridVisible().set( visible );
		render();
	}

	public SimpleBooleanProperty gridVisible() {
		if( gridVisible == null ) gridVisible = new SimpleBooleanProperty( false );
		return gridVisible;
	}

	// Visible Layers ------------------------------------------------------------

	public boolean isLayerVisible( DesignLayer layer ) {
		return visibleLayers.contains( layer );
	}

	public List<DesignLayer> getVisibleLayers() {
		return visibleLayers;
	}

	public void setVisibleLayers( Collection<DesignLayer> layers ) {
		visibleLayers.clear();
		visibleLayers.addAll( layers );
		render();
	}

	public ObservableList<DesignLayer> visibleLayers() {
		return visibleLayers;
	}

	// Enabled Layers ------------------------------------------------------------

	public boolean isLayerEnabled( DesignLayer layer ) {
		return enabledLayers.contains( layer );
	}

	/**
	 * Get a list of the enabled layers. The list is ordered the same as the layers in the design.
	 *
	 * @return A list of the enabled layers
	 */
	public List<DesignLayer> getEnabledLayers() {
		return enabledLayers;
	}

	/**
	 * Set the enabled layers. The list of layers is ordered the same as the layers in the design.
	 *
	 * @param layers The list of enabled layers
	 */
	public void setEnabledLayers( Collection<DesignLayer> layers ) {
		enabledLayers.clear();
		enabledLayers.addAll( layers );
		render();
	}

	/**
	 * Get the enabled layers property.
	 *
	 * @return The enabled layers property
	 */
	public ObservableList<DesignLayer> enabledLayers() {
		return enabledLayers;
	}

	// Visible Shapes ------------------------------------------------------------

	public List<DesignShape> getVisibleShapes() {
		return getVisibleLayers().stream().flatMap( l -> l.getShapeSet().stream() ).collect( Collectors.toList() );
	}

	// Select Aperture -----------------------------------------------------------

	public void setSelectAperture( DesignShape aperture ) {
		if( aperture != null ) {
			if( aperture instanceof DesignEllipse ) {
				aperture.setDrawPaint( "#00000000" );
			} else if( aperture instanceof DesignBox ) {
				aperture.setDrawPaint( getApertureDrawPaint() );
			}
			aperture.setFillPaint( getApertureFillPaint() );
		}
		selectAperture.set( aperture );
	}

	public DesignShape getSelectAperture() {
		return selectAperture.get();
	}

	public SimpleObjectProperty<DesignShape> selectAperture() {
		return selectAperture;
	}

	public Paint calcApertureDrawPaint() {
		// TODO Cache this value for rendering performance
		return Colors.parse( getApertureDrawPaint() );
	}

	public String getApertureDrawPaint() {
		return apertureDrawPaint.get();
	}

	public void setApertureDrawPaint( String paint ) {
		apertureDrawPaint.set( paint );
		if( selectAperture.get() != null ) selectAperture.get().setDrawPaint( paint );
	}

	public SimpleStringProperty apertureDrawPaint() {
		return apertureDrawPaint;
	}

	public Paint calcApertureFillPaint() {
		// TODO Cache this value for rendering performance
		return Colors.parse( getApertureFillPaint() );
	}

	public String getApertureFillPaint() {
		return apertureFillPaint.get();
	}

	public void setApertureFillPaint( String paint ) {
		apertureFillPaint.set( paint );
		if( selectAperture.get() != null ) selectAperture.get().setFillPaint( paint );
	}

	public SimpleStringProperty apertureFillPaint() {
		return apertureFillPaint;
	}

	// Selected Paints -----------------------------------------------------------

	public Paint calcSelectedDrawPaint() {
		if( cachedSelectedDrawPaint == null ) cachedSelectedDrawPaint = Colors.parse( getSelectedDrawPaint() );
		return cachedSelectedDrawPaint;
	}

	public String getSelectedDrawPaint() {
		return selectedDrawPaint.get();
	}

	public void setSelectedDrawPaint( String paint ) {
		selectedDrawPaint.set( paint );
		cachedSelectedDrawPaint = null;
	}

	public SimpleStringProperty selectedDrawPaint() {
		return selectedDrawPaint;
	}

	public Paint calcSelectedFillPaint() {
		if( cachedSelectedFillPaint == null ) cachedSelectedFillPaint = Colors.parse( getSelectedFillPaint() );
		return cachedSelectedFillPaint;
	}

	public String getSelectedFillPaint() {
		return selectedFillPaint.get();
	}

	public void setSelectedFillPaint( String paint ) {
		selectedFillPaint.set( paint );
		cachedSelectedFillPaint = null;
	}

	public SimpleStringProperty selectedFillPaint() {
		return selectedFillPaint;
	}

	// Preview Paints -----------------------------------------------------------

	public Paint calcPreviewDrawPaint() {
		// TODO Cache this value for rendering performance
		return Colors.parse( getPreviewDrawPaint() );
	}

	public String getPreviewDrawPaint() {
		return previewDrawPaint.get();
	}

	public void setPreviewDrawPaint( String paint ) {
		previewDrawPaint.set( paint );
	}

	public SimpleStringProperty previewDrawPaint() {
		return previewDrawPaint;
	}

	public Paint calcPreviewFillPaint() {
		// TODO Cache this value for rendering performance
		return Colors.parse( getPreviewFillPaint() );
	}

	public String getPreviewFillPaint() {
		return previewFillPaint.get();
	}

	public void setPreviewFillPaint( String paint ) {
		previewFillPaint.set( paint );
	}

	public SimpleStringProperty previewFillPaint() {
		return previewFillPaint;
	}

	// Reference Paints ----------------------------------------------------------

	public Paint calcReferenceDrawPaint() {
		// TODO Cache this value for rendering performance
		return Colors.parse( getReferenceDrawPaint() );
	}

	public String getReferenceDrawPaint() {
		return referenceDrawPaint.get();
	}

	public void setReferenceDrawPaint( String paint ) {
		referenceDrawPaint.set( paint );
	}

	public SimpleStringProperty referenceDrawPaint() {
		return referenceDrawPaint;
	}

	public Paint calcReferenceFillPaint() {
		// TODO Cache this value for rendering performance
		return Colors.parse( getReferenceFillPaint() );
	}

	public String getReferenceFillPaint() {
		return referenceFillPaint.get();
	}

	public void setReferenceFillPaint( String paint ) {
		referenceFillPaint.set( paint );
	}

	public SimpleStringProperty referenceFillPaint() {
		return referenceFillPaint;
	}

	// Other ---------------------------------------------------------------------

	/**
	 * Convenience method to get the viewpoint of the renderer.
	 *
	 * @return The viewpoint of the renderer
	 */
	public Point2D getViewpoint() {
		return new Point2D( renderer.getViewpointX(), renderer.getViewpointY() );
	}

	/**
	 * Convenience method to set the viewpoint of the renderer.
	 *
	 * @param viewpoint The viewpoint to set
	 */
	public void setViewpoint( Point3D viewpoint ) {
		renderer.setViewpoint( viewpoint.getX(), viewpoint.getY() );
	}

	public DoubleProperty viewpointXProperty() {
		return renderer.viewpointXProperty();
	}

	public DoubleProperty viewpointYProperty() {
		return renderer.viewpointYProperty();
	}

	/**
	 * Convenience method to get the view rotate of the renderer.
	 *
	 * @return The view rotate of the renderer
	 */
	public double getViewRotate() {
		return renderer.getViewRotate();
	}

	/**
	 * Convenience method to set the view rotate of the renderer.
	 *
	 * @param angle The angle to set
	 */
	public void setViewRotate( double angle ) {
		renderer.setViewRotate( angle );
	}

	public DoubleProperty viewRotateProperty() {
		return renderer.viewRotateProperty();
	}

	/**
	 * Convenience method to get the zoom of the renderer.
	 *
	 * @return The zoom of the renderer
	 */
	public Point2D getZoom() {
		return new Point2D( renderer.getZoomX(), renderer.getZoomY() );
	}

	/**
	 * Convenience method to set the zoom of the renderer.
	 *
	 * @param zoomX The zoom to set for the X axis
	 * @param zoomY The zoom to set for the Y axis
	 */
	public void setZoom( double zoomX, double zoomY ) {
		renderer.setZoom( zoomX, zoomY );
	}

	public double getZoomX() {
		return renderer.getZoomX();
	}

	public double getZoomY() {
		return renderer.getZoomY();
	}

	public DoubleProperty zoomXProperty() {
		return renderer.zoomXProperty();
	}

	public DoubleProperty zoomYProperty() {
		return renderer.zoomYProperty();
	}

	/**
	 * Change the zoom by the zoom factor. The zoom is centered on the provided
	 * anchor point in world coordinates. The current zoom is multiplied by the
	 * zoom factor.
	 *
	 * @param anchor The anchor point in world coordinates
	 * @param factor The zoom factor
	 */
	public void zoom( Point3D anchor, double factor ) {
		Point3D offset = renderer.getViewpoint().subtract( anchor );

		// The zoom has to be set before the viewpoint
		renderer.setZoom( renderer.getZoom().multiply( factor ) );
		renderer.setViewpoint( anchor.add( offset.multiply( 1 / factor ) ) );
	}

	/**
	 * Change the view point due to mouse movement.
	 *
	 * @param viewpointAnchor The view point location before being dragged (world)
	 * @param dragAnchor The point where the mouse was pressed (screen)
	 * @param dragTarget The new view point (screen)
	 */
	@Deprecated
	public void pan( Point3D viewpointAnchor, Point3D dragAnchor, Point3D dragTarget ) {
		// Convert the view anchor to screen coordinates
		Point3D anchor = renderer.localToParent( viewpointAnchor );

		// Calculate the drag offset in screen coordinates
		Point3D delta = dragAnchor.subtract( dragTarget );

		// Set the new viewpoint in world coordinates
		renderer.setViewpoint( renderer.parentToLocal( anchor.add( delta ) ) );
	}

	/**
	 * Request that geometry be rendered. This method will collapse multiple
	 * sequential render requests to improve performance. This method is safe to
	 * call from any thread.
	 */
	public void render() {
		Fx.run( new RenderTrigger() );
	}

	public Transform getWorldToScreenTransform() {
		return renderer.getWorldToScreenTransform();
	}

	@Override
	public Point2D localToParent( double localX, double localY ) {
		return renderer.localToParent( localX, localY );
	}

	@Override
	public Point2D localToParent( Point2D localPoint ) {
		return renderer.localToParent( localPoint );
	}

	@Override
	public Point3D localToParent( double localX, double localY, double localZ ) {
		return renderer.localToParent( localX, localY, localZ );
	}

	@Override
	public Point3D localToParent( Point3D localPoint ) {
		return renderer.localToParent( localPoint );
	}

	@Override
	public Bounds localToParent( Bounds localBounds ) {
		return renderer.localToParent( localBounds );
	}

	public Transform getScreenToWorldTransform() {
		return renderer.getScreenToWorldTransform();
	}

	@Override
	public Point2D parentToLocal( double parentX, double parentY ) {
		return renderer.parentToLocal( parentX, parentY );
	}

	@Override
	public Point2D parentToLocal( Point2D parentPoint ) {
		return renderer.parentToLocal( parentPoint );
	}

	@Override
	public Point3D parentToLocal( double parentX, double parentY, double parentZ ) {
		return renderer.parentToLocal( parentX, parentY, parentZ );
	}

	@Override
	public Point3D parentToLocal( Point3D parentPoint ) {
		return renderer.parentToLocal( parentPoint );
	}

	@Override
	public Bounds parentToLocal( Bounds parentBounds ) {
		return renderer.parentToLocal( parentBounds );
	}

	public List<DesignShape> worldPointFind( Point3D anchor, DesignValue tolerance ) {
		return worldPointFind( anchor, realToWorld( tolerance ) );
	}

	public List<DesignShape> worldPointFind( Point3D anchor, double radius ) {
		return worldPointFind( anchor, new Point3D( radius, radius, 0 ) );
	}

	public List<DesignShape> worldPointFind( Point3D anchor, Point3D radii ) {
		DesignEllipse selector = new DesignEllipse( anchor, radii );
		return doFindByShape( selector, true );
	}

	/**
	 * Find the nodes contained by, or intersecting, the window specified by points a and b.
	 *
	 * @param a One corner of the window
	 * @param b The other corner of the window
	 * @param intersect True to select shapes by intersection
	 * @return The set of discovered nodes
	 */
	public List<DesignShape> worldWindowFind( Point3D a, Point3D b, boolean intersect ) {
		double x = Math.min( a.getX(), b.getX() );
		double y = Math.min( a.getY(), b.getY() );
		double w = Math.abs( a.getX() - b.getX() );
		double h = Math.abs( a.getY() - b.getY() );

		DesignBox selector = new DesignBox( x, y, w, h );
		return doFindByShape( selector, intersect );
	}

	double realToWorld( DesignValue value ) {
		// Convert the provided value to design units and divide by the zoom factor
		return value.to( design.calcDesignUnit() ).getValue() / getZoomX();
	}

	double realToScreen( DesignValue value ) {
		return value.to( DesignUnit.INCH ).getValue() * getDpiX();
	}

	// Rendering -----------------------------------------------------------------

	public void printRender( double factor ) {
		renderer.widthProperty().unbind();
		renderer.heightProperty().unbind();

		double zoomX = renderer.getZoomX();
		double zoomY = renderer.getZoomY();

		renderer.setWidth( factor * this.getPrefWidth() );
		renderer.setHeight( factor * this.getPrefHeight() );
		renderer.setTranslateX( -0.5 * (factor - 1) * this.getPrefWidth() );
		renderer.setTranslateY( -0.5 * (factor - 1) * this.getPrefHeight() );

		renderer.setScaleX( 1.0 / factor );
		renderer.setScaleY( 1.0 / factor );
		renderer.setZoomX( zoomX );
		renderer.setZoomY( zoomY );

		log.atConfig().log( "Print size: " + renderer.getWidth() + "x" + renderer.getHeight() );

		long startNs = System.nanoTime();
		doRender();
		ThreadUtil.pause( 1000 );
		long endNs = System.nanoTime();
		long duration = (long)(0.000001 * (endNs - startNs));
		log.atWarn().log( "Print render time: {0} ms", duration );
	}

	/**
	 * Should only be called on the FX application thread from the
	 * {@link #render()} method.
	 */
	private synchronized void doRender() {
		long startNs = System.nanoTime();

		// Update the workplane bounds to match the renderer pane
		workplane.setBounds( renderer.parentToLocal( renderer.getBoundsInParent() ) );

		renderer.clear();
		renderWorkplane();
		renderLayers();
		renderHintGeometry();
		renderReferenceGeometry();
		renderSelectAperture();

		//		renderer.setFillPen( Color.RED );
		//		renderer.fillScreenBox( 0, 0, 100, 100 );

		long endNs = System.nanoTime();
		long duration = (long)(0.000001 * (endNs - startNs));

		if( duration > 20 ) log.atWarn().log( "Render time: {0} ms", duration );
	}

	private void renderWorkplane() {
		if( workplane == null ) return;

		// Render grid
		if( isGridVisible() ) workplane.getCoordinateSystem().drawMareaGeometryGrid( renderer, workplane );
	}

	private void renderLayers() {
		if( design == null ) return;

		List<DesignLayer> orderedLayers = new ArrayList<>( getVisibleLayers() );

		// Render the layers in reverse order
		orderedLayers.sort( Collections.reverseOrder() );

		orderedLayers.forEach( this::renderLayer );
	}

	private void renderLayer( DesignLayer layer ) {
		List<DesignShape> orderedShapes = new ArrayList<>( layer.getShapes() );
		Collections.sort( orderedShapes );
		renderShapes( orderedShapes );
	}

	private void renderShapes( List<DesignShape> orderedShapes ) {
		Paint boundingDrawPaint = Color.RED;
		for( DesignShape shape : orderedShapes ) {
			boolean selected = shape.isSelected();

			Paint drawPaint = setDrawPen( shape, selected, calcSelectedDrawPaint() );
			Paint fillPaint = setFillPen( shape, selected, calcSelectedFillPaint() );

			// Fill the shape
			if( fillPaint != null ) {
				switch( shape.getType() ) {
					case BOX -> this.fillBox( (DesignBox)shape );
					case ELLIPSE -> this.fillEllipse( (DesignEllipse)shape );
					case PATH -> this.fillPath( (DesignPath)shape );
					case TEXT -> this.fillText( (DesignText)shape );
				}
			}

			// Draw the shape
			if( drawPaint != null ) {
				switch( shape.getType() ) {
					case ARC -> this.drawArc( (DesignArc)shape );
					case BOX -> this.drawBox( (DesignBox)shape );
					case CUBIC -> this.drawCubic( (DesignCubic)shape );
					case ELLIPSE -> this.drawEllipse( (DesignEllipse)shape );
					case LINE -> this.drawLine( (DesignLine)shape );
					case MARKER -> this.drawMarker( (DesignMarker)shape );
					case QUAD -> this.drawQuad( (DesignQuad)shape );
					case PATH -> this.drawPath( (DesignPath)shape );
					case TEXT -> this.drawText( (DesignText)shape );
				}
			}

			// NOTE Temporary code to show the bounding box
			if( 0 == 1 ) {
				Bounds bounds = shape.getSelectBounds();
				renderer.setDrawPen( selected ? calcSelectedDrawPaint() : boundingDrawPaint, 0.01, LineCap.valueOf( shape.calcDrawCap().name() ), LineJoin.ROUND, null, 0.0, false );
				renderer.drawBox( bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight(), 0 );
			}
		}
	}

	private Paint setFillPen( DesignShape shape, boolean selected, Paint selectedFillPaint ) {
		Paint fillPaint = shape.calcFillPaint();
		if( fillPaint == null ) {
			renderer.setFillPen( null );
		} else {
			renderer.setFillPen( selected ? selectedFillPaint : fillPaint );
		}
		return fillPaint;
	}

	private Paint setDrawPen( DesignShape shape, boolean selected, Paint selectedDrawPaint ) {
		Paint drawPaint = shape.calcDrawPaint();

		if( drawPaint == null ) {
			renderer.setDrawPen( null, 0.0, null, null, null, 0.0, false );
		} else {
			renderer.setDrawPen(
				selected ? selectedDrawPaint : drawPaint,
				shape.calcDrawWidth(),
				LineCap.valueOf( shape.calcDrawCap().name() ),
				LineJoin.ROUND,
				shape.calcDrawPattern().stream().mapToDouble( d -> d ).toArray(),
				0.0,
				shape.getType() == DesignShape.Type.TEXT
			);
		}

		return drawPaint;
	}

	private void drawArc( DesignArc arc ) {
		renderer.drawArc( arc.getOrigin().getX(), arc.getOrigin().getY(), arc.getRadii().getX(), arc.getRadii().getY(), arc.calcRotate(), arc.calcStart(), arc.calcExtent() );
	}

	private void drawCubic( DesignCubic cubic ) {
		renderer.drawCubic(
			cubic.getOrigin().getX(),
			cubic.getOrigin().getY(),
			cubic.getOriginControl().getX(),
			cubic.getOriginControl().getY(),
			cubic.getPointControl().getX(),
			cubic.getPointControl().getY(),
			cubic.getPoint().getX(),
			cubic.getPoint().getY()
		);
	}

	private void fillEllipse( DesignEllipse ellipse ) {
		renderer.fillEllipse( ellipse.getOrigin().getX(), ellipse.getOrigin().getY(), ellipse.getRadii().getX(), ellipse.getRadii().getY(), ellipse.calcRotate() );
	}

	private void drawEllipse( DesignEllipse ellipse ) {
		renderer.drawEllipse( ellipse.getOrigin().getX(), ellipse.getOrigin().getY(), ellipse.getRadii().getX(), ellipse.getRadii().getY(), ellipse.calcRotate() );
	}

	private void drawLine( DesignLine line ) {
		renderer.drawLine( line.getOrigin().getX(), line.getOrigin().getY(), line.getPoint().getX(), line.getPoint().getY() );
	}

	private void fillBox( DesignBox box ) {
		renderer.fillBox( box.getOrigin().getX(), box.getOrigin().getY(), box.getSize().getX(), box.getSize().getY(), box.calcRotate() );
	}

	private void drawBox( DesignBox box ) {
		renderer.drawBox( box.getOrigin().getX(), box.getOrigin().getY(), box.getSize().getX(), box.getSize().getY(), box.calcRotate() );
	}

	private void drawMarker( DesignMarker marker ) {
		DesignPath path = marker.calcType().getDesignPath();
		if( path != null ) {
			CadTransform transform = path.getValue( getClass().getName() + ":marker-transform", () -> {
				double size = marker.calcSize();
				CadTransform translate = CadTransform.translation( marker.getOrigin() );
				CadTransform scale = CadTransform.scale( size, size, size );
				return translate.combine( scale );
			} );
			path.apply( transform );
			renderer.fillPath( toMareaPathSteps( path ) );
		} else {
			log.atWarn().log( "Undefined marker type: {0}", marker.getMarkerType() );
		}
	}

	private void drawQuad( DesignQuad quad ) {
		renderer.drawQuad( quad.getOrigin().getX(), quad.getOrigin().getY(), quad.getControl().getX(), quad.getControl().getY(), quad.getPoint().getX(), quad.getPoint().getY() );
	}

	private void fillPath( DesignPath path ) {
		renderer.fillPath( toMareaPathSteps( path ) );
	}

	private void drawPath( DesignPath path ) {
		renderer.drawPath( toMareaPathSteps( path ) );
	}

	private void fillText( DesignText text ) {
		renderer.fillText( text.getOrigin().getX(), text.getOrigin().getY(), text.calcTextSize(), text.calcRotate(), text.getText(), Font.of( text.calcFont() ) );
	}

	private void drawText( DesignText text ) {
		renderer.drawText( text.getOrigin().getX(), text.getOrigin().getY(), text.calcTextSize(), text.calcRotate(), text.getText(), Font.of( text.calcFont() ) );
	}

	private List<Path.Step> toMareaPathSteps( DesignPath path ) {
		List<DesignPath.Step> steps = path.getSteps();
		if( steps.isEmpty() ) return List.of();

		DesignPath.Step move = steps.getFirst();
		if( move.command() != DesignPath.Command.M ) {
			log.atError().log( "DesignPath does not start with a move command" );
			return List.of();
		}

		Path mareaPath = new Path();
		for( DesignPath.Step step : steps ) {
			double[] data = step.data();
			switch( step.command() ) {
				case M -> mareaPath.move( data[ 0 ], data[ 1 ] );
				case A -> mareaPath.arc( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ], data[ 4 ], data[ 5 ], data[ 6 ] );
				case L -> mareaPath.line( data[ 0 ], data[ 1 ] );
				case B -> mareaPath.curve( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ], data[ 4 ], data[ 5 ] );
				case Q -> mareaPath.quad( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ] );
				case Z -> mareaPath.close();
			}
		}

		return mareaPath.getSteps();
	}

	private void renderHintGeometry() {
		// Render hint geometry

		// Hint geometry are:
		//  - temporary geometry are things like preview geometry for commands

		// TODO Render temporary geometry
		renderLayer( previewLayer );
	}

	private void renderReferenceGeometry() {
		// reference points, construction points, etc.
		// TODO Render reference geometry
		renderLayer( referenceLayer );
	}

	private void renderSelectAperture() {
		if( selectAperture == null ) return;
		DesignShape aperture = selectAperture.get();
		if( aperture == null ) return;

		Paint fillColor = setFillPen( aperture, false, null );
		Paint drawColor = setDrawPen( aperture, false, null );

		if( aperture instanceof DesignEllipse ellipse ) {
			drawEllipse( ellipse );
			double x = ellipse.getOrigin().getX() - ellipse.getRadii().getX();
			double y = ellipse.getOrigin().getY() - ellipse.getRadii().getY();
			double w = 2 * ellipse.getRadii().getX();
			double h = 2 * ellipse.getRadii().getY();
			renderer.setFillPen( fillColor );
			renderer.fillScreenOval( x, y, w, h );
			renderer.setDrawPen( drawColor, 1.0, LineCap.SQUARE, LineJoin.MITER, null, 0.0, false );
			renderer.drawScreenOval( x, y, w, h );
		}

		if( aperture instanceof DesignBox rectangle ) {
			renderer.setFillPen( fillColor );
			renderer.fillScreenBox( rectangle.getOrigin().getX(), rectangle.getOrigin().getY(), rectangle.getSize().getX(), rectangle.getSize().getY() );
			renderer.setDrawPen( drawColor, 1.0, LineCap.SQUARE, LineJoin.MITER, null, 0.0, false );
			renderer.drawScreenBox( rectangle.getOrigin().getX(), rectangle.getOrigin().getY(), rectangle.getSize().getX(), rectangle.getSize().getY() );
		}
	}

	double getInternalScaleX() {
		// TODO This value can be cached
		double scale = DesignUnit.INCH.per( design.calcDesignUnit() );
		return scale * renderer.getDpiX() * getZoomX();
	}

	double getInternalScaleY() {
		// TODO This value can be cached
		double scale = DesignUnit.INCH.per( design.calcDesignUnit() );
		return scale * renderer.getDpiY() * getZoomY();
	}

	/**
	 * Select nodes using a shape. The selecting shape can be any shape but it
	 * usually a {@link DesignEllipse} or a {@link DesignBox}. Returns the list
	 * of discovered shapes in order from top to bottom.
	 * <p>
	 * The selector shape is defined in world coordinates.
	 *
	 * @param selector The selecting shape
	 * @param intersect True to select shapes by intersection
	 * @return The list of discovered shapes
	 */
	private List<DesignShape> doFindByShape( final DesignShape selector, final boolean intersect ) {
		// Ensure the selector does not have a draw width
		selector.setDrawWidth( "0" );
		selector.setDrawPaint( "#ff00ffff" );
		selector.setFillPaint( "#ff00ffff" );

		// This method should be thread agnostic. It should be safe to call from any thread.
		return getVisibleShapes().stream().filter( shape -> matches( selector, shape, intersect ) ).collect( Collectors.toList() );
	}

	/**
	 * Test if the selector shape should select the specific shape. The intersect
	 * parameter indicates if the selector needs to contain or just intersect the
	 * shape.
	 * <p>
	 * Both the selector and the shape are defined in world coordinates.
	 *
	 * @param selector The selector shape
	 * @param shape The shape to test
	 * @param intersect The intersect flag
	 * @return True if the selector shape should select the shape
	 */
	private boolean matches( DesignShape selector, DesignShape shape, boolean intersect ) {
		Bounds selectorBounds = selector.getSelectBounds();
		Bounds shapeBounds = shape.getSelectBounds();

		// This first test is an optimization for fully excluded shapes
		if( !selectorBounds.intersects( shapeBounds ) ) return false;

		// This second test is an optimization for fully contained shapes
		if( selectorBounds.contains( shapeBounds ) ) return true;

		// This is the slow but accurate test if the shape is contained when the selector is not a box
		Shape fxSelector = selector.getFxShape();
		Shape fxShape = shape.getFxShape();

		if( intersect ) {
			return !((javafx.scene.shape.Path)Shape.intersect( fxShape, fxSelector )).getElements().isEmpty();
		} else {
			return ((javafx.scene.shape.Path)Shape.subtract( fxShape, fxSelector )).getElements().isEmpty();
		}
	}

	private void setLengthUnit( String value ) {
		renderer.setLengthUnit( RenderUnit.valueOf( value.toUpperCase() ) );
	}

	/**
	 * This class is used to collapse multiple render requests. As requests are
	 * made this class links requests together. Then, when the first of multiple
	 * requests is executed it marks subsequent requests as cancelled and proceeds
	 * to render. As cancelled requests are executed they simply exit.
	 */
	private class RenderTrigger implements Runnable {

		private static RenderTrigger latest;

		private RenderTrigger next;

		private boolean cancelled;

		public RenderTrigger() {
			synchronized( RenderTrigger.class ) {
				if( latest == null ) {
					latest = this;
				} else {
					latest.next = this;
				}
			}
		}

		public void run() {
			// If this trigger is already cancelled just skip it
			if( cancelled ) return;

			// Ensure that we are on the FX application thread
			Fx.affirmOnFxThread();

			// Cancel all triggers that are currently waiting
			while( next != null ) {
				next.cancelled = true;
				next = next.next;
			}

			// Now actually draw the geometry
			doRender();
		}

	}

}
