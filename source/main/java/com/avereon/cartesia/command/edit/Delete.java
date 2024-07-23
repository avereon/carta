package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.tool.CommandContext;
import lombok.CustomLog;

@CustomLog
public class Delete extends EditCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		if( context.getTool().selectedShapes().isEmpty() ) return COMPLETE;

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		deleteShapes(  context.getTool() );

		return COMPLETE;
	}

}
