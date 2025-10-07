package com.avereon.cartesia;

import com.avereon.cartesia.data.Design2D;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.product.Rb;
import com.avereon.settings.Settings;
import com.avereon.xenon.RbKey;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.Resource;
import com.avereon.xenon.asset.exception.ResourceException;
import com.avereon.xenon.asset.ResourceType;

public class Design2DResourceType extends ResourceType {

	public static final String KEY = "design2d";

	public Design2DResourceType( XenonProgramProduct product ) {
		super( product, KEY );
		setDefaultCodec( new CartesiaDesignCodec2D( product ) );
	}

	@Override
	public boolean assetNew( Xenon program, Resource resource ) throws ResourceException {
		Design2D design = initModel( resource );

		// Create the default layer
		String constructionLayerName = Rb.textOr( RbKey.LABEL, "layer-construction", "construction" ).toLowerCase();
		DesignLayer layer = new DesignLayer().setName( constructionLayerName );
		design.getLayers().addLayer( layer );

		// Initialize the design settings
		Settings settings = program.getSettingsManager().getAssetSettings( resource );
		settings.set( "grid-major-x", "1.0" );
		settings.set( "grid-major-y", "1.0" );
		settings.set( "grid-minor-x", "0.5" );
		settings.set( "grid-minor-y", "0.5" );
		settings.set( "grid-snap-x", "0.1" );
		settings.set( "grid-snap-y", "0.1" );

		return true;
	}

	@Override
	public boolean assetOpen( Xenon program, Resource resource ) throws ResourceException {
		initModel( resource );

		resource.setCaptureUndoChanges( true );
		return true;
	}

	private Design2D initModel( Resource resource ) {
		// There might already be a model
		Design2D design = resource.getModel();

		// If there is not already a model, create one
		if( design == null ) resource.setModel( design = new Design2D() );

		return design;
	}

}
