package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.data.DesignShapeDistanceComparator;
import com.avereon.curve.math.Geometry;
import com.avereon.curve.math.Point;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

import java.util.*;

public class Shapes {

	private static final System.Logger log = Log.get();

//	public static Point3D polarToCartesian( Point3D point ) {
//		return toFxPoint( Geometry.polarToCartesian( asPoint( point ) ) );
//	}
//
//	public static Point3D cartesianToPolar( Point3D point ) {
//		return toFxPoint( Geometry.cartesianToPolar( asPoint( point ) ) );
//	}

	public static Point3D polarDegreesToCartesian( Point3D point ) {
		return toFxPoint( Geometry.polarDegreesToCartesian( asPoint( point ) ) );
	}

	public static Point3D cartesianToPolarDegrees( Point3D point ) {
		return toFxPoint( Geometry.cartesianToPolarDegrees( asPoint( point ) ) );
	}

	public static Point3D parsePoint( String input ) {
		return parsePoint( input, null );
	}

	public static Point3D parsePoint( String input, Point3D anchor ) {
		input = Objects.requireNonNull( input ).trim();

		try {
			boolean relative = false;
			boolean polar = false;
			boolean reverse = false;

			// Relative
			if( input.charAt( 0 ) == '@' ) {
				input = input.substring( 1 ).trim();
				relative = true;
			}

			// Polar (radius first)
			if( input.charAt( 0 ) == '<' ) {
				input = input.substring( 1 ).trim();
				polar = true;
			}

			// Reverse Polar (angle first)
			if( input.charAt( 0 ) == '>' ) {
				input = input.substring( 1 ).trim();
				polar = true;
				reverse = true;
			}

			String[] coords = input.split( "," );
			Point3D point = switch( coords.length ) {
				case 1 -> new Point3D( Maths.eval( coords[ 0 ] ), 0, 0 );
				case 2 -> new Point3D( Maths.eval( coords[ 0 ] ), Maths.eval( coords[ 1 ] ), 0 );
				case 3 -> new Point3D( Maths.eval( coords[ 0 ] ), Maths.eval( coords[ 1 ] ), Maths.eval( coords[ 2 ] ) );
				default -> null;
			};

			if( point == null ) return null;
			if( polar ) {
				if( reverse ) {
					point = fromPolar( new Point3D( point.getY(), point.getX(), point.getZ() ) );
				} else {
					point = fromPolar( point );
				}
			}
			if( relative ) {
				if( anchor != null ) {
					point = anchor.add( point );
				} else {
					log.log( Log.ERROR, "Missing anchor for relative point: {0}", input );
					return null;
				}
			}
			return point;
		} catch( ExpressionException exception ) {
			return null;
		}
	}

	public static DesignShape findNearestShapeToPoint( Collection<DesignShape> shapes, Point3D point ) {
		if( shapes.isEmpty() ) return DesignShape.NONE;
		if( shapes.size() == 1 ) return shapes.iterator().next();

		List<DesignShape> sortedShapes = new ArrayList<>( shapes );
		sortedShapes.sort( new DesignShapeDistanceComparator( point ) );
		return sortedShapes.get(0);
	}

	public static double[] asPoint( Point3D point ) {
		return Point.of( point.getX(), point.getY(), point.getZ() );
	}

	public static Point3D toFxPoint( double[] point ) {
		return new Point3D( point[ 0 ], point[ 1 ], point[ 2 ] );
	}

	static Point3D fromPolar( Point3D point ) {
		return toFxPoint( Geometry.polarToCartesian( Point.of( point.getX(), point.getY() ) ) );
	}

}
