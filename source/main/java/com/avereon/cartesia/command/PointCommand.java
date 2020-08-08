package com.avereon.cartesia.command;

import com.avereon.cartesia.*;
import com.avereon.cartesia.geometry.CsaPoint;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;

import java.util.List;

public class PointCommand extends Command {

	private static final System.Logger log = Log.get();

	@Override
	public List<Command> getPreSteps( DesignTool tool ) {
		return List.of( new PromptCommand( tool, "select-point" ) );
	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		Object origin = processor.pullValue();

		if( origin instanceof Point3D ) {
			CsaPoint point = new CsaPoint( (Point3D)origin );
			processor.pushValue( tool, point );
			log.log( Log.DEBUG, "Add point=" + point );
		} else {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-create-point", origin );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}
	}

}
