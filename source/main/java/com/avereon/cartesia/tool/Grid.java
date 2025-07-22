package com.avereon.cartesia.tool;

import com.avereon.curve.math.Arithmetic;
import com.avereon.curve.math.Constants;
import com.avereon.marea.fx.FxRenderer2d;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/*
FIXME - This class currently has three return types for grid geometry.
Not sure if this is the approach I want to take. The other option is to generate
design geometry and map it to native geometry for the renderer, but that "just"
slows it down.
 */
public interface Grid {

	Grid ORTHO = new GridOrthographic();

	Grid POLAR = new GridPolar();

	Grid ISO = new GridIsometric();

	String name();

	Point3D getNearest( Workplane workplane, Point3D point );

	Collection<Shape> createFxGeometryGrid( Workplane workplane, double scale );

	Collection<Shape> updateFxGeometryGrid( Workplane workplane, double scale, ObservableList<Node> existing );

	/**
	 * @param renderer The FX renderer to draw the grid
	 * @param workplane The workplane that defines the grid configuration
	 * @deprecated Most likely be moving back to using FX geometry
	 */
	@Deprecated
	default void drawMareaGeometryGrid( FxRenderer2d renderer, Workplane workplane ) {}

	static Grid valueOf( String name ) {
		return switch( name ) {
			case "POLAR" -> POLAR;
			case "ISO" -> ISO;
			default -> ORTHO;
		};
	}

	static boolean isNearAny( Double value, Collection<Double> values ) {
		for( Double check : values ) {
			if( Math.abs( check - value ) <= Constants.RESOLUTION_LENGTH ) return true;
		}
		return false;
	}

	static List<Double> getOffsets( double origin, double spacing, double lowLimit, double highLimit ) {
		return getOffsets( origin, spacing, lowLimit, highLimit, false );
	}

	static List<Double> getOffsets( double origin, double spacing, double lowLimit, double highLimit, boolean radial ) {
		double n1 = Arithmetic.nearestAbove( lowLimit - origin, spacing ) + origin;
		double n2 = Arithmetic.nearestBelow( highLimit - origin, spacing ) + origin;

		int count = (int)Math.round( (n2 - n1) / spacing ) + 1;
		List<Double> offsets = new ArrayList<>( count );
		double max = radial ? 360 : Double.MAX_VALUE;

		for( int index = 0; index < count; index++ ) {
			double value = index * spacing + n1;
			if( value < max ) offsets.add( value );
		}

		return offsets;
	}

	static double getBoundaryX1( double x1, double x2, double scale, double majorIntervalX ) {
		return Math.min( x1, x2 ) * scale - majorIntervalX;
	}

	static double getBoundaryX2( double x1, double x2, double scale, double majorIntervalX ) {
		return Math.max( x1, x2 ) * scale + majorIntervalX;
	}

	static double getBoundaryY1( double y1, double y2, double scale, double majorIntervalY ) {
		return Math.min( y1, y2 ) * scale - majorIntervalY;
	}

	static double getBoundaryY2( double y1, double y2, double scale, double majorIntervalY ) {
		return Math.max( y1, y2 ) * scale + majorIntervalY;
	}

	static Line reuseOrNewLine( Collection<Line> prior, double x1, double y1, double x2, double y2 ) {
		if( prior.isEmpty() ) return new Line( x1, y1, x2, y2 );

		// Reuse a line
		Iterator<Line> iterator = prior.iterator();
		Line line = iterator.next();
		iterator.remove();

		// Update the position
		line.setStartX( x1 );
		line.setStartY( y1 );
		line.setEndX( x2 );
		line.setEndY( y2 );

		return line;
	}

	static Circle reuseOrNewCircle( Collection<Circle> prior, double cx, double cy, double r ) {
		if( prior.isEmpty() ) return new Circle( cx, cy, r );

		// Reuse a line
		Iterator<Circle> iterator = prior.iterator();
		Circle circle = iterator.next();
		iterator.remove();

		// Update the position
		circle.setCenterX( cx );
		circle.setCenterY( cy );
		circle.setRadius( r );

		return circle;
	}

}
