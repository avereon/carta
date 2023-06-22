package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignShape;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;

import java.util.List;

public class GridIsometric implements Grid {

	@Override
	public String name() {
		return "ISO";
	}

	@Override
	public Point3D getNearest( DesignWorkplane workplane, Point3D point) {
		return null;
	}

	@Override
	public List<DesignShape> generateGrid( DesignWorkplane workplane, GridStyle style ) {
		return List.of();
	}

	@Override
	public List<Shape> getGridDots( DesignWorkplane workplane) {
		return List.of();
	}

	@Override
	public List<Shape> getGridLines( DesignWorkplane workplane) {
		return List.of();
	}

}
