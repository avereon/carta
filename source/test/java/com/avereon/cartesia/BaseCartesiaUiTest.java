package com.avereon.cartesia;

import com.avereon.xenos.BaseModUiTestCase;

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
		return 96 - MIN_INITIAL_MEMORY_MiB;
	}

	@Override
	protected double getAllowedMemoryGrowthPercent() {
		return 2.0;
	}

}
