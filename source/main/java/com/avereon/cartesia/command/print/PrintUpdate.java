package com.avereon.cartesia.command.print;

import com.avereon.cartesia.command.CommandTask;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

public class PrintUpdate extends PrintCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
//		DesignPrint print = task.getTool().getSelectedPrint();
//		if( print == null ) return COMPLETE;
//
//		print.setOrigin( task.getTool().getViewPoint() );
//		print.setZoom( task.getTool().getZoom() );
//		print.setRotate( task.getTool().getViewRotate() );
//		print.setLayers( task.getTool().getVisibleLayers() );

		return SUCCESS;
	}


}
