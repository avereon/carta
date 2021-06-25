package com.avereon.cartesia;

import com.avereon.xenon.test.BaseModUIT;
import org.junit.jupiter.api.BeforeEach;

public class BaseCartesiaUIT extends BaseModUIT {

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		initMod( new CartesiaMod() );
	}

}
