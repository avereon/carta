package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.DesignNode;

public class DesignNodeView {

	private final DesignPane pane;

	private final DesignNode node;

	public DesignNodeView( DesignPane pane, DesignNode node ) {
		this.pane = pane;
		this.node = node;
	}

	public DesignPane getPane() {
		return pane;
	}

	public DesignNode getDesignNode() {
		return node;
	}

	void registerListeners() {}

	void unregisterListeners() {}

}
