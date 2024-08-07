package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;
import static com.avereon.cartesia.command.Command.Result.*;

public class DrawEllipse3 extends DrawCommand {

	private DesignLine previewLine;

	private DesignEllipse previewEllipse;

	private Point3D origin;

	private Point3D xPoint;

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
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
			previewEllipse.setRadii( new Point3D( CadGeometry.distance( previewEllipse.getOrigin(), xPoint ), 0, 0 ) );
			previewEllipse.setRotate( String.valueOf( deriveRotate( origin, xPoint ) ) );
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
			context.getTool().getCurrentLayer().addShape( new DesignEllipse( origin, xRadius, yRadius, rotate ) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-create-shape", exception );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return SUCCESS;
	}

	@Override
	public void handle( DesignCommandContext context, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			BaseDesignTool tool = (BaseDesignTool)event.getSource();
			Point3D point = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );

			switch( getStep() ) {
				case 1 -> {
					previewLine.setOrigin( point );
					previewLine.setPoint( point );
				}
				case 2 -> {
					previewLine.setPoint( point );
					previewEllipse.setRotate( String.valueOf( deriveRotate( origin, point ) ) );
				}
				case 3 -> {
					previewLine.setPoint( point );
					previewEllipse.setRadii( new Point3D( previewEllipse.getXRadius(), deriveYRadius( origin, xPoint, point ), 0 ) );
				}
			}
		}
	}

}
