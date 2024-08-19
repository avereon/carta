package com.avereon.cartesia.command.layer;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignLayer;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.FAILURE;
import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;

/**
 * This command adds a layer as a peer to the current layer
 */
@CustomLog
public class LayerCreate extends LayerCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		if( task.getParameterCount() == 0 ) {
			promptForText( task, "layer-name" );
			return INCOMPLETE;
		}

		if( task.hasParameter(0 ) ) {
			String name = asText( task, "layer-name", 0 );
			DesignLayer yy = new DesignLayer().setName( name );
			yy = addLayer( task.getTool().getSelectedLayer(), yy );
			task.getTool().setLayerVisible( yy, true );
			return yy;
		}

		return FAILURE;
	}

	/**
	 * This implementation adds the new layer as a peer to the current layer.
	 *
	 * @param layer The layer to add the new layer as a peer
	 * @param yy The new peer layer
	 */
	DesignLayer addLayer( DesignLayer layer, DesignLayer yy ) {
		// Add yy as a peer (child of parent) of the currentLayer
		return layer.getLayer().addLayerBeforeOrAfter( yy, layer, true );
	}

}
