package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.DesignToolLayersGuide;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.product.Rb;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.tool.guide.Guide;
import com.avereon.xenon.tool.guide.GuideNode;
import javafx.collections.SetChangeListener;
import lombok.CustomLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CustomLog
public class LayersGuide extends Guide {

	public static final String GUIDE_BINDER_ICON = "layers";

	public static final String GUIDE_LAYER_ICON = "layer";

	public static final String GUIDE_LAYER_HIDDEN_ICON = "layer-hidden";

	private static final String NAME_HANDLER = DesignToolLayersGuide.class.getName() + ":name-handler";

	private static final String ORDER_HANDLER = DesignToolLayersGuide.class.getName() + ":order-handler";

	private static final String VISIBLE_HANDLER = DesignToolLayersGuide.class.getName() + ":visible-handler";

	private final XenonProgramProduct product;

	private final FxRenderDesignTool tool;

	private final Map<DesignLayer, GuideNode> layerGuideNodes;

	private final Map<GuideNode, DesignLayer> guideNodeLayers;

	public LayersGuide( XenonProgramProduct product, FxRenderDesignTool tool ) {
		this.product = product;
		this.tool = tool;
		this.layerGuideNodes = new ConcurrentHashMap<>();
		this.guideNodeLayers = new ConcurrentHashMap<>();
		setIcon( GUIDE_BINDER_ICON );
		setTitle( Rb.textOr( RbKey.LABEL, "layers", "Layers" ) );
		setDragAndDropEnabled( true );
	}

	protected Xenon getProgram() {
		return product.getProgram();
	}

	@Override
	protected void moveNode( GuideNode source, GuideNode target, Guide.Drop drop ) {
		if( drop == Drop.NONE ) return;

		// NOTE This implementation operates directly on the data model,
		// which, in turn, causes events that update the guide accordingly.

		// The source and target should have design layers
		DesignLayer sourceLayer = guideNodeLayers.get( source );
		DesignLayer targetLayer = guideNodeLayers.get( target );

		log.atDebug().log( "Move layer %s to %s %s", sourceLayer, targetLayer, drop );

		// Remove the source layer from its current parent
		sourceLayer.getLayer().removeLayer( sourceLayer );

		// Add the source layer to the target layer
		if( drop == Drop.ABOVE || drop == Drop.BELOW ) {
			targetLayer.getLayer().addLayerBeforeOrAfter( sourceLayer, targetLayer, drop == Drop.BELOW );
		} else if( drop == Drop.CHILD ) {
			targetLayer.addLayer( sourceLayer );
		}
	}

	/**
	 * This method is called when the design is ready to be used.
	 *
	 * @param request The open asset request
	 */
	protected void ready( OpenAssetRequest request ) {
		// Create guide nodes for all the design layers
		Design design = request.getAsset().getModel();
		design.getAllLayers().forEach( this::addLayer );

		// NOTE Layer structure changes come from the design
		design.register( NodeEvent.CHILD_ADDED, e -> addLayer( e.getNewValue() ) );
		design.register( NodeEvent.CHILD_REMOVED, e -> removeLayer( e.getOldValue() ) );

		// NOTE Layer name changes come from the design
		// NOTE Layer order changes come from the design

		// NOTE Layer visibility changes come from the tool
		tool.visibleLayers().addListener( (SetChangeListener<DesignLayer>)( change ) -> {
			if( change.wasAdded() ) {
				DesignLayer layer = change.getElementAdded();
				GuideNode node = layerGuideNodes.get( layer );
				node.setIcon( GUIDE_LAYER_ICON );
			} else if( change.wasRemoved() ) {
				DesignLayer layer = change.getElementRemoved();
				GuideNode node = layerGuideNodes.get( layer );
				node.setIcon( GUIDE_LAYER_HIDDEN_ICON );
			}
		} );

		// NOTE Current layer changes come from the tool
		tool.currentLayerProperty().addListener( ( observable, oldValue, newValue ) -> {
			// TODO Change something about the guide node to indicate the current layer
			//if( oldValue != null ) layerGuideNodes.get( oldValue ).setSelected( false );
			//if( newValue != null ) layerGuideNodes.get( newValue ).setSelected( true );
		} );
	}

	private void addLayer( DesignLayer layer ) {
		GuideNode node = new GuideNode( getProgram(), layer.getId(), layer.getName(), GUIDE_LAYER_ICON, layer.getOrder() );
		layerGuideNodes.put( layer, node );
		guideNodeLayers.put( node, layer );
		addNode( layerGuideNodes.get( layer.getLayer() ), node );

		// Create the change handlers
		EventHandler<NodeEvent> nameHandler = node.setValue( NAME_HANDLER, e -> node.setName( layer.getName() ) );
		EventHandler<NodeEvent> orderHandler = node.setValue( ORDER_HANDLER, e -> node.setOrder( layer.getOrder() ) );

		// Register the change handlers
		layer.register( DesignLayer.NAME, nameHandler );
		layer.register( DesignLayer.ORDER, orderHandler );
	}

	private void removeLayer( DesignLayer layer ) {
		// Get the guide node and remove it from the guide
		GuideNode node = layerGuideNodes.remove( layer );
		guideNodeLayers.remove( node );
		removeNode( node );

		// Unregister the change handlers
		layer.unregister( DesignLayer.ORDER, node.getValue( ORDER_HANDLER ) );
		layer.unregister( DesignLayer.NAME, node.getValue( NAME_HANDLER ) );
	}

}
