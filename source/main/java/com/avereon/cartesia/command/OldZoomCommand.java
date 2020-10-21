package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.OldCommand;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;

import java.util.List;

public class OldZoomCommand extends OldCommand {

	@Override
	public List<OldCommand> getPreSteps( DesignTool tool ) {
		return List.of( new PromptForValueCommand( tool, "zoom" ) );
	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		Object zoom = processor.pullValue();

		if( zoom instanceof Point3D ) {
			tool.setZoom( ((Point3D)zoom).getX() );
		} else {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-zoom", zoom );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}
	}


}
