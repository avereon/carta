package com.avereon.cartesia.tool;

import com.avereon.cartesia.math.Constants;
import com.avereon.cartesia.math.Geometry;
import com.avereon.math.Arithmetic;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class CoordinateSystemPolar implements CoordinateSystem {

	@Override
	public Point3D getNearest( Workplane workplane, Point3D point ) {
		// This can be determined by calculating the nearest point
		// and then converting from polar to cartesian coordinates
		Point3D origin = Geometry.parsePoint( workplane.getOrigin() );
		point = point.subtract( origin );
		point = Geometry.cartesianToPolar( point );

		point = new Point3D(
			Arithmetic.nearest( point.getX(), workplane.calcSnapGridX() ),
			Arithmetic.nearest( point.getY(), workplane.calcSnapGridY() ),
			Arithmetic.nearest( point.getZ(), workplane.calcSnapGridZ() )
		);

		point = Geometry.polarToCartesian( point );
		point = point.add( origin );

		return point;
	}

	@Override
	public List<Shape> getGridDots( Workplane workplane ) {
		return List.of();
	}

	@Override
	public List<Shape> getGridLines( Workplane workplane ) {
		// The x spacing will be radius
		// The y spacing will be angle in degrees

		List<Shape> grid = new ArrayList<>();

		Point3D origin = Geometry.parsePoint( workplane.getOrigin() );
		double boundaryXmin = Math.min( workplane.getBoundaryX1(), workplane.getBoundaryX2() ) - origin.getX();
		double boundaryXmax = Math.max( workplane.getBoundaryX1(), workplane.getBoundaryX2() ) - origin.getX();
		double boundaryYmin = Math.min( workplane.getBoundaryY1(), workplane.getBoundaryY2() ) - origin.getY();
		double boundaryYmax = Math.max( workplane.getBoundaryY1(), workplane.getBoundaryY2() ) - origin.getY();

		Point3D a = new Point3D( boundaryXmin, boundaryYmin, 0 );
		Point3D b = new Point3D( boundaryXmin, boundaryYmax, 0 );
		Point3D c = new Point3D( boundaryXmax, boundaryYmin, 0 );
		Point3D d = new Point3D( boundaryXmax, boundaryYmax, 0 );
		double da = Point3D.ZERO.distance( a );
		double db = Point3D.ZERO.distance( b );
		double dc = Point3D.ZERO.distance( c );
		double dd = Point3D.ZERO.distance( d );
		double minX = Math.min( Math.abs( b.getX() ), Math.abs( c.getX() ) );
		double minY = Math.min( Math.abs( a.getY() ), Math.abs( b.getY() ) );

		BoundingBox bb = new BoundingBox( boundaryXmin, boundaryYmin, boundaryXmax - boundaryXmin, boundaryYmax - boundaryYmin );
		double boundaryRmin = bb.contains( Point3D.ZERO ) ? 0.0 : Math.min( minX, minY );
		double boundaryRmax = Math.max( da, Math.max( db, Math.max( dc, dd ) ) );

		double majorIntervalR = workplane.calcMajorGridX();
		double majorIntervalA = workplane.calcMajorGridY();
		double minorIntervalR = workplane.calcMinorGridX();
		double minorIntervalA = workplane.calcMinorGridY();

		// Get all offsets
		List<Double> majorOffsetsR = CoordinateSystem.getOffsets( 0, majorIntervalR, boundaryRmin, boundaryRmax );
		List<Double> minorOffsetsR = CoordinateSystem.getOffsets( 0, minorIntervalR, boundaryRmin, boundaryRmax );
		List<Double> axisOffsetsA = List.of( 0.0, 90.0, 180.0, 270.0 );
		List<Double> majorOffsetsA = CoordinateSystem.getOffsets( 0, majorIntervalA, 0, 360, true );
		List<Double> minorOffsetsA = CoordinateSystem.getOffsets( 0, minorIntervalA, 0, 360, true );

		// Check for conflicts
		minorOffsetsR.removeIf( value -> CoordinateSystem.isNearAny( value, majorOffsetsR ) );
		majorOffsetsR.removeIf( value -> value < Constants.DISTANCE_TOLERANCE );
		minorOffsetsA.removeIf( value -> CoordinateSystem.isNearAny( value, majorOffsetsA ) );
		majorOffsetsA.removeIf( value -> CoordinateSystem.isNearAny( value, axisOffsetsA ) );

		// Circles (radius) need to be centered at origin
		double maxR = 0;
		for( double value : minorOffsetsR ) {
			if( value > maxR ) maxR = value;
			Circle shape = new Circle( origin.getX(), origin.getY(), value );
			shape.setStroke( Workplane.DEFAULT_MINOR_GRID_COLOR );
			shape.setFill( Color.TRANSPARENT );
			grid.add( shape );
		}
		for( double value : majorOffsetsR ) {
			if( value > maxR ) maxR = value;
			Circle shape = new Circle( origin.getX(), origin.getY(), value );
			shape.setStroke( Workplane.DEFAULT_MAJOR_GRID_COLOR );
			shape.setFill( Color.TRANSPARENT );
			grid.add( shape );
		}
		// Lines (angles) need to be centered at origin
		for( double value : minorOffsetsA ) {
			Point3D p = Geometry.polarToCartesian( new Point3D( maxR, value, 0 ) );
			// The center can get a bit crowded, can I fix this?
			Line shape = new Line( origin.getX(), origin.getY(), origin.getX() + p.getX(), origin.getY() + p.getY() );
			shape.setStroke( Workplane.DEFAULT_MINOR_GRID_COLOR );
			grid.add( shape );
		}
		for( double value : majorOffsetsA ) {
			Point3D p = Geometry.polarToCartesian( new Point3D( maxR, value, 0 ) );
			// The center can get a bit crowded, can I fix this?
			Line shape = new Line( origin.getX(), origin.getY(), origin.getX() + p.getX(), origin.getY() + p.getY() );
			shape.setStroke( Workplane.DEFAULT_MAJOR_GRID_COLOR );
			grid.add( shape );
		}
		for( double value : axisOffsetsA ) {
			Point3D p = Geometry.polarToCartesian( new Point3D( maxR, value, 0 ) );
			// The center can get a bit crowded, can I fix this?
			Line shape = new Line( origin.getX(), origin.getY(), origin.getX() + p.getX(), origin.getY() + p.getY() );
			shape.setStroke( Workplane.DEFAULT_AXIS_COLOR );
			grid.add( shape );
		}

		grid.forEach( s -> s.setStrokeWidth( 0.05 ) );

		return grid;
	}

}
