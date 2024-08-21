package com.avereon.cartesia.data.util;

import com.avereon.cartesia.data.DesignShape;

import java.util.Comparator;

public class DesignShapeOrderComparator implements Comparator<DesignShape> {

	@Override
	public int compare( DesignShape a, DesignShape b ) {
		if( a == null && b == null ) {
			return 0;
		} else if( a == null ) {
			return 1;
		} else if( b == null ) {
			return -1;
		} else {
			return b.getOrder() - a.getOrder();
		}
	}

}
