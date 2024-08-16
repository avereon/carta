package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class DrawLinePerpendicular extends DrawCommand {

	private DesignShape reference;

	private DesignLine preview;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		// Step 1
		if( task.getParameterCount() == 0 ) {
			promptForShape( task, "reference-shape-perpendicular" );
			return INCOMPLETE;
		}

		// Step 2
		if( task.getParameterCount() == 1 ) {
			Point3D point = asPoint( task, "reference-shape-perpendicular", 0 );
			reference = selectNearestShapeAtPoint( task, point );
			if( reference == DesignShape.NONE ) return INVALID;

			if( preview == null ) preview = createReferenceLine( task );
			promptForPoint( task, "start-point" );
			return INCOMPLETE;
		}

		// Step 3
		if( task.getParameterCount() == 2 ) {
			if( preview == null ) preview = createReferenceLine( task );
			preview.setOrigin( asPoint( task, "start-point", 1 ) );
			promptForPoint( task, "end-point" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 2 ) ) {
			setCaptureUndoChanges( task, true );

			Point3D shapePoint = asPoint( task, "start-point", 0 );
			Point3D origin = asPoint( task, "origin",1 );
			Point3D secondPoint = asPoint( task, "end-point", 2 );

			DesignShape shape = findNearestShapeAtPoint( task.getContext(), shapePoint );
			Point3D point = getPerpendicular( shape, origin, secondPoint );
			// Start an undo multi-change
			task.getTool().getCurrentLayer().addShape( new DesignLine( origin, point ) );
			// Done with undo multi-change

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
				case 2 -> {
					preview.setOrigin( point );
					preview.setPoint( point );
				}
				case 3 -> preview.setPoint( getPerpendicular( reference, preview.getOrigin(), point ) );
			}
		}
	}

	private Point3D getPerpendicular( DesignShape reference, Point3D origin, Point3D mouse ) {
		if( reference instanceof DesignLine line ) {
			Point3D u = line.getPoint().subtract( line.getOrigin() );
			Point3D v = new Point3D( -u.getY(), u.getX(), 0 ).normalize();
			double m = mouse.subtract( origin ).dotProduct( v );
			return origin.add( v.multiply( m ) );
		} else if( reference instanceof DesignEllipse ellipse ) {
			// FIXME This works well for circles and circle arcs,
			//  not for ellipses and ellipse arcs
			Point3D v = ellipse.getOrigin().subtract( origin ).normalize();
			double m = mouse.subtract( origin ).dotProduct( v );
			return origin.add( v.multiply( m ) );
		}

		return CadPoints.NONE;
	}

}
