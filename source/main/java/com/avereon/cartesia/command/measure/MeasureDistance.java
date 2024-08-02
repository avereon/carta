package com.avereon.cartesia.command.measure;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import com.avereon.zarra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import java.text.ParseException;
import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class MeasureDistance extends MeasureCommand {

	private DesignLine referenceLine;

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			addReference( context, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "start-point" );
			return INCOMPLETE;
		}

		if( parameters.length < 2 ) {
			referenceLine.setOrigin( asPoint( context.getWorldAnchor(), parameters[ 0 ] ) );
			promptForPoint( context, "end-point" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );

		try {
			Point3D p1 = asPoint( context.getWorldAnchor(), parameters[ 0 ] );
			Point3D p2 = asPoint( context.getWorldAnchor(), parameters[ 1 ] );
			double distance = p1.distance( p2 );

			String title = Rb.text( RbKey.NOTICE, "measurement" );
			String message = Rb.text( RbKey.NOTICE, "distance", distance );
			Notice notice = new Notice( title, message );
			notice.setAction( () -> Fx.run( () -> {
				Clipboard clipboard = Clipboard.getSystemClipboard();
				ClipboardContent content = new ClipboardContent();
				// TODO Run the distance value through the design value formatter
				content.putString( String.valueOf( distance ) );
				clipboard.setContent( content );
			} ) );
			if( context.isInteractive() ) context.getProduct().getProgram().getNoticeManager().addNotice( notice );

			log.atDebug().log( "Measured distance=%s", distance );
			return distance;
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-measure-distance", exception.getMessage() );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return SUCCESS;
	}

	@Override
	public void handle( DesignCommandContext context, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			if( referenceLine != null ) {
				BaseDesignTool tool = (BaseDesignTool)event.getSource();
				Point3D point = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );
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
