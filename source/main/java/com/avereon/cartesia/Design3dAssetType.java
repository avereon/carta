package com.avereon.cartesia;

import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetException;
import com.avereon.xenon.asset.AssetType;

public class Design3dAssetType extends AssetType {

	public Design3dAssetType( ProgramProduct product ) {
		super( product, "design3d" );
	}

	@Override
	public boolean assetInit( Program program, Asset asset ) throws AssetException {
		asset.setModel( new Design3d() );
		return true;
	}

}
