package com.avereon.cartesia.rb;

public class CartesiaHelp_es extends CartesiaHelp {

	@Override
	protected Object handleGetObject( String key ) {
		return getClass().getResource( key + "_es.html" );
	}

}
