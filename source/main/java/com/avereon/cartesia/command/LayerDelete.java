package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.CommandContext;

import java.util.List;

public class LayerDelete extends LayerCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		DesignLayer layer = context.getTool().getSelectedLayer();
		DesignLayer nextLayer = getNextValidLayer( layer );

		layer.getLayer().removeLayer( layer );
		// FIXME Remove the layer from any views
		// FIXME Remove the layer from any prints


		context.getTool().setCurrentLayer( nextLayer );
		return COMPLETE;
	}

	private DesignLayer getNextValidLayer( DesignLayer layer ) {
		DesignLayer parent = layer.getLayer();
		List<DesignLayer> layers = parent.getLayers();
		int count = layers.size();
		int order = layer.getOrder();

		DesignLayer next;
		if( count == 1 ) {
			next = parent;
		} else {
			next = layers.get( order == 0 ? order + 1 : order - 1 );
		}

		return next;
	}

}
