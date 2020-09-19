package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignShapeView extends DesignDrawableView {

	private Shape shape;

	private List<Shape> geometry;

	private List<ConstructionPoint> cps;

	private EventHandler<NodeEvent> selectedHandler;

	public DesignShapeView( DesignPane pane, DesignShape designShape, Shape shape ) {
		super( pane, designShape );

		this.shape = shape;
		geometry = designShape.generateGeometry();
		cps = designShape.generateConstructionPoints( pane, geometry );

		shape.getProperties().put( DesignShape.SHAPE_META_DATA, designShape );

		shape.setStrokeWidth( designShape.calcDrawWidth() );
		shape.setStroke( designShape.calcDrawColor() );
		shape.setFill( designShape.calcFillColor() );
	}

	public DesignShape getDesignShape() {
		return (DesignShape)getDesignNode();
	}

	public Shape getShape() {
		return shape;
	}

	protected void updateGeometry() {

	}

	public void registerListeners() {
//		designShape.register( DesignShape.DRAW_WIDTH, drawWidthHandler = e -> Fx.run( () -> fxShape.setStrokeWidth( designShape.calcDrawWidth() ) ) );
//		designShape.register( DesignShape.DRAW_COLOR, drawColorHandler = e -> Fx.run( () -> fxShape.setStroke( designShape.calcDrawColor() ) ) );
//		designShape.register( DesignShape.FILL_COLOR, fillColorHandler = e -> Fx.run( () -> fxShape.setFill( designShape.calcFillColor() ) ) );
//		designShape.register( DesignShape.SELECTED, selectedHandler = e -> Fx.run( () -> {
//			fxShape.setStroke( e.getNewValue() ? designShape.calcSelectDrawColor() : designShape.calcDrawColor() );
//			fxShape.setFill( e.getNewValue() ? designShape.calcSelectFillColor() : designShape.calcFillColor() );
//		} ) );
	}

	public void unregisterListeners() {
//		designShape.unregister( DesignShape.SELECTED, selectedHandler );
//		designShape.unregister( DesignShape.FILL_COLOR, fillColorHandler );
//		designShape.unregister( DesignShape.DRAW_COLOR, drawColorHandler );
//		designShape.unregister( DesignShape.DRAW_WIDTH, drawWidthHandler );
	}

}
