package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;

import java.util.List;

public class PanCommand extends Command {

	@Override
	public List<Command> getPreSteps( DesignTool tool ) {
		return List.of( new PromptCommand( tool, "pan-point" ) );
	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		Object origin = processor.pullValue();

		if( origin instanceof Point3D ) {
			tool.setPan( (Point3D)origin );
		} else {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-create-point", origin );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}
	}

}
