package com.avereon.cartesia.data;

import com.avereon.data.IdNode;

import java.util.Map;

/**
 * The base node for all design data objects.
 */
public abstract class DesignNode extends IdNode {

	protected Map<String, Object> asMap() {
		return asMap( ID );
	}

	public DesignNode updateFrom( Map<String, String> map ) {
		if( map.containsKey( ID ) ) setId( map.get( ID ) );
		return this;
	}

}
