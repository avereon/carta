package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandEventKey;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;

@CustomLog
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

		// For this command the incoming parameter is the mouse event that triggered it
		if( parameters[ 0 ] instanceof MouseEvent ) {
			MouseEvent event = (MouseEvent)parameters[ 0 ];
			if( event.getEventType() == MouseEvent.MOUSE_PRESSED ) {
				return mousePressed( context, event );
			} else if( event.getEventType() == MouseEvent.MOUSE_RELEASED ) {
				return mouseReleased( context, event );
			}
		}

		throw new IllegalArgumentException( "Invalid parameter=%s" + parameters[0] );
	}

	private Object mousePressed( CommandContext context, MouseEvent event ) {
		dragAnchor = new Point3D( event.getX(), event.getY(), 0 );
		eventKey = CommandEventKey.of( event );
		return INCOMPLETE;
	}

	private Object mouseReleased( CommandContext context, MouseEvent event ) {
		DesignTool tool = context.getTool();
		Point3D point = new Point3D( event.getX(), event.getY(), event.getZ() );
		if( context.isSelectMode() ) {
			if( event.isStillSincePress() ) {
				tool.screenPointSelect( new Point3D( event.getX(), event.getY(), event.getZ() ), isSelectToggleEvent( event ) );
				return tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			} else {
				tool.mouseWindowSelect( dragAnchor, point, isSelectByContains( event ) );
				return createBounds( dragAnchor, point );
			}
		} else if( context.isPenMode() ) {
			if( event.isStillSincePress() ) {
				return tool.mouseToWorkplane( point );
			} else {
				return createBounds( dragAnchor, point );
			}
		}
		return COMPLETE;
	}

	private Bounds createBounds( Point3D a, Point3D b ) {
		double x = Math.min( a.getX(), b.getX() );
		double y = Math.min( a.getY(), b.getY() );
		double w = Math.abs( a.getX() - b.getX() );
		double h = Math.abs( a.getY() - b.getY() );
		return new BoundingBox( x, y, w, h );
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
