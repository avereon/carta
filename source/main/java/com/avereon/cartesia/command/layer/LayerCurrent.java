package com.avereon.cartesia.command.layer;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;

public class LayerCurrent extends LayerCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		// Get the selected layer
		DesignLayer yy = context.getTool().getSelectedLayer();

		// Make the layer selected in the guide the current layer
		context.getTool().setCurrentLayer( yy );

		return yy;
	}

}

