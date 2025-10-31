package com.avereon.cartesia;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import org.jspecify.annotations.NonNull;

public class ParseUtil {

	public static Double parseValue( @NonNull String string ) {
		String[] coords = string.split( "," );
		if( coords.length >= 1 ) return parseDouble( coords[ 0 ] );
		return 0.0;
	}

	public static Point2D parsePoint2D( @NonNull String string ) {
		String[] coords = string.split( "," );
		if( coords.length >= 2 ) return new Point2D( parseDouble( coords[ 0 ] ), parseDouble( coords[ 1 ] ) );
		if( coords.length == 1 ) return new Point2D( parseDouble( coords[ 0 ] ), 0 );
		return Point2D.ZERO;
	}

	public static Point3D parsePoint3D( @NonNull String string ) {
		String[] coords = string.split( "," );
		if( coords.length >= 3 ) return new Point3D( parseDouble( coords[ 0 ] ), parseDouble( coords[ 1 ] ), parseDouble( coords[ 2 ] ) );
		if( coords.length == 2 ) return new Point3D( parseDouble( coords[ 0 ] ), parseDouble( coords[ 1 ] ), 0 );
		if( coords.length == 1 ) return new Point3D( parseDouble( coords[ 0 ] ), 0, 0 );
		return Point3D.ZERO;
	}

	private static Double parseDouble( @NonNull String string ) {
		return string.isBlank() ? 0.0 : Double.parseDouble( string );
	}

}
