package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignShape;
import javafx.scene.shape.Shape;

import java.util.List;

public class DesignGeometry {

	private final DesignPane pane;

	private final DesignShape shape;

	private List<Shape> geometry;

	private List<ConstructionPoint> cps;

	public DesignGeometry( DesignPane pane, DesignShape shape ) {
		this.pane = pane;
		this.shape = shape;
		generate();

		// TODO Some listeners should be added here
		// ... like point type and point size because they change the geometry
		// or maybe they should be generated with the geometry with this class passed in
	}

	public DesignShape getDesignShape() {
		return shape;
	}

	public List<Shape> getFxShapes() {
		return geometry;
	}

	public List<ConstructionPoint> getConstructionPoints() {
		return cps;
	}

	private void updateGeometry() {
		pane.removeDesignGeometry(this);
		generate();
		pane.addDesignGeometry( this );
	}

	private void generate() {
		geometry = shape.generateGeometry( this );
		cps = shape.generateConstructionPoints( pane, geometry );
	}

}
