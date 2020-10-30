package com.avereon.cartesia.tool;

import com.avereon.cartesia.math.Constants;
import com.avereon.math.Arithmetic;
import javafx.geometry.Point3D;
import javafx.scene.shape.Line;

import java.util.*;

public class CoordinateSystemOrthographic implements CoordinateSystem {

	@Override
	public Point3D getNearest( Workplane workplane, Point3D point ) {
		point = point.subtract( workplane.getOrigin() );
		point = new Point3D(
			Arithmetic.nearest( point.getX(), workplane.getSnapSpacingX() ),
			Arithmetic.nearest( point.getY(), workplane.getSnapSpacingY() ),
			Arithmetic.nearest( point.getZ(), workplane.getSnapSpacingZ() )
		);
		point = point.add( workplane.getOrigin() );
		return point;
	}

	@Override
	public Set<Line> getGridLines( Workplane workplane ) {
		Set<Line> grid = new HashSet<>();

		Point3D origin = workplane.getOrigin();
		double boundaryX1 = Math.min( workplane.getBoundaryX1(), workplane.getBoundaryX2() );
		double boundaryX2 = Math.max( workplane.getBoundaryX1(), workplane.getBoundaryX2() );
		double boundaryY1 = Math.min( workplane.getBoundaryY1(), workplane.getBoundaryY2() );
		double boundaryY2 = Math.max( workplane.getBoundaryY1(), workplane.getBoundaryY2() );
		double minorIntervalX = workplane.getMinorIntervalX();
		double minorIntervalY = workplane.getMinorIntervalY();
		double majorIntervalX = workplane.getMajorIntervalX();
		double majorIntervalY = workplane.getMajorIntervalY();

		// Get all offsets
		List<Double> axisOffsetsX = new ArrayList<>();
		List<Double> axisOffsetsY = new ArrayList<>();
		if( origin.getX() >= boundaryX1 && origin.getX() <= boundaryX2 ) axisOffsetsX.add( origin.getX() );
		if( origin.getY() >= boundaryY1 && origin.getY() <= boundaryY2 ) axisOffsetsY.add( origin.getY() );
		List<Double> majorOffsetsX = getOffsets( origin.getX(), majorIntervalX, boundaryX1, boundaryX2 );
		List<Double> majorOffsetsY = getOffsets( origin.getY(), majorIntervalY, boundaryY1, boundaryY2 );
		List<Double> minorOffsetsX = getOffsets( origin.getX(), minorIntervalX, boundaryX1, boundaryX2 );
		List<Double> minorOffsetsY = getOffsets( origin.getY(), minorIntervalY, boundaryY1, boundaryY2 );

		// Check for conflicts
		minorOffsetsX.removeIf( value -> isNearAny( value, majorOffsetsX ) );
		minorOffsetsY.removeIf( value -> isNearAny( value, majorOffsetsY ) );
		majorOffsetsX.removeIf( value -> isNearAny( value, axisOffsetsX ) );
		majorOffsetsY.removeIf( value -> isNearAny( value, axisOffsetsY ) );

		for( double value : minorOffsetsX ) {
			Line line = new Line( value, boundaryY1, value, boundaryY2 );
			line.setStroke( Workplane.DEFAULT_MINOR_GRID_COLOR );
			grid.add( line );
		}
		for( double value : minorOffsetsY ) {
			Line line = new Line( boundaryX1, value, boundaryX2, value );
			line.setStroke( Workplane.DEFAULT_MINOR_GRID_COLOR );
			grid.add( line );
		}
		for( double value : majorOffsetsX ) {
			Line line = new Line( value, boundaryY1, value, boundaryY2 );
			line.setStroke( Workplane.DEFAULT_MAJOR_GRID_COLOR );
			grid.add( line );
		}
		for( double value : majorOffsetsY ) {
			Line line = new Line( boundaryX1, value, boundaryX2, value );
			line.setStroke( Workplane.DEFAULT_MAJOR_GRID_COLOR );
			grid.add( line );
		}
		for( double value : axisOffsetsX ) {
			Line line = new Line( value, boundaryY1, value, boundaryY2 );
			line.setStroke( Workplane.DEFAULT_AXIS_COLOR );
			grid.add( line );
		}
		for( double value : axisOffsetsY ) {
			Line line = new Line( boundaryX1, value, boundaryX2, value );
			line.setStroke( Workplane.DEFAULT_AXIS_COLOR );
			grid.add( line );
		}

		return grid;
	}

	boolean isNearAny( Double value, Collection<Double> values ) {
		for( Double check : values ) {
			if( Math.abs( check - value ) <= Constants.DISTANCE_TOLERANCE ) return true;
		}
		return false;
	}

	List<Double> getOffsets( double origin, double spacing, double lowLimit, double highLimit ) {
		double x1 = Arithmetic.nearestAbove( lowLimit - origin, spacing ) + origin;
		double x2 = Arithmetic.nearestBelow( highLimit - origin, spacing ) + origin;

		int count = (int)((x2 - x1) / spacing) + 1;
		List<Double> offsets = new ArrayList<>( count );

		for( int index = 0; index < count; index++ ) {
			double value = index * spacing + x1;
			if( value <= x2 ) offsets.add( value );
		}

		return offsets;
	}

}
