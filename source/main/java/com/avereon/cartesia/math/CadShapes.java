package com.avereon.cartesia.math;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.data.DesignShapeDistanceComparator;
import com.avereon.curve.math.Geometry;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@CustomLog
public class CadShapes {

	//	public static Point3D polarToCartesian( Point3D point ) {
	//		return toFxPoint( Geometry.polarToCartesian( asPoint( point ) ) );
	//	}
	//
	//	public static Point3D cartesianToPolar( Point3D point ) {
	//		return toFxPoint( Geometry.cartesianToPolar( asPoint( point ) ) );
	//	}

	public static Point3D polarDegreesToCartesian( Point3D point ) {
		return CadPoints.toFxPoint( Geometry.polarDegreesToCartesian( CadPoints.asPoint( point ) ) );
	}

	public static Point3D cartesianToPolarDegrees( Point3D point ) {
		return CadPoints.toFxPoint( Geometry.cartesianToPolarDegrees( CadPoints.asPoint( point ) ) );
	}

	public static String toString( Point3D point ) {
		return point.getX() + "," + point.getY() + "," + point.getZ();
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
				case 1 -> new Point3D( CadMath.eval( coords[ 0 ] ), 0, 0 );
				case 2 -> new Point3D( CadMath.eval( coords[ 0 ] ), CadMath.eval( coords[ 1 ] ), 0 );
				case 3 -> new Point3D( CadMath.eval( coords[ 0 ] ), CadMath.eval( coords[ 1 ] ), CadMath.eval( coords[ 2 ] ) );
				default -> null;
			};

			if( point == null ) return null;
			if( polar ) {
				if( reverse ) {
					point = CadPoints.fromPolar( new Point3D( point.getY(), point.getX(), point.getZ() ) );
				} else {
					point = CadPoints.fromPolar( point );
				}
			}
			if( relative ) {
				if( anchor != null ) {
					point = anchor.add( point );
				} else {
					log.atSevere().log( "Missing anchor for relative point: %s", input );
					return null;
				}
			}
			return point;
		} catch( CadMathExpressionException exception ) {
			return null;
		}
	}

	public static List<Double> parseDashPattern( String pattern ) {
		List<Double> values = CadMath.evalExpressions( pattern );
		double sum = values.stream().reduce( Double::sum ).orElse(0.0);
		if( sum == 0.0 ) return List.of();
		return values;
	}

	public static DesignShape findNearestShapeToPoint( Collection<DesignShape> shapes, Point3D point ) {
		if( shapes.isEmpty() ) return DesignShape.NONE;
		if( shapes.size() == 1 ) return shapes.iterator().next();

		List<DesignShape> sortedShapes = new ArrayList<>( shapes );
		sortedShapes.sort( new DesignShapeDistanceComparator( point ) );
		return sortedShapes.get( 0 );
	}

}
