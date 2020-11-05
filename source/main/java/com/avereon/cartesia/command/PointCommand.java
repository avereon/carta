package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignPoint;
import com.avereon.cartesia.data.DesignPoints;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

public class PointCommand extends DrawCommand {

	private static final System.Logger log = Log.get();

	private Node preview;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			Point3D mouse = context.getMouse();
			tool.setPreview( preview = DesignPoints.createPoint( DesignPoints.Type.CROSS, 0, 0, 1 ) );
			preview.setLayoutX( mouse.getX() );
			preview.setLayoutY( mouse.getY() );
			promptForValue( context, tool, BundleKey.PROMPT, "select-point" );
			return incomplete();
		}

		try {
			tool.clearPreview();
			DesignPoint point = new DesignPoint( asPoint( tool, parameters[ 0 ], context.getAnchor() ) );
			tool.getCurrentLayer().addShape( point );
		} catch( ParseException exception ) {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-create-point", exception );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return complete();
	}

	@Override
	public void handle( MouseEvent event ) {
		if( preview != null && event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			Fx.run( () -> {
				DesignTool tool = (DesignTool)event.getSource();
				Point3D mouse = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
				preview.setLayoutX( mouse.getX() );
				preview.setLayoutY( mouse.getY() );
			} );
		}
	}

}
