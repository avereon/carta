package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.*;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.tool.DesignWorkplane;
import com.avereon.data.NodeEvent;
import com.avereon.marea.Font;
import com.avereon.marea.LineCap;
import com.avereon.marea.Pen;
import com.avereon.marea.Shape2d;
import com.avereon.marea.fx.FxRenderer2d;
import com.avereon.marea.geom.*;
import com.avereon.zarra.javafx.Fx;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.layout.BorderPane;
import lombok.CustomLog;

import java.util.ArrayList;
import java.util.Collections;
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
		gridVisible().set( visible );
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

		// Render the layers in reverse order
		List<DesignLayer> orderedLayers = design.getAllLayers();
		Collections.reverse( orderedLayers );

		for( DesignLayer layer : orderedLayers ) {
			// Do not render hidden layers
			if( !isLayerVisible( layer ) ) continue;

			List<DesignShape> orderedShapes = new ArrayList<>( layer.getShapes() );
			Collections.sort( orderedShapes );

			// Render the geometry for the layer
			for( DesignShape shape : orderedShapes ) {
				// Set fill pen
				Pen fillPen = shape.getValue( "cache.carta.pen.fill" );
				if( fillPen == null ) {
					fillPen = createFillPen( shape );
					shape.setValue( "cache.carta.pen.fill", fillPen );
				}
				renderer.setFillPen( fillPen.paint() );

				// Set draw pen
				Pen drawPen = shape.getValue( "cache.carta.pen.draw" );
				if( drawPen == null ) {
					drawPen = createDrawPen( shape );
					shape.setValue( "cache.carta.pen.draw", drawPen );
				}
				renderer.setDrawPen( drawPen.paint(), drawPen.width(), drawPen.cap(), drawPen.join(), drawPen.dashes(), drawPen.offset() );

				// Fill the shape
				if( fillPen.paint() != null ) {
					switch( shape.getType() ) {
						case ELLIPSE -> this.fillEllipse( (DesignEllipse)shape );
						case PATH -> this.fillPath( (DesignPath)shape );
						case TEXT -> this.fillText( (DesignText)shape );
					}
				}

				// Draw the shape
				if( drawPen.paint() != null ) {
					switch( shape.getType() ) {
						case ARC -> this.renderArc( (DesignArc)shape );
						case CUBIC -> this.renderCubic( (DesignCubic)shape );
						case ELLIPSE -> this.renderEllipse( (DesignEllipse)shape );
						case LINE -> this.renderLine( (DesignLine)shape );
						case MARKER -> this.renderMarker( (DesignMarker)shape );
						case QUAD -> this.renderQuad( (DesignQuad)shape );
						case PATH -> this.renderPath( (DesignPath)shape );
						case TEXT -> this.renderText( (DesignText)shape );
					}
				}
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

	private void fillEllipse( DesignEllipse ellipse ) {
		renderer.fillEllipse( ellipse.getOrigin().getX(), ellipse.getOrigin().getY(), ellipse.getRadii().getX(), ellipse.getRadii().getY(), ellipse.calcRotate() );
	}

	private void renderEllipse( DesignEllipse ellipse ) {
		renderer.drawEllipse( ellipse.getOrigin().getX(), ellipse.getOrigin().getY(), ellipse.getRadii().getX(), ellipse.getRadii().getY(), ellipse.calcRotate() );
	}

	private void renderLine( DesignLine line ) {
		renderer.drawLine( line.getOrigin().getX(), line.getOrigin().getY(), line.getPoint().getX(), line.getPoint().getY() );
	}

	private void renderMarker( DesignMarker marker ) {
		Point3D origin = marker.getOrigin();
		DesignPath path = marker.calcType().getDesignPath();

		if( path != null ) {
			renderer.fillPath( origin.getX(), origin.getY(), toPathElements( path.getElements() ) );
		} else {
			log.atError().log( "Undefined marker type: {0}", marker.getMarkerType() );
		}
	}

	private void renderQuad( DesignQuad quad ) {
		renderer.drawQuad( quad.getOrigin().getX(), quad.getOrigin().getY(), quad.getControl().getX(), quad.getControl().getY(), quad.getPoint().getX(), quad.getPoint().getY() );
	}

	private void fillPath( DesignPath path ) {
		Point3D origin = path.getOrigin();
		renderer.fillPath( origin.getX(), origin.getY(), toPathElements( path.getElements() ) );
	}

	private void renderPath( DesignPath path ) {
		Point3D origin = path.getOrigin();
		renderer.drawPath( origin.getX(), origin.getY(), toPathElements( path.getElements() ) );
	}

	private void fillText( DesignText text ) {
		renderer.fillText( text.getOrigin().getX(), text.getOrigin().getY(), text.calcTextSize(), text.calcRotate(), text.getText(), Font.of( text.calcFont() ) );
	}

	private void renderText( DesignText text ) {
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
					pen = createDrawPen( shape );
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

	private Pen createDrawPen( DesignShape shape ) {
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

	private Pen createFillPen( DesignShape shape ) {
		return new Pen( shape.calcFillPaint() );
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
		double height = shape.calcFont().getSize();
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
