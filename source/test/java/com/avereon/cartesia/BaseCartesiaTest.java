package com.avereon.cartesia;

import com.avereon.zerra.BaseModTestCase;

public abstract class BaseCartesiaTest extends BaseModTestCase<CartesiaMod> {

	protected BaseCartesiaTest() {
		super( CartesiaMod.class );
	}

	@Override
	protected CartesiaMod getMod() {
		return (CartesiaMod)super.getMod();
	}

}
