package com.avereon.cartesia.command.measure;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignArc;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import com.avereon.zarra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class MeasureAngle extends MeasureCommand {

	private DesignLine referenceLine;

	private DesignArc referenceArc;

	private Point3D spinAnchor;

	private double spin;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		setCaptureUndoChanges( task, false );

		// Step 1 - Prompt for origin
		if( task.getParameterCount() == 0 ) {
			if( referenceLine == null ) referenceLine = createReferenceLine( task );

			promptForPoint( task, "center" );
			return INCOMPLETE;
		}

		// Step 2 - Get origin, prompt for start
		if( task.getParameterCount() == 1 ) {
			Point3D origin = asPoint( task, "center", 0 );

			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			referenceLine.setOrigin( origin );
			referenceLine.setPoint( origin );

			if( referenceArc == null ) referenceArc = createReferenceArc( task, origin );

			promptForPoint( task, "start" );
			return INCOMPLETE;
		}

		// Step 3 - Get start, prompt for extent
		if( task.getParameterCount() == 2 ) {
			Point3D origin = asPoint( task, "center", 0 );
			Point3D point = asPoint( task, "start", 1 );

			if( referenceArc == null ) referenceArc = createReferenceArc( task, origin );
			referenceArc.setRadius( CadGeometry.distance( referenceArc.getOrigin(), point ) );
			referenceArc.setStart( deriveStart( referenceArc.getOrigin(), referenceArc.getXRadius(), referenceArc.getYRadius(), referenceArc.calcRotate(), point ) );
			referenceArc.setExtent( 0.0 );
			spinAnchor = point;

			promptForPoint( task, "extent" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 3 ) ) spin = asDouble( task, "spin", 3 );

		if( task.hasParameter( 2 ) ) {
			setCaptureUndoChanges( task, true );

			Point3D origin = asPoint( task, "center", 0 );
			Point3D startPoint = asPoint( task, "start", 1 );
			Point3D extentPoint = asPoint( task, "extent", 2 );
			double radius = CadGeometry.distance( origin, startPoint );
			double start = deriveStart( origin, radius, radius, 0.0, startPoint );
			double extent = deriveExtent( origin, radius, radius, 0.0, start, extentPoint, spin );

			if( task.getContext().isInteractive() ) {
				String title = Rb.text( RbKey.NOTICE, "measurement" );
				String message = Rb.text( RbKey.NOTICE, "angle", extent );
				Notice notice = new Notice( title, message );
				notice.setAction( () -> Fx.run( () -> {
					Clipboard clipboard = Clipboard.getSystemClipboard();
					ClipboardContent content = new ClipboardContent();
					// TODO Run the angle value through the design value formatter
					content.putString( String.valueOf( extent ) );
					clipboard.setContent( content );
				} ) );
				task.getContext().getProgram().getNoticeManager().addNotice( notice );
			}

			log.atDebug().log( "Measured distance=%s", extent );
			return extent;
		}

		return FAILURE;
	}

	@Override
	public void handle( CommandTask task, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			BaseDesignTool tool = (BaseDesignTool)event.getSource();
			Point3D point = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );
			spin = referenceArc == null ? spin : getExtentSpin( referenceArc.getOrigin(),
				referenceArc.getXRadius(),
				referenceArc.getYRadius(),
				referenceArc.calcRotate(),
				referenceArc.getStart(),
				spinAnchor,
				point,
				spin
			);

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
					referenceArc.setExtent( deriveExtent( referenceArc.getOrigin(), referenceArc.getXRadius(), referenceArc.getYRadius(), referenceArc.calcRotate(), referenceArc.getStart(), point, spin ) );
					spinAnchor = point;
				}
			}
		}
	}

}
