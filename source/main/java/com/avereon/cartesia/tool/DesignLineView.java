package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignLine;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignLineView extends DesignShapeView{

	private EventHandler<NodeEvent> originHandler;

	private EventHandler<NodeEvent> pointHandler;

	public DesignLineView( DesignPane pane, DesignLine line ) {
		super( pane, line );
	}

	public DesignLine getDesignLine() {
		return (DesignLine)getDesignNode();
	}

	@Override
	protected List<Shape> generateGeometry() {
		Line line = new Line( getDesignLine().getOrigin().getX(), getDesignLine().getOrigin().getY(), getDesignLine().getPoint().getX(), getDesignLine().getPoint().getY() );
		line.setFill( Color.GREEN );
		return List.of( line );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Line line = (Line)shapes.get( 0 );
		ConstructionPoint o = cp( pane, line.startXProperty(), line.startYProperty() );
		ConstructionPoint p = cp( pane, line.endXProperty(), line.endYProperty() );
		o.visibleProperty().bind( line.visibleProperty() );
		p.visibleProperty().bind( line.visibleProperty() );
		return setConstructionPoints( line, List.of( o, p ) );
	}

	@Override
	void registerListeners() {
		super.registerListeners();
		getDesignShape().register( DesignLine.ORIGIN, originHandler = e -> Fx.run( () -> {
			((Line)getShape()).setStartX( getDesignLine().getOrigin().getX() );
			((Line)getShape()).setStartY( getDesignLine().getOrigin().getY() );
		}));
		getDesignShape().register( DesignLine.POINT, pointHandler = e -> Fx.run( () -> {
			((Line)getShape()).setEndX( getDesignLine().getPoint().getX() );
			((Line)getShape()).setEndY( getDesignLine().getPoint().getY() );
		}));
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignLine.POINT, pointHandler );
		getDesignShape().unregister( DesignLine.ORIGIN, originHandler );
		super.unregisterListeners();
	}

}
