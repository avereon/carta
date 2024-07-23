package com.avereon.cartesia.command.print;

import com.avereon.cartesia.tool.CommandContext;

public class PrintDelete extends PrintCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
//		DesignPrint print = context.getTool().getSelectedPrint();
//		if( print == null ) return COMPLETE;
//
//		context.getTool().getDesign().removePrint( print );

		return COMPLETE;
	}

}
