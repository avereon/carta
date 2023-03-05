package com.avereon.cartesia.tool;

import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.cursor.ReticleCursor;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.data.DesignView;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.workpane.ToolException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

import java.util.Collection;
import java.util.List;

public class FxRenderDesignTool extends DesignTool {

	public FxRenderDesignTool( ProgramProduct product, Asset asset ) {
		super( product, asset );

		// Settings and settings listeners should go in the ready() method
	}

	@Override
	protected void ready( OpenAssetRequest request ) throws ToolException {
		super.ready( request );

		setTitle( getAsset().getName() );
		setGraphic( getProgram().getIconLibrary().getIcon( getProduct().getCard().getArtifact() ) );

	}

	@Override
	public DesignContext getDesignContext() {
		return null;
	}

	@Override
	public CommandContext getCommandContext() {
		return null;
	}

	@Override
	public CoordinateSystem getCoordinateSystem() {
		return null;
	}

	@Override
	public void setCoordinateSystem( CoordinateSystem system ) {

	}

	@Override
	public DesignWorkplane getWorkplane() {
		return null;
	}

	@Override
	public Point3D getViewPoint() {
		return null;
	}

	@Override
	public void setViewPoint( Point3D point ) {

	}

	@Override
	public double getViewRotate() {
		return 0;
	}

	@Override
	public void setViewRotate( double angle ) {

	}

	@Override
	public double getZoom() {
		return 0;
	}

	@Override
	public void setZoom( double zoom ) {

	}

	@Override
	public ReticleCursor getReticle() {
		return null;
	}

	@Override
	public void setView( DesignPortal portal ) {

	}

	@Override
	public void setView( Point3D center, double zoom ) {

	}

	@Override
	public void setView( Point3D center, double zoom, double rotate ) {

	}

	@Override
	public void setViewport( Bounds viewport ) {

	}

	@Override
	public void setSelectAperture( DesignValue aperture ) {

	}

	@Override
	public DesignValue getSelectAperture() {
		return null;
	}

	@Override
	public ObjectProperty<DesignValue> selectApertureProperty() {
		return null;
	}

	@Override
	public ObservableList<Shape> selectedShapes() {
		return null;
	}

	@Override
	public Shape nearestShape2d( Collection<Shape> shapes, Point3D point ) {
		return null;
	}

	@Override
	public Point3D nearestCp( Collection<Shape> shapes, Point3D point ) {
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
		return null;
	}

	@Override
	public List<Shape> getVisibleShapes() {
		return null;
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
	public void pan( Point3D viewAnchor, Point3D dragAnchor, double x, double y ) {

	}

	@Override
	public Point3D mouseToWorld( Point3D point ) {
		return null;
	}

	@Override
	public Point3D mouseToWorld( double x, double y, double z ) {
		return null;
	}

	@Override
	public Point3D mouseToWorkplane( Point3D point ) {
		return null;
	}

	@Override
	public Point3D mouseToWorkplane( double x, double y, double z ) {
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
	public void updateSelectWindow( Point3D anchor, Point3D mouse ) {

	}

	@Override
	public List<Shape> screenPointFindOneAndWait( Point3D mouse ) {
		return null;
	}

	@Override
	public List<Shape> screenPointFindAllAndWait( Point3D mouse ) {
		return null;
	}

	@Override
	public List<Shape> screenPointSelectAndWait( Point3D mouse ) {
		return null;
	}

	@Override
	public void screenPointSelect( Point3D mouse ) {

	}

	@Override
	public void screenPointSelect( Point3D mouse, boolean toggle ) {

	}

	@Override
	public void mouseWindowSelect( Point3D a, Point3D b, boolean contains ) {

	}

	@Override
	public void worldPointSelect( Point3D point ) {

	}

	@Override
	public void worldPointSelect( Point3D point, boolean toggle ) {

	}

	@Override
	public void clearSelected() {

	}

	@Override
	public List<DesignShape> findShapesWithMouse( Point3D mouse ) {
		return null;
	}

	@Override
	public List<DesignShape> findShapesWithPoint( Point3D point ) {
		return null;
	}

	@Override
	public List<DesignShape> getSelectedGeometry() {
		return null;
	}

	@Override
	public DesignPortal getPriorPortal() {
		return null;
	}

	@Override
	protected void showCommandPrompt() {

	}

}
