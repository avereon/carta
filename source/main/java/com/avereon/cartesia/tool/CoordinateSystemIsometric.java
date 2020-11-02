package com.avereon.cartesia.tool;

import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;

import java.util.List;

public class CoordinateSystemIsometric implements CoordinateSystem {

	@Override
	public Point3D getNearest( Workplane workplane, Point3D point) throws Exception {
		return null;
	}

	@Override
	public List<Shape> getGridDots( Workplane workplane) throws Exception {
		return List.of();
	}

	@Override
	public List<Shape> getGridLines( Workplane workplane) throws Exception {
		return List.of();
	}

}
