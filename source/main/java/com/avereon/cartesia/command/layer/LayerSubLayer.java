package com.avereon.cartesia.command.layer;

import com.avereon.cartesia.data.DesignLayer;

public class LayerSubLayer extends LayerCreate {

	/**
	 * This implementation adds the new layer to the current layer as a child.
	 *
	 * @param layer The parent layer
	 * @param yy The new layer
	 */
	DesignLayer addLayer( DesignLayer layer, DesignLayer yy ) {
		// Add yy as a child to currentLayer
		return layer.addLayer( yy );
	}

}
