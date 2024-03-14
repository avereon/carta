package com.avereon.cartesia.tool;

import com.avereon.cartesia.CartesiaMod;
import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.cursor.ReticleCursor;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.tool.view.DesignShapeView;
import com.avereon.skill.WritableIdentity;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.tool.guide.GuidedTool;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

import java.util.Collection;
import java.util.List;

/**
 * The design tool is the base class for all design tools.
 */
public abstract class BaseDesignTool extends GuidedTool implements EventTarget, WritableIdentity {

	public static final boolean DEFAULT_GRID_SNAP_ENABLED = true;

	static final String RETICLE = "reticle";

	static final String SELECT_APERTURE_SIZE = "select-aperture-size";

	static final String SELECT_APERTURE_UNIT = "select-aperture-unit";

	static final String REFERENCE_POINT_SIZE = "reference-point-size";

	static final String REFERENCE_POINT_TYPE = "reference-point-type";

	static final String REFERENCE_POINT_PAINT = "reference-point-paint";

	static final boolean DEFAULT_GRID_VISIBLE = true;

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

	public BaseDesignTool( XenonProgramProduct product, Asset asset ) {
		super( product, asset );
		addStylesheet( CartesiaMod.STYLESHEET );
		getStyleClass().add( "design-tool" );

		this.workplane = new DesignWorkplane();
	}

	public final Design getDesign() {
		return getAssetModel();
	}

	/**
	 * A convenience method to get the design context.
	 *
	 * @return The design context
	 */
	public final DesignContext getDesignContext() {
		return getDesign().getDesignContext( getProduct() );
	}

	/**
	 * A convenience method to get the command context.
	 *
	 * @return The command context
	 */
	public final CommandContext getCommandContext() {
		return getDesignContext().getCommandContext();
	}

	public final DesignWorkplane getWorkplane() {
		return workplane;
	}

	/**
	 * A convenience method to get the workplane coordinate system.
	 *
	 * @return The workplane coordinate system
	 */
	public final Grid getCoordinateSystem() {
		return getWorkplane().getCoordinateSystem();
	}

	/**
	 * A convenience method to set the workplane coordinate system.
	 *
	 * @param system The coordinate system
	 */
	public final void setCoordinateSystem( Grid system ) {
		getWorkplane().setCoordinateSystem( system );
	}

	public abstract Point3D getViewPoint();

	public abstract void setViewPoint( Point3D point );

	public abstract double getViewRotate();

	public abstract void setViewRotate( double angle );

	public abstract double getZoom();

	public abstract void setZoom( double zoom );

	public abstract void setView( DesignPortal portal );

	public abstract void setView( Point3D center, double zoom );

	public abstract void setView( Point3D center, double zoom, double rotate );

	public abstract ReticleCursor getReticleCursor();

	/**
	 * Set the camera viewport using a screen-based rectangular viewport. The
	 * appropriate zoom and center will be calculated.
	 *
	 * @param viewport The screen viewport
	 */
	public abstract void setViewport( Bounds viewport );

	public abstract DesignValue getSelectAperture();

	public abstract void setSelectAperture( DesignValue aperture );

	/**
	 * The select aperture is a design value (unit and value) for the selection
	 * aperture size. The value is the aperture size as measured on the screen.
	 * The aperture size is generally bound to the mod setting.
	 *
	 * @return The select aperture property
	 */
	public abstract ObjectProperty<DesignValue> selectApertureProperty();

	public abstract ObservableList<Shape> selectedShapes();

	public abstract Shape nearestShape2d( Collection<Shape> shapes, Point3D point );

	public abstract Point3D nearestCp( Collection<Shape> shapes, Point3D point );

	public abstract void setCurrentLayer( DesignLayer layer );

	public abstract DesignLayer getCurrentLayer();

	public abstract ObjectProperty<DesignLayer> currentLayerProperty();

	public DesignLayer getSelectedLayer() {
		return getCurrentLayer();
	}

	public void setSelectedLayer( DesignLayer layer ) {
		setCurrentLayer( layer );
	}

	public ObjectProperty<DesignLayer> selectedLayerProperty() {
		return currentLayerProperty();
	}

	public abstract boolean isLayerVisible( DesignLayer layer );

	public abstract void setLayerVisible( DesignLayer layer, boolean visible );

	/**
	 * Get a list of the visible layers. The list is ordered the same as the layers in the design.
	 *
	 * @return A list of the visible layers
	 */
	public abstract List<DesignLayer> getVisibleLayers();

	@Deprecated
	// FIXME This really should return design shapes and not FX shapes
	public abstract List<Shape> getVisibleShapes();

	public abstract Paint getSelectedDrawPaint();

	public abstract Paint getSelectedFillPaint();

	public abstract boolean isReferenceLayerVisible();

	public abstract void setReferenceLayerVisible( boolean visible );

	public abstract void setCurrentView( DesignView view );

	public abstract DesignView getCurrentView();

	public abstract ObjectProperty<DesignView> currentViewProperty();

	/**
	 * Change the zoom value by a factor.
	 *
	 * @param anchor The zoom anchor
	 * @param factor The zoom factor
	 */
	public abstract void zoom( Point3D anchor, double factor );

	/**
	 * Pan the view by mouse coordinates.
	 *
	 * @param viewAnchor The view point location before being dragged (world)
	 * @param dragAnchor The drag anchor (screen)
	 * @param x The new X coordinate (screen)
	 * @param y The new Y coordinate (screen)
	 */
	public abstract void pan( Point3D viewAnchor, Point3D dragAnchor, double x, double y );

	public abstract Point3D mouseToWorld( Point3D point );

	public abstract Point3D mouseToWorld( double x, double y, double z );

	public abstract Point3D mouseToWorkplane( Point3D point );

	public abstract Point3D mouseToWorkplane( double x, double y, double z );

	public abstract Point3D worldToScreen( double x, double y, double z );

	public abstract Point3D worldToScreen( Point3D point );

	public abstract Bounds worldToScreen( Bounds bounds );

	public abstract Point3D screenToWorld( double x, double y, double z );

	public abstract Point3D screenToWorld( Point3D point );

	public abstract Bounds screenToWorld( Bounds bounds );

	public abstract boolean isGridVisible();

	public abstract void setGridVisible( boolean visible );

	public abstract BooleanProperty gridVisible();

	public abstract boolean isGridSnapEnabled();

	public abstract void setGridSnapEnabled( boolean enabled );

	public abstract BooleanProperty gridSnapEnabled();

	public abstract void updateSelectWindow( Point3D anchor, Point3D mouse );

	public abstract List<Shape> screenPointFindOneAndWait( Point3D mouse );

	public abstract List<Shape> screenPointFindAllAndWait( Point3D mouse );

	public abstract List<Shape> screenPointSelectAndWait( Point3D mouse );

	public abstract void screenPointSelect( Point3D mouse );

	public abstract void screenPointSelect( Point3D mouse, boolean toggle );

	public abstract void mouseWindowSelect( Point3D a, Point3D b, boolean contains );

	public abstract void worldPointSelect( Point3D point );

	public abstract void worldPointSelect( Point3D point, boolean toggle );

	public abstract void clearSelected();

	public abstract List<DesignShape> findShapesWithMouse( Point3D mouse );

	public abstract List<DesignShape> findShapesWithPoint( Point3D point );

	public abstract List<DesignShape> getSelectedGeometry();

	public abstract DesignPortal getPriorPortal();

	protected abstract void showCommandPrompt();

	static DesignShape getDesignData( Shape s ) {
		return DesignShapeView.getDesignData( s );
	}

}
