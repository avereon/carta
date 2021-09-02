package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignMarker;
import com.avereon.cartesia.data.DesignMarkers;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.layout.Region;
import javafx.scene.shape.Shape;

import java.util.Collection;
import java.util.Set;

public class ConstructionPoint extends Region {

	public static final double DEFAULT_SIZE = 4;

	private DoubleProperty size;

	public ConstructionPoint() {
		this( DesignMarker.Type.CIRCLE );
	}

	ConstructionPoint( DesignMarker.Type type ) {
		getStyleClass().addAll( "construction-point" );
		setManaged( false );
		setType( type );
	}

	public double getSize() {
		return size == null ? DEFAULT_SIZE : sizeProperty().get();
	}

	public void setSize( double size ) {
		sizeProperty().set( size );
	}

	public Point3D getLocation() {
		return new Point3D( getLayoutX(), getLayoutY(), 0 );
	}

	public ConstructionPoint setType( DesignMarker.Type type ) {
		getChildren().addAll( getShapes( type ) );
		return this;
	}

	private DoubleProperty sizeProperty() {
		if( size == null ) size = new SimpleDoubleProperty();
		return size;
	}

	private double getRadius() {
		return 0.5 * getSize();
	}

	private Collection<Shape> getShapes( DesignMarker.Type type ) {
		return Set.of( DesignMarkers.createMarker( type, 0, 0, getRadius() ) );
	}

}
