package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;
import static com.avereon.cartesia.command.Command.Result.*;

public class Undo extends EditCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
//		if( context.getTool().selectedShapes().isEmpty() ) return COMPLETE;
//
//		clearReferenceAndPreview( context );
//		setCaptureUndoChanges( context, true );
//
//		deleteShapes( getCommandShapes( context.getTool() ) );

		return SUCCESS;
	}

}

