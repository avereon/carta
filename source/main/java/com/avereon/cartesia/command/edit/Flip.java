package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;
import static com.avereon.cartesia.command.Command.Result.*;

public class Flip extends EditCommand {

	private DesignLine referenceLine;

	private Point3D anchor;

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		if( context.getTool().selectedFxShapes().isEmpty() ) return SUCCESS;

		setCaptureUndoChanges( context, false );

		// Ask for an anchor point
		if( parameters.length < 1 ) {
			addReference( context, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "axis-anchor" );
			return INCOMPLETE;
		}

		// Ask for a target point
		if( parameters.length < 2 ) {
			anchor = asPoint( context, parameters[ 0 ] );
			referenceLine.setPoint( anchor ).setOrigin( anchor );
			addPreview( context, createPreviewShapes( context.getTool().getSelectedShapes() ) );
			promptForPoint( context, "axis-point" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			final Point3D anchor = asPoint( context, parameters[ 0 ] );
			final Point3D point = asPoint( context, parameters[ 1 ] );

			flipShapes( context.getTool(), anchor, point );
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
				case 1 -> referenceLine.setPoint( point ).setOrigin( point );
				case 2 -> {
					referenceLine.setPoint( point );

					resetPreviewGeometry();
					flipShapes( getPreview(), anchor, point );
				}
			}
		}
	}

}
