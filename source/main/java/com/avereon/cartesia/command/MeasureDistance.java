package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

public class MeasureDistance extends MeasureCommand {

	private static final System.Logger log = Log.get();

	private int step;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			setPreview( tool, new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "start-point" );
			step = 1;
			return INCOMPLETE;
		}

		if( parameters.length < 2 ) {
			getPreview().setOrigin( asPoint( tool, parameters[ 0 ], context.getAnchor() ) );
			promptForPoint( context, tool, "end-point" );
			step = 2;
			return INCOMPLETE;
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

			removePreview( tool );

			log.log( Log.DEBUG, "Measured distance=" + distance );
			return distance;
		} catch( ParseException exception ) {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-measure-distance", exception.getMessage() );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}

	@Override
	public void handle( MouseEvent event ) {
		DesignLine preview = getPreview();
		if( preview != null && event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			Fx.run( () -> {
				DesignTool tool = (DesignTool)event.getSource();
				Point3D mouse = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
				if( step < 2 ) preview.setOrigin( mouse );
				preview.setPoint( mouse );
			} );
		}
	}

}
