package com.avereon.cartesia;

import com.avereon.xenon.ProgramProduct;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseCartesiaTest {

	private CartesiaMod mod;

	@BeforeEach
	void setup() {
		mod = new CartesiaMod();
	}

	public ProgramProduct getProduct() {
		return getMod();
	}

	public CartesiaMod getMod() {
		return mod;
	}

}
