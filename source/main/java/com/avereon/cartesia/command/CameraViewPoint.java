package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;

import java.text.ParseException;

public class CameraViewPoint extends CameraCommand {

	private static final System.Logger log = Log.get();

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForPoint( context, tool, "select-viewpoint" );
			return INCOMPLETE;
		}

		try {
			tool.setViewPoint( asPoint( context.getAnchor(), parameters[ 0 ] ) );
		} catch( ParseException exception ) {
			String title = Rb.text( BundleKey.NOTICE, "command-error" );
			String message = Rb.text( BundleKey.NOTICE, "unable-to-move-to-viewpoint", exception );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}

}
