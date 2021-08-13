package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.tool.CommandContext;
import lombok.CustomLog;

/**
 * This command creates a view from the current view settings
 */
@CustomLog
public class ViewCreate extends ViewCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForText( context, "view-name" );
			return INCOMPLETE;
		}

		DesignView ww = new DesignView().setName( String.valueOf( parameters[ 0 ] ) );
		ww.setOrigin( context.getTool().getViewPoint() );
		ww.setZoom( context.getTool().getZoom() );
		ww.setRotate( context.getTool().getViewRotate() );
		ww.setLayers(context.getTool().getVisibleLayers());

		context.getTool().getDesign().addView( ww );
		context.getTool().setCurrentView( ww );

		return ww;
	}

}
