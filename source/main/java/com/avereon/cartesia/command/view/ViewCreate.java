package com.avereon.cartesia.command.view;

import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.command.CommandTask;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.FAILURE;
import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;

/**
 * This command creates a view from the current view settings
 */
@CustomLog
public class ViewCreate extends ViewCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		if( task.getParameterCount() == 0 ) {
			promptForText( task, "view-name" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 0 ) ) {
			// Create a view from the current view settings
			DesignView view = new DesignView().setName( asText( task, "view-name", 0 ) );
			view.setOrigin( task.getTool().getViewCenter() );
			view.setZoom( task.getTool().getViewZoom() );
			view.setRotate( task.getTool().getViewRotate() );
			view.setLayers( task.getTool().getVisibleLayers() );

			// Add the view to the design
			task.getTool().getDesign().addView( view );

			// Set the current view to the new view
			task.getTool().setCurrentView( view );

			return view;
		}

		return FAILURE;
	}

}
