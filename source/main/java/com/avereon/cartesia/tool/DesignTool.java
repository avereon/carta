package com.avereon.cartesia.tool;

import com.avereon.cartesia.CartesiaMod;
import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.cursor.Reticle;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.tool.design.DesignRenderer;
import com.avereon.xenon.XenonProgram;
import com.avereon.xenon.asset.Asset;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Transform;

import java.util.Collection;
import java.util.List;

public interface DesignTool extends RenderConstants {

	/**
	 * The default zoom magnification reached by applying zoom in or out the {@link #DEFAULT_ZOOM_STEPS} times.
	 */
	double DEFAULT_ZOOM_MAGNIFICATION = 2.0;

	/**
	 * The number of steps required to reach the {@link #DEFAULT_ZOOM_MAGNIFICATION}.
	 */
	int DEFAULT_ZOOM_STEPS = 4;

	/**
	 * This factor is applied to the zoom when zooming in or out. It is generated by calculating a factor that will increase the zoom by a specific magnification in a specific number of steps.
	 */
	double ZOOM_IN_FACTOR = Math.pow( DEFAULT_ZOOM_MAGNIFICATION, 1.0 / DEFAULT_ZOOM_STEPS );

	double ZOOM_OUT_FACTOR = 1.0 / ZOOM_IN_FACTOR;

	DesignValue DEFAULT_SELECT_TOLERANCE = new DesignValue( 2, DesignUnit.MM );

	Reticle DEFAULT_RETICLE = Reticle.DUPLEX;

	String DEFAULT_APERTURE_DRAW = "#c0c000ff";

	String DEFAULT_APERTURE_FILL = "#c0c00040";

	String DEFAULT_PREVIEW_DRAW = "#ff00c0ff";

	String DEFAULT_PREVIEW_FILL = "#ff00c040";

	String DEFAULT_SELECTED_DRAW = "#ff00c0ff";

	String DEFAULT_SELECTED_FILL = "#ff00c040";

	boolean DEFAULT_GRID_SNAP_ENABLED = true;

	boolean DEFAULT_SHOW_HOTSPOT_ENABLED = false;

	boolean DEFAULT_GRID_VISIBLE = true;

	double CM_PER_INCH = 2.54;

	double INCH_PER_CM = 1 / CM_PER_INCH;

	XenonProgram getProgram();

	CartesiaMod getMod();

	Asset getAsset();

	Design getDesign();

	/**
	 * A convenience method to get the design context.
	 *
	 * @return The design context
	 */
	DesignContext getDesignContext();

	/**
	 * A convenience method to get the command context.
	 *
	 * @return The command context
	 */
	DesignCommandContext getCommandContext();

	Workplane getWorkplane();

	/**
	 * A convenience method to get the workplane grid system.
	 *
	 * @return The workplane grid system
	 */
	Grid getGridSystem();

	/**
	 * A convenience method to set the workplane grid system.
	 *
	 * @param system The grid system
	 */
	void setGridSystem( Grid system );

	Point3D getViewCenter();

	void setViewCenter( Point3D point );

	double getViewRotate();

	void setViewRotate( double angle );

	double getDpi();

	void setDpi( double dpi );

	double getViewZoom();

	void setViewZoom( double viewZoom );

	void setView( DesignPortal portal );

	void setView( Point3D center, double zoom );

	void setView( Point3D center, double zoom, double rotate );

	void setView( DesignView view );

	DesignView createView();

	/**
	 * Get the reticle for this tool.
	 *
	 * @return The reticle for this tool
	 */
	Reticle getReticle();

	/**
	 * Set the reticle for this tool.
	 *
	 * @param reticle The reticle for this tool
	 */
	void setReticle( Reticle reticle );

	/**
	 * Get the reticle cursor for this tool.
	 *
	 * @return The cursor for this tool.
	 */
	Cursor getReticleCursor();

	/**
	 * Set the camera viewport using a screen-based rectangular viewport. The
	 * appropriate center and zoom will be calculated and used to set the view.
	 *
	 * @param viewport The screen viewport
	 */
	@Deprecated
	void setScreenViewport( Bounds viewport );

	/**
	 * Set the camera viewport using a world-based rectangular viewport. The
	 * appropriate center and zoom are calculated and used to set the view.
	 *
	 * @param viewport The world viewport
	 */
	void setWorldViewport( Bounds viewport );

	DesignValue getSelectTolerance();

	void setSelectTolerance( DesignValue aperture );

	/**
	 * The select aperture is a design value (unit and value) for the selection
	 * aperture size. The value is the aperture size as measured on the screen.
	 * The aperture size is generally bound to the mod setting.
	 *
	 * @return The select aperture property
	 */
	ObjectProperty<DesignValue> selectTolerance();

	Point3D nearestReferencePoint( Collection<DesignShape> shapes, Point3D point );

	// What is the difference between selected layer and current layer?
	// We don't think there is a difference other than name.
	// BaseDesignTool even has them linked together.
	DesignLayer getCurrentLayer();

