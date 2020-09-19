package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignShapeView extends DesignDrawableView {

	private List<Shape> geometry;

	private List<ConstructionPoint> cps;

	private EventHandler<NodeEvent> drawWidthHandler;

	private EventHandler<NodeEvent> drawColorHandler;

	private EventHandler<NodeEvent> fillColorHandler;

	private EventHandler<NodeEvent> selectedHandler;

	public DesignShapeView( DesignPane pane, DesignShape designShape ) {
		super( pane, designShape );
		generate();
	}

	public DesignShape getDesignShape() {
		return (DesignShape)getDesignNode();
	}

	public List<Shape> getGeometry() {
		return geometry;
	}

	public List<ConstructionPoint> getConstructionPoints() {
		return cps;
	}

	protected void updateGeometry() {
		removeShapeGeometry();
		generate();
		addShapeGeometry();
	}

	void addShapeGeometry() {
		getPane().addShapeGeometry( this );
		registerListeners();
	}

	void removeShapeGeometry() {
		unregisterListeners();
		getPane().removeShapeGeometry( this );
	}

	private Shape getShape() {
		return getGeometry().get( 0 );
	}

	private void generate() {
		geometry = getDesignShape().generateGeometry();
		cps = getDesignShape().generateConstructionPoints( getPane(), geometry );
		geometry.forEach( this::configureShape );
	}

	private void configureShape( Shape shape ) {
		shape.getProperties().put( DesignShape.SHAPE_META_DATA, getDesignShape() );
		shape.setStrokeWidth( getDesignShape().calcDrawWidth() );
		shape.setStroke( getDesignShape().calcDrawColor() );
		shape.setFill( getDesignShape().calcFillColor() );
	}

	public void registerListeners() {
		getDesignShape().register( DesignShape.DRAW_WIDTH, drawWidthHandler = e -> Fx.run( () -> getShape().setStrokeWidth( getDesignShape().calcDrawWidth() ) ) );
		getDesignShape().register( DesignShape.DRAW_COLOR, drawColorHandler = e -> Fx.run( () -> getShape().setStroke( getDesignShape().calcDrawColor() ) ) );
		getDesignShape().register( DesignShape.FILL_COLOR, fillColorHandler = e -> Fx.run( () -> getShape().setFill( getDesignShape().calcFillColor() ) ) );
		getDesignShape().register( DesignShape.SELECTED, selectedHandler = e -> Fx.run( () -> {
			getShape().setStroke( e.getNewValue() ? getDesignShape().calcSelectDrawColor() : getDesignShape().calcDrawColor() );
			getShape().setFill( e.getNewValue() ? getDesignShape().calcSelectFillColor() : getDesignShape().calcFillColor() );
		} ) );
	}

	public void unregisterListeners() {
		getDesignShape().unregister( DesignShape.SELECTED, selectedHandler );
		getDesignShape().unregister( DesignShape.FILL_COLOR, fillColorHandler );
		getDesignShape().unregister( DesignShape.DRAW_COLOR, drawColorHandler );
		getDesignShape().unregister( DesignShape.DRAW_WIDTH, drawWidthHandler );
	}

}
