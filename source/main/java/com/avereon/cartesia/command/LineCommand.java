package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;

import java.util.List;

public class LineCommand extends Command {

	private static final System.Logger log = Log.get();

	@Override
	public void mouse( Point3D point ) {
		// NEXT This method receives the mouse movements to allow for preview
	}

	@Override
	public List<Command> getPreSteps( DesignTool tool ) {
		return List.of( new PromptForPointCommand( tool, "start-point" ), new PromptForPointCommand( tool, "end-point" ) );
	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		// Get the end point first
		Object point = processor.pullValue();
		// Get the start point last
		Object origin = processor.pullValue();

		if( origin instanceof Point3D && point instanceof Point3D ) {
			DesignLine line = new DesignLine( (Point3D)origin, (Point3D)point );
			processor.pushValue( tool, line );
		} else {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-create-line", origin, point );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}
	}

}
