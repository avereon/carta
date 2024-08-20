package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.command.CommandTask;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

@CustomLog
public class Delete extends EditCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		if( task.getTool().getSelectedShapes().isEmpty() ) return SUCCESS;

		setCaptureUndoChanges( task, true );
		deleteShapes( task.getTool() );

		return SUCCESS;
	}

}
