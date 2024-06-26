package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.tool.DesignWorkplane;
import com.avereon.data.NodeEvent;
import com.avereon.marea.Font;
import com.avereon.marea.LineCap;
import com.avereon.marea.LineJoin;
import com.avereon.marea.fx.FxRenderer2d;
import com.avereon.marea.geom.Path;
import com.avereon.zarra.color.Colors;
import com.avereon.zarra.javafx.Fx;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import lombok.CustomLog;

import java.util.*;
import java.util.stream.Collectors;

@CustomLog
public class DesignRenderer extends BorderPane {

	private Design design;

	private DesignWorkplane workplane;

	private final FxRenderer2d renderer;

	private final ObservableSet<DesignLayer> visibleLayers;

	private final ObservableSet<DesignShape> selectedShapes;

	private SimpleBooleanProperty gridVisible;

	private final SimpleObjectProperty<DesignShape> selectAperture;

	private SimpleBooleanProperty referencePointsVisible;

	private SimpleBooleanProperty constructionPointsVisible;

	public DesignRenderer() {
		visibleLayers = FXCollections.observableSet();
		selectedShapes = FXCollections.observableSet();
		selectAperture = new SimpleObjectProperty<>();

		// Create and add the renderer to the center
		setCenter( this.renderer = new FxRenderer2d() );

		// Disable the default renderer mouse actions
		renderer.setOnMousePressed( null );
		renderer.setOnMouseDragged( null );
		renderer.setOnMouseReleased( null );
		renderer.setOnScroll( null );

		// Bind the renderer width and height to the parent
		renderer.widthProperty().bind( this.widthProperty() );
		renderer.heightProperty().bind( this.heightProperty() );

		// TEMPORARY
		// Add listeners for properties that should update the render
		renderer.zoomXProperty().addListener( ( p, o, n ) -> render() );
		renderer.zoomYProperty().addListener( ( p, o, n ) -> render() );
		renderer.viewpointXProperty().addListener( ( p, o, n ) -> render() );
		renderer.viewpointYProperty().addListener( ( p, o, n ) -> render() );
		renderer.widthProperty().addListener( ( p, o, n ) -> render() );
		renderer.heightProperty().addListener( ( p, o, n ) -> render() );

		visibleLayers.addListener( (SetChangeListener<? super DesignLayer>)( c ) -> render() );
		selectedShapes.addListener( (SetChangeListener<? super DesignShape>)( c ) -> render() );
		selectAperture.addListener( (ChangeListener<? super DesignShape>)( p, o, n ) -> render() );
	}

	public void setDesign( Design design ) {
		// TODO Disconnect listeners

		this.design = design;
		visibleLayers().addAll( design.getAllLayers() );

		// TODO Connect listeners

		// Temporary listener for testing
		design.register( NodeEvent.VALUE_CHANGED, e -> {
			//log.atConfig().log( "Something changed in the design: " + e.getKey() );
			render();
		} );
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
		return new Point2D( renderer.getPpiX(), renderer.getPpiY() );
	}

	public double getDpiX() {
		return renderer.getPpiX();
	}

	public double getDpiY() {
		return renderer.getPpiY();
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
		renderer.setPpi( dpiX, dpiY );
	}

