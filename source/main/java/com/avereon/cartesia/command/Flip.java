package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

public class Flip extends EditCommand {

	private DesignLine referenceLine;

	private Point3D anchor;

	private Point3D lastPoint;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( tool.selectedShapes().isEmpty() ) return COMPLETE;

		setCaptureUndoChanges( context, false );

		// Ask for an anchor point
		if( parameters.length < 1 ) {
			addReference( context, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "anchor" );
			return INCOMPLETE;
		}

		// Ask for a target point
		if( parameters.length < 2 ) {
			anchor = asPoint( context, parameters[ 0 ] );
			referenceLine.setPoint( anchor ).setOrigin( anchor );
			addPreview( context, cloneReferenceShapes( tool.getSelectedShapes() ) );
			promptForPoint( context, "target" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			// Start an undo multi-change
			flipShapes( getExecuteShapes( tool ), asPoint( context, parameters[ 0 ] ), asPoint( context, parameters[ 1 ] ) );
			// Done with undo multi-change
		} catch( ParseException exception ) {
			String title = Rb.text( BundleKey.NOTICE, "command-error" );
			String message = Rb.text( BundleKey.NOTICE, "unable-to-create-shape", exception );
			if( context.isInteractive() ) tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 1 -> referenceLine.setPoint( point ).setOrigin( point );
				case 2 -> {
					referenceLine.setPoint( point );

					if( lastPoint == null ) {
						flipShapes( getPreview(), anchor, point );
					} else {
						reflipShapes( getPreview(), anchor, lastPoint, point );
					}
					lastPoint = point;
				}
			}
		}
	}

}
