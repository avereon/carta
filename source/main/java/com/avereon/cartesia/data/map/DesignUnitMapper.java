package com.avereon.cartesia.data.map;

import com.avereon.cartesia.DesignUnit;

public interface DesignUnitMapper {

	static DesignUnit map( final String unit ) {
		if( unit == null || unit.isBlank() ) return null;
		String nameOrAbbreviation = unit.trim().toUpperCase();
		try {
			return DesignUnit.valueOf( nameOrAbbreviation );
		} catch( IllegalArgumentException e ) {
			return DesignUnit.valueOf( mapNameToAbbreviation( nameOrAbbreviation ).toUpperCase() );
		}
	}

	static String mapNameToAbbreviation( String name ) {
		return switch( name.toLowerCase() ) {
			case "meter" -> "m";
			case "millimeter" -> "mm";
			case "centimeter" -> "cm";
			case "decimeter" -> "dm";
			case "kilometer" -> "km";
			case "inch" -> "in";
			case "foot" -> "ft";
			case "yard" -> "yd";
			case "mile" -> "mi";
			case "nautical mile" -> "nm";
			default -> throw new IllegalArgumentException( "Unknown design unit named: " + name );
		};
	}

	static String mapAbbreviationToName( String abbreviation ) {
		return switch( abbreviation.toLowerCase() ) {
			case "m" -> "Meter";
			case "mm" -> "Millimeter";
			case "cm" -> "Centimeter";
			case "dm" -> "Decimeter";
			case "km" -> "Kilometer";
			case "in" -> "Inch";
			case "ft" -> "Foot";
			case "yd" -> "Yard";
			case "mi" -> "Mile";
			case "nm" -> "Nautical Mile";
			default -> throw new IllegalArgumentException( "Unknown design unit abbreviated: " + abbreviation );
		};
	}

}
