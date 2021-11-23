package com.avereon.cartesia;

import com.avereon.product.ProductCard;
import com.avereon.product.Rb;
import com.avereon.xenon.ProgramProduct;
import com.avereon.zerra.test.BaseXenonTestCase;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseCartesiaTest extends BaseXenonTestCase {

	private CartesiaMod mod;

	@BeforeEach
	protected void setup() throws Exception {
		mod = new CartesiaMod();
		mod.init( getProgram(), ProductCard.card( mod ) );
		Rb.init(mod);
	}

	protected ProgramProduct getProduct() {
		return getMod();
	}

	protected CartesiaMod getMod() {
		return mod;
	}

}
