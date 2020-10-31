package com.avereon.cartesia.tool;

import com.avereon.math.Arithmetic;
import javafx.geometry.Point3D;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

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
	public List<Shape> getGridDots( Workplane workplane ) {
		return List.of();
	}

	@Override
	public List<Shape> getGridLines( Workplane workplane ) {
		List<Shape> grid = new ArrayList<>();

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
		List<Double> majorOffsetsX = CoordinateSystem.getOffsets( origin.getX(), majorIntervalX, boundaryX1, boundaryX2 );
		List<Double> majorOffsetsY = CoordinateSystem.getOffsets( origin.getY(), majorIntervalY, boundaryY1, boundaryY2 );
		List<Double> minorOffsetsX = CoordinateSystem.getOffsets( origin.getX(), minorIntervalX, boundaryX1, boundaryX2 );
		List<Double> minorOffsetsY = CoordinateSystem.getOffsets( origin.getY(), minorIntervalY, boundaryY1, boundaryY2 );

		// Check for conflicts
		minorOffsetsX.removeIf( value -> CoordinateSystem.isNearAny( value, majorOffsetsX ) );
		minorOffsetsY.removeIf( value -> CoordinateSystem.isNearAny( value, majorOffsetsY ) );
		majorOffsetsX.removeIf( value -> CoordinateSystem.isNearAny( value, axisOffsetsX ) );
		majorOffsetsY.removeIf( value -> CoordinateSystem.isNearAny( value, axisOffsetsY ) );

		for( double value : minorOffsetsX ) {
			Line shape = new Line( value, boundaryY1, value, boundaryY2 );
			shape.setStroke( Workplane.DEFAULT_MINOR_GRID_COLOR );
			grid.add( shape );
		}
		for( double value : minorOffsetsY ) {
			Line shape = new Line( boundaryX1, value, boundaryX2, value );
			shape.setStroke( Workplane.DEFAULT_MINOR_GRID_COLOR );
			grid.add( shape );
		}
		for( double value : majorOffsetsX ) {
			Line shape = new Line( value, boundaryY1, value, boundaryY2 );
			shape.setStroke( Workplane.DEFAULT_MAJOR_GRID_COLOR );
			grid.add( shape );
		}
		for( double value : majorOffsetsY ) {
			Line shape = new Line( boundaryX1, value, boundaryX2, value );
			shape.setStroke( Workplane.DEFAULT_MAJOR_GRID_COLOR );
			grid.add( shape );
		}
		for( double value : axisOffsetsX ) {
			Line shape = new Line( value, boundaryY1, value, boundaryY2 );
			shape.setStroke( Workplane.DEFAULT_AXIS_COLOR );
			grid.add( shape );
		}
		for( double value : axisOffsetsY ) {
			Line shape = new Line( boundaryX1, value, boundaryX2, value );
			shape.setStroke( Workplane.DEFAULT_AXIS_COLOR );
			grid.add( shape );
		}

		grid.forEach( s -> s.setStrokeWidth( 0.05 ) );

		return grid;
	}

}
