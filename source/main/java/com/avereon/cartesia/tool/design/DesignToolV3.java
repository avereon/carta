package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.DesignPortal;
import com.avereon.cartesia.tool.GridStyle;
import com.avereon.product.Rb;
import com.avereon.xenon.XenonMode;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.workpane.ToolException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Transform;
import javafx.stage.Screen;
import lombok.CustomLog;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@CustomLog
public class DesignToolV3 extends BaseDesignTool {

	/**
	 * The toast label. This is used to display messages to the user.
	 */
	private final Label toast;

	@SuppressWarnings( "unused" )
	public DesignToolV3( XenonProgramProduct product, Asset asset ) {
		this( product, asset, new DesignToolV3Renderer() );
	}

	DesignToolV3( XenonProgramProduct product, Asset asset, DesignRenderer renderer ) {
		super( product, asset, renderer );

		// Create the objects
		this.toast = new Label( Rb.text( RbKey.LABEL, "loading", asset.getName() ) + " ..." );

		// The renderer is configured to render to the primary screen by default,
		// but it can be configured to render to different media just as easily by
		// changing the DPI setting.
		// Should be:
		// Sapphire: 162 @ 1x
		// Graphene: 153 @ 1x
		getRenderer().setDpiX( Screen.getPrimary().getDpi() );
		getRenderer().setDpiY( Screen.getPrimary().getDpi() );

		getRenderer().setOutputScaleX( Screen.getPrimary().getOutputScaleX() );
		getRenderer().setOutputScaleY( Screen.getPrimary().getOutputScaleY() );

		// Align the toast label to the center of the screen
		StackPane.setAlignment( toast, Pos.CENTER );

		// Initially the renderer is hidden and the toast is shown
		toast.setVisible( true );
		getRenderer().setVisible( false );

		// Add the components to the parent
		getChildren().addAll( getRenderer(), toast );
	}

	/**
	 * Called when both the tool and the asset are ready to be used.
	 *
	 * @param request The request to open the asset
	 * @throws ToolException If there is a problem preparing the tool for use
	 */
	@Override
	protected void ready( OpenAssetRequest request ) throws ToolException {
		super.ready( request );

		Design design = request.getAsset().getModel();

		// DEVELOPMENT
		if( Objects.equals( getProgram().getMode(), XenonMode.DEV ) ) design = ExampleDesigns.design1();

		// Show the grid TODO replace with settings eventually
		getRenderer().setGridVisible( true );

		// Set the design model
		getRenderer().setDesign( design );
		// Show the first layer TODO replace with settings eventually
		getRenderer().setLayerVisible( design.getLayers().getLayers().getFirst(), true );

		toast.setVisible( false );
		getRenderer().setVisible( true );

		// Set the workplane settings TODO replace with settings eventually
		getWorkplane().setGridStyle( GridStyle.CROSS );
		getWorkplane().setMinorGridX( "0.2" );
		getWorkplane().setMinorGridY( "0.2" );
		getWorkplane().setBoundaryX1( -10 );
		getWorkplane().setBoundaryX2( 10 );
		getWorkplane().setBoundaryY1( -10 );
		getWorkplane().setBoundaryY2( 10 );
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
	public void zoom( Point3D anchor, double factor ) {

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
		return getRenderer().parentToLocal( bounds );
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

	@Override
	public Class<? extends DesignRenderer> getPrintDesignRendererClass() {
		return DesignToolV3Renderer.class;
	}

}
