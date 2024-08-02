package com.avereon.cartesia.command.print;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;
import static com.avereon.cartesia.command.Command.Result.*;

public class PrintUpdate extends PrintCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
//		DesignPrint print = context.getTool().getSelectedPrint();
//		if( print == null ) return COMPLETE;
//
//		print.setOrigin( context.getTool().getViewPoint() );
//		print.setZoom( context.getTool().getZoom() );
//		print.setRotate( context.getTool().getViewRotate() );
//		print.setLayers( context.getTool().getVisibleLayers() );

		return SUCCESS;
	}


}
