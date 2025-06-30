package com.avereon.cartesia;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignUnitTest extends BaseCartesiaUnitTest {

	@Test
	void testMetricUnits() {
		assertThat( DesignUnit.M.conversion() ).isEqualTo( 1.0 );
		assertThat( DesignUnit.MM.conversion() ).isEqualTo( 0.001 );
		assertThat( DesignUnit.CM.conversion() ).isEqualTo( 0.01 );
		assertThat( DesignUnit.DM.conversion() ).isEqualTo( 0.1 );
		assertThat( DesignUnit.KM.conversion() ).isEqualTo( 1000.0 );
	}

	@Test
	void testImperialUnits() {
		assertThat( DesignUnit.IN.conversion() ).isEqualTo( 2.54 * DesignUnit.CM.conversion() );
		assertThat( DesignUnit.FT.conversion() ).isEqualTo( 12 * DesignUnit.IN.conversion() );
		assertThat( DesignUnit.YD.conversion() ).isEqualTo( 3 * DesignUnit.FT.conversion() );
		assertThat( DesignUnit.MI.conversion() ).isEqualTo( 5280 * DesignUnit.FT.conversion() );
		assertThat( DesignUnit.NM.conversion() ).isEqualTo( 1852.0 );
	}

	@Test
	void testConvertTo() {
		assertThat( DesignUnit.IN.to( 1, DesignUnit.CM )).isEqualTo( 2.54  );
		assertThat( DesignUnit.MI.to( 1, DesignUnit.M )).isEqualTo( 1609.344  );
	}

	@Test
	void testConvertFrom() {
		assertThat( DesignUnit.CM.from( 1, DesignUnit.IN )).isEqualTo( 2.54  );
		assertThat( DesignUnit.M.from( 1, DesignUnit.MI )).isEqualTo( 1609.344  );
	}

}
