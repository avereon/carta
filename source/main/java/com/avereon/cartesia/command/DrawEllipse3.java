package com.avereon.cartesia.command;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

public class DrawEllipse3 extends DrawCommand {

	private DesignLine previewLine;

	private DesignEllipse previewEllipse;

	private Point3D origin;

	private Point3D xPoint;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		// Step 1 - Prompt for the origin
		if( parameters.length < 1 ) {
			addPreview( context, previewLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "center" );
			return INCOMPLETE;
		}

		// Step 2 - Get center, prompt for x-radius
		if( parameters.length < 2 ) {
			origin = asPoint( context, parameters[ 0 ] );
			previewLine.setOrigin( origin );
			previewLine.setPoint( origin );
			addPreview( context, previewEllipse = new DesignEllipse( origin, 0.0 ) );
			promptForNumber( context, "radius" );
			return INCOMPLETE;
		}

		// Step 3 - Get x-point, prompt for y-radius
		if( parameters.length < 3 ) {
			xPoint = asPoint( context, parameters[ 1 ] );
			previewEllipse.setXRadius( CadGeometry.distance( previewEllipse.getOrigin(), xPoint ) );
			previewEllipse.setRotate( deriveRotate( origin, xPoint ) );
			promptForNumber( context, "radius" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			origin = asPoint( context, parameters[ 0 ] );
			xPoint = asPoint( context, parameters[ 1 ] );
			Point3D yPoint = asPoint( context, parameters[ 2 ] );
			double xRadius = asDouble( origin, xPoint );
			double yRadius = deriveYRadius( origin, xPoint, yPoint );
			double rotate = deriveRotate( origin, xPoint );
			context.getTool().getCurrentLayer().addShape( new DesignEllipse(origin, xRadius, yRadius, rotate) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-create-shape", exception );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			BaseDesignTool tool = (BaseDesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );

			switch( getStep() ) {
				case 1 -> {
					previewLine.setOrigin( point );
					previewLine.setPoint( point );
				}
				case 2 -> {
					previewLine.setPoint( point );
					previewEllipse.setRotate( deriveRotate( origin, point ) );
				}
				case 3 -> {
					previewLine.setPoint( point );
					previewEllipse.setYRadius( deriveYRadius( origin, xPoint, point ) );
				}
			}
		}
	}

}
