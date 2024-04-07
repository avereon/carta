package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.*;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.tool.DesignWorkplane;
import com.avereon.data.NodeEvent;
import com.avereon.marea.LineCap;
import com.avereon.marea.Pen;
import com.avereon.marea.Shape2d;
import com.avereon.marea.fx.FxRenderer2d;
import com.avereon.marea.geom.*;
import com.avereon.zarra.javafx.Fx;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.layout.BorderPane;
import lombok.CustomLog;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@CustomLog
public class DesignRenderer extends BorderPane {

	private final FxRenderer2d renderer;

	private final Map<Class<? extends DesignShape>, Function<DesignShape, Shape2d>> designCreateMap;

	private final ObservableSet<DesignLayer> visibleLayers;

	private Design design;

	private DesignWorkplane workplane;

	private SimpleBooleanProperty gridVisible;

	public DesignRenderer() {
		// FIXME Would an enum and switch be faster? Or maybe direct method calls?
		designCreateMap = new ConcurrentHashMap<>();
		designCreateMap.put( DesignArc.class, s -> createArc( (DesignArc)s ) );
		designCreateMap.put( DesignCubic.class, s -> createCurve( (DesignCubic)s ) );
		designCreateMap.put( DesignLine.class, s -> createLine( (DesignLine)s ) );
		designCreateMap.put( DesignEllipse.class, s -> createEllipse( (DesignEllipse)s ) );
		designCreateMap.put( DesignMarker.class, s -> createMarker( (DesignMarker)s ) );
		designCreateMap.put( DesignPath.class, s -> createPath( (DesignPath)s ) );
		// ?? designCreateMap.put( DesignQuad.class, s -> createQuad( (DesignQuad)s ) );
		designCreateMap.put( DesignText.class, s -> createText( (DesignText)s ) );

		visibleLayers = FXCollections.observableSet();

		// Create and add the renderer to the center
		setCenter( this.renderer = new FxRenderer2d() );

		// TODO Change the renderer mouse actions
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
	}

	public void setDesign( Design design ) {
		// TODO Disconnect listeners

		this.design = design;

		// TODO Connect listeners

		// Temporary listener for testing
		design.register( NodeEvent.VALUE_CHANGED, e -> {
			log.atConfig().log( "Something changed in the design: " + e.getKey() );
		} );
	}

