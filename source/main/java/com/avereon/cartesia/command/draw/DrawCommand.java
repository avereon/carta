package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.CommandTask;
import javafx.geometry.Point3D;

public abstract class DrawCommand extends Command {

	protected double deriveRotate( Point3D origin, Point3D point ) {
		return CadGeometry.angle360( point.subtract( origin ) );
	}

	protected double deriveYRadius( Point3D origin, Point3D xPoint, Point3D yPoint ) {
		// This is the origin y-point distance
		//return origin.distance( yPoint );

		// This is the y-point distance perpendicular to the origin x-point line
		return CadGeometry.linePointDistance( origin, xPoint, yPoint );
	}

	protected DesignLine createReferenceLine( CommandTask task ) {
		DesignLine line = new DesignLine( task.getContext().getWorldMouse(), task.getContext().getWorldMouse() );
		addReference( task, line );
		return line;
	}

	protected DesignArc createPreviewArc( CommandTask task, Point3D origin ) {
		DesignArc arc = new DesignArc( origin, 0.0, 0.0, 360.0, DesignArc.Type.OPEN );
		addPreview( task, setAttributesFromLayer( arc, task.getTool().getCurrentLayer() ) );
		arc.setDrawPaint( "#ff0000" );
		return arc;
	}

	protected DesignArc createPreviewArc3( CommandTask task, Point3D start, Point3D mid ) {
		DesignArc arc = CadGeometry.arcFromThreePoints( start, mid, mid );
		addPreview( task, setAttributesFromLayer( arc, task.getTool().getCurrentLayer() ) );
		return arc;
	}

}
