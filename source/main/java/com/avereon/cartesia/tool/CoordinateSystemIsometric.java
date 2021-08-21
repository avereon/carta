package com.avereon.cartesia.tool;

import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;

import java.util.List;

public class CoordinateSystemIsometric implements CoordinateSystem {

	@Override
	public String name() {
		return "ISO";
	}

	@Override
	public Point3D getNearest( DesignWorkplane workplane, Point3D point) {
		return null;
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