	public void setWorkplane( DesignWorkplane workplane ) {
		// TODO Disconnect listeners

		this.workplane = workplane;

		// TODO Connect listeners

		// Temporary listener for testing
		workplane.register( NodeEvent.VALUE_CHANGED, e -> {
			log.atConfig().log( "Something changed in the workplane: " + e.getKey() );
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

	public boolean isGridVisible() {
		return gridVisible != null && gridVisible().get();
	}

	public void setGridVisible( boolean visible ) {
		gridVisible.set( visible );
		render();
	}

	public SimpleBooleanProperty gridVisible() {
		if( gridVisible == null ) gridVisible = new SimpleBooleanProperty( false );
		return gridVisible;
	}

	public boolean isLayerVisible( DesignLayer layer ) {
		return visibleLayers != null && visibleLayers.contains( layer );
	}

	//	public Set<DesignLayer> getVisibleLayers() {
	//		return visibleLayers;
	//	}
	//
	//	public void setVisibleLayers( Set<DesignLayer> visibleLayers ) {
	//		visibleLayersProperty().clear();
	//		visibleLayersProperty().addAll( visibleLayers );
	//		render();
	//	}

	public ObservableSet<DesignLayer> visibleLayers() {
		return visibleLayers;
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
	 * Request that geometry be rendered. This method collapses multiple
	 * sequential render requests to improve performance. This method is safe to
	 * call from any thread.
	 */
	public void render() {
		Fx.run( new RenderTrigger() );
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

	private void doRender() {
		//long startNs = System.nanoTime();

		renderer.clear();
		renderWorkplane();
		renderVisibleLayers();
		renderHintGeometry();
		renderReferenceGeometry();
		renderSelectorGeometry();

		//long endNs = System.nanoTime();

		//log.atConfig().log( "Render time: {0} ns", (endNs - startNs) );
	}

	private void renderWorkplane() {
		if( workplane == null ) return;

		// Update the workplane bounds to match this pane
		workplane.setBounds( renderer.parentToLocal( Point2D.ZERO ), renderer.parentToLocal( new Point2D( getWidth(), getHeight() ) ) );

		// Render grid
		if( isGridVisible() ) workplane.getCoordinateSystem().drawMareaGeometryGrid( renderer, workplane );
	}

	private void renderVisibleLayers() {
		if( design == null ) return;
		for( DesignLayer layer : design.getAllLayers() ) {
			// Do not render hidden layers
			if( !isLayerVisible( layer ) ) continue;

			// Render the geometry for the layer
			for( DesignShape shape : layer.getShapes() ) {
				Pen pen = shape.getValue( "cache.marea.pen" );
				if( pen == null ) {
					pen = createPen( shape );
					shape.setValue( "cache.marea.pen", pen );
				}
				renderer.setPen( pen.paint(), pen.width(), pen.cap(), pen.join(), pen.dashes(), pen.offset() );

				switch( shape.getType() ) {
					case ARC -> this.renderArc( (DesignArc)shape );
					case LINE -> this.renderLine( (DesignLine)shape );
				}

				// NEXT Continue implementing new render methods
			}
		}
	}

	private void renderArc( DesignArc arc ) {
		renderer.drawArc( arc.getOrigin().getX(), arc.getOrigin().getY(), arc.getRadii().getX(), arc.getRadii().getY(), arc.calcRotate(), arc.calcStart(), arc.calcExtent() );
	}

	private void renderCubic( DesignCubic cubic ) {
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

	private void renderLine( DesignLine line ) {
		renderer.drawLine( line.getOrigin().getX(), line.getOrigin().getY(), line.getPoint().getX(), line.getPoint().getY() );
	}

	private void renderMarker( DesignMarker marker ) {
		DesignPath path = marker.calcType().getDesignPath();
		if( path == null ) {
			log.atError().log( "Undefined marker path: {0}", marker.getMarkerType() );
		} else {
			renderer.drawPath( toPathElements( path.getElements() ) );
		}
	}

	private void renderPath( DesignPath path ) {
		renderer.drawPath( toPathElements( path.getElements() ) );
	}

	private void renderQuad( DesignQuad quad ) {
		renderer.drawQuad(
			quad.getOrigin().getX(),
			quad.getOrigin().getY(),
			quad.getControl().getX(),
			quad.getControl().getY(),
			quad.getPoint().getX(),
			quad.getPoint().getY()
		);
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

	private void renderVisibleLayersOld() {
		if( design == null ) return;

		for( DesignLayer layer : design.getAllLayers() ) {
			// Do not render hidden layers
			if( !isLayerVisible( layer ) ) continue;

			// Render the geometry for the layer
			for( DesignShape shape : layer.getShapes() ) {
				// FIXME To improve rendering performance, consider direct render methods on the renderer.

				// NOTE Caching the pen really helped
				Pen pen = shape.getValue( "cache.marea.pen" );
				if( pen == null ) {
					pen = createPen( shape );
					shape.setValue( "cache.marea.pen", pen );
				}

				// NOTE Caching the shape helped a bunch also
				Shape2d drawable = shape.getValue( "cache.marea.shape" );
				//Shape2d drawable = null;
				if( drawable == null ) {
					Function<DesignShape, Shape2d> converter = designCreateMap.get( shape.getClass() );
					if( converter != null ) {

						drawable = designCreateMap.get( shape.getClass() ).apply( shape );
						shape.setValue( "cache.marea.shape", drawable );
					} else {
						log.atWarn().log( "Geometry not supported yet: {0}", shape.getClass().getSimpleName() );
					}
				}

				// Draw the geometry
				if( drawable != null ) {
					// TODO If the shape is selected, don't render here, render as a hint
					if( shape instanceof DesignMarker ) {
						renderer.fill( drawable, pen );
					} else {
						renderer.draw( drawable, pen );
					}
				}

			}
		}
	}

	private void renderHintGeometry() {
		// Render hint geometry

		// Hint geometry are:
		//  - temporary geometry are things like preview geometry for commands
		//  - selected geometry

		// TODO Render temporary geometry

		// TODO Render selected geometry
	}

	private void renderReferenceGeometry() {
		// TODO Render reference geometry
		// reference points, construction points, etc.
	}

	private void renderSelectorGeometry() {
		// TODO Render selector geometry
		// mainly the selector window

	}

	private Pen createPen( DesignShape shape ) {
		// TODO Can/should pens be cached?
		Pen pen = new Pen( shape.calcDrawPaint(), shape.calcDrawWidth() );
		// TODO Can probably cache this transform
		pen.cap( LineCap.valueOf( shape.calcDrawCap().name() ) );
		// TODO Can probably cache this transform
		//pen.join(shape.calcDrawJoin());
		pen.dashes( shape.calcDrawPattern().stream().mapToDouble( d -> d ).toArray() );
		//pen.offset( shape.calcDrawPatternOffset());
		return pen;
	}

	private Arc createArc( DesignArc shape ) {
		double[] origin = CadPoints.asPoint( shape.getOrigin() );
		double[] radius = CadPoints.asPoint( shape.getRadii() );
		double rotate = shape.calcRotate();
		double start = shape.calcStart();
		double extent = shape.calcExtent();
		return new Arc( origin, radius, rotate, start, extent );
	}

	private Line createLine( DesignLine shape ) {
		double[] origin = CadPoints.asPoint( shape.getOrigin() );
		double[] point = CadPoints.asPoint( shape.getPoint() );
		return new Line( origin, point );
	}

	private Curve createCurve( DesignCubic shape ) {
		double[] origin = CadPoints.asPoint( shape.getOrigin() );
		double[] originControl = CadPoints.asPoint( shape.getOriginControl() );
		double[] pointControl = CadPoints.asPoint( shape.getPointControl() );
		double[] point = CadPoints.asPoint( shape.getPoint() );
		return new Curve( origin, originControl, pointControl, point );
	}

	private Ellipse createEllipse( DesignEllipse shape ) {
		double[] origin = CadPoints.asPoint( shape.getOrigin() );
		double[] radius = CadPoints.asPoint( shape.getRadii() );
		double rotate = shape.calcRotate();
		return new Ellipse( origin, radius, rotate );
	}

	private Path createMarker( DesignMarker shape ) {
		DesignPath path = shape.calcType().getDesignPath();
		if( path == null ) log.atError().log( "Undefined marker path: {0}", shape.getMarkerType() );
		return createPath( shape.calcType().getDesignPath() );
	}

	private Path createPath( DesignPath shape ) {
		double[] origin = CadPoints.asPoint( shape.getOrigin() );

		com.avereon.marea.geom.Path path = new com.avereon.marea.geom.Path( origin, 0.0 );
		for( DesignPath.Element element : shape.getElements() ) {
			double[] data = element.data();
			switch( element.command() ) {
				case MOVE -> path.move( data[ 0 ], data[ 1 ] );
				case LINE -> path.line( data[ 0 ], data[ 1 ] );
				case ARC -> path.arc( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ], data[ 4 ], data[ 5 ] );
				case CUBIC -> path.curve( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ], data[ 4 ], data[ 5 ] );
				case QUAD -> path.quad( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ] );
				case CLOSE -> path.close();
			}
		}

		return path;
	}

	private Text createText( DesignText shape ) {
		String string = shape.getText();
		double[] origin = CadPoints.asPoint( shape.getOrigin() );
		double height = shape.calcTextFont().getSize();
		return new Text( string, origin, height );
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
