package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.text.ParseException;

public class MeasureDistanceCommand extends MeasureCommand {

	private static final System.Logger log = Log.get();

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForPoint( context, tool, BundleKey.PROMPT, "start-point" );
			return incomplete();
		}
		if( parameters.length < 2 ) {
			promptForPoint( context, tool, BundleKey.PROMPT, "end-point" );
			return incomplete();
		}

		try {
			Point3D p1 = asPoint( tool, parameters[ 0 ], context.getAnchor() );
			Point3D p2 = asPoint( tool, parameters[ 1 ], context.getAnchor() );
			double distance = p1.distance( p2 );

			String title = context.getProduct().rb().text( BundleKey.NOTICE, "measurement" );
			String message = context.getProduct().rb().text( BundleKey.NOTICE, "distance", distance );
			Notice notice = new Notice( title, message );
			notice.setAction( () -> Fx.run( () -> {
				Clipboard clipboard = Clipboard.getSystemClipboard();
				ClipboardContent content = new ClipboardContent();
				// TODO Run the distance value through the design value formatter
				content.putString( String.valueOf( distance ) );
				clipboard.setContent( content );
			} ) );
			context.getProduct().getProgram().getNoticeManager().addNotice( notice );

			log.log( Log.DEBUG, "Measured distance=" + distance );
			return distance;
		} catch( ParseException exception ) {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-measure-distance", exception.getMessage() );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return complete();
	}

}
