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
import javafx.beans.value.ChangeListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DesignToolGuide extends Guide {

	private static final System.Logger log = Log.get();

	private final ProgramProduct product;

	private final Map<DesignNode, GuideNode> nodes;

	private Design design;

	private DesignPane pane;

	private ChangeListener<Boolean> visibleHandler;

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

	void loadDesign( Design design, DesignPane pane ) {
		this.design = design;
		this.pane = pane;

		String layersLabel = getProduct().rb().textOr( BundleKey.LABEL, "layers", "Layers" );
		GuideNode layers = new GuideNode( getProgram(), design.getRootLayer().getId(), layersLabel, "layers" );
		nodes.put( design.getRootLayer(), layers );
		addNode( layers );

		// Go through the design and generate the initial guide
		design.getAllLayers().forEach( this::addLayer );
		//design.getAllViews().forEach( this::addView );

		design.register( NodeEvent.CHILD_ADDED, this::doChildAddedAction );
		design.register( NodeEvent.CHILD_REMOVED, this::doChildRemovedAction );
	}

	private void doChildAddedAction( NodeEvent event ) {
		Node child = event.getNewValue();
		if( child instanceof DesignLayer ) addLayer( (DesignLayer)child );
	}

	private void doChildRemovedAction( NodeEvent event ) {
		Node child = event.getOldValue();
		if( child instanceof DesignLayer ) removeLayer( (DesignLayer)child );
	}

	private void addLayer( DesignLayer layer ) {
		GuideNode parentGuideNode = nodes.get( layer.getLayer() );
		GuideNode layerGuideNode = new GuideNode( getProgram(), layer.getId(), layer.getName(), "layer" );
		addNode( parentGuideNode, layerGuideNode );
		nodes.put( layer, layerGuideNode );

		visibleHandler = ( p, o, n ) -> layerGuideNode.setIcon( n ? "layer" : "layer-hidden" );
		pane.getDesignLayerView( layer ).getLayer().visibleProperty().addListener( visibleHandler );
	}

	private void removeLayer( DesignLayer layer ) {
		pane.getDesignLayerView( layer ).getLayer().visibleProperty().removeListener( visibleHandler );

		GuideNode parentGuideNode = nodes.get( layer.getLayer() );
		GuideNode layerGuideNode = nodes.get( layer );
		removeNode( parentGuideNode, layerGuideNode );
		nodes.remove( layer );
	}

}
