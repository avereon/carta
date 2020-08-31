package com.avereon.cartesia.tool;

import com.avereon.cartesia.math.Points;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Region;
import javafx.scene.shape.Shape;

import java.util.Collection;
import java.util.Set;

public class ConstructionPoint extends Region {

	public static final double DEFAULT_SIZE = 8;

	private DoubleProperty size;

	public ConstructionPoint() {
		this( Points.Type.X );
	}

	ConstructionPoint( Points.Type type ) {
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

	private DoubleProperty sizeProperty() {
		if( size == null ) size = new SimpleDoubleProperty();
		return size;
	}

	private void setType( Points.Type type ) {
		getChildren().addAll( getShapes( type ) );
	}

	private double getRadius() {
		return 0.5 * getSize();
	}

	private Collection<Shape> getShapes( Points.Type type ) {
		return Set.of( Points.createPoint( type, 0, 0, getRadius() ) );
	}

}
