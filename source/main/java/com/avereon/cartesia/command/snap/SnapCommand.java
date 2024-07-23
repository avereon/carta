package com.avereon.cartesia.command.snap;

import com.avereon.cartesia.command.Command;

public abstract class SnapCommand extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

}
