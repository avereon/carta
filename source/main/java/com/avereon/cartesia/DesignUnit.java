package com.avereon.cartesia;

/**
 * DesignUnit represents various units of measurement.
 */
public enum DesignUnit {

	// The base unit of length (https://en.wikipedia.org/wiki/Metre)
	M( "Meter", 1 ),

	MM( "Millimeter", 0.001 ),
	CM( "Centimeter", 0.01 ),
	DM( "Decimeter", 0.1 ),
	KM( "Kilometer", 1000 ),

	// Defined by international agreement (https://en.wikipedia.org/wiki/Inch_(unit))
	IN( "Inch", 2.54, CM ),
	// Defined by international agreement (https://en.wikipedia.org/wiki/Foot_(unit))
	FT( "Foot", 12, IN ),
	// Defined by international agreement (https://en.wikipedia.org/wiki/Yard_(unit))
	YD( "Yard", 3, FT ),
	// Defined by international agreement (https://en.wikipedia.org/wiki/Mile_(unit))
	MI( "Mile", 5280, FT ),
	// Defined by international agreement (https://en.wikipedia.org/wiki/Nautical_mile)
	NM( "Nautical Mile", 1852, M );

	private final String name;

	private final double conversion;

	DesignUnit( String name, double conversion ) {
		this.name = name;
		this.conversion = conversion;
	}

	DesignUnit( String name, double value, DesignUnit unit ) {
		this( name, value * unit.conversion );
	}

	public double conversion() {
		return conversion;
	}

	/**
	 * Used to convert a value from this unit to the specified unit. For example,
	 * to convert 1 inch to centimeters, use:
	 *
	 * <pre>DesignUnit.INCH.to( 1, DesignUnit.CENTIMETER )</code>
	 *
	 * @param value The value to convert
	 * @param unit The unit to convert to
	 * @return The converted value in the specified unit
	 */
	public double to( double value, DesignUnit unit ) {
		return value * conversion / unit.conversion;
	}

	/**
	 * Used to convert a value from the specified unit to this unit. For example,
	 * to convert 1 centimeter to inches, use:
	 *
	 * <pre>DesignUnit.CENTIMETER.from( 1, DesignUnit.INCH )</code>
	 *
	 * @param value The value to convert
	 * @param unit The unit to convert from
	 * @return The converted value in this unit
	 */
	public double from( double value, DesignUnit unit ) {
		return value * unit.conversion / conversion;
	}

	/**
	 * Returns the conversion factor from this unit to the specified unit.
	 *
	 * @param unit The unit to convert to
	 * @return The conversion factor from this unit to the specified unit
	 */
	public double per( DesignUnit unit ) {
		return unit.conversion / conversion;
	}

}
