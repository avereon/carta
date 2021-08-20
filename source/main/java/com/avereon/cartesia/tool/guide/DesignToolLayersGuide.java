package com.avereon.cartesia.tool.guide;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.product.Rb;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.guide.Guide;
import com.avereon.xenon.tool.guide.GuideNode;
import com.avereon.zerra.javafx.Fx;
import lombok.CustomLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CustomLog
public class DesignToolLayersGuide extends Guide {

	private static final String SHOWING_HANDLER = DesignToolLayersGuide.class.getName() + ":showing-handler";

	private static final String NAME_HANDLER = DesignToolLayersGuide.class.getName() + ":name-handler";

	private static final String ORDER_HANDLER = DesignToolLayersGuide.class.getName() + ":order-handler";

	private static final String VISIBLE_HANDLER = DesignToolLayersGuide.class.getName() + ":visible-handler";

	private final ProgramProduct product;

	private final DesignTool tool;

	private final Map<DesignLayer, GuideNode> layerNodes;

	private final Map<GuideNode, DesignLayer> nodeLayers;

	public DesignToolLayersGuide( ProgramProduct product, DesignTool tool ) {
		this.product = product;
		this.tool = tool;
		this.layerNodes = new ConcurrentHashMap<>();
		this.nodeLayers = new ConcurrentHashMap<>();
		setIcon( "layers" );
		setTitle( Rb.textOr( BundleKey.LABEL, "layers", "Layers" ) );
		setDragAndDropEnabled( true );
	}

	@Override
	protected void moveNode( GuideNode source, GuideNode target, Guide.Drop drop ) {
		if( drop == Drop.NONE ) return;

		// NOTE This implementation operates directly on the data model which, in
		// turn, causes events that update the guide accordingly.

		// The source and target should have design layers
		DesignLayer sourceLayer = nodeLayers.get( source );
		DesignLayer targetLayer = nodeLayers.get( target );

		log.atDebug().log( "Move layer %s to %s %s", sourceLayer, targetLayer, drop );

		sourceLayer.getLayer().removeLayer( sourceLayer );

		if( drop == Drop.CHILD ) {
			targetLayer.addLayer( sourceLayer );
		} else {
			targetLayer.getLayer().addLayerBeforeOrAfter( sourceLayer, targetLayer, drop == Drop.BELOW );
		}
	}

	ProgramProduct getProduct() {
		return product;
	}

	Program getProgram() {
		return product.getProgram();
	}

	public synchronized void link( Design design ) {
		// Populate the guide
		design.getAllLayers().forEach( this::addLayer );

		// Add listeners for changes
		design.register( NodeEvent.CHILD_ADDED, e -> {
			if( e.getSetKey().equals( Design.LAYERS ) ) Fx.run( () -> addLayer( e.getNewValue() ) );
		} );
		design.register( NodeEvent.CHILD_REMOVED, e -> {
			if( e.getSetKey().equals( Design.LAYERS ) ) Fx.run( () -> removeLayer( e.getOldValue() ) );
		} );
	}

	private void addLayer( DesignLayer layer ) {
		GuideNode parentGuideNode = layerNodes.get( layer.getLayer() );
		GuideNode layerGuideNode = new GuideNode( getProgram(), layer.getId(), layer.getName(), "layer", layer.getOrder() );

		addNode( parentGuideNode, layerGuideNode );
		layerNodes.put( layer, layerGuideNode );
		nodeLayers.put( layerGuideNode, layer );

		log.atConfig().log( "Add layer=" + layer.getName() );

		EventHandler<NodeEvent> nameHandler = e -> layerGuideNode.setName( layer.getName() );
		EventHandler<NodeEvent> orderHandler = e -> layerGuideNode.setOrder( layer.getOrder() );
		EventHandler<NodeEvent> visibleHandler = e -> layerGuideNode.setIcon( e.getNewValue() ? "layer" : "layer-hidden" );

		layerGuideNode.setValue( NAME_HANDLER, nameHandler );
		layerGuideNode.setValue( ORDER_HANDLER, orderHandler );
		layerGuideNode.setValue( VISIBLE_HANDLER, visibleHandler );

		layer.register( DesignLayer.NAME, nameHandler );
		layer.register( DesignLayer.ORDER, orderHandler );
		layer.register( DesignLayer.VISIBLE, visibleHandler );
		layer.setValue( GUIDE_NODE, layerGuideNode );
	}

	private void removeLayer( DesignLayer layer ) {
		log.atConfig().log( "Remove layer=" + layer.getName() );

		GuideNode layerGuideNode = layer.getValue( GUIDE_NODE );

		layer.unregister( DesignLayer.VISIBLE, layerGuideNode.getValue( VISIBLE_HANDLER ) );
		layer.unregister( DesignLayer.ORDER, layerGuideNode.getValue( ORDER_HANDLER ) );
		layer.unregister( DesignLayer.NAME, layerGuideNode.getValue( NAME_HANDLER ) );
		layer.setValue( GUIDE_NODE, null );

		removeNode( layerNodes.get( layer ) );
		nodeLayers.remove( layerNodes.get( layer ) );
		layerNodes.remove( layer );
	}

}
