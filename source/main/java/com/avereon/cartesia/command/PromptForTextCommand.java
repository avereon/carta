package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.Cursor;

public class PromptForTextCommand extends PromptCommand {

	public PromptForTextCommand( String prompt ) {
		super( prompt );
	}

	@Override
	public boolean isInputCommand() {
		return true;
	}

	@Override
	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) {
		Fx.run( () -> tool.setCursor( Cursor.TEXT ) );
		return super.execute( context, tool, parameters );
	}

}
