package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import com.avereon.zarra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import lombok.CustomLog;

@CustomLog
public class MeasureLength extends MeasureCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		if( parameters.length < 1 ) {
			promptForShape( context, "select-shape" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );

		try {
			Point3D point = context.getScreenMouse();
			DesignShape shape = selectNearestShapeAtMouse( context, point );

			double length = shape.pathLength();

			String title = Rb.text( BundleKey.NOTICE, "measurement" );
			String message = shape == DesignShape.NONE ? Rb.text( BundleKey.NOTICE, "shape-not-selected" ) : Rb.text( BundleKey.NOTICE, "length", length );
			Notice notice = new Notice( title, message );
			notice.setAction( () -> Fx.run( () -> {
				Clipboard clipboard = Clipboard.getSystemClipboard();
				ClipboardContent content = new ClipboardContent();
				content.putString( message );
				clipboard.setContent( content );
			} ) );
			if( context.isInteractive() ) context.getProduct().getProgram().getNoticeManager().addNotice( notice );

			log.atDebug().log( "Measured length=%s", length );
			return point;
		} catch( Exception exception ) {
			String title = Rb.text( BundleKey.NOTICE, "command-error" );
			String message = Rb.text( BundleKey.NOTICE, "unable-to-measure-shape", exception.getMessage() );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}



}
