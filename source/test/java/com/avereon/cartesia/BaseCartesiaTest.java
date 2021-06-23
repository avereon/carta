package com.avereon.cartesia;

import com.avereon.xenon.BaseModUIT;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseCartesiaTest extends BaseModUIT {

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		initMod( new CartesiaMod() );
	}

}
