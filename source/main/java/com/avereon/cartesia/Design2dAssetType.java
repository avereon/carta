package com.avereon.cartesia;

import com.avereon.cartesia.data.Design2D;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.product.Rb;
import com.avereon.settings.Settings;
import com.avereon.xenon.BundleKey;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetException;
import com.avereon.xenon.asset.AssetType;

public class Design2dAssetType extends AssetType {

	public static final String KEY = "design2d";

	public Design2dAssetType( ProgramProduct product ) {
		super( product, KEY );
		setDefaultCodec( new CartesiaDesignCodec2D( product ) );
	}

	@Override
	public boolean assetNew( Program program, Asset asset ) throws AssetException {
		Design2D design = initModel( asset );

		// Create the default layer
		String constructionLayerName = Rb.textOr( BundleKey.LABEL, "layer-construction", "construction" ).toLowerCase();
		DesignLayer layer = new DesignLayer().setName( constructionLayerName );
		design.getRootLayer().addLayer( layer );

		// Initialize the design settings
		Settings settings = program.getSettingsManager().getAssetSettings( asset );

		return true;
	}

	@Override
	public boolean assetOpen( Program program, Asset asset ) throws AssetException {
		Design2D design = initModel( asset );

		asset.setCaptureUndoChanges( true );
		return true;
	}

	private Design2D initModel( Asset asset ) {
		// There might already be a model from assetNew()
		Design2D design = asset.getModel();

		// If there is not already a model, create one
		if( design == null ) asset.setModel( design = new Design2D() );

		return design;
	}

}
