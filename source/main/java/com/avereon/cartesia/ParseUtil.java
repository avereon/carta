package com.avereon.cartesia;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;

public class ParseUtil {

	public static Double parseDouble( String string ) {
		return string == null ? null : Double.parseDouble( string );
	}

	public static Point2D parsePoint2D( String string ) {
		String[] coords = string.split( "," );
		if( coords.length == 2 ) return new Point2D( Double.parseDouble( coords[ 0 ] ), Double.parseDouble( coords[ 1 ] ) );
		if( coords.length == 1 ) return new Point2D( Double.parseDouble( coords[ 0 ] ), 0 );
		return Point2D.ZERO;
	}

	public static Point3D parsePoint3D( String string ) {
		if( string == null ) throw new NullPointerException( "Point string cannot be null" );
		if( string.isBlank() ) throw new IllegalArgumentException( "Input string cannot be blank" );

		String[] coords = string.split( "," );
		if( coords.length == 3 ) return new Point3D( Double.parseDouble( coords[ 0 ] ), Double.parseDouble( coords[ 1 ] ), Double.parseDouble( coords[ 2 ] ) );
		if( coords.length == 2 ) return new Point3D( Double.parseDouble( coords[ 0 ] ), Double.parseDouble( coords[ 1 ] ), 0 );
		if( coords.length == 1 ) return new Point3D( Double.parseDouble( coords[ 0 ] ), 0, 0 );
		return Point3D.ZERO;
	}

}
