package com.avereon.cartesia.command.layer;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.scene.input.InputEvent;

public class LayerToggle extends LayerCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		DesignTool tool = context.getTool();
		DesignLayer layer = tool.getSelectedLayer();
		tool.setLayerVisible( layer, !tool.isLayerVisible( layer ) );
		return SUCCESS;
	}

}
