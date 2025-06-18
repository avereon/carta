package com.avereon.cartesia.tool;

import com.avereon.cartesia.math.CadShapes;
import com.avereon.curve.math.Arithmetic;
import com.avereon.curve.math.Constants;
import com.avereon.marea.Shape2d;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class GridPolar implements Grid {

	@Override
	public String name() {
		return "POLAR";
	}

	@Override
	public Point3D getNearest( Workplane workplane, Point3D point ) {
		// This can be determined by calculating the nearest point
		// and then converting from polar to cartesian coordinates
		Point3D origin = CadShapes.parsePoint( workplane.getOrigin() );
		point = point.subtract( origin );
		point = CadShapes.cartesianToPolarDegrees( point );

		point = new Point3D( Arithmetic.nearest( point.getX(), workplane.calcSnapGridX() ),
			Arithmetic.nearest( point.getY(), workplane.calcSnapGridY() ),
			0
		);

		point = CadShapes.polarDegreesToCartesian( point );
		point = point.add( origin );

		return point;
	}

	@Override
	public List<Shape2d> createMareaGeometryGrid( Workplane workplane ) {
		return switch( workplane.getGridStyle() ) {
			case DOT -> generateMareaGridDots( workplane );
			case LINE -> generateMareaGridLines( workplane );
		};
	}

	private List<Shape2d> generateMareaGridDots( Workplane workplane ) {
		return List.of();
	}

	private List<Shape2d> generateMareaGridLines( Workplane workplane ) {
		return List.of();
	}

	@Override
	public List<Shape> createFxGeometryGrid( Workplane workplane ) {
		// The x spacing will be radius
		// The y spacing will be angle in degrees

		List<Shape> grid = new ArrayList<>();

		Point3D origin = CadShapes.parsePoint( workplane.getOrigin() );
		double boundaryXmin = Math.min( workplane.getBoundaryX1(), workplane.getBoundaryX2() ) - origin.getX();
		double boundaryXmax = Math.max( workplane.getBoundaryX1(), workplane.getBoundaryX2() ) - origin.getX();
		double boundaryYmin = Math.min( workplane.getBoundaryY1(), workplane.getBoundaryY2() ) - origin.getY();
		double boundaryYmax = Math.max( workplane.getBoundaryY1(), workplane.getBoundaryY2() ) - origin.getY();
		boolean axisVisible = workplane.isGridAxisVisible();
		Paint axisPaint = workplane.calcGridAxisPaint();
		double axisWidth = workplane.calcGridAxisWidth();
		boolean majorVisible = workplane.isMajorGridShowing() && workplane.isMajorGridVisible();
		Paint majorPaint = workplane.calcMajorGridPaint();
		double majorWidth = workplane.calcMajorGridWidth();
		boolean minorVisible = workplane.isMinorGridShowing() && workplane.isMinorGridVisible();
		Paint minorPaint = workplane.calcMinorGridPaint();
		double minorWidth = workplane.calcMinorGridWidth();

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
		List<Double> majorOffsetsR = Grid.getOffsets( 0, majorIntervalR, boundaryRmin, boundaryRmax );
		List<Double> minorOffsetsR = Grid.getOffsets( 0, minorIntervalR, boundaryRmin, boundaryRmax );
		List<Double> axisOffsetsA = List.of( 0.0, 90.0, 180.0, 270.0 );
		List<Double> majorOffsetsA = Grid.getOffsets( 0, majorIntervalA, 0, 360, true );
		List<Double> minorOffsetsA = Grid.getOffsets( 0, minorIntervalA, 0, 360, true );

		// Check for conflicts
		if( majorVisible ) {
			minorOffsetsR.removeIf( value -> Grid.isNearAny( value, majorOffsetsR ) );
			minorOffsetsA.removeIf( value -> Grid.isNearAny( value, majorOffsetsA ) );
		}
		if( axisVisible ) {
			majorOffsetsR.removeIf( value -> value < Constants.RESOLUTION_LENGTH );
			majorOffsetsA.removeIf( value -> Grid.isNearAny( value, axisOffsetsA ) );
		}

		// Circles (radius) need to be centered at origin
		double maxR = 0;
		if( minorVisible ) {
			for( double value : minorOffsetsR ) {
				if( value > maxR ) maxR = value;
				Circle shape = new Circle( origin.getX(), origin.getY(), value );
				shape.setStroke( minorPaint );
				shape.setFill( null );
				grid.add( shape );
			}
		}
		if( majorVisible ) {
			for( double value : majorOffsetsR ) {
				if( value > maxR ) maxR = value;
				Circle shape = new Circle( origin.getX(), origin.getY(), value );
				shape.setStroke( majorPaint );
				shape.setFill( null );
				grid.add( shape );
			}
		}

		// Lines (angles) need to be centered at origin
		if( minorVisible ) {
			for( double value : minorOffsetsA ) {
				Point3D p = CadShapes.polarDegreesToCartesian( new Point3D( maxR, value, 0 ) );
				// The center can get a bit crowded, can I fix this?
				Line shape = new Line( origin.getX(), origin.getY(), origin.getX() + p.getX(), origin.getY() + p.getY() );
				shape.setStroke( minorPaint );
				shape.setStrokeWidth( minorWidth );
				grid.add( shape );
			}
		}
		if( majorVisible ) {
			for( double value : majorOffsetsA ) {
				Point3D p = CadShapes.polarDegreesToCartesian( new Point3D( maxR, value, 0 ) );
				// The center can get a bit crowded, can I fix this?
				Line shape = new Line( origin.getX(), origin.getY(), origin.getX() + p.getX(), origin.getY() + p.getY() );
				shape.setStroke( majorPaint );
				shape.setStrokeWidth( majorWidth );
				grid.add( shape );
			}
		}

		if( axisVisible ) {
			for( double value : axisOffsetsA ) {
				Point3D p = CadShapes.polarDegreesToCartesian( new Point3D( maxR, value, 0 ) );
				// The center can get a bit crowded, can I fix this?
				Line shape = new Line( origin.getX(), origin.getY(), origin.getX() + p.getX(), origin.getY() + p.getY() );
				shape.setStroke( axisPaint );
				shape.setStrokeWidth( axisWidth );
				grid.add( shape );
			}
		}

		grid.forEach( s -> s.setStrokeWidth( 0.05 ) );

		return grid;
	}

}
