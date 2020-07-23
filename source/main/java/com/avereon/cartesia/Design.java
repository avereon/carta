package com.avereon.cartesia;

import com.avereon.data.Node;

public abstract class Design extends Node {

	public static final DesignUnit DEFAULT_DESIGN_UNIT = DesignUnit.CENTIMETER;

	private static final String ID = "id";

	public static final String NAME = "name";

	public static final String UNIT = "unit";

	private final CommandProcessor commandProcessor;

	public Design() {
		definePrimaryKey( ID );
		addModifyingKeys( NAME, UNIT );
		setDesignUnit( DEFAULT_DESIGN_UNIT );

		this.commandProcessor = new CommandProcessor();
	}

	public String getName() {
		return getValue( NAME );
	}

	public Design setName( String name ) {
		setValue( NAME, name );
		return this;
	}

	public DesignUnit getDesignUnit() {
		return getValue( UNIT );
	}

	public Design setDesignUnit( DesignUnit unit ) {
		setValue( UNIT, unit );
		return this;
	}

	public CommandProcessor getCommandProcessor() {
		return commandProcessor;
	}

}
