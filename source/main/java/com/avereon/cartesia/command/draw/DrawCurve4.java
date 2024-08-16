package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignCubic;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class DrawCurve4 extends DrawCommand {

	private DesignCubic preview;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		// Step 1
		if( task.getParameterCount() == 0 ) {
			if( preview == null ) preview = createPreviewCubic( task );
			promptForPoint( task, "start-point" );
			return INCOMPLETE;
		}

		// Step 2
		if( task.getParameterCount() == 1 ) {
			if( preview == null ) preview = createPreviewCubic( task );
			preview.setOrigin( asPoint( task, "start-point", 0 ) );
			promptForPoint( task, "control-point" );
			return INCOMPLETE;
		}

		// Step 3
		if( task.getParameterCount() == 2 ) {
			if( preview == null ) preview = createPreviewCubic( task );
			preview.setOriginControl( asPoint( task, "control-point", 1 ) );
			promptForPoint( task, "control-point" );
			return INCOMPLETE;
		}

		// Step 4
		if( task.getParameterCount() == 3 ) {
			if( preview == null ) preview = createPreviewCubic( task );
			preview.setPointControl( asPoint( task, "control-point", 2 ) );
			promptForPoint( task, "end-point" );
			return INCOMPLETE;
		}

		setCaptureUndoChanges( task, true );

		if( task.hasParameter( 3 ) ) {
			Point3D a = asPoint( task, "start-point", 0 );
			Point3D b = asPoint( task, "control-point", 1 );
			Point3D c = asPoint( task, "control-point", 2 );
			Point3D d = asPoint( task, "end-point", 3 );
			task.getTool().getCurrentLayer().addShape( new DesignCubic( a, b, c, d ) );

			return SUCCESS;
		}

		return FAILURE;
	}

	@Override
	public void handle( CommandTask task, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			BaseDesignTool tool = (BaseDesignTool)event.getSource();
			Point3D point = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 1 -> {
					preview.setOrigin( point );
					preview.setOriginControl( point );
					preview.setPointControl( point );
					preview.setPoint( point );
				}
				case 2 -> {
					//preview.setOrigin( point );
					preview.setOriginControl( point );
					preview.setPointControl( point );
					preview.setPoint( point );
				}
				case 3 -> {
					//preview.setOrigin( point );
					//preview.setOriginControl( point );
					preview.setPointControl( point );
					preview.setPoint( point );
				}
				case 4 -> {
					//preview.setOrigin( point );
					//preview.setOriginControl( point );
					//preview.setPointControl( point );
					preview.setPoint( point );
				}
			}
		}
	}

}
