package com.avereon.cartesia;

import com.avereon.xenon.Xenon;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetException;
import com.avereon.xenon.scheme.ProductScheme;

public class CartesiaScheme extends ProductScheme {

	public static final String ID = "cartesia";

	public CartesiaScheme( Xenon program ) {
		super( program, ID );
	}

	@Override
	public boolean canLoad( Asset asset ) throws AssetException {
		return true;
	}

}
