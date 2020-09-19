package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignPoint;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignGeometry {

	@Deprecated
	private final DesignPane pane;

	@Deprecated
	private final DesignShape designShape;

	@Deprecated
	private List<Shape> geometry;

	@Deprecated
	private List<ConstructionPoint> cps;

	@Deprecated
	private EventHandler<NodeEvent> drawWidthHandler;

	@Deprecated
	private EventHandler<NodeEvent> drawColorHandler;

	@Deprecated
	private EventHandler<NodeEvent> fillColorHandler;

	@Deprecated
	private EventHandler<NodeEvent> selectedHandler;

	@Deprecated
	private EventHandler<NodeEvent> pointTypeHandler;

	@Deprecated
	private EventHandler<NodeEvent> pointSizeHandler;

	public DesignGeometry( DesignPane pane, DesignShape designShape ) {
		this.pane = pane;
		this.designShape = designShape;
		generate();
	}

	public DesignPane getPane() {
		return pane;
	}

	public DesignShape getDesignShape() {
		return designShape;
	}

	public List<Shape> getFxShapes() {
		return geometry;
	}

	public List<ConstructionPoint> getConstructionPoints() {
		return cps;
	}

	public static DesignPointView from( DesignPane pane, DesignPoint point, Shape shape ) {
		return new DesignPointView( pane, point, shape );
	}

	public static DesignLineView from( DesignPane pane, DesignLine line, Shape shape ) {
		return new DesignLineView( pane, line, shape );
	}

	private void updateGeometry() {
		pane.removeDesignGeometry( this );
		generate();
		pane.addDesignGeometry( this );
	}

	void addToPane() {
		pane.addDesignGeometry( this );
		registerListeners(  geometry.get( 0 ) );
	}

	void removeFromPane() {
		unregisterListeners( );
		pane.removeDesignGeometry( this );
	}

	private void generate() {
		geometry = designShape.generateGeometry();
		cps = designShape.generateConstructionPoints( pane, geometry );

		geometry.forEach( s -> configureGeometry( s ) );
	}

	Shape configureGeometry( Shape fxShape ) {
		fxShape.getProperties().put( DesignShape.SHAPE_META_DATA, designShape );
		fxShape.setStrokeWidth( designShape.calcDrawWidth() );
		fxShape.setStroke( designShape.calcDrawColor() );
		fxShape.setFill( designShape.calcFillColor() );
		return fxShape;
	}

	private void registerListeners( Shape fxShape ) {
		// FIXME This is working, but I don't like the 'if' statement
		if( designShape instanceof DesignPoint ) {
			designShape.register( DesignPoint.TYPE, pointTypeHandler = e -> Fx.run( () -> updateGeometry() ) );
			designShape.register( DesignPoint.SIZE, pointSizeHandler = e -> Fx.run( () -> updateGeometry() ) );
		}

		// Add listeners for property changes
		designShape.register( DesignShape.DRAW_WIDTH, drawWidthHandler = e -> Fx.run( () -> fxShape.setStrokeWidth( designShape.calcDrawWidth() ) ) );
		designShape.register( DesignShape.DRAW_COLOR, drawColorHandler = e -> Fx.run( () -> fxShape.setStroke( designShape.calcDrawColor() ) ) );
		designShape.register( DesignShape.FILL_COLOR, fillColorHandler = e -> Fx.run( () -> fxShape.setFill( designShape.calcFillColor() ) ) );

		// Selection listener
		designShape.register( DesignShape.SELECTED, selectedHandler = e -> Fx.run( () -> {
			fxShape.setStroke( e.getNewValue() ? designShape.calcSelectDrawColor() : designShape.calcDrawColor() );
			fxShape.setFill( e.getNewValue() ? designShape.calcSelectFillColor() : designShape.calcFillColor() );
		} ) );
	}

	private void unregisterListeners( ) {
		designShape.unregister( DesignShape.SELECTED, selectedHandler );
		designShape.unregister( DesignShape.FILL_COLOR, fillColorHandler );
		designShape.unregister( DesignShape.DRAW_COLOR, drawColorHandler );
		designShape.unregister( DesignShape.DRAW_WIDTH, drawWidthHandler );

		// FIXME This is working, but I don't like the 'if' statement
		if( designShape instanceof DesignPoint ) {
			designShape.unregister( DesignPoint.SIZE, pointSizeHandler );
			designShape.unregister( DesignPoint.TYPE, pointTypeHandler );
		}
	}

}
