package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import com.avereon.zarra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.InputEvent;
import lombok.CustomLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class ShapeInformation extends Command {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		setCaptureUndoChanges( context, false );

		if( context.getTool().getSelectedShapes().isEmpty() ) {
			if( parameters.length < 1 ) {
				promptForShape( context, "select-shape" );
				return INCOMPLETE;
			}
		}

		try {
			DesignShape shape;
			if( context.getTool().getSelectedShapes().isEmpty() ) {
				Point3D point = context.getScreenMouse();
				shape = selectNearestShapeAtMouse( context, point );
			} else {
				shape = context.getTool().getSelectedShapes().getFirst();
			}

			Map<String, Object> information = shape.getInformation();
			if( information.isEmpty() ) return SUCCESS;

			Map<String, Object> view = information.keySet().stream().collect( Collectors.toMap( k -> Rb.text( RbKey.LABEL, k ), information::get ) );
			List<String> labels = new ArrayList<>( view.keySet() );
			Collections.sort( labels );

			StringBuilder infoString = new StringBuilder();
			labels.forEach( l -> {
				infoString.append( l );
				// TODO Need to format values "nicely"
				infoString.append( view.get( l ) );
				infoString.append( "\n" );
			} );
			String description = infoString.toString().trim();

			String title = Rb.text( RbKey.NOTICE, "measurement" );
			String message = shape == DesignShape.NONE ? Rb.text( RbKey.NOTICE, "shape-not-selected" ) : description;
			Notice notice = new Notice( title, message );
			notice.setAction( () -> Fx.run( () -> {
				Clipboard clipboard = Clipboard.getSystemClipboard();
				ClipboardContent content = new ClipboardContent();
				content.putString( message );
				clipboard.setContent( content );
			} ) );
			if( context.isInteractive() ) context.getProduct().getProgram().getNoticeManager().addNotice( notice );

			log.atDebug().log( "Measured shape=%s", shape );
			return shape;
		} catch( Exception exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-measure-shape", exception.getMessage() );
			if( context.isInteractive() ) context.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		} finally {
			clearReferenceAndPreview( context );
		}

		return SUCCESS;
	}

}
