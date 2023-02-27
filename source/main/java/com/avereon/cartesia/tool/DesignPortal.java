package com.avereon.cartesia.tool;

import javafx.geometry.Point3D;
import lombok.Data;

@Data
public class DesignPortal {

	public static final DesignPortal DEFAULT = new DesignPortal( Point3D.ZERO, 1, 0 );

	private final Point3D viewpoint;

	private final double zoom;

	private final double rotate;
}
