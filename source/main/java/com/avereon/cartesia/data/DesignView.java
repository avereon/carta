package com.avereon.cartesia.data;

import com.avereon.data.IdNode;

import java.util.Set;

/**
 * A class to represent arbitrary selections of layers to provide different
 * "views" of the design.
 */
public class DesignView extends IdNode {

	public static final String LAYER_LINKS = "layer-links";

	public Set<DesignLayerLink> getLinks() {
		return getValues( LAYER_LINKS );
	}

	public DesignView addLink( DesignLayerLink link ) {
		addToSet( LAYER_LINKS, link );
		return this;
	}

	public DesignView removeLink( DesignLayerLink link ) {
		removeFromSet( LAYER_LINKS, link );
		return this;
	}

}
