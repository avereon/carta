package com.avereon.cartesia.tool;

import javafx.geometry.Point3D;
import javafx.scene.shape.Line;

import java.util.Set;

public class CoordinateSystemPolar implements CoordinateSystem {

	@Override
	public Point3D getNearest( Workplane workplane, Point3D point) {
		return null;
	}

	@Override
	public Set<Line> getGridLines( Workplane workplane) {
		return null;
	}

}
