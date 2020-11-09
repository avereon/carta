package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;

import java.text.ParseException;

public class LineCommand extends DrawCommand {

	private static final System.Logger log = Log.get();

	private Line preview;

	private int step;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {

		if( parameters.length < 1 ) {
			Point3D mouse = context.getMouse();
			tool.setPreview( preview = new Line( mouse.getX(), mouse.getY(), mouse.getX(), mouse.getY() ) );
			promptForValue( context, tool, BundleKey.PROMPT, "start-point" );
			step = 1;
			return incomplete();
		}

		if( parameters.length < 2 ) {
			Point3D start = asPoint( tool, parameters[ 0 ], context.getAnchor() );
			preview.setStartX( start.getX() );
			preview.setStartY( start.getY() );
			promptForValue( context, tool, BundleKey.PROMPT, "end-point" );
			step = 2;
			return incomplete();
		}

		try {
			tool.clearPreview();
			Point3D p1 = asPoint( tool, parameters[ 0 ], context.getAnchor() );
			Point3D p2 = asPoint( tool, parameters[ 1 ], context.getAnchor() );
			DesignLine line = new DesignLine( p1, p2 );
			tool.getCurrentLayer().addShape( line );
		} catch( ParseException exception ) {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-create-line", exception.getMessage() );
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
				if( step < 2 ) {
					preview.setStartX( mouse.getX() );
					preview.setStartY( mouse.getY() );
				}
				preview.setEndX( mouse.getX() );
				preview.setEndY( mouse.getY() );
			} );
		}
	}

}
