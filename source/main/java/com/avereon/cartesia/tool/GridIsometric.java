package com.avereon.cartesia.tool;

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
	public Collection<Shape> createFxGeometryGrid( Workplane workplane, double scale ) {
		return List.of();
	}

	@Override
	public Collection<Shape> updateFxGeometryGrid( Workplane workplane, double scale, ObservableList<Node> existing ) {
		return List.of();
	}

}
