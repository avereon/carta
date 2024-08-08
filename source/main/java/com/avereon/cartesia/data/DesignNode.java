package com.avereon.cartesia.data;

import com.avereon.data.IdNode;
import com.avereon.data.Node;
import com.avereon.data.NodeEvent;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The base node for all design data objects.
 */
public abstract class DesignNode extends IdNode {

	@Getter( AccessLevel.PROTECTED )
	private final Map<String, Object> cache;

	public DesignNode() {
		this.cache = new ConcurrentHashMap<>();

		register( NodeEvent.MODIFIED, e -> cache.clear() );
	}

	protected Map<String, Object> asMap() {
		return asMap( ID );
	}

	public DesignNode updateFrom( Map<String, Object> map ) {
		if( map.containsKey( ID ) ) setId( (String)map.get( ID ) );
		return this;
	}

	public Optional<Design> getDesign() {
		Node node = this;
		while( node != null && !(node instanceof Design) ) {
			node = node.getParent();
		}
		return Optional.ofNullable( (Design)node );
	}

}
