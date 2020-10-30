package com.avereon.cartesia.math;

import com.avereon.util.Log;
import javafx.geometry.Point3D;

import java.text.ParseException;
import java.util.Objects;

public class Geometry {

	private static final System.Logger log = Log.get();

	public static Point3D polarToCartesian( Point3D point ) {
		double x = point.getX() * Math.cos( Math.toRadians( point.getY() ) );
		double y = point.getX() * Math.sin( Math.toRadians( point.getY() ) );
		return new Point3D( x, y, 0 );
	}

	public static Point3D cartesianToPolar( Point3D point ) {
		double r = Point3D.ZERO.distance( point );
		double a = Math.toDegrees( Math.atan2( point.getY(), point.getX() ) );
		return new Point3D( r, a, 0 );
	}

	public static Point3D parsePoint( String input ) {
		return parsePoint( input, null );
	}

	public static Point3D parsePoint( String input, Point3D anchor ) {
		input = Objects.requireNonNull( input ).trim();

		try {
			boolean relative = false;
			boolean polar = false;

			if( input.charAt( 0 ) == '@' ) {
				input = input.substring( 1 ).trim();
				relative = true;
			}
			if( input.charAt( 0 ) == '>' || input.charAt( 0 ) == '<' ) {
				// Modifier > is for polar coords
				input = input.substring( 1 ).trim();
				polar = true;
			}

			String[] coords = input.split( "," );
			Point3D point = switch( coords.length ) {
				case 1 -> new Point3D( Maths.eval( coords[ 0 ] ), 0, 0 );
				case 2 -> new Point3D( Maths.eval( coords[ 0 ] ), Maths.eval( coords[ 1 ] ), 0 );
				case 3 -> new Point3D( Maths.eval( coords[ 0 ] ), Maths.eval( coords[ 1 ] ), Maths.eval( coords[ 2 ] ) );
				default -> null;
			};

			if( point == null ) return null;
			if( polar ) point = fromPolar( point );
			if( relative ) {
				if( anchor != null ) {
					point = anchor.add( point );
				} else {
					log.log( Log.ERROR, "Missing anchor for relative point: {0}", input );
					return null;
				}
			}
			return point;
		} catch( ParseException exception ) {
			return null;
		}
	}

	static Point3D fromPolar( Point3D point ) {
		double angle = point.getX();
		double radius = point.getY();
		return new Point3D( radius * java.lang.Math.cos( angle ), radius * java.lang.Math.sin( angle ), 0 );
	}
}
