package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.*;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.curve.math.Point;
import com.avereon.marea.LineCap;
import com.avereon.marea.Pen;
import com.avereon.marea.Shape2d;
import com.avereon.marea.fx.FxRenderer2d;
import com.avereon.marea.geom.*;
import javafx.scene.layout.BorderPane;
import lombok.CustomLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@CustomLog
public class DesignRenderer extends BorderPane {

	private final FxRenderer2d renderer;

	private DesignWorkplane workplane;

	private Design design;

	private final Map<Class<? extends DesignShape>, Function<DesignShape, Shape2d>> designCreateMap;

	public DesignRenderer() {
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

		// Add listeners for properties that should update the render
		renderer.zoomXProperty().addListener( ( p, o, n ) -> render() );
		renderer.zoomYProperty().addListener( ( p, o, n ) -> render() );
		renderer.viewpointXProperty().addListener( ( p, o, n ) -> render() );
		renderer.viewpointYProperty().addListener( ( p, o, n ) -> render() );
		renderer.widthProperty().addListener( ( p, o, n ) -> render() );
		renderer.heightProperty().addListener( ( p, o, n ) -> render() );
	}

	public void setDesign( Design design ) {
		this.design = design;
	}

	public void render() {
		renderer.clear();

		renderWorkplane();

		renderVisibleLayers();

		// Render hint geometry

		// Render reference geometry

		// Render selection geometry - mainly the selection window

	}

	private void renderWorkplane() {
		if( workplane == null ) return;

		// Render grid
	}

	private void renderVisibleLayers() {
		if( design == null ) return;

		for( DesignLayer layer : design.getAllLayers() ) {
			//if(!isLayerVisible( layer )) continue;
			for( DesignShape shape : layer.getShapes() ) {
				Pen pen = createPen( shape );

				// Draw the geometry
				Function<DesignShape, Shape2d> converter = designCreateMap.get( shape.getClass() );
				if( converter != null ) {
					Shape2d drawable = designCreateMap.get( shape.getClass() ).apply( shape );
					if( shape instanceof DesignMarker ) {
						renderer.fill( drawable, pen );
					} else {
						renderer.draw( drawable, pen );
					}
				} else {
					log.atWarn().log( "Geometry not supported yet: {0}", shape.getClass().getSimpleName() );
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
		double[] radius = Point.of( shape.getXRadius(), shape.getYRadius() );
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
		double[] radius = Point.of( shape.getXRadius(), shape.getYRadius() );
		double rotate = shape.calcRotate();
		return new Ellipse( origin, radius, rotate );
	}

	private com.avereon.marea.geom.Path createMarker( DesignMarker shape ) {
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
				case LINE -> path.line( data[ 0 ], data[ 1] );
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

}
