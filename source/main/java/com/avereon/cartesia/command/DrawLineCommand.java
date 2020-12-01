package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawLineCommand extends DrawCommand {

	private static final System.Logger log = Log.get();

	private int step;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {

		if( parameters.length < 1 ) {
			setPreview( tool, new DesignLine( context.getMouse(), context.getMouse() ) );
			promptForPoint( context, tool, BundleKey.PROMPT, "start-point" );
			step = 1;
			return incomplete();
		}

		if( parameters.length < 2 ) {
			getPreview().setOrigin( asPoint( tool, parameters[ 0 ], context.getAnchor() ) );
			promptForPoint( context, tool, BundleKey.PROMPT, "end-point" );
			step = 2;
			return incomplete();
		}

		if( parameters.length < 3 ) {
			((DesignLine)getPreview()).setPoint( asPoint( tool, parameters[ 1 ], context.getAnchor() ) );
		}

		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		DesignLine preview = getPreview();
		if( preview != null && event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			Fx.run( () -> {
				DesignTool tool = (DesignTool)event.getSource();
				Point3D mouse = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
				if( step < 2 ) preview.setOrigin( mouse );
				preview.setPoint( mouse );
			} );
		}
	}

}
