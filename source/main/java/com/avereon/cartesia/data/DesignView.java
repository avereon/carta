package com.avereon.cartesia.data;

import com.avereon.data.IdNode;
import com.avereon.data.NodeLink;

import java.util.Set;

/**
 * A class to represent arbitrary selections of layers to provide different
 * "views" of the design.
 */
public class DesignView extends IdNode {

	public static final String LAYER_LINKS = "layer-links";

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

}
