package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignShape;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

/**
 * The DesignContext is for sharing design-related information between design
 * tools. It should only be accessed through the design tool and its associated
 * classes.
 * <pre>
 * {@link DesignTool} -> {@link Design} -> {@link DesignContext}
 * </pre>
 */
@Getter
public class DesignContext {

	private final ObservableList<DesignShape> previewShapes;

	private final ObservableList<DesignShape> selectedShapes;

	public DesignContext() {
		this.previewShapes = FXCollections.synchronizedObservableList( FXCollections.observableArrayList() );
		this.selectedShapes = FXCollections.synchronizedObservableList( FXCollections.observableArrayList() );
	}

}
