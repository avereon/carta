package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.Workplane;

import java.util.Collection;
import java.util.List;

public interface DesignRenderer extends CommonToolRenderer {

	/**
	 * Retrieves the current {@code Design} instance associated with the renderer.
	 *
	 * @return The {@code Design} instance
	 */
	Design getDesign();

	/**
	 * Associates a {@code Design} instance with the renderer, handling the
	 * registration and unregistration of change listeners. The method updates
	 * the internal state to support rendering consistent with the provided
	 * design. If null, the current design association is removed.
	 *
	 * @param design The {@code Design} instance to associate with the renderer.
	 */
	void setDesign( Design design );

	/**
	 * Retrieves the current {@code Workplane} instance associated with the renderer.
	 *
	 * @return The {@code Workplane} instance
	 */
	Workplane getWorkplane();

	/**
	 * Sets the workplane for the renderer. If a workplane is already associated,
	 * it unregisters the current instance as a listener for workplane events
	 * before associating the new workplane. The method ensures proper event
	 * handling and updates the grid geometry based on the newly assigned
	 * workplane. If null, the current workplane association is removed.
	 *
	 * @param workplane The {@code Workplane} instance to associate with the renderer.
	 *
	 */
	void setWorkplane( Workplane workplane );

	/**
	 * Determines whether the grid is currently visible in the renderer.
	 *
	 * @return true if the grid is visible, false otherwise.
	 */
	boolean isGridVisible();

	/**
	 * Sets the visibility of the grid in the renderer. When the grid is made
	 * visible, the required grid geometry is created and added to the rendering
	 * system. Conversely, when the grid is hidden, its geometry is removed to
	 * optimize rendering performance.
	 *
	 * @param visible True to make the grid visible, false to hide it.
	 */
	void setGridVisible( boolean visible );

	/**
	 * Determines whether the specified design layer is visible within the renderer.
	 *
	 * @param layer The design layer whose visibility is to be checked.
	 * @return True if the layer is visible, false otherwise.
	 */
	boolean isLayerVisible( DesignLayer layer );

	/**
	 * Sets the visibility of a specific design layer in the renderer. When a layer
	 * is made visible, its geometry is created and added to the rendering system.
	 * Conversely, when a layer is hidden, its geometry is removed to optimize
	 * rendering performance.
	 *
	 * @param layer The design layer whose visibility is being set.
	 * @param visible True to make the layer visible, false to make it hidden.
	 */
	void setLayerVisible( DesignLayer layer, boolean visible );

	List<DesignLayer> getVisibleLayers();

	void setVisibleLayers( Collection<DesignLayer> layers );

	/**
	 * Called to request the design be rendered.
	 */
	void render();

	/**
	 * Called to request the design be printed.
	 *
	 * @param factor The scale factor to apply to the design when printing.
	 */
	@Deprecated
	void print( double factor );

}
