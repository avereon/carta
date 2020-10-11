package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;

import java.util.List;

public class CurveCommand extends Command {

	@Override
	public List<Command> getPreSteps( DesignTool tool ) {
		return List.of(
			new PromptForPointCommand( tool, "start-point" ),
			new PromptForPointCommand( tool, "control-point" ),
			new PromptForPointCommand( tool, "control-point" ),
			new PromptForPointCommand( tool, "end-point" )
		);
	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		// Get the end point first
		Object point = processor.pullValue();
		// Get the start point last
		Object c3 = processor.pullValue();
		// Get the start point last
		Object c2 = processor.pullValue();
		// Get the start point last
		Object origin = processor.pullValue();

		if( origin instanceof Point3D && c2 instanceof Point3D && c3 instanceof Point3D && point instanceof Point3D ) {
			//DesignCurve curve = new DesignCurve( (Point3D)origin, (Point3D)c2, (Point3D)c3, (Point3D)point );
			//processor.pushValue( tool, curve );
		} else {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-create-curve", origin, point );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}
	}

}
