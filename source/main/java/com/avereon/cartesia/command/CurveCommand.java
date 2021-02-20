package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;

import java.text.ParseException;

public class CurveCommand extends DrawCommand {

	//	@Override
	//	public List<OldCommand> getPreSteps( DesignTool tool ) {
	//		return List.of(
	//			new PromptForPointCommand( tool, "start-point" ),
	//			new PromptForPointCommand( tool, "control-point" ),
	//			new PromptForPointCommand( tool, "control-point" ),
	//			new PromptForPointCommand( tool, "end-point" )
	//		);
	//	}

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForPoint( context, tool, "point" );
			return INCOMPLETE;
		}
		if( parameters.length < 2 ) {
			promptForPoint( context, tool, "point" );
			return INCOMPLETE;
		}
		if( parameters.length < 3 ) {
			promptForPoint( context, tool, "point" );
			return INCOMPLETE;
		}
		if( parameters.length < 4 ) {
			promptForPoint( context, tool, "point" );
			return INCOMPLETE;
		}

		try {
			// Get the start point last
			Point3D origin = asPoint( tool, parameters[0], context.getAnchor() );
			// Get the start point last
			Object c2 = asPoint( tool, parameters[1], context.getAnchor() );
			// Get the start point last
			Object c3 = asPoint( tool, parameters[2], context.getAnchor() );
			// Get the end point first
			Object point = asPoint( tool, parameters[3], context.getAnchor() );

			//DesignCurve curve = new DesignCurve( origin, c2, c3, point );
			// TODO Create an undo command
			//return curve;
		} catch( ParseException exception ) {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-create-curve", exception );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}

}
