package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;
import java.util.Collection;

public class Stretch extends EditCommand {

	private Bounds bounds;

	private DesignLine referenceLine;

	private Point3D anchor;

	private Point3D lastPoint;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		if( context.getTool().selectedShapes().isEmpty() ) return COMPLETE;

		setCaptureUndoChanges( context, false );

		if( parameters.length < 1 ) {
			promptForWindow( context, "stretch-points" );
			return INCOMPLETE;
		}

		// Ask for an anchor point
		if( parameters.length < 2 ) {
			bounds = asBounds( context, parameters[ 0 ] );
			addReference( context, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "anchor" );
			return INCOMPLETE;
		}

		// Ask for a target point
		if( parameters.length < 3 ) {
			anchor = asPoint( context, parameters[ 1 ] );
			referenceLine.setPoint( anchor ).setOrigin( anchor );
			addPreview( context, cloneReferenceShapes( context.getTool().getSelectedGeometry() ) );
			promptForPoint( context, "target" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			stretchShapes( getCommandShapes( context.getTool() ), asBounds( context, parameters[ 0 ] ), asPoint( context, parameters[ 1 ] ), asPoint( context, parameters[ 2 ] ) );
		} catch( ParseException exception ) {
			String title = Rb.text( BundleKey.NOTICE, "command-error" );
			String message = Rb.text( BundleKey.NOTICE, "unable-to-stretch-shapes", exception );
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
				case 2 -> referenceLine.setPoint( point ).setOrigin( point );
				case 3 -> {
					referenceLine.setPoint( point ).setOrigin( anchor );

					if( lastPoint == null ) lastPoint = anchor;
					stretchShapes( getPreview(), bounds, lastPoint, point );
					lastPoint = point;
				}
			}
		}
	}

	private static void stretchShapes( Collection<DesignShape> shapes, Bounds bounds, Point3D anchor, Point3D target ) {
		// TODO Implement Stretch.stretchShapes()
	}

}
