package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;

public abstract class CameraCommand extends Command {

	@Override
	public boolean clearSelectionWhenComplete() {
		return false;
	}

}
