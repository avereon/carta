package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.math.CadGeometry;
import javafx.geometry.Point3D;

public abstract class DrawCommand extends Command {

	protected double deriveRotate( Point3D origin, Point3D point ) {
		return CadGeometry.angle360( point.subtract( origin ) );
	}

	protected double deriveXRadius( Point3D origin, Point3D xPoint ) {
		// This is the x-point distance from the origin
		return CadGeometry.distance( origin, xPoint );
	}

	protected double deriveYRadius( Point3D origin, Point3D xPoint, Point3D yPoint ) {
		// This is the y-point distance perpendicular to the origin x-point line
		return CadGeometry.linePointDistance( origin, xPoint, yPoint );
	}

	protected DesignLine createReferenceLine( CommandTask task ) {
		DesignLine line = new DesignLine( task.getContext().getWorldMouse(), task.getContext().getWorldMouse() );
		addReference( task, line );
		return line;
	}

	protected DesignLine createPreviewLine( CommandTask task ) {
		DesignLine line = new DesignLine( task.getContext().getWorldMouse(), task.getContext().getWorldMouse() );
		addPreview( task, setAttributesFromLayer( line, task.getTool().getCurrentLayer() ) );
		return line;
	}

	protected DesignBox createPreviewBox( CommandTask task ) {
		DesignBox box = new DesignBox( task.getContext().getWorldMouse(), Point3D.ZERO );
		addPreview( task, setAttributesFromLayer( box, task.getTool().getCurrentLayer() ) );
		return box;
	}

	protected DesignArc createPreviewArc( CommandTask task, Point3D origin ) {
		DesignArc arc = new DesignArc( origin, 0.0, 0.0, 360.0, DesignArc.Type.OPEN );
		addPreview( task, setAttributesFromLayer( arc, task.getTool().getCurrentLayer() ) );
		return arc;
	}

	protected DesignArc createPreviewArc3( CommandTask task, Point3D start, Point3D mid ) {
		DesignArc arc = CadGeometry.arcFromThreePoints( start, mid, mid );
		addPreview( task, setAttributesFromLayer( arc, task.getTool().getCurrentLayer() ) );
		return arc;
	}

	protected DesignEllipse createPreviewEllipse( CommandTask task, Point3D origin ) {
		DesignEllipse ellipse = new DesignEllipse( origin, 0.0, 0.0 );
		addPreview( task, setAttributesFromLayer( ellipse, task.getTool().getCurrentLayer() ) );
		return ellipse;
	}

	protected DesignEllipse createPreviewEllipse3( CommandTask task, Point3D start, Point3D mid ) {
		DesignArc arc = CadGeometry.arcFromThreePoints( start, mid, mid );
		DesignEllipse ellipse = new DesignEllipse( arc.getOrigin(), arc.getXRadius(), arc.getYRadius() );
		addPreview( task, setAttributesFromLayer( ellipse, task.getTool().getCurrentLayer() ) );
		return ellipse;
	}

	protected DesignCubic createPreviewCubic( CommandTask task ) {
		Point3D worldMouse = task.getContext().getWorldMouse();
		DesignCubic cubic = new DesignCubic( worldMouse, worldMouse, worldMouse, worldMouse );
		addPreview( task, setAttributesFromLayer( cubic, task.getTool().getCurrentLayer() ) );
		return cubic;
	}

	protected DesignMarker createPreviewMarker( CommandTask task ) {
		DesignMarker marker = new DesignMarker( task.getContext().getWorldMouse() );
		addPreview( task, setAttributesFromLayer( marker, task.getTool().getCurrentLayer() ) );
		return marker;
	}

	protected DesignText createPreviewText( CommandTask task ) {
		DesignText text = new DesignText( task.getContext().getWorldMouse() );
		addPreview( task, setTextAttributesFromLayer( text, task.getTool().getCurrentLayer() ) );
		return text;
	}

}
