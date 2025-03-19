package com.avereon.cartesia.command;

public abstract class ToggleCommand extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public boolean clearReferenceAndPreviewWhenComplete() {
		return false;
	}

}
