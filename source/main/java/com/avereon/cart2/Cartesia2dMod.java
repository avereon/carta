package com.avereon.cart2;

import com.avereon.xenon.Mod;

public class Cartesia2dMod extends Mod {

	@Override
	public void startup() {
		super.startup();
		registerIcon( "cart2", new Cartesia2dIcon() );
	}

	@Override
	public void shutdown() {
		unregisterIcon( "cart2", new Cartesia2dIcon() );
		super.shutdown();
	}

}
