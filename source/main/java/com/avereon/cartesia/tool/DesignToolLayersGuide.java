package com.avereon.cartesia.tool;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.view.DesignLayerEvent;
import com.avereon.cartesia.tool.view.DesignLayerPane;
import com.avereon.cartesia.tool.view.DesignPane;
import com.avereon.cartesia.tool.view.DesignShapeView;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.product.Rb;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.tool.guide.Guide;
import com.avereon.xenon.tool.guide.GuideNode;
import javafx.beans.value.ChangeListener;
import lombok.CustomLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CustomLog
public class DesignToolLayersGuide extends Guide {

	private static final String NAME_HANDLER = DesignToolLayersGuide.class.getName() + ":name-handler";

	private static final String ORDER_HANDLER = DesignToolLayersGuide.class.getName() + ":order-handler";

	private static final String ENABLED_HANDLER = DesignToolLayersGuide.class.getName() + ":visible-handler";

	private final ProgramProduct product;

	private final FxShapeDesignTool tool;

	private final Map<DesignLayer, GuideNode> layerGuideNodes;

	private final Map<GuideNode, DesignLayer> guideNodeLayers;

	public DesignToolLayersGuide( ProgramProduct product, FxShapeDesignTool tool ) {
		this.product = product;
		this.tool = tool;
		this.layerGuideNodes = new ConcurrentHashMap<>();
		this.guideNodeLayers = new ConcurrentHashMap<>();
		setIcon( "layers" );
		setTitle( Rb.textOr( RbKey.LABEL, "layers", "Layers" ) );
		setDragAndDropEnabled( true );
	}

	@Override
	protected void moveNode( GuideNode source, GuideNode target, Guide.Drop drop ) {
		if( drop == Drop.NONE ) return;

		// NOTE This implementation operates directly on the data model which, in
		// turn, causes events that update the guide accordingly.

		// The source and target should have design layers
		DesignLayer sourceLayer = guideNodeLayers.get( source );
		DesignLayer targetLayer = guideNodeLayers.get( target );

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

	Xenon getProgram() {
		return product.getProgram();
	}

	public void link() {
		Design design = tool.getDesign();
		DesignPane pane = tool.getDesignPane();

		// Populate the guide
		design.getAllLayers().forEach( l -> addLayer( l, null ) );

		// Add listeners for changes
		pane.addEventFilter( DesignLayerEvent.LAYER_ADDED, e -> {
			DesignLayerPane l = e.getLayer();
			addLayer( (DesignLayer)DesignShapeView.getDesignData( l ), l );
		} );
		pane.addEventFilter( DesignLayerEvent.LAYER_REMOVED, e -> {
			DesignLayerPane l = e.getLayer();
			removeLayer( (DesignLayer)DesignShapeView.getDesignData( l ), l );
		} );
	}

	@SuppressWarnings( "unchecked" )
	private synchronized void addLayer( DesignLayer layer, DesignLayerPane layerPane ) {
		// This method has to handle two scenarios:
		// 1. When the guide is initially populated there are no layer panes
		// 2. When layers are added or removed there are layer panes associated
		// Also note that the addNode() method is not idempotent
		GuideNode node = layerGuideNodes.computeIfAbsent( layer, k -> {
			GuideNode layerGuideNode = new GuideNode( getProgram(), layer.getId(), layer.getName(), "layer", layer.getOrder() );
			addNode( layerGuideNodes.get( layer.getLayer() ), layerGuideNode );
			guideNodeLayers.put( layerGuideNode, layer );

			EventHandler<NodeEvent> nameHandler = e -> layerGuideNode.setName( layer.getName() );
			EventHandler<NodeEvent> orderHandler = e -> layerGuideNode.setOrder( layer.getOrder() );
			ChangeListener<Boolean> enabledHandler = ( p, o, n ) -> layerGuideNode.setIcon( n ? "layer" : "layer-hidden" );

			layerGuideNode.setValue( NAME_HANDLER, nameHandler );
			layerGuideNode.setValue( ORDER_HANDLER, orderHandler );
			layerGuideNode.setValue( ENABLED_HANDLER, enabledHandler );

			layer.register( DesignLayer.NAME, nameHandler );
			layer.register( DesignLayer.ORDER, orderHandler );
			return layerGuideNode;
		} );

		if( layerPane != null ) {
			String icon = layerPane.isEnabled() ? "layer" : "layer-hidden";
			layerPane.enabledProperty().addListener( (ChangeListener<Boolean>)node.getValue( ENABLED_HANDLER ) );
			node.setIcon( icon );
		}
	}

	@SuppressWarnings( "unchecked" )
	private synchronized void removeLayer( DesignLayer layer, DesignLayerPane layerPane ) {
		layerGuideNodes.computeIfPresent( layer, ( k, layerGuideNode ) -> {
			layerPane.enabledProperty().removeListener( (ChangeListener<Boolean>)layerGuideNode.getValue( ENABLED_HANDLER ) );
			layer.unregister( DesignLayer.ORDER, layerGuideNode.getValue( ORDER_HANDLER ) );
			layer.unregister( DesignLayer.NAME, layerGuideNode.getValue( NAME_HANDLER ) );

			removeNode( layerGuideNode );
			guideNodeLayers.remove( layerGuideNode );
			layerGuideNodes.remove( layer );
			return null;
		} );
	}

}
