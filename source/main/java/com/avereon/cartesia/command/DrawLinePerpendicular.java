package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawLinePerpendicular extends DrawCommand {

	private static final System.Logger log = Log.get();

	private DesignShape reference;

	private int step;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {

		if( parameters.length < 1 ) {
			promptForShape( context, tool, "reference-shape-perpendicular" );
			step = 1;
			return incomplete();
		}

		if( parameters.length < 2 ) {
			reference = selectNearestShapeAtPoint( tool, asPoint( tool, parameters[ 0 ], context.getAnchor() ) );
			if( reference == DesignShape.NONE ) return INVALID;

			promptForPoint( context, tool, "start-point" );
			step = 2;
			return incomplete();
		}

		if( parameters.length < 3 ) {
			Point3D point = asPoint( tool, parameters[ 1 ], context.getAnchor() );
			setPreview( tool, new DesignLine( point, point ) );
			promptForPoint( context, tool, "end-point" );
			step = 3;
			return incomplete();
		}

		if( parameters.length < 4 ) {
			Point3D origin = asPoint( tool, parameters[ 1 ], context.getAnchor() );
			Point3D point = getPerpendicular( reference, origin, asPoint( tool, parameters[ 2 ], context.getAnchor() ) );
			((DesignLine)getPreview()).setPoint( point );
		}

		try {
			return commitPreview( tool );
		} finally {
			tool.clearSelected();
		}
	}

	@Override
	public void handle( MouseEvent event ) {
		DesignLine preview = getPreview();
		if( preview != null && event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D mouse = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );

			Fx.run( () -> {
				if( step < 3 ) {
					preview.setOrigin( mouse );
					preview.setPoint( mouse );
				} else {
					preview.setPoint( getPerpendicular( reference, preview.getOrigin(), mouse ) );
				}
			} );
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
