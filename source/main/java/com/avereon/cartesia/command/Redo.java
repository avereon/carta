package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;

public class Redo extends EditCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
//		if( context.getTool().selectedShapes().isEmpty() ) return COMPLETE;
//
//		clearReferenceAndPreview( context );
//		setCaptureUndoChanges( context, true );
//
//		deleteShapes( getCommandShapes( context.getTool() ) );

		return COMPLETE;
	}

}
