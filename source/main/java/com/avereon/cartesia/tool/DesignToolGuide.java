package com.avereon.cartesia.tool;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignNode;
import com.avereon.data.Node;
import com.avereon.data.NodeEvent;
import com.avereon.util.Log;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.guide.Guide;
import com.avereon.xenon.tool.guide.GuideNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DesignToolGuide extends Guide {

	private static final System.Logger log = Log.get();

	private final ProgramProduct product;

	private GuideNode layers;

	private Design design;

	private Map<DesignNode, GuideNode> nodes;

	public DesignToolGuide( ProgramProduct product ) {
		this.product = product;
		this.nodes = new ConcurrentHashMap<>();
	}

	ProgramProduct getProduct() {
		return product;
	}

	Program getProgram() {
		return product.getProgram();
	}

	void loadDesign( Design design ) {
		this.design = design;

		String layersLabel = getProduct().rb().textOr( BundleKey.LABEL, "layers", "Layers"  );
		addNode( layers = new GuideNode( getProgram(), design.getRootLayer().getId(), layersLabel, "layers" ) );
		nodes.put( design.getRootLayer(), layers );

		// Go through the design and generate the initial guide
		design.getAllLayers().forEach( this::addLayer );
		//design.getAllViews().forEach( this::addView );

		design.register( NodeEvent.CHILD_ADDED, this::doChildAddedAction );
		design.register( NodeEvent.CHILD_REMOVED, this::doChildRemovedAction );
	}

	private void doChildAddedAction( NodeEvent event ) {
		Node parent = event.getNode();
		Node child = event.getNewValue();

		if( child instanceof DesignLayer ) addLayer( (DesignLayer)child );
	}

	private void doChildRemovedAction( NodeEvent event ) {
		Node parent = event.getNode();
		Node child = event.getOldValue();

		if( child instanceof DesignLayer ) removeLayer( (DesignLayer)child );
	}

	private void addLayer( DesignLayer layer ) {
		log.log( Log.WARN, "parentid=" + layer.getLayer().getId() );
		GuideNode parentGuideNode = nodes.get( layer.getLayer() );
		GuideNode layerGuideNode = new GuideNode( getProgram(), layer.getId(), layer.getName(), "layer" );
		addNode( parentGuideNode, layerGuideNode );
		nodes.put( layer, layerGuideNode );
	}

	private void removeLayer( DesignLayer layer ) {
		GuideNode parentGuideNode = nodes.get( layer.getLayer() );
		GuideNode layerGuideNode = nodes.get( layer );
		removeNode( parentGuideNode, layerGuideNode );
		nodes.remove( layer );
	}

}
