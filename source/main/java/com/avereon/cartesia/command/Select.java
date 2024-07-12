package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandEventKey;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.CommandContext;
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
		if( parameters[ 0 ] instanceof MouseEvent event ) {
			if( event.getClickCount() > 1 ) return COMPLETE;
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
		BaseDesignTool tool = context.getTool();
		Point3D point = new Point3D( event.getX(), event.getY(), event.getZ() );
		if( context.isSelectMode() ) {
			if( event.isStillSincePress() ) {
				tool.screenPointSelect( new Point3D( event.getX(), event.getY(), event.getZ() ), isSelectToggle( event ) );
				return tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			} else {
				tool.screenWindowSelect( dragAnchor, point, isSelectByIntersect( event ), isSelectToggle( event ) );
				return CadGeometry.getBounds( dragAnchor, point );
			}
		} else if( context.isPenMode() ) {
			if( event.isStillSincePress() ) {
				return tool.mouseToWorkplane( point );
			} else {
				return CadGeometry.getBounds( dragAnchor, point );
			}
		}
		return COMPLETE;
	}

	@Override
	public void handle( MouseEvent event ) {
		BaseDesignTool tool = (BaseDesignTool)event.getSource();
		if( eventKey != null ) {
			Point3D mouse = new Point3D( event.getX(), event.getY(), event.getZ() );
			if( event.getEventType() == MouseEvent.MOUSE_DRAGGED && event.getButton() == eventKey.getButton() ) {
				tool.updateSelectAperture( dragAnchor, mouse );
			} else if( event.getEventType() == MouseEvent.MOUSE_RELEASED && event.getButton() == eventKey.getButton() ) {
				tool.updateSelectAperture( mouse, mouse );
				tool.getCommandContext().resubmit( tool, this, event );
			}
		}
	}

	private boolean isSelectToggle( MouseEvent event ) {
		return event.isControlDown();
	}

	private boolean isSelectByIntersect( MouseEvent event ) {
		return event.isAltDown();
	}

}
