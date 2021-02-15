package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;

/**
 * This command adds a layer as a peer to the current layer
 */
public class LayerCreateCommand extends LayerCommand {

	private static final System.Logger log = Log.get();

	@Override
	public boolean isInputCommand() {
		return true;
	}

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) {
		if( parameters.length < 1 ) {
			promptForText( context, tool, "layer-name" );
			return incomplete();
		}

		return addLayer( tool.getCurrentLayer(), new DesignLayer().setName( String.valueOf( parameters[ 0 ] ) ) );
	}

	/**
	 * This implementation adds the new layer as a peer to the current layer.
	 *
	 * @param currentLayer The current layer
	 * @param yy The new layer
	 */
	DesignLayer addLayer( DesignLayer currentLayer, DesignLayer yy ) {
		return currentLayer.getParentLayer().addLayer( yy );
	}

}
