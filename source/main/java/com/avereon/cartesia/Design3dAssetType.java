package com.avereon.cartesia;

import com.avereon.cartesia.data.Design3D;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.product.Rb;
import com.avereon.xenon.RbKey;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetException;
import com.avereon.xenon.asset.AssetType;

public class Design3dAssetType extends AssetType {

	public static final String KEY = "design3d";

	public Design3dAssetType( XenonProgramProduct product ) {
		super( product, KEY );
		setDefaultCodec( new CartesiaDesignCodec3D( product ) );
	}

	@Override
	public boolean assetNew( Xenon program, Asset asset ) throws AssetException {
		// There might already be a model from assetNew()
		Design3D design = asset.getModel();

		// If there is not already a model, create one
		if( design == null ) asset.setModel( design = new Design3D() );

		// If there is not a default layer, create one
		if( design.getLayers().getLayers().size() == 0 ) {
			String constructionLayerName = Rb.textOr( RbKey.LABEL, "layer-construction", "construction" ).toLowerCase();
			DesignLayer layer = new DesignLayer().setName( constructionLayerName );
			design.getLayers().addLayer( layer );
		}

		return true;
	}

	@Override
	public boolean assetOpen( Xenon program, Asset asset ) throws AssetException {
		asset.setCaptureUndoChanges( true );
		return true;
	}

}
