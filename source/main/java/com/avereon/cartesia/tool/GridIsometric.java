package com.avereon.cartesia.tool;

import com.avereon.marea.Shape2d;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.shape.Shape;

import java.util.Collection;
import java.util.List;

public class GridIsometric implements Grid {

	@Override
	public String name() {
		return "ISO";
	}

	@Override
	public Point3D getNearest( Workplane workplane, Point3D point ) {
		return null;
	}

	@Override
	public List<Shape> createFxGeometryGrid( Workplane workplane ) {
		return List.of();
	}

	@Override
	public Collection<Shape> createFxGeometryGrid( Workplane workplane, double scale ) {
		return List.of();
	}

	@Override
	public Collection<Shape> updateFxGeometryGrid( Workplane workplane, double scale, ObservableList<Node> existing ) {
		return List.of();
	}

	@Override
	public List<Shape2d> createMareaGeometryGrid( Workplane workplane ) {
		return switch( workplane.getGridStyle() ) {
			case DOT -> generateMareaGridDots( workplane );
			case CROSS -> generateMareaGridLines( workplane );
			case LINE -> generateMareaGridLines( workplane );
		};
	}

	private List<Shape2d> generateMareaGridDots( Workplane workplane ) {
		return List.of();
	}

	private List<Shape2d> generateMareaGridLines( Workplane workplane ) {
		return List.of();
	}

}
