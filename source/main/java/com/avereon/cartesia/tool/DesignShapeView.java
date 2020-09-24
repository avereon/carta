package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignShapeView extends DesignDrawableView {

	static final String CONSTRUCTION_POINTS = "construction-points";

	static final String DESIGN_DATA = "design-data";

	private Group group;

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

	public Group getGroup() {
		return group;
	}

//	@Deprecated
//	public List<Shape> getGeometry() {
//		return geometry;
//	}

//	@Deprecated
//	public List<ConstructionPoint> getConstructionPoints() {
//		return cps;
//	}

	protected List<Shape> generateGeometry() {
		return List.of();
	}

	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		return List.of();
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
		return geometry.get( 0 );
	}

	private void generate() {
		geometry = generateGeometry();
		geometry.forEach( this::configureShape );
		cps = generateConstructionPoints( getPane(), geometry );

		group = new Group();
		group.getChildren().addAll( geometry );
		//group.getChildren().addAll( cps );
		group.getProperties().put( DESIGN_DATA, getDesignShape() );
	}

	private void configureShape( Shape shape ) {
		shape.setStrokeWidth( getDesignShape().calcDrawWidth() );
		shape.setStroke( getDesignShape().calcDrawColor() );
		shape.setFill( getDesignShape().calcFillColor() );
	}

	@Override
	void registerListeners() {
		getDesignShape().register( DesignShape.DRAW_WIDTH, drawWidthHandler = e -> Fx.run( () -> getShape().setStrokeWidth( getDesignShape().calcDrawWidth() ) ) );
		getDesignShape().register( DesignShape.DRAW_COLOR, drawColorHandler = e -> Fx.run( () -> getShape().setStroke( getDesignShape().calcDrawColor() ) ) );
		getDesignShape().register( DesignShape.FILL_COLOR, fillColorHandler = e -> Fx.run( () -> getShape().setFill( getDesignShape().calcFillColor() ) ) );
		getDesignShape().register( DesignShape.SELECTED, selectedHandler = e -> Fx.run( () -> {
			getShape().setStroke( e.getNewValue() ? getDesignShape().calcSelectDrawColor() : getDesignShape().calcDrawColor() );
			getShape().setFill( e.getNewValue() ? getDesignShape().calcSelectFillColor() : getDesignShape().calcFillColor() );
		} ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignShape.SELECTED, selectedHandler );
		getDesignShape().unregister( DesignShape.FILL_COLOR, fillColorHandler );
		getDesignShape().unregister( DesignShape.DRAW_COLOR, drawColorHandler );
		getDesignShape().unregister( DesignShape.DRAW_WIDTH, drawWidthHandler );
	}

	ConstructionPoint cp( DesignPane pane, DoubleProperty xProperty, DoubleProperty yProperty ) {
		ConstructionPoint cp = new ConstructionPoint();
		cp.layoutXProperty().bind( xProperty.multiply( pane.scaleXProperty() ) );
		cp.layoutYProperty().bind( yProperty.multiply( pane.scaleYProperty() ).negate() );
		return cp;
	}

}
