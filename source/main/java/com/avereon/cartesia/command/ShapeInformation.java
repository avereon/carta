package com.avereon.cartesia.command;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.CommandTask;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import com.avereon.zarra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import lombok.CustomLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.avereon.cartesia.command.Command.Result.INCOMPLETE;
import static com.avereon.cartesia.command.Command.Result.SUCCESS;

@CustomLog
public class ShapeInformation extends Command {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		if( task.getTool().getSelectedShapes().isEmpty() ) {
			if( task.getParameterCount() < 1 ) {
				promptForShape( task.getContext(), "select-shape" );
				return INCOMPLETE;
			}
		}

		try {
			// Limit the information to one shape
			DesignShape shape;
			if( task.getTool().getSelectedShapes().isEmpty() ) {
				Point3D point = task.getContext().getScreenMouse();
				shape = selectNearestShapeAtMouse( task.getContext(), point );
			} else {
				shape = task.getTool().getSelectedShapes().getFirst();
			}

			// Get the shape information
			Map<String, Object> information = shape.getInformation();
			if( information.isEmpty() ) return SUCCESS;

			// Format and sort the information
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

			// Create the notice object
			if( task.getContext().isInteractive() ) {
				String title = Rb.text( RbKey.NOTICE, "measurement" );
				String message = shape == DesignShape.NONE ? Rb.text( RbKey.NOTICE, "shape-not-selected" ) : description;
				Notice notice = new Notice( title, message );
				notice.setAction( () -> Fx.run( () -> {
					Clipboard clipboard = Clipboard.getSystemClipboard();
					ClipboardContent content = new ClipboardContent();
					content.putString( message );
					clipboard.setContent( content );
				} ) );
				task.getContext().getProgram().getNoticeManager().addNotice( notice );
			}

			log.atDebug().log( "Measured shape=%s", shape );
			return shape;
		} catch( Exception exception ) {
			String title = Rb.text( RbKey.NOTICE, "command-error" );
			String message = Rb.text( RbKey.NOTICE, "unable-to-measure-shape", exception.getMessage() );
			if( task.getContext().isInteractive() ) task.getContext().getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}

		return SUCCESS;
	}

}
