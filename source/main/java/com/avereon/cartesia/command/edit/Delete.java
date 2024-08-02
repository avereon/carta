package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;
import lombok.CustomLog;
import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class Delete extends EditCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		if( context.getTool().getSelectedShapes().isEmpty() ) return SUCCESS;

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		deleteShapes( context.getTool() );

		return SUCCESS;
	}

}
