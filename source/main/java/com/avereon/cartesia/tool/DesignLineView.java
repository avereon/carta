package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignLine;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignLineView extends DesignShapeView{

	public DesignLineView( DesignPane pane, DesignLine line ) {
		super( pane, line );
	}

	public DesignLine getDesignLine() {
		return (DesignLine)getDesignNode();
	}

	@Override
	public List<Shape> generateGeometry() {
		Line line = new Line( getDesignLine().getOrigin().getX(), getDesignLine().getOrigin().getY(), getDesignLine().getPoint().getX(), getDesignLine().getPoint().getY() );
		return List.of( line );
	}

	@Override
	public List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Line line = (Line)shapes.get( 0 );
		ConstructionPoint o = cp( pane, line.startXProperty(), line.startYProperty() );
		ConstructionPoint p = cp( pane, line.endXProperty(), line.endYProperty() );
		o.visibleProperty().bind( line.visibleProperty() );
		p.visibleProperty().bind( line.visibleProperty() );

		List<ConstructionPoint> cps = List.of( o, p );
		line.getProperties().put( CONSTRUCTION_POINTS, cps );
		return cps;
	}

}