	public void setDpi( Point2D dpi ) {
		renderer.setPpi( dpi.getX(), dpi.getY() );
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

	public Set<DesignLayer> getVisibleLayers() {
		return visibleLayers;
	}

	public void setVisibleLayers( Collection<DesignLayer> layers ) {
		visibleLayers.clear();
		visibleLayers.addAll( layers );
		render();
	}

	public ObservableSet<DesignLayer> visibleLayers() {
		return visibleLayers;
	}

	// Visible Shapes ------------------------------------------------------------

	public Set<DesignShape> getVisibleShapes() {
		return getVisibleLayers().stream().flatMap( l -> l.getShapes().stream() ).collect( Collectors.toSet() );
	}

	// Visible Shapes ------------------------------------------------------------

	public void setSelectAperture( DesignShape aperture ) {
		selectAperture.set( aperture );
	}

	public DesignShape getSelectAperture() {
		return selectAperture.get();
	}

	public SimpleObjectProperty<DesignShape> selectAperture() {
		return selectAperture;
	}

	// Selected Shapes -----------------------------------------------------------

	public boolean isShapeSelected( DesignShape shape ) {
		return selectedShapes.contains( shape );
	}

	public Set<DesignShape> getSelectedShapes() {
		return selectedShapes;
	}

	public void clearSelectedShapes() {
		setSelectedShapes( null );
	}

	public void setSelectedShapes( Collection<DesignShape> shapes ) {
		selectedShapes.clear();
		if( shapes != null ) selectedShapes.addAll( shapes );
	}

	public ObservableSet<DesignShape> selectedShapes() {
		// FIXME Maintain the visible shape order
		return selectedShapes;
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
	public void setViewpoint( Point2D viewpoint ) {
		renderer.setViewpoint( viewpoint.getX(), viewpoint.getY() );
	}

	public DoubleProperty viewpointXProperty() {
		return renderer.viewpointXProperty();
	}

	public DoubleProperty viewpointYProperty() {
		return renderer.viewpointYProperty();
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
	 * @param zoom The zoom to set
	 */
	public void setZoom( Point2D zoom ) {
		renderer.setZoom( zoom.getX(), zoom.getY() );
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
		Point2D anchor2d = new Point2D( anchor.getX(), anchor.getY() );
		Point2D offset2d = renderer.getViewpoint().subtract( anchor2d );

		// The zoom has to be set before the viewpoint
		renderer.setZoom( renderer.getZoom().multiply( factor ) );
		renderer.setViewpoint( anchor2d.add( offset2d.multiply( 1 / factor ) ) );
	}

	/**
	 * Change the view point due to mouse movement.
	 *
	 * @param viewAnchor The view point location before being dragged (world)
	 * @param dragAnchor The point where the mouse was pressed (screen)
	 * @param x The mouse event X coordinate (screen)
	 * @param y The mouse event Y coordinate (screen)
	 */
	public void pan( Point3D viewAnchor, Point3D dragAnchor, double x, double y ) {
		// Convert the view anchor to screen coordinates
		Point2D anchor = renderer.localToParent( CadPoints.toPoint2d( viewAnchor ) );

		// Calculate the drag offset in screen coordinates
		Point2D delta = new Point2D( dragAnchor.getX() - x, dragAnchor.getY() - y );

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

	@Override
	public Point3D localToParent( Point3D localPoint ) {
		return renderer.localToParent( localPoint );
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
	public Point3D localToParent( double x, double y, double z ) {
		return renderer.localToParent( x, y, z );
	}

	@Override
	public Bounds localToParent( Bounds localBounds ) {
		return renderer.localToParent( localBounds );
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
	public Point3D parentToLocal( Point3D parentPoint ) {
		return renderer.parentToLocal( parentPoint );
	}

	@Override
	public Point3D parentToLocal( double parentX, double parentY, double parentZ ) {
		return renderer.parentToLocal( parentX, parentY, parentZ );
	}

	@Override
	public Bounds parentToLocal( Bounds parentBounds ) {
		return renderer.parentToLocal( parentBounds );
	}

	public List<DesignShape> screenPointSelect( Point3D point, DesignValue tolerance ) {
		double size = realToWorld( tolerance );
		return worldPointSelect( parentToLocal( point ), new Point3D( size, size, 0 ) );
	}

	public List<DesignShape> screenWindowSelect( Point3D a, Point3D b, boolean intersect ) {
		return worldWindowSelect( parentToLocal( a ), parentToLocal( b ), intersect );
	}

	public List<DesignShape> worldPointSelect( Point3D anchor, DesignValue v ) {
		return worldPointSelect( anchor, v.getValue() );
	}

	public List<DesignShape> worldPointSelect( Point3D anchor, double radius ) {
		return worldPointSelect( anchor, new Point3D( radius, radius, 0 ) );
	}

	public List<DesignShape> worldPointSelect( Point3D anchor, Point3D radii ) {
		return doFindByShape( new DesignEllipse( anchor, radii ), true );
		//return doFindByShape( new DesignBox( anchor.subtract( radii ), radii.multiply( 2 ) ), true );
	}

	/**
	 * Find the nodes contained by, or intersecting, the window specified by points a and b.
	 *
	 * @param a One corner of the window
	 * @param b The other corner of the window
	 * @param intersect True to select shapes by intersection
	 * @return The set of selected nodes
	 */
	public List<DesignShape> worldWindowSelect( Point3D a, Point3D b, boolean intersect ) {
		double x = Math.min( a.getX(), b.getX() );
		double y = Math.min( a.getY(), b.getY() );
		double w = Math.abs( a.getX() - b.getX() );
		double h = Math.abs( a.getY() - b.getY() );

		DesignBox box = new DesignBox( x, y, w, h );
		return doFindByShape( box, intersect );
	}

	private void doRender() {
		//long startNs = System.nanoTime();

		// Update the workplane bounds to match this pane
		// FIXME Probably should not be updating the bounds during the render process
		// Listeners can be added to the width and height properties to update the bounds
		workplane.setBounds( renderer.parentToLocal( Point2D.ZERO ), renderer.parentToLocal( new Point2D( getWidth(), getHeight() ) ) );

		renderer.clear();
		renderWorkplane();
		renderLayers();
		renderReferenceGeometry();
		renderHintGeometry();
		renderSelectorGeometry();

		//long endNs = System.nanoTime();

		//log.atConfig().log( "Render time: {0} ns", (endNs - startNs) );
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

		for( DesignLayer layer : orderedLayers ) {
			List<DesignShape> orderedShapes = new ArrayList<>( layer.getShapes() );
			Collections.sort( orderedShapes );

			Paint selectedFillPaint = Colors.translucent( Color.MAGENTA, 0.2 );
			Paint selectedDrawPaint = Colors.translucent( Color.MAGENTA, 0.8 );
			Paint boundingDrawPaint = Colors.translucent( Color.RED, 0.5 );

			// Render the geometry for the layer
			for( DesignShape shape : orderedShapes ) {
				// TODO Would this be faster as a flag on the shape?
				boolean selected = isShapeSelected( shape );

				Paint fillPaint = setFillPen( shape, selected, selectedFillPaint );
				Paint drawPaint = setDrawPen( shape, selected, selectedDrawPaint );

				// Fill the shape
				if( fillPaint != null ) {
					switch( shape.getType() ) {
						case ELLIPSE -> this.fillEllipse( (DesignEllipse)shape );
						case PATH -> this.fillPath( (DesignPath)shape );
						case TEXT -> this.fillText( (DesignText)shape );
					}
				}

				// Draw the shape
				if( drawPaint != null ) {
					switch( shape.getType() ) {
						case ARC -> this.drawArc( (DesignArc)shape );
						case CUBIC -> this.drawCubic( (DesignCubic)shape );
						case ELLIPSE -> this.drawEllipse( (DesignEllipse)shape );
						case LINE -> this.drawLine( (DesignLine)shape );
						case MARKER -> this.drawMarker( (DesignMarker)shape );
						case QUAD -> this.drawQuad( (DesignQuad)shape );
						case PATH -> this.drawPath( (DesignPath)shape );
						case TEXT -> this.drawText( (DesignText)shape );
					}
				}

				// FIXME Temporary code to show the bounding box
				if( 1 == 1 ) {
					renderer.setDrawPen( selected ? selectedDrawPaint : boundingDrawPaint, 0.01, LineCap.valueOf( shape.calcDrawCap().name() ), LineJoin.ROUND, null, 0.0, false );

					Bounds bounds = shape.getVisualBounds();
					renderer.drawBox( bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight() );
				}
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

	private void drawMarker( DesignMarker marker ) {
		Point3D origin = marker.getOrigin();
		DesignPath path = marker.calcType().getDesignPath();

		if( path != null ) {
			renderer.fillMarker( origin.getX(), origin.getY(), toPathElements( path.getElements() ) );
		} else {
			log.atError().log( "Undefined marker type: {0}", marker.getMarkerType() );
		}
	}

	private void drawQuad( DesignQuad quad ) {
		renderer.drawQuad( quad.getOrigin().getX(), quad.getOrigin().getY(), quad.getControl().getX(), quad.getControl().getY(), quad.getPoint().getX(), quad.getPoint().getY() );
	}

	private void fillPath( DesignPath path ) {
		Point3D origin = path.getOrigin();
		renderer.fillPath( origin.getX(), origin.getY(), toPathElements( path.getElements() ) );
	}

	private void drawPath( DesignPath path ) {
		Point3D origin = path.getOrigin();
		renderer.drawPath( origin.getX(), origin.getY(), toPathElements( path.getElements() ) );
	}

	private void fillText( DesignText text ) {
		renderer.fillText( text.getOrigin().getX(), text.getOrigin().getY(), text.calcTextSize(), text.calcRotate(), text.getText(), Font.of( text.calcFont() ) );
	}

	private void drawText( DesignText text ) {
		renderer.drawText( text.getOrigin().getX(), text.getOrigin().getY(), text.calcTextSize(), text.calcRotate(), text.getText(), Font.of( text.calcFont() ) );
	}

	private List<Path.Element> toPathElements( List<DesignPath.Element> elements ) {
		DesignPath.Element move = elements.getFirst();
		if( move.command() != DesignPath.Command.MOVE ) {
			log.atError().log( "DesignPath does not start with a move command" );
			return List.of();
		}

		Path path = new Path( move.data()[ 0 ], move.data()[ 1 ] );
		for( int index = 1; index < elements.size(); index++ ) {
			DesignPath.Element element = elements.get( index );
			double[] data = element.data();
			switch( element.command() ) {
				case MOVE -> path.move( data[ 0 ], data[ 1 ] );
				case ARC -> path.arc( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ], data[ 4 ], data[ 5 ] );
				case LINE -> path.line( data[ 0 ], data[ 1 ] );
				case CUBIC -> path.curve( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ], data[ 4 ], data[ 5 ] );
				case QUAD -> path.quad( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ] );
				case CLOSE -> path.close();
			}
		}

		return path.getElements();
	}

	private void renderHintGeometry() {
		// Render hint geometry

		// Hint geometry are:
		//  - temporary geometry are things like preview geometry for commands

		// TODO Render temporary geometry
	}

	private void renderReferenceGeometry() {
		// TODO Render reference geometry
		// reference points, construction points, etc.
	}

	private void renderSelectorGeometry() {
		if( selectAperture == null ) return;
		DesignShape aperture = selectAperture.get();
		if( aperture == null ) return;

		Paint fillColor = setFillPen( aperture, false, null );
		Paint drawColor = setDrawPen( aperture, false, null );

		if( aperture instanceof DesignEllipse ellipse ) {
			renderer.setFillPen( fillColor );
			renderer.fillScreenOval( ellipse.getOrigin().getX(), ellipse.getOrigin().getY(), ellipse.getRadii().getX(), ellipse.getRadii().getY() );
			renderer.setDrawPen( drawColor, 1.0, LineCap.SQUARE, LineJoin.MITER, null, 0.0, false );
			renderer.drawScreenOval( ellipse.getOrigin().getX(), ellipse.getOrigin().getY(), ellipse.getRadii().getX(), ellipse.getRadii().getY() );
		}

		if( aperture instanceof DesignBox rectangle ) {
			renderer.setFillPen( fillColor );
			renderer.fillScreenBox( rectangle.getOrigin().getX(), rectangle.getOrigin().getY(), rectangle.getSize().getX(), rectangle.getSize().getY() );
			renderer.setDrawPen( drawColor, 1.0, LineCap.SQUARE, LineJoin.MITER, null, 0.0, false );
			renderer.drawScreenBox( rectangle.getOrigin().getX(), rectangle.getOrigin().getY(), rectangle.getSize().getX(), rectangle.getSize().getY() );
		}
	}

	private double realToWorld( DesignValue value ) {
		// Convert the provided value to design units and divide by the zoom factor
		return value.to( design.calcDesignUnit() ).getValue() / getZoomX();
	}

//	private double valueToScreen( DesignValue v ) {
//		return v.to( DesignUnit.INCH ).getValue() * getDpiX() * Screen.getPrimary().getOutputScaleX();
//	}
//
//	private double getInternalScale() {
//		return this.getDpiX() * getZoomX();
//	}

	/**
	 * Select nodes using a shape. The selecting shape can be any shape but it
	 * usually a {@link DesignEllipse} or a {@link DesignBox}. Returns the list
	 * of selected shapes in order from top to bottom.
	 *
	 * @param selector The selecting shape
	 * @param intersect True to select shapes by intersection
	 * @return The list of selected shapes
	 */
	private List<DesignShape> doFindByShape( final DesignShape selector, final boolean intersect ) {
		// Ensure the selector does not have a draw width
		selector.setDrawWidth( "0" );

		// This method should be thread agnostic. It should be safe to call from any thread.
		return getVisibleShapes().stream().filter( shape -> matches( selector, shape, intersect ) ).collect( Collectors.toList() );
	}

	private boolean matches( DesignShape selector, DesignShape shape, boolean intersect ) {
		return intersect ? isIntersecting( selector, shape ) : isContained( selector, shape );
	}

	private boolean isContained( DesignShape selector, DesignShape shape ) {
		Bounds selectorBounds = renderer.localToParent( selector.getVisualBounds() );
		Bounds shapeBounds = renderer.localToParent( shape.getVisualBounds() );

		// This first test is an optimization to determine if the accurate test can be skipped
		if( !localToParent( selectorBounds ).intersects( shapeBounds ) ) return false;

		// This second test is an optimization for fully contained shapes
		if( localToParent( selectorBounds ).contains( shapeBounds ) ) return true;

		// This is the slow but accurate test if the shape is contained when the selector is not a box
		Shape fxSelector = selector.getFxShape();
		Shape fxShape = shape.getFxShape();
		return !((javafx.scene.shape.Path)Shape.subtract( fxShape, fxSelector )).getElements().isEmpty();
	}

	private boolean isIntersecting( DesignShape selector, DesignShape shape ) {
		// This first test is an optimization to determine if the accurate test can be skipped
		if( !localToParent( selector.getVisualBounds() ).intersects( localToParent( shape.getVisualBounds() ) ) ) return false;

		// This is the slow but accurate test if the shape is intersecting
		Shape fxSelector = selector.getFxShape();
		Shape fxShape = shape.getFxShape();
		return !((javafx.scene.shape.Path)Shape.intersect( fxShape, fxSelector )).getElements().isEmpty();
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
			if( latest != null ) latest.next = this;
			latest = this;
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
