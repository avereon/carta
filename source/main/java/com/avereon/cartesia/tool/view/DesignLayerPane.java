package com.avereon.cartesia.tool.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;

/**
 * This is the internal layer that represents the design layer.
 */
@Deprecated
public class DesignLayerPane extends Pane {

	private final BooleanProperty enabled;

	public DesignLayerPane() {
		enabled = new SimpleBooleanProperty( true );
		setVisible( false );
	}

	public boolean isEnabled() {
		return enabled.get();
	}

	public DesignLayerPane setEnabled( boolean enabled ) {
		enabledProperty().set( enabled );
		return this;
	}

	public BooleanProperty enabledProperty() {
		return enabled;
	}

}
