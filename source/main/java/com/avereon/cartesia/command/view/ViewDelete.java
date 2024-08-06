package com.avereon.cartesia.command.view;

import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.tool.CommandTask;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class ViewDelete extends ViewCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		DesignView view = task.getTool().getCurrentView();
		if( view == null ) return SUCCESS;

		task.getTool().getDesign().removeView( view );

		return SUCCESS;
	}

}
