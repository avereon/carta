package com.avereon.cartesia.tool;

import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.curve.math.Arithmetic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.*;

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

		point = new Point3D( Arithmetic.nearest( point.getX(), workplane.calcSnapGridX() ), Arithmetic.nearest( point.getY(), workplane.calcSnapGridY() ), 0 );

		point = CadShapes.polarDegreesToCartesian( point );
		point = point.add( origin );

		return point;
	}

	@Override
	public Collection<Shape> createFxGeometryGrid( Workplane workplane, double scale ) {
		return updateFxGeometryGrid( workplane, scale, FXCollections.observableArrayList() );
	}

	@Override
	public Collection<Shape> updateFxGeometryGrid( Workplane workplane, double scale, ObservableList<Node> existing ) {
		if( workplane == null ) return Collections.emptyList();

		// The x spacing will be radius
		// The y spacing will be angle in degrees
		// (it could also be a distance used to determine what angle lines to show)

		// Map the existing geometry from the node list to collections
		final Set<Circle> circles = new HashSet<>();
		final Set<Line> lines = new HashSet<>();
		existing.forEach( s -> {
			if( s instanceof Line line ) {
				lines.add( line );
			} else {
				if( s instanceof Circle circle ) {
					circles.add( circle );
				}
			}
		} );

		// This will become the collection of grid geometry after the update
		Set<Shape> grid = new HashSet<>();

		Point2D origin = CadPoints.toPoint2d( CadShapes.parsePoint( workplane.getOrigin() ) ).multiply( scale );
		double originX = origin.getX();
		double originY = origin.getY();

		boolean axisVisible = workplane.isGridAxisVisible();
		Paint axisPaint = workplane.calcGridAxisPaint();
		double axisWidth = workplane.calcGridAxisWidth() * scale;
		axisWidth = 2.0;

		boolean majorVisible = workplane.isMajorGridShowing() && workplane.isMajorGridVisible();
		double majorIntervalR = workplane.calcMajorGridX() * scale;
		double majorIntervalA = workplane.calcMajorGridY() * scale;
		Paint majorPaint = workplane.calcMajorGridPaint();
		double majorWidth = workplane.calcMajorGridWidth() * scale;
		majorWidth = 1.0;

		boolean minorVisible = workplane.isMinorGridShowing() && workplane.isMinorGridVisible();
		double minorIntervalR = workplane.calcMinorGridX() * scale;
		double minorIntervalA = workplane.calcMinorGridY() * scale;
		Paint minorPaint = workplane.calcMinorGridPaint();
		double minorWidth = workplane.calcMinorGridWidth() * scale;
		minorWidth = 0.5;

		double snapIntervalR = workplane.calcSnapGridX() * scale;
		double snapIntervalA = workplane.calcSnapGridY() * scale;

		double boundaryX1 = Grid.getBoundaryX1( workplane.getBoundaryX1(), workplane.getBoundaryX2(), scale, majorIntervalR );
		double boundaryX2 = Grid.getBoundaryX2( workplane.getBoundaryX1(), workplane.getBoundaryX2(), scale, majorIntervalR );
		double boundaryY1 = Grid.getBoundaryY1( workplane.getBoundaryY1(), workplane.getBoundaryY2(), scale, majorIntervalA );
		double boundaryY2 = Grid.getBoundaryY2( workplane.getBoundaryY1(), workplane.getBoundaryY2(), scale, majorIntervalA );

		// Create points for the corners of the view
		Point2D a = new Point2D( boundaryX1, boundaryY1 );
		Point2D b = new Point2D( boundaryX1, boundaryY2 );
		Point2D c = new Point2D( boundaryX2, boundaryY1 );
		Point2D d = new Point2D( boundaryX2, boundaryY2 );

		// Distances to the walls of the view
		double dw = Math.abs( boundaryX1 );
		double de = Math.abs( boundaryX2 );
		double dn = Math.abs( boundaryY1 );
		double ds = Math.abs( boundaryY2 );

		// Distances to the corners of the view
		double da = origin.distance( a );
		double db = origin.distance( b );
		double dc = origin.distance( c );
		double dd = origin.distance( d );

		BoundingBox bb = new BoundingBox( boundaryX1, boundaryY1, boundaryX2 - boundaryX1, boundaryY2 - boundaryY1 );
		boolean containsOrigin = bb.contains( origin );

		// Determine the smallest radius needed
		double boundaryRmin = 0.0;
		double boundaryRmax = Math.max( da, Math.max( db, Math.max( dc, dd ) ) );
		double boundaryAmin = -180.0;
		double boundaryAmax = 180.0;

		if( !containsOrigin ) {
			// Expand the minimum radius if possible
			boundaryRmin = Math.min( dw, Math.min( de, Math.min( dn, ds ) ) );

			// Determine the angles of all the corners from the origin
			double aa = CadGeometry.theta360( origin, a );
			double ab = CadGeometry.theta360( origin, b );
			double ac = CadGeometry.theta360( origin, c );
			double ad = CadGeometry.theta360( origin, d );

			boundaryAmin = Math.min( aa, Math.min( ab, Math.min( ac, ad ) ) );
			boundaryAmax = Math.max( aa, Math.max( ab, Math.max( ac, ad ) ) );
		}

		// If the largest angle is less than the smallest angle,
		// Then the sweep goes across positive 180
		// If the largest angle

		// Get all offsets
		List<Double> axisOffsetsX = new ArrayList<>();
		List<Double> axisOffsetsY = new ArrayList<>();
		if( originX >= boundaryX1 && originX <= boundaryX2 ) axisOffsetsX.add( originX );
		if( originY >= boundaryY1 && originY <= boundaryY2 ) axisOffsetsY.add( originY );
		List<Double> majorOffsetsR = Grid.getOffsets( 0, majorIntervalR, boundaryRmin, boundaryRmax );
		List<Double> minorOffsetsR = Grid.getOffsets( 0, minorIntervalR, boundaryRmin, boundaryRmax );
		//List<Double> majorOffsetsA = Grid.getOffsets( 0, majorIntervalA, boundaryAmin, boundaryAmax, true );
		//List<Double> minorOffsetsA = Grid.getOffsets( 0, minorIntervalA, boundaryAmin, boundaryAmax, true );
		List<Double> majorOffsetsA = Grid.getOffsets( 0, 10, boundaryAmin, boundaryAmax, true );
		List<Double> minorOffsetsA = Grid.getOffsets( 0, 2, boundaryAmin, boundaryAmax, true );

		double majorBoundaryR1 = majorOffsetsR.getFirst();
		double majorBoundaryR2 = majorOffsetsR.getLast();
		double majorBoundaryA1 = majorOffsetsA.getFirst();
		double majorBoundaryA2 = majorOffsetsA.getLast();

		// Check for conflicts
		if( workplane.getGridStyle() == GridStyle.LINE ) {
			if( majorVisible ) {
				minorOffsetsR.removeIf( value -> Grid.isNearAny( value, majorOffsetsR ) );
				minorOffsetsA.removeIf( value -> Grid.isNearAny( value, majorOffsetsA ) );
			}
			if( axisVisible ) {
				majorOffsetsR.removeIf( value -> Grid.isNearAny( value, axisOffsetsX ) );
				majorOffsetsA.removeIf( value -> Grid.isNearAny( value, axisOffsetsY ) );
			}
		}

		// Radii (circles) need to be centered at origin
		if( minorVisible ) {
			for( double value : minorOffsetsR ) {
				Circle shape = Grid.reuseOrNewCircle( circles, origin.getX(), origin.getY(), value );
				shape.setFill( null );
				shape.setStroke( minorPaint );
				shape.setStrokeWidth( minorWidth );
				//shape.setStrokeDashOffset( dashOffset );
				//shape.getStrokeDashArray().setAll( dashSpacingY );
				grid.add( shape );
			}
		}
		if( majorVisible ) {
			for( double value : majorOffsetsR ) {
				Circle shape = Grid.reuseOrNewCircle( circles, origin.getX(), origin.getY(), value );
				shape.setFill( null );
				shape.setStroke( majorPaint );
				shape.setStrokeWidth( majorWidth );
				//shape.setStrokeDashOffset( dashOffset );
				//shape.getStrokeDashArray().setAll( dashSpacingY );
				grid.add( shape );
			}
		}

		// Angles (lines) need to be centered at origin
		if( minorVisible ) {
			// Grid style
			double dashOffset = 0.0;
			Double[] dashSpacingX = new Double[]{};
			Double[] dashSpacingY = new Double[]{};
			if( workplane.getGridStyle() == GridStyle.CROSS ) {
				dashOffset = 0.25 * minorIntervalR;
				dashSpacingX = new Double[]{ 0.5 * minorIntervalR, 0.5 * minorIntervalR };
				dashSpacingY = new Double[]{ 0.5 * minorIntervalA, 0.5 * minorIntervalA };
			} else if( workplane.getGridStyle() == GridStyle.DOT ) {
				dashSpacingX = new Double[]{ 0.0, snapIntervalR };
				dashSpacingY = new Double[]{ 0.0, snapIntervalA };
			}

			for( double value : minorOffsetsA ) {
				Point2D p = CadShapes.polarDegreesToCartesian( new Point2D( majorBoundaryR2, value ) );
				// The center can get a bit crowded, can I fix this?
				Line shape = Grid.reuseOrNewLine( lines, origin.getX(), origin.getY(), origin.getX() + p.getX(), origin.getY() + p.getY() );
				shape.setStroke( minorPaint );
				shape.setStrokeWidth( minorWidth );
				shape.setStrokeDashOffset( dashOffset );
				shape.getStrokeDashArray().setAll( dashSpacingY );
				grid.add( shape );
			}
		}
		if( majorVisible ) {
			// Grid style
			double dashOffset = 0.0;
			Double[] dashSpacingX = new Double[]{};
			Double[] dashSpacingY = new Double[]{};
			if( workplane.getGridStyle() == GridStyle.CROSS ) {
				dashOffset = 0.25 * majorIntervalR;
				dashSpacingX = new Double[]{ 0.5 * majorIntervalR, 0.5 * majorIntervalR };
				dashSpacingY = new Double[]{ 0.5 * majorIntervalA, 0.5 * majorIntervalA };
			} else if( workplane.getGridStyle() == GridStyle.DOT ) {
				dashSpacingX = new Double[]{ 0.0, minorIntervalR };
				dashSpacingY = new Double[]{ 0.0, minorIntervalA };
			}

			for( double value : majorOffsetsA ) {
				Point2D p = CadShapes.polarDegreesToCartesian( new Point2D( majorBoundaryR2, value ) );
				// The center can get a bit crowded, can I fix this?
				Line shape = Grid.reuseOrNewLine( lines, origin.getX(), origin.getY(), origin.getX() + p.getX(), origin.getY() + p.getY() );
				shape.setStroke( majorPaint );
				shape.setStrokeWidth( majorWidth );
				shape.setStrokeDashOffset( dashOffset );
				shape.getStrokeDashArray().setAll( dashSpacingY );
				grid.add( shape );
			}
		}

		if( axisVisible ) {
			// Grid style
			double dashOffset = 0.0;
			Double[] dashSpacingX = new Double[]{};
			Double[] dashSpacingY = new Double[]{};
			// Match the style of the major grid
			if( workplane.getGridStyle() == GridStyle.CROSS ) {
				dashOffset = 0.25 * majorIntervalR;
				dashSpacingX = new Double[]{ 0.5 * majorIntervalR, 0.5 * majorIntervalR };
				dashSpacingY = new Double[]{ 0.5 * majorIntervalA, 0.5 * majorIntervalA };
			} else if( workplane.getGridStyle() == GridStyle.DOT ) {
				dashSpacingX = new Double[]{ 0.0, majorIntervalR };
				dashSpacingY = new Double[]{ 0.0, majorIntervalA };
			}

			// Lines
			for( double value : axisOffsetsX ) {
				Line shape = Grid.reuseOrNewLine( lines, value, -majorBoundaryR2, value, majorBoundaryR2 );
				shape.setStroke( axisPaint );
				shape.setStrokeWidth( axisWidth );
				shape.setStrokeDashOffset( dashOffset );
				shape.getStrokeDashArray().setAll( dashSpacingY );
				grid.add( shape );
			}
			for( double value : axisOffsetsY ) {
				Line shape = Grid.reuseOrNewLine( lines, -majorBoundaryR2, value, majorBoundaryR2, value );
				shape.setStroke( axisPaint );
				shape.setStrokeWidth( axisWidth );
				shape.setStrokeDashOffset( dashOffset );
				shape.getStrokeDashArray().setAll( dashSpacingX );
				grid.add( shape );
			}
		}

		existing.removeAll( circles );
		existing.removeAll( lines );
		existing.setAll( grid );

		return grid;
	}

}
