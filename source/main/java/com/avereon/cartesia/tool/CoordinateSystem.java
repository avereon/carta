package com.avereon.cartesia.tool;

import com.avereon.cartesia.math.Constants;
import com.avereon.math.Arithmetic;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface CoordinateSystem {

	CoordinateSystem ORTHOGRAPHIC = new CoordinateSystemOrthographic();

	CoordinateSystem ISOMETRIC = new CoordinateSystemIsometric();

	CoordinateSystem POLAR = new CoordinateSystemPolar();

	Point3D getNearest( Workplane workplane, Point3D point );

	List<Shape> getGridDots( Workplane workplane );

	List<Shape> getGridLines( Workplane workplane );

	static boolean isNearAny( Double value, Collection<Double> values ) {
		for( Double check : values ) {
			if( Math.abs( check - value ) <= Constants.DISTANCE_TOLERANCE ) return true;
		}
		return false;
	}

	static List<Double> getOffsets( double origin, double spacing, double lowLimit, double highLimit ) {
		return getOffsets( origin, spacing, lowLimit, highLimit, false );
	}

	static List<Double> getOffsets( double origin, double spacing, double lowLimit, double highLimit, boolean radial ) {
		double x1 = Arithmetic.nearestAbove( lowLimit - origin, spacing ) + origin;
		double x2 = Arithmetic.nearestBelow( highLimit - origin, spacing ) + origin;

		int count = (int)((x2 - x1) / spacing) + 1;
		List<Double> offsets = new ArrayList<>( count );
		double max = radial ? 360 : Double.MAX_VALUE;

		for( int index = 0; index < count; index++ ) {
			double value = index * spacing + x1;
			if( value <= x2 && value < max ) offsets.add( value );
		}

		return offsets;
	}

}
