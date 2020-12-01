package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignCircle;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawCircleCommand extends DrawCommand {

	private static final System.Logger log = Log.get();

	private int step;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {

		if( parameters.length < 1 ) {
			setPreview( tool, new DesignCircle( context.getMouse(), 0D ) );
			promptForValue( context, tool, BundleKey.PROMPT, "center" );
			step = 1;
			return incomplete();
		}

		if( parameters.length < 2 ) {
			getPreview().setOrigin( asPoint( tool, parameters[ 0 ], context.getAnchor() ) );
			promptForValue( context, tool, BundleKey.PROMPT, "radius" );
			step = 2;
			return incomplete();
		}

		if( parameters.length < 3 ) {
			DesignCircle preview = getPreview();
			preview.setRadius( asPoint( tool, parameters[ 1 ], context.getAnchor() ).distance( preview.getOrigin() ) );
		}

		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		DesignCircle preview = getPreview();
		if( preview != null && event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			Fx.run( () -> {
				DesignTool tool = (DesignTool)event.getSource();
				Point3D mouse = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
				if( step < 2 ) preview.setOrigin( mouse );
				preview.setRadius( mouse.distance( preview.getOrigin() ) );
			} );
		}
	}

}
