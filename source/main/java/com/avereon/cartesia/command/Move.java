package com.avereon.cartesia.command;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

public class Move extends EditCommand {

	private DesignLine referenceLine;

	private Point3D anchor;

	private Point3D lastPoint;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		if( context.getTool().selectedShapes().isEmpty() ) return COMPLETE;

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
			addPreview( context, cloneAndAddReferenceShapes( context.getTool().getSelectedGeometry() ) );
			promptForPoint( context, "target" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			final Point3D anchor = asPoint( context, parameters[ 0 ] );
			final Point3D target = asPoint( context, parameters[ 1 ] );
			moveShapes( context.getTool(), anchor, target );
			//edit( context.getTool(), CadTransform.translation( target.subtract( anchor ) ) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-move-shapes", exception );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
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
					// FIXME Need to handle preview geometry without the need for prior data
					referenceLine.setPoint( point ).setOrigin( anchor );

					if( lastPoint == null ) lastPoint = anchor;
					moveShapes( getPreview(), lastPoint, point );
					lastPoint = point;
				}
			}
		}
	}

}
