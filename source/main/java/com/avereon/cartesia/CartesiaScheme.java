package com.avereon.cartesia;

import com.avereon.xenon.Xenon;
import com.avereon.xenon.asset.Resource;
import com.avereon.xenon.asset.exception.ResourceException;
import com.avereon.xenon.scheme.ProductScheme;

public class CartesiaScheme extends ProductScheme {

	public static final String ID = "cartesia";

	public CartesiaScheme( Xenon program ) {
		super( program, ID );
	}

	@Override
	public boolean canLoad( Resource resource ) throws ResourceException {
		return true;
	}

}
