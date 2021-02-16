package com.avereon.cartesia.data;

import javafx.geometry.Point3D;

public class DesignArc extends DesignShape {

	public static final String ARC = "arc";

	public static final String CIRCLE = "circle";

	public static final String ELLIPSE = "ellipse";

	public static final String X_RADIUS = "x-radius";

	public static final String Y_RADIUS = "y-radius";

	public static final String START = "start";

	public static final String EXTENT = "extent";

	public static final String ROTATE = "rotate";

	public static final String TYPE = "type"; // open, chord, pie

	public DesignArc() {
		addModifyingKeys( ORIGIN, X_RADIUS, Y_RADIUS, START, EXTENT, ROTATE, TYPE );
	}

	public DesignArc( Point3D origin, Double xRadius, Double yRadius, Double start, Double extent, Double rotate, String type ) {
		this();
		setOrigin( origin );
		setXRadius( xRadius );
		setYRadius( yRadius );
		setStart( start );
		setExtent( extent );
		setRotate( rotate );
	}

	public Double getXRadius() {
		return getValue( X_RADIUS );
	}

	public DesignShape setXRadius( Double value ) {
		setValue( X_RADIUS, value );
		return this;
	}

	public Double getYRadius() {
		return getValue( Y_RADIUS );
	}

	public DesignShape setYRadius( Double value ) {
		setValue( Y_RADIUS, value );
		return this;
	}

	public Double getStart() {
		return getValue( START );
	}

	public DesignShape setStart( Double value ) {
		setValue( START, value );
		return this;
	}

	public Double getExtent() {
		return getValue( EXTENT );
	}

	public DesignShape setExtent( Double value ) {
		setValue( EXTENT, value );
		return this;
	}

	public Double getRotate() {
		return getValue( ROTATE );
	}

	public DesignShape setRotate( Double value ) {
		setValue( ROTATE, value );
		return this;
	}

	public String getType() {
		return getValue( TYPE );
	}

	public DesignShape setType( String value ) {
		setValue( TYPE, value );
		return this;
	}

}
