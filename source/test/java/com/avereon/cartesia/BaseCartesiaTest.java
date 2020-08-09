package com.avereon.cartesia;

import org.junit.jupiter.api.BeforeEach;

public abstract class BaseCartesiaTest {

	protected CartesiaMod mod;

	@BeforeEach
	void setup() {
		mod = new CartesiaMod();
	}

}
