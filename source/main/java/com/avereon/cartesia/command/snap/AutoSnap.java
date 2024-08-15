package com.avereon.cartesia.command.snap;

import com.avereon.cartesia.command.InvalidInputException;
import com.avereon.cartesia.snap.Snap;
import com.avereon.cartesia.tool.CommandTask;
import com.avereon.cartesia.tool.DesignTool;

import static com.avereon.cartesia.command.Command.Result.FAILURE;

/**
 * Auto snap commands use the first parameter to determine what kind of snap to
 * perform. The command will then use the event or a second parameter to
 * determine the reference point to snap.
 */
public class AutoSnap extends SnapCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		Snap snap = (Snap)task.getParameter( 0 );
		if( snap == null ) throw new InvalidInputException( this, "snap", null );

		if( task.getEvent() == null & !task.hasParameter( 1 ) ) throw new InvalidInputException( this, "snap", null );

		DesignTool tool = task.getTool();

		// Get the snap point from the parameter
		if( task.hasParameter( 1 ) ) return snap.snap( tool, asPoint( task, "snap", 1 ) );

		// Get the snap point from the event
		if( task.getEvent() != null ) return snap.snap( tool, asPoint( task, "snap", task.getEvent() ) );

		return FAILURE;
	}

}
