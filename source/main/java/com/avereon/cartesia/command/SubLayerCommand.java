package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLayer;

public class SubLayerCommand extends LayerCommand {

	/**
	 * This implementation adds the new layer to the current layer as a child.
	 *
	 * @param currentLayer The current layer
	 * @param yy The new layer
	 */
	void addLayer( DesignLayer currentLayer, DesignLayer yy ) {
		currentLayer.addLayer( yy );
	}

}
