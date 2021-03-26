package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
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

	private DesignLine referenceLine;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			addReference( context, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "start-point" );
			return INCOMPLETE;
		}

		if( parameters.length < 2 ) {
			referenceLine.setOrigin( asPoint( context.getAnchor(), parameters[ 0 ] ) );
			promptForPoint( context, "end-point" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );

		try {
			Point3D p1 = asPoint( context.getAnchor(), parameters[ 0 ] );
			Point3D p2 = asPoint( context.getAnchor(), parameters[ 1 ] );
			double distance = p1.distance( p2 );

			String title = Rb.text( BundleKey.NOTICE, "measurement" );
			String message = Rb.text( BundleKey.NOTICE, "distance", distance );
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
			String title = Rb.text( BundleKey.NOTICE, "command-error" );
			String message = Rb.text( BundleKey.NOTICE, "unable-to-measure-distance", exception.getMessage() );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			if( referenceLine != null ) {
				DesignTool tool = (DesignTool)event.getSource();
				Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
				switch( getStep() ) {
					case 1 -> {
						referenceLine.setOrigin( point );
						referenceLine.setPoint( point );
					}
					case 2 -> referenceLine.setPoint( point );
				}
			}
		}
	}

}
