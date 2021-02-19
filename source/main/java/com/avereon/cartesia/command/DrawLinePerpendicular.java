package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DrawLinePerpendicular extends DrawCommand {

	private static final System.Logger log = Log.get();

	private DesignShape reference;

	private int step;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {

		if( parameters.length < 1 ) {
			promptForShape( context, tool, "reference-shape-perpendicular" );
			step = 1;
			return incomplete();
		}

		if( parameters.length < 2 ) {
			reference = selectNearestShapeAtMouse( tool, context.getScreenMouse() );
			if( reference == null ) return INVALID;

			setPreview( tool, new DesignLine( context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "start-point" );
			step = 2;
			return incomplete();
		}

		if( parameters.length < 3 ) {
			getPreview().setOrigin( asPoint( tool, parameters[ 0 ], context.getAnchor() ) );
			promptForPoint( context, tool, "end-point" );
			step = 3;
			return incomplete();
		}

		if( parameters.length < 4 ) {
			((DesignLine)getPreview()).setPoint( asPoint( tool, parameters[ 1 ], context.getAnchor() ) );
		}

		return commitPreview( tool );
	}

	@Override
	public void handle( MouseEvent event ) {
		DesignLine preview = getPreview();
		if( preview != null && event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D mouse = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );

			Fx.run( () -> {
				if( step < 3 ) preview.setOrigin( mouse );
				preview.setPoint( mouse );
			} );
		}
	}

}
