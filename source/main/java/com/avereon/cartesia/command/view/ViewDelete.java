package com.avereon.cartesia.command.view;

import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.tool.CommandContext;

public class ViewDelete extends ViewCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		DesignView view = context.getTool().getCurrentView();
		if( view == null ) return COMPLETE;

		context.getTool().getDesign().removeView( view );

		return COMPLETE;
	}

}
