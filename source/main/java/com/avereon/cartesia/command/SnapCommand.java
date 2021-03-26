package com.avereon.cartesia.command;

public abstract class SnapCommand extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

}
