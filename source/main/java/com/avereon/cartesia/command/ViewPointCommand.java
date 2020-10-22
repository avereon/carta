package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.OldCommand;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;

public class ViewPointCommand extends OldCommand {

	private static final System.Logger log = Log.get();

//	@Override
//	public List<OldCommand> getPreSteps( DesignTool tool ) {
//		return List.of( new PromptForPointCommand( tool, "select-viewpoint" ) );
//	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		Object viewpoint = processor.pullValue();

		if( viewpoint instanceof Point3D ) {
			tool.setPan( (Point3D)viewpoint );
		} else {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-move-to-viewpoint", viewpoint );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}
	}



}
