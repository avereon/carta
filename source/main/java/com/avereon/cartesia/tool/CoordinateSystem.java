package com.avereon.cartesia.tool;

import com.avereon.curve.math.Arithmetic;
import com.avereon.curve.math.Constants;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface CoordinateSystem {

	CoordinateSystem ORTHO = new CoordinateSystemOrthographic();

	CoordinateSystem POLAR = new CoordinateSystemPolar();

	CoordinateSystem ISO = new CoordinateSystemIsometric();

	String name();

	Point3D getNearest( DesignWorkplane workplane, Point3D point );

	/**
	 * @param workplane
	 * @return
	 * @deprecated Unfortunately this method return FX shapes instead of something
	 * simpler
	 */
	@Deprecated
	List<Shape> getGridDots( DesignWorkplane workplane );

//	double[][] getGridDotsNew();

	/**
	 * @param workplane
	 * @return
	 * @deprecated Unfortunately this method return FX shapes instead of something
	 * simpler
	 */
	@Deprecated
	List<Shape> getGridLines( DesignWorkplane workplane );

	static CoordinateSystem valueOf( String name ) {
		return switch( name ) {
			case "POLAR" -> POLAR;
			case "ISO" -> ISO;
			default -> ORTHO;
		};
	}

	static boolean isNearAny( Double value, Collection<Double> values ) {
		for( Double check : values ) {
			if( Math.abs( check - value ) <= Constants.RESOLUTION_LENGTH ) return true;
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
