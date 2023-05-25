package com.avereon.cartesia;

import com.avereon.product.ProductCard;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.zerra.BaseFullModTestCase;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseCartesiaTest extends BaseFullModTestCase {

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		initMod( ProductCard.card( CartesiaMod.class ) );
	}

	protected XenonProgramProduct getProduct() {
		return getMod();
	}

	protected CartesiaMod getMod() {
		return (CartesiaMod)super.getMod();
	}

}
