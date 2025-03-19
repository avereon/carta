package com.avereon.cartesia.command.base;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.command.Command;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.product.Rb;
import com.avereon.xenon.notice.Notice;
import com.avereon.zarra.javafx.Fx;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import lombok.CustomLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.avereon.cartesia.command.Command.Result.*;

@CustomLog
public class ShapeInformation extends Command {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		if( task.getParameterCount() == 0 & task.getTool().getSelectedShapes().isEmpty() ) {
			if( task.getParameterCount() < 1 ) {
				promptForShape( task, "select-shape" );
				return INCOMPLETE;
			}
		}

		DesignShape shape;

		// Limit the information to one shape
		if( task.hasParameter( 0 ) ) {
			shape = selectNearestShapeAtPoint( task, asPoint( task, "select-shape", 0 ) );
		} else {
			shape = task.getTool().getSelectedShapes().getFirst();
			if( shape == DesignShape.NONE ) return SUCCESS;
		}

		if( shape != null ) {
			// Get the shape information
			Map<String, Object> information = shape.getInformation();

			// Format and sort the information
			List<String> labels = new ArrayList<>( information.keySet() );
			Collections.sort( labels );

			StringBuilder infoString = new StringBuilder();
			labels.forEach( key -> {
				String label = Rb.textOr( RbKey.LABEL, key, key );
				infoString.append( label );
				infoString.append( " " );
				// TODO Need to format values "nicely"
				infoString.append( information.get( key ) );
				infoString.append( "\n" );
			} );
			String description = infoString.toString().trim();

			// Fire the notice object
			if( task.getContext().isInteractive() ) fireNotice( task, shape, description );

			return shape;
		}

		return FAILURE;
	}

	private static void fireNotice( CommandTask task, DesignShape shape, String description ) {
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

}
