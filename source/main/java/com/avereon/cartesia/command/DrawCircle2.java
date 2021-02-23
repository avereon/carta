package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawCircle2 extends DrawCommand {

	private static final System.Logger log = Log.get();

	private DesignEllipse previewEllipse;

	private DesignLine previewLine;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// Step 1
		if( parameters.length < 1 ) {
			addPreview( tool, previewLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "center" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			addPreview( tool, previewEllipse = new DesignEllipse( asPoint( context, parameters[ 0 ] ), 0.0 ) );
			promptForNumber( context, tool, "radius" );
			return INCOMPLETE;
		}

		removePreview( tool, previewLine );
		previewEllipse.setRadius( asDouble( previewEllipse.getOrigin(), parameters[ 1 ] ) );
		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
				DesignTool tool = (DesignTool)event.getSource();
				Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
				switch( getStep() ) {
					case 1 -> {
						previewLine.setOrigin( point );
						previewLine.setPoint( point );
					}
					case 2 -> {
						previewLine.setPoint( point );
						previewEllipse.setRadius( point.distance( previewEllipse.getOrigin() ) );
					}
				}
		}
	}

}
