package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class Anchor extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) return COMPLETE;

		if( parameters[ 0 ] instanceof MouseEvent event && event.getEventType() == MouseEvent.MOUSE_PRESSED ) {
			return new Point3D( event.getX(), event.getY(), 0 );
		}

		return COMPLETE;
	}

}
