package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import java.text.ParseException;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static com.avereon.cartesia.command.Command.Result.SUCCESS;

@CustomLog
public class DrawCircle2 extends DrawCommand {

	private DesignEllipse previewEllipse;

	private DesignLine previewLine;

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		// Step 1
		if( parameters.length < 1 ) {
			addPreview( context, previewLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "center" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			Point3D origin = asPoint( context, parameters[ 0 ] );
			addPreview( context, previewEllipse = new DesignEllipse( origin, 0.0 ) );
			previewLine.setOrigin( origin );
			previewLine.setPoint( origin );
			promptForNumber( context, "radius" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			Point3D origin = asPoint( context, parameters[ 0 ] );
			double radius = asDouble( previewEllipse.getOrigin(), parameters[ 1 ] );
			context.getTool().getCurrentLayer().addShape( new DesignEllipse( origin, radius ) );
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
					previewLine.setOrigin( point );
					previewLine.setPoint( point );
				}
				case 2 -> {
					previewLine.setPoint( point );
					previewEllipse.setRadius( point.distance( previewEllipse.getOrigin() ) );
				}
			}
		}
	}

}
