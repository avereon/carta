package com.avereon.cartesia.tool;

import com.avereon.curve.math.Arithmetic;
import com.avereon.curve.math.Constants;
import com.avereon.marea.Shape2d;
import com.avereon.marea.fx.FxRenderer2d;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collection;
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

	/**
	 * @param workplane The workplane that defines the users work plane
	 * @return The grid geometry as Marea shapes
	 */
	default List<Shape2d> createMareaGeometryGrid( Workplane workplane ) {
		return List.of();
	}

	default void drawMareaGeometryGrid( FxRenderer2d renderer, Workplane workplane ) {}

	/**
	 * @param workplane The workplane that defines the users work plane
	 * @return The grid geometry as FX shapes
	 */
	Collection<Shape> createFxGeometryGrid( Workplane workplane );

	Collection<Shape> createFxGeometryGrid( Workplane workplane, double scale );

	Collection<Shape> updateFxGeometryGrid( Workplane workplane, double scale, ObservableList<Node> existing );

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

}
