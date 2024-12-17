package com.avereon.cartesia;

import com.avereon.zerra.BaseModUiTestCase;

public abstract class BaseCartesiaUiTest extends BaseModUiTestCase<CartesiaMod> {

	protected BaseCartesiaUiTest() {
		super( CartesiaMod.class );
	}

	@Override
	protected CartesiaMod getMod() {
		return (CartesiaMod)super.getMod();
	}

	@Override
	protected double getAllowedMemoryGrowthSize() {
		return 48;
	}

	@Override
	protected double getAllowedMemoryGrowthPercent() {
		return 1.0;
	}

}
