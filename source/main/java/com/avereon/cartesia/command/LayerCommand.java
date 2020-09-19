package com.avereon.cartesia.command;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.xenon.notice.Notice;

import java.util.List;

public class LayerCommand extends Command {

	private static final System.Logger log = Log.get();

	@Override
	public List<Command> getPreSteps( DesignTool tool ) {
		return List.of( new PromptForTextCommand( tool, "layer-name" ) );
	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		Object name = processor.pullValue();

		if( name instanceof String ) {
			log.log( Log.INFO, "Create layer name=" + name );
			// TODO Create and add layer
			//processor.pushValue( tool, new DesignPoint( (Point3D)origin ) );
		} else {
			String title = tool.getProduct().rb().text( BundleKey.NOTICE, "command-error" );
			String message = tool.getProduct().rb().text( BundleKey.NOTICE, "unable-to-create-layer", name );
			tool.getProgram().getNoticeManager().addNotice( new Notice( title, message ) );
		}
	}

}
