package com.avereon.cartesia;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignModel3D;
import com.avereon.product.Rb;
import com.avereon.xenon.RbKey;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.resource.Resource;
import com.avereon.xenon.resource.ResourceType;

public class Design3DResourceType extends ResourceType {

	public static final String KEY = "design3d";

	public Design3DResourceType( XenonProgramProduct product ) {
		super( product, KEY );
		setDefaultCodec( new CartesiaDesignCodec3D( product ) );
	}

	@Override
	public boolean assetNew( Xenon program, Resource resource ) {
		Design<DesignModel3D> design = initModel( resource );

		// There might already be a model from assetNew()
		DesignModel3D dataModel = design.getDataModel();

		// If there is not a default layer, create one
		String constructionLayerName = Rb.textOr( RbKey.LABEL, "layer-construction", "construction" ).toLowerCase();
		DesignLayer layer = new DesignLayer().setName( constructionLayerName );
		dataModel.getLayers().addLayer( layer );

		return true;
	}

	@Override
	public boolean assetOpen( Xenon program, Resource resource ) {
		initModel( resource );
		resource.setCaptureUndoChanges( true );
		return true;
	}

	private Design<DesignModel3D> initModel( Resource resource ) {
		// There might already be a model
		Design<DesignModel3D> design = resource.getModel();

		// If there is not already a model, create one
		if( design == null ) {
			design = new Design<DesignModel3D>().setDataModel( new DesignModel3D() );
			resource.setModel( design );
		}

		return design;
	}
}
