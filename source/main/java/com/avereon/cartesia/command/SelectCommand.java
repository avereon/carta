package com.avereon.cartesia.command;

public abstract class SelectCommand extends Command {

	public enum Mode {
		POINT,
		WINDOW
	}

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

	@Override
	public boolean clearReferenceAndPreviewWhenComplete() {
		return false;
	}

}
