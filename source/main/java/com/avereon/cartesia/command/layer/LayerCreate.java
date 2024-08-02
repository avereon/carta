package com.avereon.cartesia.command.layer;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;
import lombok.CustomLog;
import static com.avereon.cartesia.command.Command.Result.*;

/**
 * This command adds a layer as a peer to the current layer
 */
@CustomLog
public class LayerCreate extends LayerCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForText( context, "layer-name" );
			return INCOMPLETE;
		}

		DesignLayer yy = new DesignLayer().setName( String.valueOf( parameters[ 0 ] ) );

		yy = addLayer( context.getTool().getSelectedLayer(), yy );
		context.getTool().setLayerVisible( yy, true );

		return yy;
	}

	/**
	 * This implementation adds the new layer as a peer to the current layer.
	 *
	 * @param currentLayer The current layer
	 * @param yy The new layer
	 */
	DesignLayer addLayer( DesignLayer currentLayer, DesignLayer yy ) {
		// Add yy as a peer (child of parent) of the currentLayer
		return currentLayer.getLayer().addLayerBeforeOrAfter( yy, currentLayer, true );
	}

}
