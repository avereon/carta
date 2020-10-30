package com.avereon.cartesia.tool;

import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;

import java.util.List;

public interface CoordinateSystem {

	CoordinateSystem ORTHOGRAPHIC = new CoordinateSystemOrthographic();

	CoordinateSystem ISOMETRIC = new CoordinateSystemIsometric();

	CoordinateSystem POLAR = new CoordinateSystemPolar();

	Point3D getNearest( Workplane workplane, Point3D point );

	List<Shape> getGridDots( Workplane workplane );

	List<Shape> getGridLines( Workplane workplane );

}
