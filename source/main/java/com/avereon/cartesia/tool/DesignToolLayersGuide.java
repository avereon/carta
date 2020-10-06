package com.avereon.cartesia.tool;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
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

	private final Map<DesignLayer, GuideNode> layerNodes;

	private final Map<GuideNode, DesignLayer> nodeLayers;

	private ChangeListener<Boolean> showingHandler;

	private EventHandler<NodeEvent> orderHandler;

	public DesignToolLayersGuide( ProgramProduct product, DesignTool tool ) {
		this.product = product;
		this.tool = tool;
		this.layerNodes = new ConcurrentHashMap<>();
		this.nodeLayers = new ConcurrentHashMap<>();
		setIcon( "layers" );
		setTitle( product.rb().textOr( BundleKey.LABEL, "layers", "Layers" ) );
		setDragAndDropEnabled( true );
	}

	@Override
	protected void moveNode( GuideNode source, GuideNode target, Guide.Drop drop ) {
		if( drop == Drop.NONE ) return;

		// The source and target should have layers
		DesignLayer sourceLayer = nodeLayers.get( source );
		DesignLayer targetLayer = nodeLayers.get( target );

		log.log( Log.DEBUG, "Move layer " + sourceLayer + " to " + targetLayer + " " + drop );

		sourceLayer.getParentLayer().removeLayer( sourceLayer );

		if( drop == Drop.CHILD ) {
			targetLayer.addLayer( sourceLayer );
		} else {
			targetLayer.getParentLayer().addLayerBeforeOrAfter( sourceLayer, targetLayer, drop == Drop.BELOW );
		}
	}

	ProgramProduct getProduct() {
		return product;
	}

	Program getProgram() {
		return product.getProgram();
	}

	void link( DesignPane pane ) {
		// If the guide is linked before the design pane is loaded then these event
		// handlers will populate the guide as the layers are created in the design
		// pane.
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
		GuideNode parentGuideNode = layerNodes.get( designLayer.getParentLayer() );
		GuideNode layerGuideNode = new GuideNode( getProgram(), designLayer.getId(), designLayer.getName(), "layer", designLayer.getOrder() );

		addNode( parentGuideNode, layerGuideNode );
		layerNodes.put( designLayer, layerGuideNode );
		nodeLayers.put( layerGuideNode, designLayer );

		showingHandler = ( p, o, n ) -> layerGuideNode.setIcon( n ? "layer" : "layer-hidden" );
		orderHandler = e -> layerGuideNode.setOrder( designLayer.getOrder() );

		layer.showingProperty().addListener( showingHandler );
		designLayer.register( DesignLayer.ORDER, orderHandler );
	}

	private void removeLayer( DesignLayer designLayer, DesignPane.Layer layer ) {
		designLayer.unregister( DesignLayer.ORDER, orderHandler );
		layer.visibleProperty().removeListener( showingHandler );
		removeNode( layerNodes.get( designLayer ) );
		nodeLayers.remove( layerNodes.get( designLayer ) );
		layerNodes.remove( designLayer );
	}

}
