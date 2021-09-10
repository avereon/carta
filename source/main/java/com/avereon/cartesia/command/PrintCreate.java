package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignPrint;
import com.avereon.cartesia.tool.CommandContext;

public class PrintCreate extends PrintCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForText( context, "print-name" );
			return INCOMPLETE;
		}

		DesignPrint print = new DesignPrint().setName( String.valueOf( parameters[ 0 ] ) );
		print.setOrigin( context.getTool().getViewPoint() );
		print.setZoom( context.getTool().getZoom() );
		print.setRotate( context.getTool().getViewRotate() );
		print.setLayers( context.getTool().getVisibleLayers() );

		context.getTool().getDesign().addPrint( print );
		//context.getTool().setCurrentPrint( print );

		return print;
	}

}
