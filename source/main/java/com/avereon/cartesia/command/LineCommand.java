package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

public class LineCommand extends DrawCommand {

	private static final System.Logger log = Log.get();

	@Override
	public void handle( MouseEvent event ) {
		// NEXT This method receives the mouse movements to allow for preview
	}

	//	@Override
	//	public List<OldCommand> getPreSteps( DesignTool tool ) {
	//		return List.of( new PromptForPointCommand( tool, "start-point" ), new PromptForPointCommand( tool, "end-point" ) );
	//	}

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForValue( context, tool, BundleKey.PROMPT, "start-point" );
			return incomplete();
		}
		if( parameters.length < 2 ) {
			promptForValue( context, tool, BundleKey.PROMPT, "end-point" );
			return incomplete();
		}

		try {
			Point3D p1 = asPoint( parameters[ 0 ], context.getAnchor() );
			Point3D p2 = asPoint( parameters[ 1 ], context.getAnchor() );
			DesignLine line = new DesignLine( p1, p2 );
			tool.getCurrentLayer().addShape( line );
		} catch( ParseException exception ) {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-create-line", exception.getMessage() );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return complete();
	}

}
