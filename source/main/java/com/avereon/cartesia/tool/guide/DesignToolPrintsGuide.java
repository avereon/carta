package com.avereon.cartesia.tool.guide;

import com.avereon.cartesia.BundleKey;
import com.avereon.cartesia.data.DesignNode;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.product.Rb;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.guide.Guide;
import com.avereon.xenon.tool.guide.GuideNode;
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

}
