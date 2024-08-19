package com.avereon.cartesia.command.layer;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignLayer;

import java.util.List;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class LayerDelete extends LayerCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		DesignLayer layer = task.getTool().getSelectedLayer();
		if( layer == null ) return SUCCESS;

		task.getTool().setCurrentLayer( getNextValidLayer( layer ) );

		layer.getLayer().removeLayer( layer );
		// FIXME Remove the layer from any views
		// FIXME Remove the layer from any prints

		return layer;
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
