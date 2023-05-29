package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.*;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.curve.math.Point;
import com.avereon.marea.LineCap;
import com.avereon.marea.Pen;
import com.avereon.marea.fx.FxRenderer2d;
import com.avereon.marea.geom.Arc;
import com.avereon.marea.geom.Ellipse;
import com.avereon.marea.geom.Line;
import com.avereon.marea.geom.Text;
import javafx.scene.layout.BorderPane;

public class DesignRenderer extends BorderPane {

	private final FxRenderer2d renderer;

	private DesignWorkplane workplane;

	private Design design;

	public DesignRenderer() {
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

				if( shape instanceof DesignLine ) {
					renderer.draw( createLine( (DesignLine)shape ), pen );
				} else if( shape instanceof DesignArc ) {
					renderer.draw( createArc( (DesignArc)shape ), pen );
				} else if( shape instanceof DesignEllipse ) {
					renderer.draw( createEllipse( (DesignEllipse)shape ), pen );
				} else if( shape instanceof DesignText ) {
					renderer.draw( createText( (DesignText)shape ), pen );
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

	private Line createLine( DesignLine shape ) {
		double[] origin = CadPoints.asPoint( shape.getOrigin() );
		double[] point = CadPoints.asPoint( shape.getPoint() );
		return new Line( origin, point );
	}

	private Arc createArc( DesignArc shape ) {
		double[] origin = CadPoints.asPoint( shape.getOrigin() );
		double[] radius = Point.of( shape.getXRadius(), shape.getYRadius() );
		double rotate = shape.calcRotate();
		double start = shape.calcStart();
		double extent = shape.calcExtent();
		return new Arc( origin, radius, rotate, start, extent );
	}

	private Ellipse createEllipse( DesignEllipse shape ) {
		double[] origin = CadPoints.asPoint( shape.getOrigin() );
		double[] radius = Point.of( shape.getXRadius(), shape.getYRadius() );
		double rotate = shape.calcRotate();
		return new Ellipse( origin, radius, rotate );
	}

	private Text createText( DesignText shape ) {
		String string = shape.getText();
		double[] origin = CadPoints.asPoint( shape.getOrigin() );
		double height = shape.calcTextFont().getSize();
		return new Text( string, origin, height );
	}

}
