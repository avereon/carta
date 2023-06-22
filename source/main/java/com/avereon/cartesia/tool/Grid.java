package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.curve.math.Arithmetic;
import com.avereon.curve.math.Constants;
import com.avereon.marea.Shape2d;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface Grid {

	Grid ORTHO = new GridOrthographic();

	Grid POLAR = new GridPolar();

	Grid ISO = new GridIsometric();

	String name();

	Point3D getNearest( DesignWorkplane workplane, Point3D point );

	default Set<Shape2d> createAxisDots( DesignWorkplane workplane ) {
		return Set.of();
	}

	default Set<Shape2d> createMajorDots( DesignWorkplane workplane ) {
		return Set.of();
	}

	default Set<Shape2d> createMinorDots( DesignWorkplane workplane ) {
		return Set.of();
	}

	default Set<Shape2d> createAxisLines( DesignWorkplane workplane ) {
		return Set.of();
	}

	default Set<Shape2d> createMajorLines( DesignWorkplane workplane ) {
		return Set.of();
	}

	default Set<Shape2d> createMinorLines( DesignWorkplane workplane ) {
		return Set.of();
	}

	List<DesignShape> generateGrid( DesignWorkplane workplane, GridStyle style );

	@Deprecated
	List<Shape> getGridDots( DesignWorkplane workplane ) throws Exception;

	//	double[][] getGridDotsNew();

	/**
	 * @param workplane
	 * @return
	 * @deprecated Unfortunately this method return FX shapes instead of something
	 * simpler
	 */
	@Deprecated
	List<Shape> getGridLines( DesignWorkplane workplane );

	static Grid valueOf( String name ) {
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
