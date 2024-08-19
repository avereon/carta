package com.avereon.cartesia.command.measure;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import com.avereon.zarra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import static com.avereon.cartesia.command.Command.Result.FAILURE;
import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;

@CustomLog
public class MeasurePoint extends MeasureCommand {

	private DesignLine referenceLine;

	@Override
	public Object execute( CommandTask task ) throws Exception {
		if( task.getParameterCount() == 0 ) {
			if( referenceLine == null ) referenceLine = createReferenceLine( task );
			promptForPoint( task, "start-point" );
			return INCOMPLETE;
		}

		if( task.hasParameter( 0 ) ) {
			Point3D p1 = asPointWithoutSnap( task, "start-point", 0 );

			if( task.getContext().isInteractive() ) {
				// TODO Run the point value through the design value formatter
				String point = CadShapes.toString( p1 );
				String title = Rb.text( RbKey.NOTICE, "measurement" );
				String message = Rb.text( RbKey.NOTICE, "point", point );
				Notice notice = new Notice( title, message );
				notice.setAction( () -> Fx.run( () -> {
					Clipboard clipboard = Clipboard.getSystemClipboard();
					ClipboardContent content = new ClipboardContent();
					content.putString( point );
					clipboard.setContent( content );
				} ) );
				task.getContext().getProgram().getNoticeManager().addNotice( notice );
			}

			log.atDebug().log( "Measured point=%s", p1 );
			return p1;
		}

		return FAILURE;
	}

	@Override
	public void handle( CommandTask task, MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			if( referenceLine != null ) {
				DesignTool tool = (DesignTool)event.getSource();
				Point3D point = tool.screenToWorkplane( event.getX(), event.getY(), event.getZ() );
				switch( getStep() ) {
					case 1 -> {
						referenceLine.setOrigin( point );
						referenceLine.setPoint( point );
					}
					case 2 -> referenceLine.setPoint( point );
				}
			}
		}
	}

}
