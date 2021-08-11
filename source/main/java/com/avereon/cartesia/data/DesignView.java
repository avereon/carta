package com.avereon.cartesia.data;

import com.avereon.data.IdNode;
import com.avereon.data.NodeLink;

import java.util.Map;
import java.util.Set;

/**
 * A class to represent arbitrary selections of layers to provide different
 * "views" of the design.
 */
public class DesignView extends IdNode {

	public static final String NAME = "name";

	public static final String LAYER_LINKS = "layer-links";

	public DesignView() {
		defineNaturalKey( NAME );

		// TODO Add viewpoint and zoom
		addModifyingKeys( NAME, LAYER_LINKS );
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

	public DesignView updateFrom( Map<String,?> map ) {
		setId( String.valueOf( map.get( DesignLayer.ID ) ) );
		setName( String.valueOf( map.get( DesignLayer.NAME ) ) );
		return this;
	}

}
