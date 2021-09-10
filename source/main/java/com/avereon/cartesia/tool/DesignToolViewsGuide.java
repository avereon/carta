package com.avereon.cartesia.tool;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignNode;
import com.avereon.cartesia.data.DesignView;
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
public class DesignToolViewsGuide extends Guide {

	private final ProgramProduct product;

	private final DesignTool tool;

	private final Map<DesignNode, GuideNode> viewNodes;

	private final Map<GuideNode, DesignNode> nodeViews;

	public DesignToolViewsGuide( ProgramProduct product, DesignTool tool ) {
		this.product = product;
		this.tool = tool;
		this.viewNodes = new ConcurrentHashMap<>();
		this.nodeViews = new ConcurrentHashMap<>();
		setTitle( Rb.textOr( BundleKey.LABEL, "views", "Views" ) );
		setIcon( "views" );
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
		design.getViews().forEach( this::addView );

		// Add listeners for changes
		design.register( NodeEvent.CHILD_ADDED, e -> {
			if( Design.VIEWS.equals( e.getSetKey() ) ) Fx.run( () -> addView( e.getNewValue() ) );
		} );
		design.register( NodeEvent.CHILD_REMOVED, e -> {
			if( Design.VIEWS.equals( e.getSetKey() ) ) Fx.run( () -> removeView( e.getOldValue() ) );
		} );
	}

	private void addView( DesignView view ) {
		GuideNode viewGuideNode = new GuideNode( getProgram(), view.getId(), view.getName(), "view", view.getOrder() );
		addNode( getRoot().getValue(), viewGuideNode );
		viewNodes.put( view, viewGuideNode );
		nodeViews.put( viewGuideNode, view );
	}

	private void removeView( DesignView view ) {
		removeNode( viewNodes.get( view ) );
		nodeViews.remove( viewNodes.get( view ) );
		viewNodes.remove( view );
	}

}
