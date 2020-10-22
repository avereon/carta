package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;

import java.text.ParseException;

public class MeasureDistanceCommand extends MeasureCommand {

	private static final System.Logger log = Log.get();

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		log.log( Log.WARN, "Parameter count=" + parameters.length );
		if( parameters.length < 1 ) {
			promptForValue( context, tool, BundleKey.PROMPT, "start-point", this );
			return incomplete();
		}

		if( parameters.length < 2 ) {
			promptForValue( context, tool, BundleKey.PROMPT, "select-point", this );
			return incomplete();
		}

		try {
			Point3D p1 = asPoint( parameters[ 0 ], context.getAnchor() );
			Point3D p2 = asPoint( parameters[ 1 ], context.getAnchor() );
			return p1.distance( p2 );
		} catch( ParseException exception ) {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-measure-distance", exception.getMessage() );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return setComplete();
	}

}
