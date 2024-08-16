package com.avereon.cartesia.command.camera;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import java.text.ParseException;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class CameraViewPoint extends CameraCommand {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		if( task.getParameterCount() < 1 && task.getEvent() == null ) {
			promptForPoint( task, "select-viewpoint" );
			return INCOMPLETE;
		}

		if( task.getEvent() instanceof MouseEvent mouseEvent ) {
			Point3D mouse = new Point3D( mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getZ() );
			task.getTool().setViewPoint( task.getTool().screenToWorld( mouse ) );
			return SUCCESS;
		}

		if( task.hasParameter( 0 ) ) {
			try {
				Point3D viewPoint = asPoint( task, "select-viewpoint", 0 );
				task.getTool().setViewPoint( viewPoint );
				return SUCCESS;
			} catch( ParseException exception ) {
				// TODO Maybe time to have a CommandException to use for the notice
				// throw new InvalidParameterException( exception, "unable-to-move-to-viewpoint", task.getParameter( 0 ) );
				// throw new CommandException( exception, "unable-to-move-to-viewpoint", task.getParameter( 0 ) );

				String title = Rb.text( RbKey.NOTICE, "command-error" );
				String message = Rb.text( RbKey.NOTICE, "unable-to-move-to-viewpoint", exception );
				task.getContext().getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
			}
		}

		return FAILURE;
	}

}
