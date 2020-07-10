package com.avereon.cartesia;

import com.avereon.xenon.Mod;

public class CartesiaMod extends Mod {

	@Override
	public void startup() {
		super.startup();
		registerIcon( "cart2", new CartesiaIcon() );
	}

	@Override
	public void shutdown() {
		unregisterIcon( "cart2", new CartesiaIcon() );
		super.shutdown();
	}

}
