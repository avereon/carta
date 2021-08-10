package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignCurve;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.CommandContext;
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
import java.util.List;
import java.util.Set;

@CustomLog
public class Stretch extends EditCommand {

	private Set<PointCoordinate> movingPoints;

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
			List<DesignShape> preview = cloneReferenceShapes( context.getTool().getSelectedGeometry() );
			addPreview( context, preview );
			movingPoints = computeMovingPoints( context.getTool(), preview, asBounds( context, parameters[ 0 ] ) );

			addReference( context, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "anchor" );
			return movingPoints.isEmpty() ? INVALID : INCOMPLETE;
		}

		// Ask for a target point
		if( parameters.length < 3 ) {
			anchor = asPoint( context, parameters[ 1 ] );
			referenceLine.setPoint( anchor ).setOrigin( anchor );
			promptForPoint( context, "target" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			Bounds bounds = asBounds( context, parameters[ 0 ] );
			Set<PointCoordinate> points = computeMovingPoints( context.getTool(), getCommandShapes( context.getTool() ), bounds );
			stretchShapes( points, asPoint( context, parameters[ 1 ] ), asPoint( context, parameters[ 2 ] ) );
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
					stretchShapes( movingPoints, lastPoint, point );
					lastPoint = point;
				}
			}
		}
	}

	private static Set<PointCoordinate> computeMovingPoints( DesignTool tool, Collection<DesignShape> shapes, Bounds bounds ) {
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
		} else if( shape instanceof DesignCurve ) {
			return Set.of( DesignLine.ORIGIN, DesignLine.POINT );
		}
		return Set.of();
	}

	private static void stretchShapes( Set<PointCoordinate> points, Point3D anchor, Point3D target ) {
		// Get a movement vector
		Point3D vector = target.subtract( anchor );

		// Go through the movement points and add the vector
		Txn.run( () -> points.forEach( p -> p.setPoint( p.getPoint().add( vector ) ) ) );
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

		public void setPoint( Point3D point ) {
			shape.setValue( key, point );
		}

	}

}
