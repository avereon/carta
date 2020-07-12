package com.avereon.cartesia;

import com.avereon.data.Node;

public class Design2D extends Node {

	private static final String ID = "id";

	private static final String NAME = "name";

	private static final String UNIT = "unit";

	private static final String GRIDX = "grid-x";

	private static final String GRIDY = "grid-y";

	public Design2D() {
		definePrimaryKey( ID );
	}

	public String getName() {
		return getValue( NAME );
	}

	public Design2D setName( String name ) {
		setValue( NAME, name );
		return this;
	}

}
