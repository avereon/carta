package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignPoint;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawPointCommand extends DrawCommand {

	private static final System.Logger log = Log.get();

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			// Need to start with the point at ZERO until it is added
			setPreview( tool, new DesignPoint( Point3D.ZERO ) );
			getPreview().setOrigin( context.getMouse() );
			promptForPoint( context, tool, BundleKey.PROMPT, "select-point" );
			return incomplete();
		}

		if( parameters.length < 2 ) {
			getPreview().setOrigin( asPoint( tool, parameters[ 0 ], context.getAnchor() ) );
		}

		return commitPreview(tool);
	}

	@Override
	public void handle( MouseEvent event ) {
		DesignPoint preview = getPreview();
		if( preview != null && event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			Fx.run( () -> {
				DesignTool tool = (DesignTool)event.getSource();
				Point3D mouse = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
				preview.setOrigin( mouse );
			} );
		}
	}

}
