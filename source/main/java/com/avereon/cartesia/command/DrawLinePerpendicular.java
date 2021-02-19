package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawLinePerpendicular extends DrawCommand {

	private static final System.Logger log = Log.get();

	private DesignShape reference;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// Step 1
		if( parameters.length < 1 ) {
			promptForShape( context, tool, "reference-shape-perpendicular" );
			return incomplete();
		}

		// Step 2
		if( parameters.length < 2 ) {
			reference = selectNearestShapeAtPoint( tool, asPoint( tool, parameters[ 0 ], context.getAnchor() ) );
			if( reference == DesignShape.NONE ) return INVALID;

			promptForPoint( context, tool, "start-point" );
			return incomplete();
		}

		// Step 3
		if( parameters.length < 3 ) {
			Point3D point = asPoint( tool, parameters[ 1 ], context.getAnchor() );
			setPreview( tool, new DesignLine( point, point ) );
			promptForPoint( context, tool, "end-point" );
			return incomplete();
		}

		DesignShape shape = findNearestShapeAtPoint( tool, asPoint( tool, parameters[ 0 ], context.getAnchor() ) );
		Point3D origin = asPoint( tool, parameters[ 1 ], context.getAnchor() );
		Point3D point = getPerpendicular( shape, origin, asPoint( tool, parameters[ 2 ], context.getAnchor() ) );
		((DesignLine)getPreview()).setPoint( point );
		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D mouse = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );

			switch( getStep() ) {
				case 3 -> {
					DesignLine preview = getPreview();
					preview.setPoint( getPerpendicular( reference, preview.getOrigin(), mouse ) );
				}
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
