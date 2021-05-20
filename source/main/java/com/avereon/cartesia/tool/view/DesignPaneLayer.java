package com.avereon.cartesia.tool.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;

/**
 * This is the internal layer that represents the design layer.
 */
public class DesignPaneLayer extends Pane {

	private final BooleanProperty showing;

	public DesignPaneLayer() {
		showing = new SimpleBooleanProperty( true );
		setVisible( false );
	}

	public boolean isShowing() {
		return showing != null && showing.get();
	}

	public BooleanProperty showingProperty() {
		return showing;
	}

}
