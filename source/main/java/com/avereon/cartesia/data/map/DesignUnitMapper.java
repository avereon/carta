package com.avereon.cartesia.data.map;

public interface DesignUnitMapper {

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
			default -> throw new IllegalArgumentException( "Unknown design unit: " + name );
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
			default -> throw new IllegalArgumentException( "Unknown design unit abbreviation: " + abbreviation );
		};
	}

}
