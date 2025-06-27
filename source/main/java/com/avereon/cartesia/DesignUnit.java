package com.avereon.cartesia;

/**
 * DesignUnit represents various units of measurement.
 */
public enum DesignUnit {

	// The standard measure (https://en.wikipedia.org/wiki/Metre)
	METER( 1 ),

	MILLIMETER( 0.001, METER ),
	CENTIMETER( 0.01, METER ),
	DECIMETER( 0.1, METER ),
	KILOMETER( 1000, METER ),

	// Defined by international agreement (https://en.wikipedia.org/wiki/Inch_(unit))
	INCH( 2.54, CENTIMETER ),
	// Defined by international agreement (https://en.wikipedia.org/wiki/Foot_(unit))
	FOOT( 12, INCH ),
	// Defined by international agreement (https://en.wikipedia.org/wiki/Yard_(unit))
	YARD( 3, FOOT ),
	// Defined by international agreement (https://en.wikipedia.org/wiki/Mile_(unit))
	MILE( 5280, FOOT ),
	// Defined by international agreement (https://en.wikipedia.org/wiki/Nautical_mile)
	NAUTICAL_MILE( 1852, METER );

	private final double conversion;

	DesignUnit( double conversion ) {
		this.conversion = conversion;
	}

	DesignUnit( double value, DesignUnit unit ) {
		this( value * unit.conversion );
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
