package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadGeometry;
import javafx.geometry.Point3D;

import java.util.Comparator;

public class NearestShapeComparator implements Comparator<DesignShape> {

	private final Point3D anchor;

	public NearestShapeComparator( Point3D anchor ) {
		this.anchor = anchor;
	}

	@Override
	public int compare( DesignShape o1, DesignShape o2 ) {
		double d1 = o1.distanceTo( anchor );
		double d2 = o2.distanceTo( anchor );
		if( CadGeometry.areSameSize( d1, d2 ) ) return 0;
		return d1 < d2 ? -1 : 1;
	}

}
