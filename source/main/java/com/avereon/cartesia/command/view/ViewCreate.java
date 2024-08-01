package com.avereon.cartesia.command.view;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;
import lombok.CustomLog;

/**
 * This command creates a view from the current view settings
 */
@CustomLog
public class ViewCreate extends ViewCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForText( context, "view-name" );
			return INCOMPLETE;
		}

		DesignView view = new DesignView().setName( String.valueOf( parameters[ 0 ] ) );
		view.setOrigin( context.getTool().getViewPoint() );
		view.setZoom( context.getTool().getZoom() );
		view.setRotate( context.getTool().getViewRotate() );
		view.setLayers( context.getTool().getVisibleLayers() );

		context.getTool().getDesign().addView( view );
		context.getTool().setCurrentView( view );

		return view;
	}

}
