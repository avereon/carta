package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;

import java.text.ParseException;

public class ViewPointCommand extends CameraCommand {

	private static final System.Logger log = Log.get();

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForPoint( context, tool, BundleKey.PROMPT, "select-viewpoint" );
			return incomplete();
		}

		try {
			tool.setPan( asPoint( tool, parameters[0], context.getAnchor() ) );
		} catch( ParseException exception ) {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-move-to-viewpoint", exception );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return complete();
	}



}
