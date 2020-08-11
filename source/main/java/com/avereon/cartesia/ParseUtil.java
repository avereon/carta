package com.avereon.cartesia;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;

public class ParseUtil {

	public static Point2D parsePoint2D( String string ) {
		String[] coords = string.split( "," );
		return new Point2D( Double.parseDouble( coords[ 0 ] ), Double.parseDouble( coords[ 1 ] ) );
	}

	public static Point3D parsePoint3D( String string ) {
		String[] coords = string.split( "," );
		return new Point3D( Double.parseDouble( coords[ 0 ] ), Double.parseDouble( coords[ 1 ] ), Double.parseDouble( coords[ 2 ] ) );
	}

}
