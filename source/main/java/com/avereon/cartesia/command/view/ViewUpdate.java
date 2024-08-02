package com.avereon.cartesia.command.view;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;
import static com.avereon.cartesia.command.Command.Result.*;

public class ViewUpdate extends ViewCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		DesignView view = context.getTool().getCurrentView();
		if( view == null ) return SUCCESS;

		view.setOrigin( context.getTool().getViewPoint() );
		view.setZoom( context.getTool().getZoom() );
		view.setRotate( context.getTool().getViewRotate() );
		view.setLayers( context.getTool().getVisibleLayers() );

		return SUCCESS;
	}

}
