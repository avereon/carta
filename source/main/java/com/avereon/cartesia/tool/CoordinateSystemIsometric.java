package com.avereon.cartesia.tool;

import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;

import java.util.List;

public class CoordinateSystemIsometric implements CoordinateSystem {

	@Override
	public Point3D getNearest( Workplane workplane, Point3D point) {
		return null;
	}

	@Override
	public List<Shape> getGridDots( Workplane workplane) {
		return null;
	}

	@Override
	public List<Shape> getGridLines( Workplane workplane) {
		return null;
	}

}
