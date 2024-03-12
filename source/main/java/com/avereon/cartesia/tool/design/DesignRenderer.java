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
import javafx.geometry.Point2D;
import javafx.scene.layout.BorderPane;
import lombok.CustomLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@CustomLog
public class DesignRenderer extends BorderPane {

	private final FxRenderer2d renderer;

	private Design design;

	private DesignWorkplane workplane;

	private SimpleBooleanProperty gridVisible;

	private final Map<Class<? extends DesignShape>, Function<DesignShape, Shape2d>> designCreateMap;

	public DesignRenderer() {
		// FIXME Would an enum and switch be faster?
		designCreateMap = new ConcurrentHashMap<>();
		designCreateMap.put( DesignArc.class, s -> createArc( (DesignArc)s ) );
		designCreateMap.put( DesignCurve.class, s -> createCurve( (DesignCurve)s ) );
		designCreateMap.put( DesignLine.class, s -> createLine( (DesignLine)s ) );
		designCreateMap.put( DesignEllipse.class, s -> createEllipse( (DesignEllipse)s ) );
		designCreateMap.put( DesignMarker.class, s -> createMarker( (DesignMarker)s ) );
		designCreateMap.put( DesignPath.class, s -> createPath( (DesignPath)s ) );
		// ?? designCreateMap.put( DesignQuad.class, s -> createQuad( (DesignQuad)s ) );
		designCreateMap.put( DesignText.class, s -> createText( (DesignText)s ) );

		// Create and add the renderer to the center
		setCenter( this.renderer = new FxRenderer2d() );

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
	}

	public void setDesign( Design design ) {
		// TODO Disconnect listeners

		this.design = design;

		// TODO Connect listeners
		design.register( NodeEvent.VALUE_CHANGED, e -> {
			log.atConfig().log( "Something changed in the design: " + e.getKey() );
		} );
	}

	public void setWorkplane( DesignWorkplane workplane ) {
		// TODO Disconnect listeners

		this.workplane = workplane;

		// TODO Connect listeners
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
		return new Point2D( renderer.getDpiX(), renderer.getDpiY() );
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

	/**
	 * Request that geometry be rendered. This method collapses multiple
	 * sequential render requests to improve performance. This method is safe to
	 * call from any thread.
	 */
	public void render() {
		Fx.run( new RenderTrigger() );
	}

	private void doRender() {
		//long startNs = System.nanoTime();
		renderer.clear();

		if( isGridVisible() ) renderWorkplane();

		renderVisibleLayers();

		// Render hint geometry

		// Render reference geometry

		// Render selection geometry - mainly the selection window

		//long endNs = System.nanoTime();

		//log.atConfig().log( "Render time: {0} ns", (endNs - startNs) );
	}

	private void renderWorkplane() {
		if( workplane == null ) return;

		// Update the workplane bounds to match this pane
		workplane.setBounds( renderer.parentToLocal( Point2D.ZERO ), renderer.parentToLocal( new Point2D( getWidth(), getHeight() ) ) );

		// Render grid
		workplane.getCoordinateSystem().drawMareaGeometryGrid( renderer, workplane );
	}

	private void renderVisibleLayers() {
		if( design == null ) return;

		for( DesignLayer layer : design.getAllLayers() ) {
			//if(!isLayerVisible( layer )) continue;
			for( DesignShape shape : layer.getShapes() ) {

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
					if( shape instanceof DesignMarker ) {
						renderer.fill( drawable, pen );
					} else {
						renderer.draw( drawable, pen );
					}
				}

			}
		}
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

	private Curve createCurve( DesignCurve shape ) {
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
		if( path == null ) log.atError().log( "Undefined marker path: {0}", shape.getType() );
		return createPath( shape.calcType().getDesignPath() );
	}

	private Path createPath( DesignPath shape ) {
		double[] origin = CadPoints.asPoint( shape.getOrigin() );

		com.avereon.marea.geom.Path path = new com.avereon.marea.geom.Path( origin, 0.0 );
		for( DesignPath.Element element : shape.getElements() ) {
			double[] data = element.data();
			switch( element.command() ) {
				case ARC -> path.arc( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ], data[ 4 ], data[ 5 ] );
				case CURVE -> path.curve( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ], data[ 4 ], data[ 5 ] );
				case CLOSE -> path.close();
				case LINE -> path.line( data[ 0 ], data[ 1 ] );
				case MOVE -> path.move( data[ 0 ], data[ 1 ] );
				case QUAD -> path.quad( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ] );
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