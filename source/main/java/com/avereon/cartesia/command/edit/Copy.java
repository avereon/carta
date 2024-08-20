package com.avereon.cartesia.command.edit;

public class Copy extends Move {

	public Copy() {
		setCloneShapeOnExecute();
		// FIXME Did not create all undo in the same transaction
	}

}
