package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.data.DesignArc;
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

public class DrawArc2 extends DrawCommand {

	private DesignLine referenceLine;

	private DesignArc referenceArc;

	private Point3D spinAnchor;

	private double spin;

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		// Step 1 - Prompt for origin
		if( parameters.length < 1 ) {
			addReference( context, referenceLine = new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, "center" );
			return Command.INCOMPLETE;
		}

		// Step 2 - Get origin, prompt for start
		if( parameters.length < 2 ) {
			Point3D origin = asPoint( context, parameters[ 0 ] );
			referenceLine.setOrigin( origin );
			referenceLine.setPoint( origin );
			addPreview( context, referenceArc = new DesignArc( origin, 0.0, 0.0, 360.0, DesignArc.Type.OPEN ) );
			promptForPoint( context, "start" );
			return Command.INCOMPLETE;
		}

		// Step 3 - Get start, prompt for extent
		if( parameters.length < 3 ) {
			Point3D point = asPoint( context, parameters[ 1 ] );
			referenceArc.setRadius( CadGeometry.distance( referenceArc.getOrigin(), point ) );
			referenceArc.setStart( deriveStart( referenceArc.getOrigin(), referenceArc.getXRadius(), referenceArc.getYRadius(), referenceArc.calcRotate(), point ) );
			referenceArc.setExtent( 0.0 );
			spinAnchor = point;
			promptForPoint( context, "extent" );
			return Command.INCOMPLETE;
		}

		if( parameters.length > 3 ) {
			spin = asDouble( parameters[ 3 ] );
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			Point3D origin = asPoint( context, parameters[ 0 ] );
			Point3D startPoint = asPoint( context, parameters[ 1 ] );
			Point3D extentPoint = asPoint( context, parameters[ 2 ] );
			double radius = CadGeometry.distance( origin, startPoint );
			double start = deriveStart( origin, radius, radius, 0.0, startPoint );
			double extent = deriveExtent( origin, radius, radius, 0.0, start, extentPoint, spin );

			context.getTool().getCurrentLayer().addShape( new DesignArc( origin, radius, start, extent, DesignArc.Type.OPEN ) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-create-shape", exception );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return Command.SUCCESS;
	}

	@Override
	public void handle( DesignCommandContext context, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			BaseDesignTool tool = (BaseDesignTool)event.getSource();
			Point3D point = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );

			switch( getStep() ) {
				case 1 -> {
					// Arc origin
					referenceLine.setOrigin( point );
					referenceLine.setPoint( point );
				}
				case 2 -> {
					// Arc radius and start
					referenceLine.setPoint( point );
					referenceArc.setRadius( point.distance( referenceArc.getOrigin() ) );
					referenceArc.setStart( deriveStart( referenceArc.getOrigin(), referenceArc.getXRadius(), referenceArc.getYRadius(), referenceArc.calcRotate(), point ) );
				}
				case 3 -> {
					// Arc extent
					referenceLine.setPoint( point );
					spin = getExtentSpin( referenceArc.getOrigin(), referenceArc.getXRadius(), referenceArc.getYRadius(), referenceArc.calcRotate(), referenceArc.getStart(), spinAnchor, point, spin );
					referenceArc.setExtent( deriveExtent( referenceArc.getOrigin(), referenceArc.getXRadius(), referenceArc.getYRadius(), referenceArc.calcRotate(), referenceArc.getStart(), point, spin ) );
					spinAnchor = point;
				}
			}
		}
	}

}
