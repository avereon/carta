package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignShapeView extends DesignDrawableView {

	static final String DESIGN_DATA = "design-data";

	private static final String CONSTRUCTION_POINTS = "construction-points";

	private Group group;

	private List<Shape> geometry;

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

	protected void configureShape( Shape shape ) {
		shape.setStrokeWidth( getDesignShape().calcDrawWidth() );
		shape.setStroke( getDesignShape().calcDrawColor() );
		shape.setFill( getDesignShape().calcFillColor() );
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
		List<ConstructionPoint> cps = generateConstructionPoints( getPane(), geometry );

		Group cpGroup = new Group();
		cpGroup.getChildren().addAll( cps );

		group = new Group();
		group.getChildren().addAll( geometry );
		group.getChildren().addAll( cpGroup );
		group.getProperties().put( DESIGN_DATA, getDesignShape() );
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

	@SuppressWarnings( "unchecked" )
	public static List<ConstructionPoint> getConstructionPoints( Shape shape ) {
		List<ConstructionPoint> cps = (List<ConstructionPoint>)shape.getProperties().get( CONSTRUCTION_POINTS );
		return cps == null ? List.of() : cps;
	}

	static ConstructionPoint cp( DesignPane pane, DoubleProperty xProperty, DoubleProperty yProperty ) {
		ConstructionPoint cp = new ConstructionPoint();
		cp.scaleXProperty().bind( Bindings.divide( 1, pane.scaleXProperty() ) );
		cp.scaleYProperty().bind( Bindings.divide( 1, pane.scaleYProperty() ) );
		cp.layoutXProperty().bind( xProperty );
		cp.layoutYProperty().bind( yProperty );
		return cp;
	}

	static List<ConstructionPoint> setConstructionPoints( Shape shape, List<ConstructionPoint> cps ) {
		shape.getProperties().put( CONSTRUCTION_POINTS, cps );
		return cps;
	}

}
