package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.CsaShape;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignGeometry {

	private final DesignPane pane;

	private final CsaShape shape;

	private final DesignPane.Layer layer;

	private List<Shape> geometry;

	private List<ConstructionPoint> cps;

	public DesignGeometry( DesignPane pane, CsaShape shape ) {
		this.pane = pane;
		this.shape = shape;
		this.layer = pane.getShapeLayer( shape );
		regenerate();

		// TODO Some listeners should be added here
		// ... like point type and point size because they change the geometry
		// or maybe they should be generated with the geometry with this class passed in
	}

	void updateGeometry() {
		removeFromPane();
		regenerate();
		addToPane();
	}

	private void regenerate() {
		geometry = shape.generateGeometry( this );
		cps = shape.generateConstructionPoints( pane, geometry );
	}

	void addToPane() {
		Fx.run( () -> {
			layer.getChildren().addAll( geometry );
			pane.getReferenceLayer().getChildren().addAll( cps );
		} );
	}

	void removeFromPane() {
		Fx.run( () -> {
			pane.getReferenceLayer().getChildren().removeAll( cps );
			layer.getChildren().removeAll( geometry );
		} );
	}

}
