package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandEventKey;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class Select extends Command {

	private CommandEventKey eventKey;

	private Point3D dragAnchor;

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) return COMPLETE;

		if( parameters[ 0 ] instanceof MouseEvent ) {
			MouseEvent event = (MouseEvent)parameters[ 0 ];
			if( event.getEventType() == MouseEvent.MOUSE_PRESSED ) {
				return mousePressed( context, event );
			} else if( event.getEventType() == MouseEvent.MOUSE_RELEASED ) {
				return mouseReleased( context, event );
			}
		}

		// For this command the incoming parameter is the mouse event that triggered it
		return COMPLETE;
	}

	private Object mousePressed( CommandContext context, MouseEvent event ) {
		eventKey = CommandEventKey.of( event );
		dragAnchor = new Point3D( event.getX(), event.getY(), 0 );
		return INCOMPLETE;
	}

		private Object mouseReleased( CommandContext context, MouseEvent event ) {
			DesignTool tool = context.getTool();
			if( context.isPenMode() ) {
			return tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
		} else if( context.isSingleSelectMode( event ) ) {
			tool.screenPointSelect( new Point3D( event.getX(), event.getY(), event.getZ() ), isSelectToggleEvent( event ) );
			return COMPLETE;
		} else if( context.isWindowSelectMode( event ) ) {
			tool.mouseWindowSelect( dragAnchor, new Point3D( event.getX(), event.getY(), event.getZ() ), isSelectByContains( event ) );
			return COMPLETE;
		}
		return COMPLETE;
	}

	@Override
	public void handle( MouseEvent event ) {
		DesignTool tool = (DesignTool)event.getSource();
		if( eventKey != null ) {
			if( event.getEventType() == MouseEvent.MOUSE_DRAGGED && event.getButton() == eventKey.getButton() ) {
				tool.updateSelectWindow( dragAnchor, new Point3D( event.getX(), event.getY(), event.getZ() ) );
			} else if( event.getEventType() == MouseEvent.MOUSE_RELEASED && event.getButton() == eventKey.getButton() ) {
				tool.updateSelectWindow( dragAnchor, dragAnchor );
				tool.getCommandContext().resubmit( tool, this, event );
			}
		}
	}

	private boolean isSelectToggleEvent( MouseEvent event ) {
		return event.isControlDown();
	}

	private boolean isSelectByContains( MouseEvent event ) {
		return !event.isControlDown();
	}

}
