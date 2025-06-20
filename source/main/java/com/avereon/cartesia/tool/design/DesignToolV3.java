package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.DesignPortal;
import com.avereon.cartesia.tool.Workplane;
import com.avereon.product.Rb;
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
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Transform;
import javafx.stage.Screen;
import lombok.CustomLog;

import java.util.Collection;
import java.util.List;

@CustomLog
public class DesignToolV3 extends BaseDesignTool {

	/**
	 * The toast label. This is used to display messages to the user.
	 */
	private final Label toast;

	/**
	 * The design tool renderer. This is the main renderer for the design tool.
	 */
	private final DesignRenderer renderer;

	/**
	 * The tool workplane. This is the logical workplane for the tool. A 2D tool
	 * only needs one workplane.
	 */
	private final Workplane workplane;

	public DesignToolV3( XenonProgramProduct product, Asset asset ) {
		super( product, asset );

		// Create the objects
		this.renderer = new DesignToolV3Renderer();
		this.workplane = new Workplane();
		this.toast = new Label( Rb.text( RbKey.LABEL, "loading", asset.getName() ) + " ..." );

		// FIXME These are the development values. They should be changed to the default values
		double scale = Screen.getPrimary().getDpi() / CM_PER_INCH;
		renderer.getTransforms().add( javafx.scene.transform.Transform.scale( scale, -scale ) );

		// Keep the renderer in the center of the tool
		// FIXME This will probably conflict with the renderers view center
		widthProperty().addListener( ( _, _, n ) -> renderer.setTranslateX( 0.5 * n.doubleValue() ) );
		heightProperty().addListener( ( _, _, n ) -> renderer.setTranslateY( 0.5 * n.doubleValue() ) );

		// Test geometry
		Line line1 = new Line( -2, -2, 2, 2 );
		line1.setStroke( javafx.scene.paint.Color.RED.darker().darker() );
		line1.setStrokeWidth( 1 );
		line1.setStrokeLineCap( StrokeLineCap.ROUND );
		Line line2 = new Line( -2, 2, 2, -2 );
		line2.setStroke( javafx.scene.paint.Color.GREEN );
		line2.setStrokeWidth( 1 );
		line2.setStrokeLineCap( StrokeLineCap.ROUND );
		renderer.getChildren().addAll( line1, line2 );

		// Align the toast label to the center of the screen
		StackPane.setAlignment( toast, Pos.CENTER );

		// Initially the renderer is hidden and the toast is shown
		renderer.setVisible( false );
		toast.setVisible( true );

		// Add the components to the parent
		getChildren().addAll( renderer, toast );
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

		renderer.setVisible( true );
		toast.setVisible( false );
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
		/* In previous implementations this method relied on the
		 * renderer.parentToLocal( bounds ) method to convert the bounds from screen
		 * to world coordinates. Should V3 do the same? Maybe not, since there is a
		 * discussion about having an internal scaling strategy to help with pixel
		 * artifacts. If that is the case then this method would need to take that
		 * into account.
		 */
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

	@Override
	public Class<? extends DesignRenderer> getPrintDesignRenderer() {
		return DesignToolV3Renderer.class;
	}

}
