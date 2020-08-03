package com.avereon.cartesia.data;

import com.avereon.data.IdNode;

public class DesignLayerLink extends IdNode {

	private final DesignLayer layer;

	public DesignLayerLink( DesignLayer layer ) {
		this.layer = layer;
	}

	DesignLayer getLayer() {
		return layer;
	}

}
