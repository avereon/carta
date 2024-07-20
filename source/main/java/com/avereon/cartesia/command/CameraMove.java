package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

import java.text.ParseException;

@CustomLog
public class CameraMove extends CameraCommand {

	private CommandTrigger eventKey;

	private Point3D viewAnchor;

	private Point3D dragAnchor;

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForPoint( context, "pan-point" );
			return INCOMPLETE;
		}

		if( parameters[ 0 ] instanceof MouseEvent event ) {
			if( event.getEventType() == MouseEvent.MOUSE_RELEASED ) {
				return COMPLETE;
			}
			if( event.getEventType() == MouseEvent.MOUSE_PRESSED ) {
				eventKey = CommandTrigger.of( event );
				viewAnchor = context.getTool().getViewPoint();
				dragAnchor = new Point3D( event.getX(), event.getY(), 0 );
				return INCOMPLETE;
			}
		}

		try {
			context.getTool().setViewPoint( context.getTool().getViewPoint().subtract( asPoint( context.getAnchor(), parameters[ 0 ] ) ) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-create-marker", exception );
			context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}

	@Override
	public void handle( CommandContext context, MouseEvent event ) {
		BaseDesignTool tool = (BaseDesignTool)event.getSource();
		if( eventKey != null ) {
			if( CommandTrigger.of( event ).matches( eventKey, MouseEvent.MOUSE_DRAGGED ) ) {
				if( viewAnchor == null || dragAnchor == null ) return;
				tool.pan( viewAnchor, dragAnchor, event.getX(), event.getY() );
			} else if( event.getEventType() == MouseEvent.MOUSE_RELEASED && event.getButton() == eventKey.getButton() ) {
				tool.getCommandContext().resubmit( tool, this, event );
			}
		}
	}

}
