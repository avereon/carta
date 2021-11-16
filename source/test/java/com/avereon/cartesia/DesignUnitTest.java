package com.avereon.cartesia;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignUnitTest {

	@Test
	void testConvertTo() {
		assertThat( DesignUnit.INCH.to( 1, DesignUnit.CENTIMETER )).isEqualTo( 2.54  );
		assertThat( DesignUnit.MILE.to( 1, DesignUnit.METER )).isEqualTo( 1609.344  );
	}

	@Test
	void testConvertFrom() {
		assertThat( DesignUnit.CENTIMETER.from( 1, DesignUnit.INCH )).isEqualTo( 2.54  );
		assertThat( DesignUnit.METER.from( 1, DesignUnit.MILE )).isEqualTo( 1609.344  );
	}

}
