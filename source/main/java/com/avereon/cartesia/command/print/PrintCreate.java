package com.avereon.cartesia.command.print;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignPrint;

import static com.avereon.cartesia.command.Command.Result.FAILURE;
import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;

public class PrintCreate extends PrintCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		if( task.getParameterCount() == 0 ) {
			promptForText( task, "print-name" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 0 ) ) {
			DesignPrint print = new DesignPrint().setName( asText( task, "print-name", 0 ) );
			print.setOrigin( task.getTool().getViewpoint() );
			print.setZoom( task.getTool().getZoom() );
			print.setRotate( task.getTool().getViewRotate() );
			print.setLayers( task.getTool().getVisibleLayers() );

			task.getTool().getDesign().addPrint( print );
			//task.getTool().setCurrentPrint( print );

			return print;
		}

		return FAILURE;
	}

}
