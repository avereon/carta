package com.avereon.cartesia.tool;

import com.avereon.cartesia.DesignUnit;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;

public interface RenderConstants {

	DesignUnit DEFAULT_UNIT = DesignUnit.CM;

	double DEFAULT_DPI = 96;

	Point3D DEFAULT_CENTER = new Point3D( 0, 0, 0 );

	double DEFAULT_ROTATE = 0;

	Point2D DEFAULT_ZOOM = new Point2D( 1, 1 );

}
