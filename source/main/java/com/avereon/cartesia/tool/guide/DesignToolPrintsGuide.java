package com.avereon.cartesia.tool.guide;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignNode;
import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.data.NodeEvent;
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
public class DesignToolPrintsGuide extends Guide {

	private final ProgramProduct product;

	private final DesignTool tool;

	private final Map<DesignNode, GuideNode> nodes;

	public DesignToolPrintsGuide( ProgramProduct product, DesignTool tool ) {
		this.product = product;
		this.tool = tool;
		this.nodes = new ConcurrentHashMap<>();
		setTitle( Rb.textOr( BundleKey.LABEL, "prints", "Prints" ) );
		setIcon( "prints" );
	}

	ProgramProduct getProduct() {
		return product;
	}

	Program getProgram() {
		return product.getProgram();
	}

	public void link() {
		Design design = tool.getDesign();

		// Populate the guide
		design.getViews().forEach( this::addPrint );

		// Add listeners for changes
		design.register( NodeEvent.CHILD_ADDED, e -> {
			if( e.getSetKey().equals( Design.PRINTS ) ) Fx.run( () -> addPrint( e.getNewValue() ) );
		} );
		design.register( NodeEvent.CHILD_REMOVED, e -> {
			if( e.getSetKey().equals( Design.PRINTS ) ) Fx.run( () -> removePrint( e.getOldValue() ) );
		} );
	}

	private void addPrint( DesignView print ) {
		GuideNode viewGuideNode = new GuideNode( getProgram(), print.getId(), print.getName(), "print", print.getOrder() );
		addNode( getRoot().getValue(), viewGuideNode );
		//viewNodes.put( view, viewGuideNode );
		//nodeViews.put( viewGuideNode, view );
	}

	private void removePrint( DesignView view ) {
		//removeNode( viewNodes.get( view ) );
		//nodeViews.remove( viewNodes.get( view ) );
		//viewNodes.remove( view );
	}

}
