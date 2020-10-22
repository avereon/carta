package com.avereon.cartesia.math;

import com.avereon.util.Log;
import javafx.geometry.Point3D;

import java.text.ParseException;
import java.util.Objects;

public class Geometry {

	private static final System.Logger log = Log.get();

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
				case 1 -> new Point3D( Math.eval( coords[ 0 ] ), 0, 0 );
				case 2 -> new Point3D( Math.eval( coords[ 0 ] ), Math.eval( coords[ 1 ] ), 0 );
				case 3 -> new Point3D( Math.eval( coords[ 0 ] ), Math.eval( coords[ 1 ] ), Math.eval( coords[ 2 ] ) );
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
