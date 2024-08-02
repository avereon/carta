package com.avereon.cartesia.command.print;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;

public class PrintDelete extends PrintCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
//		DesignPrint print = context.getTool().getSelectedPrint();
//		if( print == null ) return COMPLETE;
//
//		context.getTool().getDesign().removePrint( print );

		return SUCCESS;
	}

}
