package com.avereon.cartesia;

import com.avereon.product.ProductCard;
import com.avereon.zerra.BaseFullModTestCase;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseCartesiaTest extends BaseFullModTestCase {

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		initMod( ProductCard.card( CartesiaMod.class ) );
	}

	@Override
	protected CartesiaMod getMod() {
		return (CartesiaMod)super.getMod();
	}

}
