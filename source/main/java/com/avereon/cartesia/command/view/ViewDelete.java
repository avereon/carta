package com.avereon.cartesia.command.view;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;

public class ViewDelete extends ViewCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		DesignView view = context.getTool().getCurrentView();
		if( view == null ) return COMPLETE;

		context.getTool().getDesign().removeView( view );

		return COMPLETE;
	}

}
