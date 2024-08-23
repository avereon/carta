package com.avereon.cartesia.command.view;

import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.command.CommandTask;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class ViewUpdate extends ViewCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		DesignView view = task.getTool().getCurrentView();
		if( view == null ) return SUCCESS;

		view.setOrigin( task.getTool().getViewpoint() );
		view.setZoom( task.getTool().getZoom() );
		view.setRotate( task.getTool().getViewRotate() );
		view.setLayers( task.getTool().getVisibleLayers() );

		return SUCCESS;
	}

}
