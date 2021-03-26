package com.avereon.cartesia.command;

public abstract class CameraCommand extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

}
