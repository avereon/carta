package com.avereon.cartesia;

import com.avereon.data.Node;

public abstract class Design extends Node {

	private static final String ID = "id";

	private static final String NAME = "name";

	private static final String UNIT = "unit";

	public Design() {
		definePrimaryKey( ID );
		addModifyingKeys( NAME, UNIT );
	}

	public String getName() {
		return getValue( NAME );
	}

	public Design setName( String name ) {
		setValue( NAME, name );
		return this;
	}

	public DesignUnit getUnit() {
		return getValue( UNIT );
	}

	public Design setUnit( DesignUnit unit ) {
		setValue( UNIT, unit );
		return this;
	}

}
