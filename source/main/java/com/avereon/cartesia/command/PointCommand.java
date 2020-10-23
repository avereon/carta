package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignPoint;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;

import java.text.ParseException;

public class PointCommand extends DrawCommand {

	private static final System.Logger log = Log.get();

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForValue( context, tool, BundleKey.PROMPT, "select-point" );
			return incomplete();
		}

		try {
			DesignPoint point = new DesignPoint( asPoint( parameters[0], context.getAnchor() ) );
			tool.getCurrentLayer().addShape( point );
		} catch( ParseException exception ) {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-create-point", exception );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return complete();
	}

}
