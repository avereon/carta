package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.cartesia.tool.view.DesignPaneMarea;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import lombok.CustomLog;

import java.text.ParseException;

@CustomLog
public class CameraZoom extends CameraCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		// This command requires one value as the zoom value
		if( parameters.length < 1 ) {
			promptForNumber( context, "zoom" );
			return INCOMPLETE;
		}

		if( parameters[ 0 ] instanceof GestureEvent event ) {
			Point3D point = context.getTool().screenToWorkplane( event.getX(), event.getY(), event.getZ() );

			if( event.getEventType() == ScrollEvent.SCROLL ) {
				// NOTE Using the shift key causes the deltaX to change :-)
				double deltaX = ((ScrollEvent)event).getDeltaX();
				double deltaY = ((ScrollEvent)event).getDeltaY();
				if( deltaX != 0.0 ) zoomByFactor( context.getTool(), point, deltaX > 0 ? DesignPaneMarea.ZOOM_IN_FACTOR : DesignPaneMarea.ZOOM_OUT_FACTOR );
				if( deltaY != 0.0 ) zoomByFactor( context.getTool(), point, deltaY > 0 ? DesignPaneMarea.ZOOM_IN_FACTOR : DesignPaneMarea.ZOOM_OUT_FACTOR );
			}

			if( event.getEventType() == ZoomEvent.ZOOM ) {
				double zoomFactor = ((ZoomEvent)event).getZoomFactor();
				if( zoomFactor != 0.0 ) zoomByFactor( context.getTool(), point, zoomFactor );
			}
			return SUCCESS;
		}

		try {
			context.getTool().setZoom( asDouble( parameters[ 0 ] ) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-zoom", exception.getMessage() );
			context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return SUCCESS;
	}

	protected void zoomByFactor( DesignTool tool, Point3D point, double factor ) {
		tool.zoom( point, factor );
	}

}
