package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.CommandEventKey;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

public class CameraMoveCommand extends CameraCommand {

	private CommandEventKey eventKey;

	private Point3D viewAnchor;

	private Point3D dragAnchor;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForValue( context, tool, BundleKey.PROMPT, "pan-point" );
			return incomplete();
		}

		// FIXME This command is not working correctly
		if( parameters[ 0 ] instanceof MouseEvent ) {
			MouseEvent event = (MouseEvent)parameters[ 0 ];
			System.out.println( "Mouse event=" + event );
			if( event.getEventType() == MouseEvent.MOUSE_RELEASED ) {
				return complete();
			}
			if( event.getEventType() == MouseEvent.MOUSE_PRESSED ) {
				eventKey = CommandEventKey.of( event );
				viewAnchor = tool.getViewPoint();
				dragAnchor = new Point3D( event.getX(), event.getY(), 0 );
				return incomplete();
			}
		}

		try {
			tool.setPan( tool.getPan().subtract( asPoint( parameters[ 0 ], context.getAnchor() ) ) );
		} catch( ParseException exception ) {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-create-point", exception );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return complete();
	}

	@Override
	public void handle( MouseEvent event ) {
		DesignTool tool = (DesignTool)event.getSource();
		if( eventKey != null ) {
			if( eventKey.matches( CommandEventKey.of( event ), MouseEvent.MOUSE_DRAGGED ) ) {
				tool.pan( viewAnchor, dragAnchor, event.getX(), event.getY() );
			} else if( eventKey.matches( CommandEventKey.of( event ), MouseEvent.MOUSE_RELEASED ) ) {
				tool.getCommandContext().submit( tool, this, event );
			}
		}
	}

}
