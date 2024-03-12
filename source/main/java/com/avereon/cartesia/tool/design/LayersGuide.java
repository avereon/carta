package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.DesignToolLayersGuide;
import com.avereon.product.Rb;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.tool.guide.Guide;
import com.avereon.xenon.tool.guide.GuideNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LayersGuide extends Guide {

	private static final String NAME_HANDLER = DesignToolLayersGuide.class.getName() + ":name-handler";

	private static final String ORDER_HANDLER = DesignToolLayersGuide.class.getName() + ":order-handler";

	private static final String ENABLED_HANDLER = DesignToolLayersGuide.class.getName() + ":visible-handler";

	private final XenonProgramProduct product;

	private final FxRenderDesignTool tool;

	private final Map<DesignLayer, GuideNode> layerGuideNodes;

	private final Map<GuideNode, DesignLayer> guideNodeLayers;

	// FIXME Is product needed?
	public LayersGuide( XenonProgramProduct product, FxRenderDesignTool tool ) {
		this.product = product;
		this.tool = tool;
		this.layerGuideNodes = new ConcurrentHashMap<>();
		this.guideNodeLayers = new ConcurrentHashMap<>();
		setIcon( "layers" );
		setTitle( Rb.textOr( RbKey.LABEL, "layers", "Layers" ) );
		setDragAndDropEnabled( true );
	}

}
