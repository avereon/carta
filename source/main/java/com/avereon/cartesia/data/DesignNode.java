package com.avereon.cartesia.data;

import com.avereon.data.IdNode;
import com.avereon.data.Node;

import java.util.Map;

/**
 * The base node for all design data objects.
 */
public abstract class DesignNode extends IdNode {

	protected Map<String, Object> asMap() {
		return asMap( ID );
	}

	public DesignNode updateFrom( Map<String, Object> map ) {
		if( map.containsKey( ID ) ) setId( (String)map.get( ID ) );
		return this;
	}

	public Design getDesign() {
		Node node = this;
		while( !(node instanceof Design ) ) {
			node = node.getParent();
		}
		return (Design)node;
	}

}
