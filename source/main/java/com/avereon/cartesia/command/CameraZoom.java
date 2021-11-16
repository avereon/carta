package com.avereon.cartesia.command;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.view.DesignPane;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

import java.text.ParseException;

public class CameraZoom extends CameraCommand {

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		// This command requires one value as the zoom value
		if( parameters.length < 1 ) {
			promptForNumber( context, "zoom" );
			return INCOMPLETE;
		}

		if( parameters[ 0 ] instanceof GestureEvent ) {
			GestureEvent event = (GestureEvent)parameters[ 0 ];
			Point3D point = context.getTool().mouseToWorkplane( event.getX(), event.getY(), event.getZ() );

			if( event.getEventType() == ScrollEvent.SCROLL ) {
				double deltaY = ((ScrollEvent)event).getDeltaY();
				if( deltaY != 0.0 ) zoomByFactor( context.getTool(), point, deltaY > 0 ? DesignPane.ZOOM_IN_FACTOR : DesignPane.ZOOM_OUT_FACTOR );
			}

			if( event.getEventType() == ZoomEvent.ZOOM ) {
				double zoomFactor = ((ZoomEvent)event).getZoomFactor();
				if( zoomFactor != 0.0 ) zoomByFactor( context.getTool(), point, zoomFactor );
			}
			return COMPLETE;
		}

		try {
			context.getTool().setZoom( asDouble( parameters[ 0 ] ) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-zoom", exception.getMessage() );
			context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}

	protected void zoomByFactor( DesignTool tool, Point3D point, double factor ) {
		tool.zoom( point, factor );
	}

}
