package com.avereon.cartesia.tool;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ConstructionPoint extends Region {

	public enum Type {
		CIRCLE,
		CROSS,
		DIAMOND,
		REFERENCE,
		SQUARE,
		X
	}

	public static final double DEFAULT_SIZE = 4;

	private static final double SQRT_ONE_HALF = Math.sqrt( 0.5 );

	private static final Color DEFAULT_COLOR = Color.web( "0x808080ff" );

	private DoubleProperty size;

	public ConstructionPoint() {
		this( Type.X );
	}

	public ConstructionPoint( Type type ) {
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

	private void setType( Type type ) {
		getChildren().addAll( getShapes( type ) );
	}

	private Collection<Shape> getShapes( Type type ) {
		switch( type ) {
			case CIRCLE:
				return createCircle( getSize() );
			case CROSS:
				return createCross( getSize() );
			case DIAMOND:
				return createDiamond( getSize() );
			case REFERENCE:
				return createReference( getSize() );
			case SQUARE:
				return createSquare( getSize() );
			default:
				return createX( getSize() );
		}
	}

	private Collection<Shape> createCircle( double size ) {
		Circle circle = new Circle( size );
		circle.setStrokeWidth( 0 );
		circle.setFill( DEFAULT_COLOR );
		return Set.of( circle );
	}

	private Collection<Shape> createCross( double size ) {
		return Set.of( line( -size, 0, size, 0 ), line( 0, -size, 0, size ) );
	}

	private Collection<Shape> createDiamond( double size ) {
		Path p = new Path();
		p.getElements().addAll( new MoveTo( -size, 0 ), new LineTo( 0, size ), new LineTo( size, 0 ), new LineTo( 0, -size ), new ClosePath() );
		p.setStrokeWidth( 0 );
		p.setFill( DEFAULT_COLOR );
		return Set.of( p );
	}

	private Collection<Shape> createReference( double size ) {
		Set<Shape> shapes = new HashSet<>();
		shapes.addAll( createCross( size ) );
		shapes.addAll( createX( size ) );
		return shapes;
	}

	private Collection<Shape> createSquare( double size ) {
		double z = SQRT_ONE_HALF * size;
		Rectangle rectangle = new Rectangle( -z, -z, z + z, z + z );
		rectangle.setStrokeWidth( 0 );
		rectangle.setFill( DEFAULT_COLOR );
		return Set.of( rectangle );
	}

	private Collection<Shape> createX( double size ) {
		double z = SQRT_ONE_HALF * size;
		return Set.of( line( -z, -z, z, z ), line( -z, z, z, -z ) );
	}

	private Line line( double x1, double y1, double x2, double y2 ) {
		Line line = new Line( x1, y1, x2, y2 );
		line.setStroke( DEFAULT_COLOR );
		line.setStrokeLineCap( StrokeLineCap.BUTT );
		//line.setStrokeWidth( 0.5 );
		return line;
	}

}
