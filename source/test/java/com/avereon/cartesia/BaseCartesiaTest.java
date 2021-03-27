package com.avereon.cartesia;

import org.junit.jupiter.api.BeforeEach;

public abstract class BaseCartesiaTest {

	private CartesiaMod mod;

	@BeforeEach
	void setup() {
		mod = new CartesiaMod();
	}

	public CartesiaMod getMod() {
		return mod;
	}

}
