package com.avereon.cartesia.command.layer;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;
import static com.avereon.cartesia.command.Command.Result.*;

public class LayerHide extends LayerCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		context.getTool().setLayerVisible( context.getTool().getSelectedLayer(), false );
		return SUCCESS;
	}

}
