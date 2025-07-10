package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.tool.BaseDesignTool;
import com.avereon.cartesia.tool.DesignPortal;
import com.avereon.cartesia.tool.Workplane;
import com.avereon.marea.LineCap;
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
	 * The tool workplane. This is the logical workplane for the tool. A 2D tool
	 * only needs one workplane.
	 */
	private final Workplane workplane;

	public DesignToolV3( XenonProgramProduct product, Asset asset ) {
		super( product, asset, new DesignToolV3Renderer() );

		// Create the objects
		this.toast = new Label( Rb.text( RbKey.LABEL, "loading", asset.getName() ) + " ..." );

		// The renderer is configured to render to the primary screen by default,
		// but it can be configured to render to different media just as easily by
		// changing the DPI setting.
		getRenderer().setDpi( Screen.getPrimary().getDpi() );

		// The tool workplane
		this.workplane = new Workplane();

		// Keep the renderer in the center of the tool
		widthProperty().addListener( ( _, _, n ) -> getRenderer().setTranslateX( 0.5 * n.doubleValue() ) );
		heightProperty().addListener( ( _, _, n ) -> getRenderer().setTranslateY( 0.5 * n.doubleValue() ) );

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

		//getRenderer().setDesign( request.getAsset().getModel() );

		// DEVELOPMENT
		Design testDesign = createTestDesign();
		getRenderer().setDesign( testDesign );
		getRenderer().setLayerVisible( testDesign.getLayers().getLayers().getFirst(), true );

		getRenderer().setVisible( true );
		toast.setVisible( false );
	}

	private Design createTestDesign() {
		Design design = new Design2D();
		design.setName( "Test Design" );

		DesignLine greenLineA = new DesignLine( -5, 5, -3, 3 );
		greenLineA.setDrawPaint( "#008000" );
		greenLineA.setDrawWidth( "1.0" );
		greenLineA.setDrawCap( LineCap.ROUND.name() );
		greenLineA.setOrder( 0 );
		DesignLine greenLineB = new DesignLine( -1, 4, 1, 4 );
		greenLineB.setDrawPaint( "#008000" );
		greenLineB.setDrawWidth( "1.0" );
		greenLineB.setDrawCap( LineCap.ROUND.name() );
		greenLineB.setOrder( 0 );
		DesignLine greenLineC = new DesignLine( 3, 3, 5, 5 );
		greenLineC.setDrawPaint( "#008000" );
		greenLineC.setDrawWidth( "1.0" );
		greenLineC.setDrawCap( LineCap.ROUND.name() );
		greenLineC.setOrder( 0 );

		DesignLine blueLineA = new DesignLine( -5, 3, -3, 1 );
		blueLineA.setDrawPaint( "#000080" );
		blueLineA.setDrawWidth( "1.0" );
		blueLineA.setDrawCap( LineCap.SQUARE.name() );
		blueLineA.setOrder( 0 );
		DesignLine blueLineB = new DesignLine( -1, 2, 1, 2 );
		blueLineB.setDrawPaint( "#000080" );
		blueLineB.setDrawWidth( "1.0" );
		blueLineB.setDrawCap( LineCap.SQUARE.name() );
		blueLineB.setOrder( 0 );
		DesignLine blueLineC = new DesignLine( 3, 1, 5, 3 );
		blueLineC.setDrawPaint( "#000080" );
		blueLineC.setDrawWidth( "1.0" );
		blueLineC.setDrawCap( LineCap.SQUARE.name() );
		blueLineC.setOrder( 0 );

		DesignLine redLineA = new DesignLine( -5, 1, -3, -1 );
		redLineA.setDrawPaint( "#800000" );
		redLineA.setDrawWidth( "1.0" );
		redLineA.setDrawCap( LineCap.BUTT.name() );
		redLineA.setOrder( 1 );
		DesignLine redLineB = new DesignLine( -1, 0, 1, 0 );
		redLineB.setDrawPaint( "#800000" );
		redLineB.setDrawWidth( "1.0" );
		redLineB.setDrawCap( LineCap.BUTT.name() );
		redLineB.setOrder( 1 );
		DesignLine redLineC = new DesignLine( 3, -1, 5, 1 );
		redLineC.setDrawPaint( "#800000" );
		redLineC.setDrawWidth( "1.0" );
		redLineC.setDrawCap( LineCap.BUTT.name() );
		redLineC.setOrder( 1 );

		DesignText hello = new DesignText( new Point3D( -5, -5, 0 ), "Hello" );
		hello.setFillPaint( "#80C0FF" );
		hello.setRotate( -45 );
		DesignText sweet = new DesignText( new Point3D( -1.5, -6, 0 ), "Sweet" );
		sweet.setFillPaint( "#80C0FF" );
		DesignText world = new DesignText( new Point3D( 3, -7, 0 ), "World" );
		world.setFillPaint( "#80C0FF" );
		world.setRotate( 45 );

		DesignLayer construction = new DesignLayer();
		construction.setName( "Construction" );
		construction.addShapes( List.of( greenLineA, greenLineB, greenLineC ) );
		construction.addShapes( List.of( blueLineA, blueLineB, blueLineC ) );
		construction.addShapes( List.of( redLineA, redLineB, redLineC ) );
		construction.addShapes( List.of( hello, sweet, world ) );
		design.getLayers().addLayer( construction );
		return design;
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
	public Class<? extends DesignRenderer> getPrintDesignRendererClass() {
		return DesignToolV3Renderer.class;
	}

}
