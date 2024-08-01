package com.avereon.cartesia.command.layer;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;

public class LayerToggle extends LayerCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		BaseDesignTool tool = context.getTool();
		DesignLayer layer = tool.getSelectedLayer();
		tool.setLayerVisible( layer, !tool.isLayerVisible( layer ) );
		return COMPLETE;
	}

}
