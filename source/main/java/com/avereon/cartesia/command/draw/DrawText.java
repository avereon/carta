package com.avereon.cartesia.command.draw;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignText;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import lombok.CustomLog;

import java.text.ParseException;
import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class DrawText extends DrawCommand {

	private Point3D anchor;

	private DesignText preview;

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		// Ask for an anchor point
		if( parameters.length < 1 ) {
			promptForPoint( context, "anchor" );
			return INCOMPLETE;
		}

		// Ask for a target point
		if( parameters.length < 2 ) {
			anchor = asPoint( context, parameters[ 0 ] );
			addPreview( context, preview = new DesignText( context.getWorldMouse() ) );
			promptForText( context, "text" );
			return INCOMPLETE;
		}

		clearReferenceAndPreview( context );
		setCaptureUndoChanges( context, true );

		try {
			final Point3D anchor = asPoint( context, parameters[ 0 ] );
			final String text = asText( context, parameters[ 1 ] );
			context.getTool().getCurrentLayer().addShape( new DesignText( anchor, text ) );
		} catch( ParseException exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-move-shapes", exception );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return SUCCESS;
	}

	@Override
	public void handle( DesignCommandContext context, KeyEvent event ) {
		super.handle( context, event );
		if( this.preview != null ) preview.setText( context.getCommandPrompt().getCommand() );
	}
}
