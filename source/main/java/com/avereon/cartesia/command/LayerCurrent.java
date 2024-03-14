package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.CommandContext;

public class LayerCurrent extends LayerCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		// Get the selected layer
		DesignLayer yy = context.getTool().getSelectedLayer();

		// Make the layer selected in the guide the current layer
		context.getTool().setCurrentLayer( yy );

		return yy;
	}

}

