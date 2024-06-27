package com.avereon.cartesia;

import lombok.Getter;

@Getter
public class DesignValue {

	private final double value;

	private final DesignUnit unit;

	public DesignValue( double value, DesignUnit unit ) {
		this.value = value;
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "DesignValue{" + "value=" + value + ", unit=" + unit + '}';
	}

	public DesignValue to( DesignUnit unit ) {
		return new DesignValue( this.unit.to( value, unit ), unit );
	}

}
