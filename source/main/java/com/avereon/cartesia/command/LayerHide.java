package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;

public class LayerHide extends LayerCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		context.getTool().setLayerVisible( context.getTool().getSelectedLayer(), false );
		return COMPLETE;
	}

}
