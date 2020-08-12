package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.tool.DesignTool;
import javafx.application.Platform;

public class PromptForPointCommand extends PromptCommand {

	public PromptForPointCommand( DesignTool tool, String key ) {
		super( tool, key );
	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		Platform.runLater( () -> tool.setCursor( tool.getReticle() ) );
		super.evaluate( processor, tool );
	}

}
