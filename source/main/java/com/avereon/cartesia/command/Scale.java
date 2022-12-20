package com.avereon.cartesia.command;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;
import java.util.Collection;

public class Scale extends EditCommand {

	private DesignLine referenceLine;

	private Point3D anchor;

	private Point3D source;

	private Point3D prior;

	private Collection<DesignShape> selected;

	private Collection<DesignShape> preview;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		DesignTool tool = context.getTool();

		if( tool.selectedShapes().isEmpty() ) return COMPLETE;

		setCaptureUndoChanges( context, false );

		// Ask for a center point
		if( parameters.length < 1 ) {
			addReference( context, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "center" );
			return INCOMPLETE;
		}

		// Ask for a start point
		if( parameters.length < 2 ) {
			anchor = asPoint( context, parameters[ 0 ] );
			referenceLine.setPoint( anchor ).setOrigin( anchor );
			promptForPoint( context, "anchor" );
			return INCOMPLETE;
		}

		// Ask for a target point
		if( parameters.length < 3 ) {
			source = asPoint( context, parameters[ 1 ] );
			referenceLine.setPoint( source ).setOrigin( source );
			selected = tool.getSelectedGeometry();
			preview = cloneAndAddReferenceShapes( selected );
			promptForPoint( context, "target" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		removePreview( tool.getCommandContext(), preview );
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

		return COMPLETE;
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D target = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 1 -> referenceLine.setPoint( target ).setOrigin( target );
				case 2 -> referenceLine.setPoint( target ).setOrigin( anchor );
				case 3 -> {
					referenceLine.setPoint( target ).setOrigin( anchor );

					// FIXME Not only does this strategy not work (not sure why) but it is also rather CPU intensive
					// Remove prior preview shapes
					removePreview( tool.getCommandContext(), preview );
					// Create new preview shapes
					scaleShapes( preview = cloneAndAddReferenceShapes( selected ), anchor, source, target );

					// FIXME Need to handle preview geometry without the need for prior data
//					if( !CadGeometry.areSamePoint( anchor, target ) ) {
//						if( prior != null ) {
//							rescaleShapes( getPreview(), anchor, source, prior, target );
//						} else {
//							scaleShapes( getPreview(), anchor, source, target );
//						}
//						prior = target;
//					}
				}
			}
		}
	}

}
