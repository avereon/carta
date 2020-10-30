package com.avereon.cartesia.tool;

import javafx.geometry.Point3D;
import javafx.scene.shape.Line;

import java.util.Set;

public interface CoordinateSystem {

	CoordinateSystem ORTHOGRAPHIC = new CoordinateSystemOrthographic();

	CoordinateSystem ISOMETRIC = new CoordinateSystemIsometric();

	CoordinateSystem POLAR = new CoordinateSystemPolar();

	Point3D getNearest( Workplane workplane, Point3D point );

	Set<Line> getGridLines( Workplane workplane );

}
