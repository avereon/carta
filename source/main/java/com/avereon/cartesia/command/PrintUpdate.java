package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;

public class PrintUpdate extends PrintCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
//		DesignPrint print = context.getTool().getSelectedPrint();
//		if( print == null ) return COMPLETE;
//
//		print.setOrigin( context.getTool().getViewPoint() );
//		print.setZoom( context.getTool().getZoom() );
//		print.setRotate( context.getTool().getViewRotate() );
//		print.setLayers( context.getTool().getVisibleLayers() );

		return COMPLETE;
	}


}
