package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.DesignTool;
import com.avereon.cartesia.geometry.CsaLine;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;

import java.util.List;

public class LineCommand extends Command {

	private static final System.Logger log = Log.get();

	@Override
	public List<Command> getPreSteps( DesignTool tool ) {
		return List.of( new PromptCommand( tool, "start-point" ), new PromptCommand( tool, "end-point" ) );
	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		Object origin = processor.pullValue();
		Object point = processor.pullValue();

		if( origin instanceof Point3D && point instanceof Point3D ) {
			CsaLine line = new CsaLine( (Point3D)origin, (Point3D)point );
			processor.pushValue( tool, line );
			log.log( Log.DEBUG, "Add line=" + line );
		} else {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-create-line", origin, point );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}
	}

}
