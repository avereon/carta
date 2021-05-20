package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;

public class ReferencePointsToggle extends Command {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		context.getTool().setReferenceLayerVisible( !context.getTool().isReferenceLayerVisible() );
		return COMPLETE;
	}

}
