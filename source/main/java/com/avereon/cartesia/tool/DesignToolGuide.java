package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.data.Node;
import com.avereon.data.NodeEvent;
import com.avereon.util.Log;
import com.avereon.xenon.Program;
import com.avereon.xenon.tool.guide.Guide;
import com.avereon.xenon.tool.guide.GuideNode;

import java.util.Map;

public class DesignToolGuide extends Guide {

	private static final System.Logger log = Log.get();

	private final Program program;

	private GuideNode layers;

	private Map<Object, GuideNode> nodes;

	public DesignToolGuide( Program program ) {
		this.program = program;

		layers = new GuideNode( program, "layers", "Layers", "layers" );

		addNode( layers );
	}

	Program getProgram() {
		return program;
	}

	void loadDesign( Design design ) {
		// TODO Go through the design and generate the initial guide
		design.getAllLayers().forEach( this::addLayer );

		design.register( NodeEvent.CHILD_ADDED, this::doChildAddedAction );
		design.register( NodeEvent.CHILD_REMOVED, this::doChildRemovedAction );
	}

	private void doChildAddedAction( NodeEvent event ) {
		Node parent = event.getNode();
		Node child = event.getNewValue();

		if( child instanceof DesignLayer ) {
			log.log( Log.WARN, "A layer was added" );
			addLayer( (DesignLayer)child );
		}
	}

	private void doChildRemovedAction( NodeEvent event ) {
		Node parent = event.getNode();
		Node child = event.getOldValue();

		if( child instanceof DesignLayer ) {
			log.log( Log.WARN, "A layer was removed" );
			removeLayer( (DesignLayer)child );
		}
	}

	public void addLayer( DesignLayer layer ) {
		// TODO Handle sublayers
		GuideNode layerGuideNode = new GuideNode( getProgram(), layer.getId(), layer.getName(), "layer" );
		addNode( layers, layerGuideNode );
	}

	public void removeLayer( DesignLayer layer ) {
		// TODO Handle sublayers
		getNode( layer.getId() );
		removeNode( getNode( layer.getId() ) );
	}

}
