package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignPane;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

public class CameraZoomGestureCommand extends CameraZoomCommand {

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) {
		if( parameters.length == 1 && parameters[ 0 ] instanceof GestureEvent ) {
			GestureEvent event = (GestureEvent)parameters[ 0 ];
			Point3D point = tool.mouseToWorld( event.getX(), event.getY(), event.getZ() );

			if( event.getEventType() == ScrollEvent.SCROLL ) {
				double deltaY = ((ScrollEvent)event).getDeltaY();
				if( deltaY != 0.0 ) zoomByFactor( tool, point, deltaY > 0 ? DesignPane.ZOOM_IN_FACTOR : DesignPane.ZOOM_OUT_FACTOR );
			}

			if( event.getEventType() == ZoomEvent.ZOOM ) {
				double zoomFactor = ((ZoomEvent)event).getZoomFactor();
				if( zoomFactor != 0.0 ) zoomByFactor( tool, point, zoomFactor );
			}
		}

		return complete();
	}

}
