package com.avereon.cartesia.tool;

import com.avereon.xenon.ProgramAction;
import com.avereon.xenon.Xenon;
import javafx.event.ActionEvent;

/**
 * General class for commands linked to actions.
 */
public class CommandAction extends ProgramAction {

	private final BaseDesignTool designTool;

	private final String shortcut;

	public CommandAction( BaseDesignTool designTool, Xenon program, String shortcut ) {
		super( program );
		this.designTool = designTool;
		this.shortcut = shortcut;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void handle( ActionEvent event ) {
		DesignCommandContext context = designTool.getCommandContext();
		if( context == null ) return;
		context.command( shortcut );
	}

}
