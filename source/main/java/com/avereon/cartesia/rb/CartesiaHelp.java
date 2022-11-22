package com.avereon.cartesia.rb;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

public class CartesiaHelp extends ResourceBundle {

	private final Enumeration<String> keys = Collections.enumeration( List.of() );

	@Override
	public Enumeration<String> getKeys() {
		return keys;
	}

	@Override
	protected Object handleGetObject( String key ) {
		return getClass().getResource( key + ".html" );
	}

}
