package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class Rotate extends EditCommand {

	private DesignLine referenceLine;

	private Point3D center;

	private Point3D anchor;

	private double angle;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( tool.selectedShapes().isEmpty() ) return COMPLETE;

		// Ask for a center point
		if( parameters.length < 1 ) {
			addReference( tool, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "center" );
			return INCOMPLETE;
		}

		// Ask for a start point
		if( parameters.length < 2 ) {
			center = asPoint( context, parameters[ 0 ] );
			referenceLine.setPoint( center ).setOrigin( center );
			promptForPoint( context, tool, "anchor" );
			return INCOMPLETE;
		}

		// Ask for a target point
		if( parameters.length < 3 ) {
			addPreview( tool, cloneShapes( tool.getSelectedShapes() ) );
			anchor = asPoint( context, parameters[ 1 ] );
			referenceLine.setPoint( anchor ).setOrigin( center );
			promptForPoint( context, tool, "target" );
			return INCOMPLETE;
		}

		clearPreview( tool );
		clearReference( tool );

		// Move the selected shapes
		setCaptureUndoChanges( tool, true );
		rotateShapes( tool.getSelectedShapes(), asPoint( context, parameters[ 0 ] ), asPoint( context, parameters[ 1 ] ), asPoint( context, parameters[ 2 ] ) );
		setCaptureUndoChanges( tool, false );

		tool.clearSelected();

		return COMPLETE;
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 1 -> referenceLine.setPoint( point ).setOrigin( point );
				case 2 -> referenceLine.setPoint( point );
				case 3 -> {
					referenceLine.setPoint( point );
					rotateShapes( getPreview(), center, -angle );
					angle = CadGeometry.pointAngle360( anchor, center, point );
					rotateShapes( getPreview(), center, angle );
				}
			}
		}
	}

}
