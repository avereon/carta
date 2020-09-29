package com.avereon.cartesia.tool;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignNode;
import com.avereon.util.Log;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.guide.Guide;
import com.avereon.xenon.tool.guide.GuideNode;
import javafx.beans.value.ChangeListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DesignToolLayersGuide extends Guide {

	private static final System.Logger log = Log.get();

	private final ProgramProduct product;

	private final DesignTool tool;

	private final Map<DesignNode, GuideNode> nodes;

	private ChangeListener<Boolean> showingHandler;

	public DesignToolLayersGuide( ProgramProduct product, DesignTool tool ) {
		this.product = product;
		this.tool = tool;
		this.nodes = new ConcurrentHashMap<>();
		setIcon( "layers" );
		setTitle( product.rb().textOr( BundleKey.LABEL, "layers", "Layers" ) );
	}

	ProgramProduct getProduct() {
		return product;
	}

	Program getProgram() {
		return product.getProgram();
	}

	void load( DesignPane pane ) {
		Design design = tool.getDesign();
		//String layersLabel = getProduct().rb().textOr( BundleKey.LABEL, "layers", "Layers" );
		//GuideNode layers = new GuideNode( getProgram(), design.getRootLayer().getId(), layersLabel, "layers" );
		//nodes.put( design.getRootLayer(), layers );
		//addNode( layers );

		// Go through the design and generate the initial guide
		// Layers will populate when the tool view is generated
		//design.getAllViews().forEach( this::addView );

		// Layer event handlers
		pane.addEventFilter( DesignLayerEvent.LAYER_ADDED, e -> {
			DesignPane.Layer l = e.getLayer();
			addLayer( DesignTool.getDesignData( l ), l );
		} );
		pane.addEventFilter( DesignLayerEvent.LAYER_REMOVED, e -> {
			DesignPane.Layer l = e.getLayer();
			removeLayer( DesignTool.getDesignData( l ), l );
		} );
	}

	private void addLayer( DesignLayer designLayer, DesignPane.Layer layer ) {
		GuideNode parentGuideNode = nodes.get( designLayer.getParentLayer() );
		GuideNode layerGuideNode = new GuideNode( getProgram(), designLayer.getId(), designLayer.getName(), "layer" );
		addNode( parentGuideNode, layerGuideNode );
		nodes.put( designLayer, layerGuideNode );

		showingHandler = ( p, o, n ) -> layerGuideNode.setIcon( n ? "layer" : "layer-hidden" );
		layer.showingProperty().addListener( showingHandler );
	}

	private void removeLayer( DesignLayer designLayer, DesignPane.Layer layer ) {
		layer.visibleProperty().removeListener( showingHandler );
		removeNode( nodes.get( designLayer ) );
		nodes.remove( designLayer );
	}

}
