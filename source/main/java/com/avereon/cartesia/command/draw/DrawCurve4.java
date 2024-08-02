package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignCubic;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import java.text.ParseException;

@CustomLog
public class DrawCurve4 extends DrawCommand {

	private DesignCubic preview;

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		// Step 1
		if( parameters.length < 1 ) {
			addPreview( context, preview = new DesignCubic( context.getWorldMouse(), context.getWorldMouse(), context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "start-point" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			preview.setOrigin( asPoint( context, parameters[ 0 ] ) );
			promptForPoint( context, "control-point" );
			return INCOMPLETE;
		}

		// Step 3
		if( parameters.length < 3 ) {
			preview.setOriginControl( asPoint( context, parameters[ 1 ] ) );
			promptForPoint( context, "control-point" );
			return INCOMPLETE;
		}

		// Step 4
		if( parameters.length < 4 ) {
			preview.setPointControl( asPoint( context, parameters[ 2 ] ) );
			promptForPoint( context, "end-point" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			Point3D a = asPoint( context, parameters[ 0 ] );
			Point3D b = asPoint( context, parameters[ 1 ] );
			Point3D c = asPoint( context, parameters[ 2 ] );
			Point3D d = asPoint( context, parameters[ 3 ] );
			context.getTool().getCurrentLayer().addShape( new DesignCubic( a, b, c, d ) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-create-shape", exception );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return SUCCESS;
	}

	@Override
	public void handle( DesignCommandContext context, MouseEvent event ) {
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
