package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignCurve;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

import java.text.ParseException;

public class DrawCurve4 extends DrawCommand {

	private static final System.Logger log = Log.get();

	private DesignCurve preview;

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		// Step 1
		if( parameters.length < 1 ) {
			addPreview( tool, preview = new DesignCurve( context.getWorldMouse(), context.getWorldMouse(), context.getWorldMouse(), context.getWorldMouse() ) );
			promptForPoint( context, tool, "start-point" );
			return INCOMPLETE;
		}

		// Step 2
		if( parameters.length < 2 ) {
			preview.setOrigin( asPoint( context, parameters[ 0 ] ) );
			promptForPoint( context, tool, "control-point" );
			return INCOMPLETE;
		}

		// Step 3
		if( parameters.length < 3 ) {
			preview.setOriginControl( asPoint( context, parameters[ 1 ] ) );
			promptForPoint( context, tool, "control-point" );
			return INCOMPLETE;
		}

		// Step 4
		if( parameters.length < 4 ) {
			preview.setPointControl( asPoint( context, parameters[ 2 ] ) );
			promptForPoint( context, tool, "end-point" );
			return INCOMPLETE;
		}

		try {
			preview.setPoint( asPoint( context, parameters[ 3 ] ) );
			return commitPreview( tool );
		} catch( ParseException exception ) {
			String title = Rb.text( BundleKey.NOTICE, "command-error" );
			String message = Rb.text( BundleKey.NOTICE, "unable-to-create-curve", exception );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return COMPLETE;
	}

	@Override
	public void handle( MouseEvent event ) {
		if( event.getEventType() == MouseEvent.MOUSE_MOVED ) {
			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );
			switch( getStep() ) {
				case 1 -> {
					preview.setOrigin( point );
					preview.setOriginControl( point );
					preview.setPointControl( point );
					preview.setPoint( point );
				}
				case 2 -> {
					//preview.setOrigin( point );
					preview.setOriginControl( point );
					preview.setPointControl( point );
					preview.setPoint( point );
				}
				case 3 -> {
					//preview.setOrigin( point );
					//preview.setOriginControl( point );
					preview.setPointControl( point );
					preview.setPoint( point );
				}
				case 4 -> {
					//preview.setOrigin( point );
					//preview.setOriginControl( point );
					//preview.setPointControl( point );
					preview.setPoint( point );
				}
			}
		}
	}

}
