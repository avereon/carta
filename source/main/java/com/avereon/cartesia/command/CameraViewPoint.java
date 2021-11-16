package com.avereon.cartesia.command;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import lombok.CustomLog;

import java.text.ParseException;

@CustomLog
public class CameraViewPoint extends CameraCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForPoint( context, "select-viewpoint" );
			return INCOMPLETE;
		}

		try {
			context.getTool().setViewPoint( asPoint( context.getAnchor(), parameters[ 0 ] ) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-move-to-viewpoint", exception );
			context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}

}
