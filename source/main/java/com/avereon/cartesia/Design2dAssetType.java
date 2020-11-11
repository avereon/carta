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
		setDefaultCodec( new CartesiaDesignCodec2D( product ) );
	}

	@Override
	public boolean assetNew( Program program, Asset asset ) throws AssetException {
		// There might already be a model from assetNew()
		Design2D design = asset.getModel();

		// If there is not already a model, create one
		if( design == null ) asset.setModel( design = new Design2D() );

		// If there is not a default layer, create one
		if( design.getRootLayer().getLayers().size() == 0 ) {
			String constructionLayerName = getProduct().rb().textOr( BundleKey.LABEL, "layer-construction", "construction" ).toLowerCase();
			DesignLayer layer = new DesignLayer().setName( constructionLayerName );
			design.getRootLayer().addLayer( layer );
		}

		return true;
	}

	@Override
	public boolean assetOpen( Program program, Asset asset ) throws AssetException {
		return true;
	}

}
