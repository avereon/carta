package com.avereon.cartesia.data;

import com.avereon.data.IdNode;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The base node for all design data objects.
 */
public abstract class DesignNode extends IdNode {

	public static final String DEFAULT_VALUE = "default";

	protected Map<String, Object> asMap() {
		return asMap( ID );
	}

	@Override
	protected Map<String, Object> asMap( String... keys ) {
		return Arrays
			.stream( keys )
			.filter( k -> getValue( k ) != null )
			.filter( k -> !DEFAULT_VALUE.equals( getValue( k ) ) )
			.collect( Collectors.toMap( k -> k, this::getValue ) );
	}

	public DesignNode updateFrom( Map<String, String> map ) {
		if( map.containsKey( ID ) ) setId( map.get( ID ) );
		return this;
	}

}
