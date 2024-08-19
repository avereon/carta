package com.avereon.cartesia.command.layer;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignLayer;

public class LayerCurrent extends LayerCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		// Get the selected layer
		DesignLayer yy = task.getTool().getSelectedLayer();

		// Make the layer selected in the guide the current layer
		task.getTool().setCurrentLayer( yy );

		return yy;
	}

}

