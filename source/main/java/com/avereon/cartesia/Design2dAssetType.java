package com.avereon.cartesia;

import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetException;
import com.avereon.xenon.asset.AssetType;

public class Design2dAssetType extends AssetType {

	public Design2dAssetType( ProgramProduct product ) {
		super( product, "design2d" );
	}

	@Override
	public boolean assetInit( Program program, Asset asset ) throws AssetException {
		asset.setModel( new Design2d() );
		return true;
	}

}
