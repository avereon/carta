package com.avereon.cartesia.tool;

import javafx.geometry.Point3D;

/**
 * Represents a portal for a design workspace. The DesignPortal class encapsulates
 * settings related to view positioning, zoom level, and rotation angle for a design view.
 */
public record DesignPortal(Point3D viewpoint, double zoom, double rotate) {

	public static final DesignPortal DEFAULT = new DesignPortal( Point3D.ZERO, 1, 0 );

}
