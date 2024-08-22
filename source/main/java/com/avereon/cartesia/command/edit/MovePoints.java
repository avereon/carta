package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignCubic;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.transaction.Txn;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.avereon.cartesia.command.Command.Result.*;

/**
 * <p>
 * The Move Points command allows the user to move a selected set of points on
 * selected geometry to a new location. The command requires the user to select
 * geometry before starting the command. The command will prompt the user to
 * select a window to define the area to move points, then the user will be
 * prompted to select an anchor point and a target point. The command will move
 * the selected points by the offset defined by the anchor and target points.
 * </p>
 *
 * <p>
 * This command is an efficient way to move points on multiple shapes at once.
 * </p>
 */
@CustomLog
public class MovePoints extends EditCommand {

	private Set<PointCoordinate> pointsToMove;

	private DesignLine referenceLine;

	private Point3D anchor;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		DesignCommandContext context = task.getContext();

		if( context.getTool().selectedFxShapes().isEmpty() ) return SUCCESS;

		setCaptureUndoChanges( context, false );

		if( task.getParameters().length < 1 ) {
			promptForWindow( task, "select-points" );
			return INCOMPLETE;
		}

		// Ask for an anchor point
		if( task.getParameters().length < 2 ) {
			Collection<DesignShape> preview = createPreviewShapes( context.getTool().getSelectedShapes() );
			addPreview( context, preview );

			pointsToMove = computePointsToMove( context.getTool(), preview, asBounds( context, task.getParameter( 0 ) ) );

			addReference( context, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "anchor" );
			return pointsToMove.isEmpty() ? INVALID : INCOMPLETE;
		}

		// Ask for a target point
		if( task.getParameters().length < 3 ) {
			anchor = asPoint( context, task.getParameter( 1 ) );
			referenceLine.setPoint( anchor ).setOrigin( anchor );
			promptForPoint( context, "target" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			Bounds bounds = asBounds( context, task.getParameter( 0 ) );
			Point3D anchor = asPoint( context, task.getParameter( 1 ) );
			Point3D target = asPoint( context, task.getParameter( 2 ) );
			modifyShapes( computePointsToMove( context.getTool(), context.getTool().getSelectedShapes(), bounds ), anchor, target );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-stretch-shapes", exception );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return SUCCESS;
	}

	@Override
	public void handle( DesignCommandContext context, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 2 -> referenceLine.setPoint( point ).setOrigin( point );
				case 3 -> {
					referenceLine.setPoint( point ).setOrigin( anchor );

					resetPreviewGeometry();
					modifyShapes( pointsToMove, anchor, point );
				}
			}
		}
	}

	private static Set<PointCoordinate> computePointsToMove( DesignTool tool, Collection<DesignShape> shapes, Bounds bounds ) {
		Set<PointCoordinate> points = new HashSet<>();

		for( DesignShape shape : shapes ) {
			for( String key : getShapePointKeys( shape ) ) {
				Point3D point = tool.worldToScreen( (Point3D)shape.getValue( key ) );
				if( bounds.contains( point ) ) points.add( new PointCoordinate( shape, key ) );
			}
		}

		return points;
	}

	private static Set<String> getShapePointKeys( DesignShape shape ) {
		if( shape instanceof DesignLine ) {
			return Set.of( DesignLine.ORIGIN, DesignLine.POINT );
		} else if( shape instanceof DesignEllipse ) {
			return Set.of( DesignLine.ORIGIN );
		} else if( shape instanceof DesignCubic ) {
			return Set.of( DesignLine.ORIGIN, DesignLine.POINT );
		}
		return Set.of();
	}

	private static void modifyShapes( Set<PointCoordinate> points, Point3D anchor, Point3D target ) {
		// Get an offset vector
		Point3D offset = target.subtract( anchor );

		// Go through the points to move and add the offset
		Txn.run( () -> points.forEach( p -> p.update( p.getPoint().add( offset ) ) ) );
	}

	private static class PointCoordinate {

		private final DesignShape shape;

		private final String key;

		public PointCoordinate( DesignShape shape, String key ) {
			this.shape = shape;
			this.key = key;
		}

		public Point3D getPoint() {
			return shape.getValue( key );
		}

		public void update( Point3D point ) {
			// FIXME This is not generating the events needed for undo to work
			shape.setValue( key, point );
		}

	}

}
