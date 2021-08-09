package com.avereon.cartesia.tool;

import com.avereon.cartesia.math.CadShapes;
import com.avereon.curve.math.Arithmetic;
import javafx.geometry.Point3D;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class CoordinateSystemOrthographic implements CoordinateSystem {

	@Override
	public String name() {
		return "ORTHO";
	}

	@Override
	public Point3D getNearest( DesignWorkplane workplane, Point3D point ) {
		Point3D origin = CadShapes.parsePoint( workplane.getOrigin() );
		point = point.subtract( origin );
		point = new Point3D( Arithmetic.nearest( point.getX(), workplane.calcSnapGridX() ),
			Arithmetic.nearest( point.getY(), workplane.calcSnapGridY() ),
			Arithmetic.nearest( point.getZ(), workplane.calcSnapGridZ() )
		);
		point = point.add( origin );
		return point;
	}

	@Override
	public List<Shape> getGridDots( DesignWorkplane workplane ) {
		return List.of();
	}

	@Override
	public List<Shape> getGridLines( DesignWorkplane workplane ) {
		List<Shape> grid = new ArrayList<>();

		Point3D origin = CadShapes.parsePoint( workplane.getOrigin() );
		double boundaryX1 = Math.min( workplane.getBoundaryX1(), workplane.getBoundaryX2() );
		double boundaryX2 = Math.max( workplane.getBoundaryX1(), workplane.getBoundaryX2() );
		double boundaryY1 = Math.min( workplane.getBoundaryY1(), workplane.getBoundaryY2() );
		double boundaryY2 = Math.max( workplane.getBoundaryY1(), workplane.getBoundaryY2() );
		double majorIntervalX = workplane.calcMajorGridX();
		double majorIntervalY = workplane.calcMajorGridY();
		double minorIntervalX = workplane.calcMinorGridX();
		double minorIntervalY = workplane.calcMinorGridY();
		boolean axisVisible = workplane.isGridAxisVisible();
		boolean majorVisible = workplane.isMajorGridShowing() && workplane.isMajorGridVisible();
		boolean minorVisible = workplane.isMinorGridShowing() && workplane.isMinorGridVisible();

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
		if( majorVisible ) {
			minorOffsetsX.removeIf( value -> CoordinateSystem.isNearAny( value, majorOffsetsX ) );
			minorOffsetsY.removeIf( value -> CoordinateSystem.isNearAny( value, majorOffsetsY ) );
		}
		if( axisVisible ) {
			majorOffsetsX.removeIf( value -> CoordinateSystem.isNearAny( value, axisOffsetsX ) );
			majorOffsetsY.removeIf( value -> CoordinateSystem.isNearAny( value, axisOffsetsY ) );
		}

		double strokeWidthX = 0.1 * minorIntervalX;
		double strokeWidthY = 0.1 * minorIntervalY;

		if( minorVisible ) {
			for( double value : minorOffsetsX ) {
				Line shape = new Line( value, boundaryY1, value, boundaryY2 );
				shape.setStroke( DesignWorkplane.DEFAULT_GRID_MINOR_COLOR );
				shape.setStrokeWidth( strokeWidthX );
				grid.add( shape );
			}
			for( double value : minorOffsetsY ) {
				Line shape = new Line( boundaryX1, value, boundaryX2, value );
				shape.setStroke( DesignWorkplane.DEFAULT_GRID_MINOR_COLOR );
				shape.setStrokeWidth( strokeWidthY );
				grid.add( shape );
			}
		}

		if( majorVisible ) {
			for( double value : majorOffsetsX ) {
				Line shape = new Line( value, boundaryY1, value, boundaryY2 );
				shape.setStroke( DesignWorkplane.DEFAULT_GRID_MAJOR_COLOR );
				shape.setStrokeWidth( strokeWidthX );
				grid.add( shape );
			}
			for( double value : majorOffsetsY ) {
				Line shape = new Line( boundaryX1, value, boundaryX2, value );
				shape.setStroke( DesignWorkplane.DEFAULT_GRID_MAJOR_COLOR );
				shape.setStrokeWidth( strokeWidthY );
				grid.add( shape );
			}
		}

		if( axisVisible ) {
			for( double value : axisOffsetsX ) {
				Line shape = new Line( value, boundaryY1, value, boundaryY2 );
				shape.setStroke( DesignWorkplane.DEFAULT_GRID_AXIS_COLOR );
				shape.setStrokeWidth( strokeWidthX );
				grid.add( shape );
			}
			for( double value : axisOffsetsY ) {
				Line shape = new Line( boundaryX1, value, boundaryX2, value );
				shape.setStroke( DesignWorkplane.DEFAULT_GRID_AXIS_COLOR );
				shape.setStrokeWidth( strokeWidthY );
				grid.add( shape );
			}
		}

		return grid;
	}

}
