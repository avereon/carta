package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import java.text.ParseException;

@CustomLog
public class DrawLinePerpendicular extends DrawCommand {

	private DesignShape reference;

	private DesignLine preview;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		// Step 1
		if( parameters.length < 1 ) {
			promptForShape( context, "reference-shape-perpendicular" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			reference = selectNearestShapeAtPoint( context, asPoint( context.getWorldAnchor(), parameters[ 0 ] ) );
			if( reference == DesignShape.NONE ) return INVALID;

			addPreview( context, preview = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "start-point" );
			return INCOMPLETE;
		}

		// Step 3
		if( parameters.length < 3 ) {
			preview.setOrigin( asPoint( context, parameters[ 1 ] ) );
			promptForPoint( context, "end-point" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			DesignShape shape = findNearestShapeAtPoint( context, asPoint( context.getWorldAnchor(), parameters[ 0 ] ) );
			Point3D origin = asPoint( context.getWorldAnchor(), parameters[ 1 ] );
			Point3D point = getPerpendicular( shape, origin, asPoint( context.getWorldAnchor(), parameters[ 2 ] ) );
			// Start an undo multi-change
			context.getTool().getCurrentLayer().addShape( new DesignLine( origin, point ) );
			// Done with undo multi-change
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-create-shape", exception );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
			return FAIL;
		}

		return COMPLETE;
	}

	@Override
	public void handle( CommandContext context, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			BaseDesignTool tool = (BaseDesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 2 -> {
					preview.setOrigin( point );
					preview.setPoint( point );
				}
				case 3 -> preview.setPoint( getPerpendicular( reference, preview.getOrigin(), point ) );
			}
		}
	}

	private Point3D getPerpendicular( DesignShape reference, Point3D origin, Point3D mouse ) {
		if( reference instanceof DesignLine ) {
			DesignLine line = (DesignLine)reference;
			Point3D u = line.getPoint().subtract( line.getOrigin() );
			Point3D v = new Point3D( -u.getY(), u.getX(), 0 ).normalize();
			double m = mouse.subtract( origin ).dotProduct( v );
			return origin.add( v.multiply( m ) );
		} else if( reference instanceof DesignEllipse ) {
			// This works well for circles and circle arcs, not for ellipses and ellipse arcs
			DesignEllipse ellipse = (DesignEllipse)reference;
			Point3D v = ellipse.getOrigin().subtract( origin ).normalize();
			double m = mouse.subtract( origin ).dotProduct( v );
			return origin.add( v.multiply( m ) );
		}

		return CadPoints.NONE;
	}

}
