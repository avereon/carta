package com.avereon.cartesia;

import com.avereon.cartesia.data.Design2D;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.xenon.BundleKey;
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
		String constructionLayerName = getProduct().rb().textOr( BundleKey.LABEL, "layer-construction", "construction" ).toLowerCase();

		Design2D design = new Design2D();
		DesignLayer layer = new DesignLayer().setName( constructionLayerName );
		design.addLayer( layer ).setCurrentLayer( layer );
		asset.setModel( design );

		return true;
	}

}
