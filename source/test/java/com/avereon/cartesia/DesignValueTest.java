package com.avereon.cartesia;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignValueTest extends BaseCartesiaUnitTest {

	@Test
	void to() {
		DesignValue value = new DesignValue( 1, DesignUnit.METER );
		DesignValue result = value.to( DesignUnit.MILLIMETER );
		assertThat( result.getValue() ).isEqualTo( 1000 );
		assertThat( result.getUnit() ).isEqualTo( DesignUnit.MILLIMETER );
	}

}
