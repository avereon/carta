package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.tool.CommandTask;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.cartesia.tool.view.DesignPaneMarea;
import javafx.geometry.Point3D;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class CameraZoom extends CameraCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		// This command requires one value as the zoom value
		if( task.getParameterCount() == 0 && task.getEvent() == null ) {
			promptForNumber( task, "zoom" );
			return INCOMPLETE;
		}

		if( task.getParameterCount() == 0  && task.getEvent() instanceof GestureEvent event ) {
			Point3D point = task.getTool().screenToWorkplane( event.getX(), event.getY(), 0 );

			if( event instanceof ScrollEvent scrollEvent ) {
				// NOTE Using the shift key causes the deltaX to change :-)
				double deltaX = scrollEvent.getDeltaX();
				double deltaY = scrollEvent.getDeltaY();
				if( deltaX != 0.0 ) zoomByFactor( task.getTool(), point, deltaX > 0 ? DesignPaneMarea.ZOOM_IN_FACTOR : DesignPaneMarea.ZOOM_OUT_FACTOR );
				if( deltaY != 0.0 ) zoomByFactor( task.getTool(), point, deltaY > 0 ? DesignPaneMarea.ZOOM_IN_FACTOR : DesignPaneMarea.ZOOM_OUT_FACTOR );
				return SUCCESS;
			}

			if( event instanceof ZoomEvent zoomEvent ) {
				double zoomFactor = zoomEvent.getZoomFactor();
				if( zoomFactor != 0.0 ) zoomByFactor( task.getTool(), point, zoomFactor );
				return SUCCESS;
			}
		}

		if( task.hasParameter( 0 ) ) {
			task.getTool().setZoom( asDouble( task.getParameter( 0 ) ) );
			return SUCCESS;
		}

		return FAILURE;
	}

	protected void zoomByFactor( DesignTool tool, Point3D point, double factor ) {
		tool.zoom( point, factor );
	}

}
