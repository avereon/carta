package com.avereon.cartesia.command.snap;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;

public class SnapGridToggle extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		DesignTool tool = context.getTool();
		tool.setGridSnapEnabled( !tool.isGridSnapEnabled() );
		return SUCCESS;
	}

}
