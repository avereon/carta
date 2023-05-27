package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.marea.Pen;
import com.avereon.marea.fx.FxRenderer2d;
import com.avereon.marea.geom.Line;
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
			for( DesignShape shape : layer.getShapes() ) {
				if( shape instanceof DesignLine ) {
					renderer.draw( new Line( CadPoints.asPoint( shape.getOrigin() ), CadPoints.asPoint( ((DesignLine)shape).getPoint() ) ), new Pen( shape.calcDrawPaint() ) );
				}
			}
		}
	}

}
