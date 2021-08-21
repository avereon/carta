package com.avereon.cartesia.data;

import com.avereon.data.Node;

public class DesignMetadata extends Node {

	public static final String NAME = "name";

	public static final String UNIT = "unit";

	public String getName() {
		return getValue( NAME );
	}

	public DesignMetadata setName( String name ) {
		setValue( NAME, name );
		return this;
	}

	public String getDesignUnit() {
		return getValue( UNIT );
	}

	public DesignMetadata setDesignUnit( String unit ) {
		setValue( UNIT, unit );
		return this;
	}

}
