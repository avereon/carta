package com.avereon.cartesia.data;

import com.avereon.data.NodeLink;

import java.util.Map;
import java.util.Set;

/**
 * A class to represent arbitrary selections of layers to provide different
 * "views" of the design.
 */
public class DesignView extends DesignNode {

	public static final String NAME = "name";

	public static final String ORDER = "order";

	public static final String LAYER_LINKS = "layer-links";

	public DesignView() {
		defineNaturalKey( NAME );

		// TODO Add viewpoint and zoom
		addModifyingKeys( NAME, ORDER, LAYER_LINKS );
	}

	/**
	 * Overridden to return the specific type of this class.
	 *
	 * @param id The node id
	 * @return This instance
	 */
	@SuppressWarnings( "unchecked" )
	public DesignView setId( String id ) {
		super.setId( id );
		return this;
	}

	public int getOrder() {
		return getValue( ORDER, -1 );
	}

	public DesignView setOrder( int order ) {
		setValue( ORDER, order );
		return this;
	}

	public String getName() {
		return getValue( NAME );
	}

	public DesignView setName( String name ) {
		setValue( NAME, name );
		return this;
	}

	public Set<NodeLink<DesignLayer>> getLayerLinks() {
		return getValues( LAYER_LINKS );
	}

	public DesignView addLayerLink( NodeLink<DesignLayer> link ) {
		addToSet( LAYER_LINKS, link );
		return this;
	}

	public DesignView removeLayerLink( NodeLink<DesignLayer> link ) {
		removeFromSet( LAYER_LINKS, link );
		return this;
	}

	public DesignView updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( NAME ) ) setName( (String)map.get( NAME ) );
		if( map.containsKey( ORDER ) ) setOrder( (Integer)map.get( ORDER ) );
		return this;
	}

}
