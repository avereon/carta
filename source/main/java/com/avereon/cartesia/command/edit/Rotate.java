package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import java.text.ParseException;

@CustomLog
public class Rotate extends EditCommand {

	private DesignLine referenceLine;

	private Point3D center;

	private Point3D anchor;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		BaseDesignTool tool = context.getTool();
		if( tool.selectedShapes().isEmpty() ) return COMPLETE;

		setCaptureUndoChanges( context, false );

		// Ask for a center point
		if( parameters.length < 1 ) {
			addReference( context, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "anchor" );
			return INCOMPLETE;
		}

		// Ask for a start point
		if( parameters.length < 2 ) {
			center = asPoint( context, parameters[ 0 ] );
			referenceLine.setPoint( center ).setOrigin( center );
			promptForPoint( context, "start-point" );
			return INCOMPLETE;
		}

		// Ask for a target point
		if( parameters.length < 3 ) {
			anchor = asPoint( context, parameters[ 1 ] );
			referenceLine.setPoint( anchor ).setOrigin( center );
			addPreview( context, cloneAndAddReferenceShapes( tool.getSelectedGeometry() ) );
			promptForPoint( context, "target" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			Point3D a = asPoint( context, parameters[ 0 ] );
			Point3D s = asPoint( context, parameters[ 1 ] );

			// FIXME Why does t appear to be relative to s, instead of a?
			Point3D t = asPoint( a, parameters[ 2 ] );

			// Furthermore, why, when using a relative coordinate, is a line not made?

			log.atConfig().log( "a=%s s=%s t=%s", a, s, t );

			// Start an undo multi-change
			rotateShapes( tool, a, s, t );
			// Done with undo multi-change
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-create-shape", exception );
			if( context.isInteractive() ) tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}

	@Override
	public void handle( CommandContext context, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			BaseDesignTool tool = (BaseDesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 1 -> referenceLine.setPoint( point ).setOrigin( point );
				case 2 -> referenceLine.setPoint( point );
				case 3 -> {
					referenceLine.setPoint( point );

					resetPreviewGeometry();
					rotateShapes( getPreview(), center, CadGeometry.pointAngle360( anchor, center, point ) );
				}
			}
		}
	}

}
