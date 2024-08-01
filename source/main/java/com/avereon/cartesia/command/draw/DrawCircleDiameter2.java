package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.command.Command;
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
import lombok.CustomLog;

import java.text.ParseException;

@CustomLog
public class DrawCircleDiameter2 extends DrawCommand {

	private DesignEllipse previewEllipse;

	private DesignLine previewLine;

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		// Step 1
		if( parameters.length < 1 ) {
			addPreview( context, previewLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "start-point" );
			return Command.INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			Point3D origin = asPoint( context, parameters[ 0 ] );
			addPreview( context, previewEllipse = new DesignEllipse( origin, 0.0 ) );
			previewLine.setOrigin( origin );
			previewLine.setPoint( origin );
			promptForNumber( context, "diameter" );
			return Command.INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			Point3D a = asPoint( context, parameters[ 0 ] );
			Point3D b = asPoint( context, parameters[ 1 ] );
			Point3D origin = CadGeometry.midpoint( a, b );
			double radius = 0.5 * CadGeometry.distance( a, b );
			context.getTool().getCurrentLayer().addShape( new DesignEllipse( origin, radius ) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-create-shape", exception );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return Command.COMPLETE;
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
					previewEllipse.setOrigin( CadGeometry.midpoint( previewLine.getOrigin(), point ) );
					previewEllipse.setRadius( point.distance( previewEllipse.getOrigin() ) );
				}
			}
		}
	}

}
