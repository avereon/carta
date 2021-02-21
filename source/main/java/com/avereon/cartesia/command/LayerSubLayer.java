package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLayer;

public class LayerSubLayer extends LayerCreate {

	/**
	 * This implementation adds the new layer to the current layer as a child.
	 *
	 * @param currentLayer The current layer
	 * @param yy The new layer
	 */
	DesignLayer addLayer( DesignLayer currentLayer, DesignLayer yy ) {
		return currentLayer.addLayer( yy );
	}

}
