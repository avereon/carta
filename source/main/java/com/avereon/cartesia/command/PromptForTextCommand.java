package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.Cursor;

public class PromptForTextCommand extends PromptCommand {

	public PromptForTextCommand( DesignTool tool, String key ) {
		super( tool, key );
	}

	@Override
	public boolean isAutoCommandSafe() {
		return false;
	}

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		Fx.run( () -> tool.setCursor( Cursor.TEXT ) );
		super.evaluate( processor, tool );
	}

}
