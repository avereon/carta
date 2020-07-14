package com.avereon.cartesia;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DesignUnitTest {

	@Test
	void testConvertTo() {
		assertThat( DesignUnit.INCH.to( 1, DesignUnit.CENTIMETER ), is( 2.54 ) );
		assertThat( DesignUnit.MILE.to( 1, DesignUnit.METER ), is( 1609.344 ) );
	}

	@Test
	void testConvertFrom() {
		assertThat( DesignUnit.CENTIMETER.from( 1, DesignUnit.INCH ), is( 2.54 ) );
		assertThat( DesignUnit.METER.from( 1, DesignUnit.MILE ), is( 1609.344 ) );
	}

}
