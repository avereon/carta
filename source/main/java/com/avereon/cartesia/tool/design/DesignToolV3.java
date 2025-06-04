package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.data.DesignView;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.DesignPortal;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.workpane.ToolException;
import javafx.beans.property.*;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Transform;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DesignToolV3 extends BaseDesignTool {

	private final ObjectProperty<Point3D> viewCenter;

	private final DoubleProperty viewRotate;

	private final DoubleProperty viewZoom;

	private final DoubleProperty viewDpi;

	public DesignToolV3( XenonProgramProduct product, Asset asset ) {
		super( product, asset );

		viewCenter = new SimpleObjectProperty<>( DEFAULT_VIEWPOINT );
		viewRotate = new SimpleDoubleProperty( DEFAULT_ROTATE );
		viewZoom = new SimpleDoubleProperty( DEFAULT_ZOOM );
		viewDpi = new SimpleDoubleProperty( DEFAULT_DPI );

		setCursor( DEFAULT_CURSOR );
	}

	@Override
	protected void ready( OpenAssetRequest request ) throws ToolException {
		super.ready( request );
	}

	@Override
	public Point3D getViewCenter() {
		return viewCenter.get();
	}

	@Override
	public void setViewCenter( Point3D viewCenter ) {
		this.viewCenter.set( Objects.requireNonNull( viewCenter ) );
	}

	public ObjectProperty<Point3D> viewCenterProperty() {
		return viewCenter;
	}

	@Override
	public double getViewRotate() {
		return viewRotate.get();
	}

	@Override
	public void setViewRotate( double rotate ) {
		this.viewRotate.set( rotate );
	}

	public DoubleProperty viewRotateProperty() {
		return viewRotate;
	}

	@Override
	public double getViewZoom() {
		return viewZoom.get();
	}

	@Override
	public void setViewZoom( double viewZoom ) {
		this.viewZoom.set( viewZoom );
	}

	public DoubleProperty viewZoomProperty() {
		return viewZoom;
	}

	@Override
	public void setView( DesignPortal portal ) {
		setView( portal.viewpoint(), portal.rotate(), portal.zoom() );
	}

	@Override
	public void setView( Point3D viewpoint, double zoom ) {
		setView( viewpoint, zoom, getViewRotate() );
	}

	@Override
	public void setView( Point3D viewpoint, double zoom, double rotate ) {
		setViewCenter( viewpoint );
		setViewZoom( zoom );
		setViewRotate( rotate );
	}

	@Override
	public double getDpi() {
		return viewDpi.get();
	}

	@Override
	public void setDpi( double dpi ) {
		viewDpi.set( dpi );
	}

	public DoubleProperty viewDpiProperty() {
		return viewDpi;
	}

	@Override
	@Deprecated
	public Cursor getReticleCursor() {
		return null;
	}

	@Override
	public void setScreenViewport( Bounds viewport ) {

	}

	@Override
	public void setWorldViewport( Bounds viewport ) {

	}

	@Override
	public DesignValue getSelectTolerance() {
		return null;
	}

	@Override
	public void setSelectTolerance( DesignValue aperture ) {

	}

	@Override
	public ObjectProperty<DesignValue> selectTolerance() {
		return null;
	}

	@Override
	public Point3D nearestReferencePoint( Collection<DesignShape> shapes, Point3D point ) {
		return null;
	}

	@Override
	public void setCurrentLayer( DesignLayer layer ) {

	}

	@Override
	public DesignLayer getCurrentLayer() {
		return null;
	}

	@Override
	public ObjectProperty<DesignLayer> currentLayerProperty() {
		return null;
	}

	@Override
	public boolean isLayerVisible( DesignLayer layer ) {
		return false;
	}

	@Override
	public void setLayerVisible( DesignLayer layer, boolean visible ) {

	}

	@Override
	public List<DesignLayer> getVisibleLayers() {
		return List.of();
	}

	@Override
	public DesignLayer getPreviewLayer() {
		return null;
	}

	@Override
	public DesignLayer getReferenceLayer() {
		return null;
	}

	@Override
	public List<DesignShape> getVisibleShapes() {
		return List.of();
	}

	@Override
	public Paint getSelectedDrawPaint() {
		return null;
	}

	@Override
	public Paint getSelectedFillPaint() {
		return null;
	}

	@Override
	public boolean isReferenceLayerVisible() {
		return false;
	}

	@Override
	public void setReferenceLayerVisible( boolean visible ) {

	}

	@Override
	public void setCurrentView( DesignView view ) {

	}

	@Override
	public DesignView getCurrentView() {
		return null;
	}

	@Override
	public ObjectProperty<DesignView> currentViewProperty() {
		return null;
	}

	@Override
	public void zoom( Point3D anchor, double factor ) {

	}

	@Override
	public void pan( Point3D viewAnchor, Point3D dragAnchor, Point3D point ) {

	}

	@Override
	public Point3D scaleScreenToWorld( Point3D point ) {
		return null;
	}

	@Override
	public Point3D scaleWorldToScreen( Point3D point ) {
		return null;
	}

	@Override
	public Point3D screenToWorkplane( Point3D point ) {
		return null;
	}

	@Override
	public Point3D screenToWorkplane( double x, double y, double z ) {
		return null;
	}

	@Override
	public Point3D snapToWorkplane( Point3D point ) {
		return null;
	}

	@Override
	public Point3D snapToWorkplane( double x, double y, double z ) {
		return null;
	}

	@Override
	public Transform getWorldToScreenTransform() {
		return null;
	}

	@Override
	public Point3D worldToScreen( double x, double y, double z ) {
		return null;
	}

	@Override
	public Point3D worldToScreen( Point3D point ) {
		return null;
	}

	@Override
	public Bounds worldToScreen( Bounds bounds ) {
		return null;
	}

	@Override
	public Transform getScreenToWorldTransform() {
		return null;
	}

	@Override
	public Point3D screenToWorld( double x, double y, double z ) {
		return null;
	}

	@Override
	public Point3D screenToWorld( Point3D point ) {
		return null;
	}

	@Override
	public Bounds screenToWorld( Bounds bounds ) {
		return null;
	}

	@Override
	public boolean isGridVisible() {
		return false;
	}

	@Override
	public void setGridVisible( boolean visible ) {

	}

	@Override
	public BooleanProperty gridVisible() {
		return null;
	}

	@Override
	public boolean isGridSnapEnabled() {
		return false;
	}

	@Override
	public void setGridSnapEnabled( boolean enabled ) {

	}

	@Override
	public BooleanProperty gridSnapEnabled() {
		return null;
	}

	@Override
	public void setSelectAperture( Point3D anchor, Point3D mouse ) {

	}

	@Override
	public List<DesignShape> screenPointSyncFindOne( Point3D mouse ) {
		return List.of();
	}

	@Override
	public List<DesignShape> worldPointSyncFindOne( Point3D mouse ) {
		return List.of();
	}

	@Override
	public List<DesignShape> screenPointSyncFindAll( Point3D mouse ) {
		return List.of();
	}

	@Override
	public List<DesignShape> worldPointSyncFindAll( Point3D mouse ) {
		return List.of();
	}

	@Override
	public List<DesignShape> screenPointSyncSelect( Point3D mouse ) {
		return List.of();
	}

	@Override
	public List<DesignShape> worldPointSyncSelect( Point3D mouse ) {
		return List.of();
	}

	@Override
	public void clearSelectedShapes() {

	}

	@Override
	public void screenPointSelect( Point3D mouse ) {

	}

	@Override
	public void screenPointSelect( Point3D mouse, boolean toggle ) {

	}

	@Override
	public void screenWindowSelect( Point3D a, Point3D b, boolean intersect, boolean toggle ) {

	}

	@Override
	public void worldPointSelect( Point3D point ) {

	}

	@Override
	public void worldPointSelect( Point3D point, boolean toggle ) {

	}

	@Override
	public void worldWindowSelect( Point3D a, Point3D b, boolean intersect, boolean toggle ) {

	}

	@Override
	public List<DesignShape> getSelectedShapes() {
		return List.of();
	}

	@Override
	public DesignPortal getPriorPortal() {
		return null;
	}

	@Override
	public void showCommandPrompt() {

	}
}
