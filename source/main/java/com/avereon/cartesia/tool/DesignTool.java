package com.avereon.cartesia.tool;

import com.avereon.cartesia.CartesiaMod;
import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.data.DesignView;
import com.avereon.xenon.XenonProgram;
import com.avereon.xenon.asset.Asset;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

import java.util.Collection;
import java.util.List;

public interface DesignTool {

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

	DesignWorkplane getWorkplane();

	/**
	 * A convenience method to get the workplane coordinate system.
	 *
	 * @return The workplane coordinate system
	 */
	Grid getCoordinateSystem();

	/**
	 * A convenience method to set the workplane coordinate system.
	 *
	 * @param system The coordinate system
	 */
	void setCoordinateSystem( Grid system );

	Point3D getViewPoint();

	void setViewPoint( Point3D point );

	double getViewRotate();

	void setViewRotate( double angle );

	double getZoom();

	void setZoom( double zoom );

	void setView( DesignPortal portal );

	void setView( Point3D center, double zoom );

	void setView( Point3D center, double zoom, double rotate );

	Cursor getReticleCursor();

	/**
	 * Set the camera viewport using a screen-based rectangular viewport. The
	 * appropriate zoom and center will be calculated.
	 *
	 * @param viewport The screen viewport
	 */
	void setScreenViewport( Bounds viewport );

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

	@Deprecated
	ObservableList<Shape> selectedFxShapes();

	@Deprecated
	Point3D nearestCp( Collection<Shape> shapes, Point3D point );

	Point3D nearestReferencePoint( Collection<DesignShape> shapes, Point3D point );

	void setCurrentLayer( DesignLayer layer );

	DesignLayer getCurrentLayer();

	ObjectProperty<DesignLayer> currentLayerProperty();

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

	@Deprecated
		// FIXME This really should return design shapes and not FX shapes
	List<Shape> getVisibleFxShapes();

	List<DesignShape> getVisibleShapes();

	Paint getSelectedDrawPaint();

	Paint getSelectedFillPaint();

	boolean isReferenceLayerVisible();

	void setReferenceLayerVisible( boolean visible );

	void setCurrentView( DesignView view );

	DesignView getCurrentView();

	ObjectProperty<DesignView> currentViewProperty();

	/**
	 * Change the zoom value by a factor.
	 *
	 * @param anchor The zoom anchor in world coordinates
	 * @param factor The zoom factor
	 */
	void zoom( Point3D anchor, double factor );

	/**
	 * Pan the view by mouse coordinates.
	 *
	 * @param viewAnchor The view point location before being dragged (world)
	 * @param dragAnchor The drag anchor (screen)
	 * @param point The new view point (screen)
	 */
	void pan( Point3D viewAnchor, Point3D dragAnchor, Point3D point );

	Point3D scaleScreenToWorld( Point3D point );

	Point3D scaleWorldToScreen( Point3D point );

	Point3D screenToWorkplane( Point3D point );

	Point3D screenToWorkplane( double x, double y, double z );

	Point3D worldToScreen( double x, double y, double z );

	Point3D worldToScreen( Point3D point );

	Bounds worldToScreen( Bounds bounds );

	Point3D screenToWorld( double x, double y, double z );

	Point3D screenToWorld( Point3D point );

	Bounds screenToWorld( Bounds bounds );

	boolean isGridVisible();

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
}
