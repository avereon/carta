package com.avereon.cartesia;

import com.avereon.cartesia.data.Design3D;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetException;
import com.avereon.xenon.asset.AssetType;

public class Design3dAssetType extends AssetType {

	public Design3dAssetType( ProgramProduct product ) {
		super( product, "design3d" );
		setDefaultCodec( new CartesiaDesignCodec3D( product ) );
	}

	@Override
	public boolean assetInit( Program program, Asset asset ) throws AssetException {
		asset.setModel( new Design3D() );
		return true;
	}

}
