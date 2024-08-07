package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;
import static com.avereon.cartesia.command.Command.Result.*;

public class Scale extends EditCommand {

	private DesignLine referenceLine;

	private Point3D anchor;

	private Point3D source;

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		DesignTool tool = context.getTool();

		if( tool.selectedFxShapes().isEmpty() ) return SUCCESS;

		setCaptureUndoChanges( context, false );

		// Ask for a center point
		if( parameters.length < 1 ) {
			addReference( context, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "anchor" );
			return INCOMPLETE;
		}

		// Ask for a start point
		if( parameters.length < 2 ) {
			anchor = asPoint( context, parameters[ 0 ] );
			referenceLine.setPoint( anchor ).setOrigin( anchor );
			promptForPoint( context, "reference" );
			return INCOMPLETE;
		}

		// Ask for a target point
		if( parameters.length < 3 ) {
			source = asPoint( context, parameters[ 1 ] );
			referenceLine.setPoint( source ).setOrigin( source );
			addPreview( context, cloneAndAddReferenceShapes( tool.getSelectedShapes() ) );
			promptForPoint( context, "target" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			// Start an undo multi-change
			scaleShapes( tool, asPoint( context, parameters[ 0 ] ), asPoint( context, parameters[ 1 ] ), asPoint( context, parameters[ 2 ] ) );
			// Done with undo multi-change
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-create-shape", exception );
			if( context.isInteractive() ) tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return SUCCESS;
	}

	@Override
	public void handle( DesignCommandContext context, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D target = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 1 -> referenceLine.setPoint( target ).setOrigin( target );
				case 2 -> referenceLine.setPoint( target ).setOrigin( anchor );
				case 3 -> {
					referenceLine.setPoint( target ).setOrigin( anchor );

					resetPreviewGeometry();
					scaleShapes( getPreview(), anchor, source, target );
				}
			}
		}
	}

}
