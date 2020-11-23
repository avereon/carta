package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignCircle;
import com.avereon.cartesia.math.Maths;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import java.text.ParseException;

public class DrawCircleCommand extends DrawCommand {

	private static final System.Logger log = Log.get();

	private Circle preview;

	private int step;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {

		if( parameters.length < 1 ) {
			Point3D mouse = context.getMouse();
			tool.setPreview( preview = new Circle( mouse.getX(), mouse.getY(), 0 ) );
			preview.setStrokeWidth( Maths.eval( tool.getCommandContext().getDrawWidth() ) );
			preview.setStroke( tool.getCommandContext().getDrawPaint() );
			preview.setFill( tool.getCommandContext().getFillPaint() );
			promptForValue( context, tool, BundleKey.PROMPT, "start-point" );
			step = 1;
			return incomplete();
		}

		if( parameters.length < 2 ) {
			Point3D start = asPoint( tool, parameters[ 0 ], context.getAnchor() );
			preview.setCenterX( start.getX() );
			preview.setCenterY( start.getY() );
			promptForValue( context, tool, BundleKey.PROMPT, "end-point" );
			step = 2;
			return incomplete();
		}

		try {
			tool.clearPreview();
			Point3D p1 = asPoint( tool, parameters[ 0 ], context.getAnchor() );
			Point3D p2 = asPoint( tool, parameters[ 1 ], context.getAnchor() );
			DesignCircle circle = new DesignCircle( p1, p1.distance( p2 ) );
			circle.setDrawWidth( tool.getCommandContext().getDrawWidth() );
			circle.setDrawColor( tool.getCommandContext().getDrawPaint().toString() );
			circle.setFillColor( tool.getCommandContext().getFillPaint().toString() );
			tool.getCurrentLayer().addShape( circle );
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
					preview.setCenterX( mouse.getX() );
					preview.setCenterY( mouse.getY() );
				}
				preview.setRadius( mouse.distance( preview.getCenterX(), preview.getCenterY(), 0 ) );
			} );
		}
	}

}
