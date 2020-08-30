package com.avereon.cartesia;

public class DesignValue {

	private final double value;

	private final DesignUnit unit;

	public DesignValue( double value, DesignUnit unit ) {
		this.value = value;
		this.unit = unit;
	}

	public double getValue() {
		return value;
	}

	public DesignUnit getUnit() {
		return unit;
	}

}
