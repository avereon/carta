package com.avereon.cartesia.tool;

import com.avereon.cartesia.CartesiaMod;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.view.DesignShapeView;
import com.avereon.skill.WritableIdentity;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.tool.guide.GuidedTool;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventTarget;
import javafx.scene.shape.Shape;

/**
 * The design tool is the base class for all design tools.
 */
public abstract class BaseDesignTool extends GuidedTool implements DesignTool, EventTarget, WritableIdentity {

	public static final boolean DEFAULT_GRID_SNAP_ENABLED = true;

	public static final boolean DEFAULT_GRID_VISIBLE = true;

	protected static final String RETICLE = "reticle";

	protected static final String SELECT_APERTURE_SIZE = "select-aperture-size";

	protected static final String SELECT_APERTURE_UNIT = "select-aperture-unit";

	protected static final String REFERENCE_POINT_SIZE = "reference-point-size";

	protected static final String REFERENCE_POINT_TYPE = "reference-point-type";

	protected static final String REFERENCE_POINT_PAINT = "reference-point-paint";

	protected static final String SETTINGS_VIEW_ZOOM = "view-zoom";

	protected static final String SETTINGS_VIEW_POINT = "view-point";

	protected static final String SETTINGS_VIEW_ROTATE = "view-rotate";

	protected static final String CURRENT_LAYER = "current-layer";

	protected static final String CURRENT_VIEW = "current-view";

	protected static final String ENABLED_LAYERS = "enabled-layers";

	protected static final String VISIBLE_LAYERS = "visible-layers";

	protected static final String GRID_VISIBLE = "grid-visible";

	protected static final String GRID_SNAP_ENABLED = "grid-snap";

	// FX properties (what others should be here?)

	// Current:
	// selectAperture
	// currentLayer
	// currentView
	// gridVisible
	// gridSnapEnabled

	// Proposed:
	// viewpoint
	// rotate
	// zoom
	// reticle
	// selectedShapes
	// visibleShapes
	// portal (viewport)

	// LAYERS
	// Reference points
	// Preview
	// Design layers
	// Grid

	private final DesignWorkplane workplane;

	protected BaseDesignTool( XenonProgramProduct product, Asset asset ) {
		super( product, asset );
		addStylesheet( CartesiaMod.STYLESHEET );
		getStyleClass().add( "design-tool" );

		this.workplane = new DesignWorkplane();
	}

	@Override
	public final Design getDesign() {
		return getAssetModel();
	}

	@Override
	public final DesignContext getDesignContext() {
		DesignContext context = getDesign().getDesignContext();
		if( context == null ) context = getDesign().createDesignContext( getProduct() );
		return context;
	}

	@Override
	public final DesignCommandContext getCommandContext() {
		return getDesignContext().getDesignCommandContext();
	}

	@Override
	public final DesignWorkplane getWorkplane() {
		return workplane;
	}

	@Override
	public final Grid getCoordinateSystem() {
		return getWorkplane().getCoordinateSystem();
	}

	@Override
	public final void setCoordinateSystem( Grid system ) {
		getWorkplane().setCoordinateSystem( system );
	}

	@Override
	public DesignLayer getSelectedLayer() {
		return getCurrentLayer();
	}

	@Override
	public void setSelectedLayer( DesignLayer layer ) {
		setCurrentLayer( layer );
	}

	@Override
	public ObjectProperty<DesignLayer> selectedLayerProperty() {
		return currentLayerProperty();
	}

	@Override
	public abstract void showCommandPrompt();

	@Deprecated
	static DesignShape getDesignData( Shape s ) {
		return DesignShapeView.getDesignData( s );
	}

}
