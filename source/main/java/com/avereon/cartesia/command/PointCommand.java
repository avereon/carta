package com.avereon.cartesia.command;

import com.avereon.cartesia.*;
import com.avereon.cartesia.data.DesignPoint;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;

import java.util.List;

public class PointCommand extends OldCommand {

	private static final System.Logger log = Log.get();

	@Override
	public List<OldCommand> getPreSteps( DesignTool tool ) {
		return List.of( new PromptForPointCommand( tool, "select-point" ) );
	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		Object origin = processor.pullValue();

		if( origin instanceof Point3D ) {
			processor.pushValue( tool, new DesignPoint( (Point3D)origin ) );
		} else {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-create-point", origin );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}
	}

}
