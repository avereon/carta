package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.OldCommand;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;

/**
 * This command adds a layer as a peer to the current layer
 */
public class LayerCreateCommand extends OldCommand {

	private static final System.Logger log = Log.get();

//	@Override
//	public List<OldCommand> getPreSteps( DesignTool tool ) {
//		return List.of( new PromptForTextCommand( tool, "layer-name" ) );
//	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		Object name = processor.pullValue();

		if( name instanceof String ) {
			addLayer( tool.getCurrentLayer(), new DesignLayer().setName( String.valueOf( name ) ) );
		} else {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-create-layer", name );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}
	}

	/**
	 * This implementation adds the new layer as a peer to the current layer.
	 *
	 * @param currentLayer The current layer
	 * @param yy The new layer
	 */
	void addLayer( DesignLayer currentLayer, DesignLayer yy ) {
		currentLayer.getParentLayer().addLayer( yy );
	}

}
