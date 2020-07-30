package com.avereon.cartesia.command;

import com.avereon.cartesia.*;
import com.avereon.cartesia.geometry.CasPoint;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;

import java.util.List;

public class PointCommand extends Command {

	private static final System.Logger log = Log.get();

	@Override
	public List<Command> getPreSteps( DesignTool tool ) {
		return List.of( new InputCommand( tool, "select-point" ) );
	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		Object object = processor.pullValue();

		if( object instanceof Point3D ) {
			processor.pushValue( tool, new CasPoint( (Point3D)object ) );
		} else {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error", object );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-create-point", object );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}
	}

}