	void setCurrentLayer( DesignLayer layer );

	ObjectProperty<DesignLayer> currentLayerProperty();

	// What is the difference between selected layer and current layer?
	// We don't think there is a difference other than name.
	// BaseDesignTool even has them linked together.
	DesignLayer getSelectedLayer();

	void setSelectedLayer( DesignLayer layer );

	ObjectProperty<DesignLayer> selectedLayerProperty();

	boolean isLayerVisible( DesignLayer layer );

	void setLayerVisible( DesignLayer layer, boolean visible );

	/**
	 * Get a list of the visible layers. The list is ordered the same as the layers in the design.
	 *
	 * @return A list of the visible layers
	 */
	List<DesignLayer> getVisibleLayers();

	void setVisibleLayers( Collection<DesignLayer> layers );

	DesignLayer getPreviewLayer();

	DesignLayer getReferenceLayer();

	List<DesignShape> getVisibleShapes();

	Paint getSelectedDrawPaint();

	Paint getSelectedFillPaint();

	boolean isReferenceLayerVisible();

	void setReferenceLayerVisible( boolean visible );

	// FIXME Setting a DesignView makes sense, but there really is not concept of
	// a current "design view" in the tool. The current view is the workplane
	// center, zoom, rotation, and layers, but does not have a name. A design view
	// could be created from the current view information, but it is not a design
	// view without a name.

	@Deprecated
	DesignView getCurrentView();

	@Deprecated
	void setCurrentView( DesignView view );

	@Deprecated
	ObjectProperty<DesignView> currentViewProperty();

	/**
	 * Change the zoom value by a factor.
	 *
	 * @param anchor The zoom anchor in world coordinates
	 * @param factor The zoom factor
	 */
	void zoom( Point3D anchor, double factor );

	//	/**
	//	 * Pan the view by mouse coordinates.
	//	 *
	//	 * @param viewAnchor The view point location before being dragged (world)
	//	 * @param dragAnchor The drag anchor (screen)
	//	 * @param point The new view point (screen)
	//	 */
	//	void pan( Point3D viewAnchor, Point3D dragAnchor, Point3D point );

	/**
	 * Scale a point from screen size to world size. This transform only applies
	 * the rendering scale to the point, but not the translation or rotation.
	 *
	 * @param point The point to transform
	 * @return The transformed point
	 */
	Point3D scaleScreenToWorld( Point3D point );

	/**
	 * Scale a point from world size to screen size. This transform only applies
	 * the rendering scale to the point, but not the translation or rotation.
	 *
	 * @param point The point to transform
	 * @return The transformed point
	 */
	Point3D scaleWorldToScreen( Point3D point );

	Point3D screenToWorkplane( Point3D point );

	Point3D screenToWorkplane( double x, double y, double z );

	Point3D snapToWorkplane( Point3D point );

	Point3D snapToWorkplane( double x, double y, double z );

	Transform getWorldToScreenTransform();

	Point3D worldToScreen( double x, double y, double z );

	Point3D worldToScreen( Point3D point );

	Bounds worldToScreen( Bounds bounds );

	Transform getScreenToWorldTransform();

	Point3D screenToWorld( double x, double y, double z );

	Point3D screenToWorld( Point3D point );

	Bounds screenToWorld( Bounds bounds );

	/**
	 * Is the workplane grid visible?
	 *
	 * @return True if the grid is visible, false otherwise
	 */
	boolean isGridVisible();

	/**
	 * Set the workplane grid visibility.
	 *
	 * @param visible True to show the grid, false to hide the grid
	 */
	void setGridVisible( boolean visible );

	BooleanProperty gridVisible();

	boolean isGridSnapEnabled();

	void setGridSnapEnabled( boolean enabled );

	BooleanProperty gridSnapEnabled();

	void setSelectAperture( Point3D anchor, Point3D mouse );

	List<DesignShape> screenPointSyncFindOne( Point3D mouse );

	List<DesignShape> worldPointSyncFindOne( Point3D mouse );

	List<DesignShape> screenPointSyncFindAll( Point3D mouse );

	List<DesignShape> worldPointSyncFindAll( Point3D mouse );

	List<DesignShape> screenPointSyncSelect( Point3D mouse );

	List<DesignShape> worldPointSyncSelect( Point3D mouse );

	void clearSelectedShapes();

	void screenPointSelect( Point3D mouse );

	void screenPointSelect( Point3D mouse, boolean toggle );

	void screenWindowSelect( Point3D a, Point3D b, boolean intersect, boolean toggle );

	void worldPointSelect( Point3D point );

	void worldPointSelect( Point3D point, boolean toggle );

	void worldWindowSelect( Point3D a, Point3D b, boolean intersect, boolean toggle );

	List<DesignShape> getSelectedShapes();

	DesignPortal getPriorPortal();

	void showCommandPrompt();

	void setCursor( Cursor cursor );

	DesignRenderer getScreenDesignRenderer();

	Class<? extends DesignRenderer> getPrintDesignRendererClass();
}
