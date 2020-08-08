package com.avereon.cartesia;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Control;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.Collection;
import java.util.Set;

public class ConstructionPoint extends Control {

	public enum Type {
		CIRCLE,
		DIAMOND,
		SQUARE,
		X
	}

	public static final double DEFAULT_SIZE = 2.5;

	private DoubleProperty size;

	public ConstructionPoint() {
		this( Type.X );
	}

	public ConstructionPoint( Type type ) {
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

	private void setType( Type type ) {
		getChildren().addAll( getShapes( type ) );
	}

	private Collection<Shape> getShapes( Type type ) {
		switch( type ) {
			case CIRCLE:
				return createCircle( getSize() );
			case DIAMOND:
				return createDiamond( getSize() );
			case SQUARE:
				return createSquare( getSize() );
			default:
				return createX( getSize() );
		}
	}

	private Collection<Shape> createCircle( double size ) {
		Circle circle = new Circle( size );
		circle.setStrokeWidth( 0 );
		circle.setFill( Color.GRAY );
		return Set.of( circle );
	}

	private Collection<Shape> createDiamond( double size ) {
		Path p = new Path();
		p.getElements().addAll( new MoveTo( -size, 0 ), new LineTo( 0, size ), new LineTo( size, 0 ), new LineTo( 0, -size ), new ClosePath() );
		p.setStrokeWidth( 0 );
		p.setFill( Color.BLUE );
		return Set.of( p );
	}

	private Collection<Shape> createSquare( double size ) {
		Rectangle rectangle = new Rectangle( -size, -size, size + size, size + size );
		rectangle.setStrokeWidth( 0 );
		rectangle.setFill( Color.GREEN );
		return Set.of( rectangle );
	}

	private Collection<Shape> createX( double size ) {
		Line a = new Line( -size, -size, size, size );
		Line b = new Line( -size, size, size, -size );
		a.setStroke( Color.WHITE );
		b.setStroke( Color.WHITE );
		return Set.of( a, b );
	}

}
