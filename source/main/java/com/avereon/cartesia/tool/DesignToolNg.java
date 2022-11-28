package com.avereon.cartesia.tool;

import com.avereon.cartesia.CartesiaMod;
import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.cursor.ReticleCursor;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.data.NodeEvent;
import com.avereon.marea.LineCap;
import com.avereon.marea.LineJoin;
import com.avereon.marea.Pen;
import com.avereon.marea.Renderer2d;
import com.avereon.marea.fx.FxRenderer2d;
import com.avereon.marea.geom.*;
import com.avereon.settings.Settings;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.tool.guide.GuidedTool;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.zarra.color.Paints;
import com.avereon.zarra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import lombok.CustomLog;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@CustomLog
public class DesignToolNg extends GuidedTool {

	public static final String RETICLE = "reticle";

	public static final String SELECT_APERTURE_SIZE = "select-aperture-size";

	public static final String SELECT_APERTURE_UNIT = "select-aperture-unit";

	public static final String REFERENCE_POINT_SIZE = "reference-point-size";

	public static final String REFERENCE_POINT_TYPE = "reference-point-type";

	public static final String REFERENCE_POINT_PAINT = "reference-point-paint";

	public static final boolean DEFAULT_GRID_VISIBLE = true;

	public static final boolean DEFAULT_GRID_SNAP_ENABLED = true;

	private static final String SETTINGS_VIEW_ZOOM = "view-zoom";

	private static final String SETTINGS_VIEW_POINT = "view-point";

	private static final String SETTINGS_VIEW_ROTATE = "view-rotate";

	// The renderer
	private final Renderer2d renderer;

	// The workplane
	private final DesignWorkplane workplane;

	private final Map<Class<?>, Consumer<DesignShape>> shapeRenderers;

	// The design model
	private Design design;

	public DesignToolNg( ProgramProduct product, Asset asset ) {
		super( product, asset );
		getStyleClass().add( "design-tool" );

		addStylesheet( CartesiaMod.STYLESHEET );

		Map<Class<?>, Consumer<DesignShape>> renderers = new HashMap<>();
		renderers.put( DesignArc.class, s -> renderArc( (DesignArc)s ) );
		renderers.put( DesignCurve.class, s -> renderCurve( (DesignCurve)s ) );
		renderers.put( DesignEllipse.class, s -> renderEllipse( (DesignEllipse)s ) );
		renderers.put( DesignLine.class, s -> renderLine( (DesignLine)s ) );
		renderers.put( DesignMarker.class, s -> renderMarker( (DesignMarker)s ) );
		renderers.put( DesignPath.class, s -> renderPath( (DesignPath)s ) );
		renderers.put( DesignTextLine.class, s -> renderTextLine( (DesignTextLine)s ) );
		renderers.put( DesignTextArea.class, s -> renderTextArea( (DesignTextArea)s ) );
		shapeRenderers = Collections.unmodifiableMap( renderers );

		// Create and configure the renderer
		this.workplane = new DesignWorkplane();

		// Create and configure the renderer
		double dpi = Screen.getPrimary().getDpi();
		this.renderer = new FxRenderer2d( 960, 540 );
		this.renderer.setZoomFactor( 0.2 );
		this.renderer.setDpi( dpi, dpi );
		this.renderer.setZoom( 10, 10 );
		this.renderer.setViewpoint( 0.5, 0.0 );

		// Force the renderer to always be the tool size
		this.renderer.widthProperty().bind( widthProperty() );
		this.renderer.heightProperty().bind( heightProperty() );

		// Repaint when certain events happen
		this.renderer.widthProperty().addListener( ( p, o, n ) -> render() );
		this.renderer.heightProperty().addListener( ( p, o, n ) -> render() );
		this.renderer.zoomXProperty().addListener( ( p, o, n ) -> render() );
		this.renderer.zoomYProperty().addListener( ( p, o, n ) -> render() );
		this.renderer.viewpointXProperty().addListener( ( p, o, n ) -> render() );
		this.renderer.viewpointYProperty().addListener( ( p, o, n ) -> render() );

		// Add the renderer to the tool
		getChildren().add( (Node)renderer );
	}

	public final DesignWorkplane getWorkplane() {
		return workplane;
	}

	@Override
	protected void ready( OpenAssetRequest request ) throws ToolException {
		super.ready( request );

		setTitle( getAsset().getName() );
		setGraphic( getProgram().getIconLibrary().getIcon( getProduct().getCard().getArtifact() ) );

		design = request.getAsset().getModel();
		design.register( NodeEvent.MODIFIED, ( e ) -> rerender() );

		configureSettingsFromProduct();
		configureSettingsFromTool();
		configureWorkplane();

		rerender();
	}

	private void rerender() {
		Fx.run( this::render );
	}

	private void render() {
		Fx.checkFxThread();

		renderer.clear();

		renderGrid();
		renderShapes();
	}

	private void renderGrid() {
		renderer.draw( new Line( -5, 0, 5, 0 ), new Pen( Color.GRAY ).width( 0.05 ) );
		renderer.draw( new Line( 0, -5, 0, 5 ), new Pen( Color.GRAY ).width( 0.05 ) );
	}

	private void renderShapes() {
		if( design == null ) return;
		design.getAllLayers().stream().flatMap( l -> l.getShapes().stream() ).forEach( s -> shapeRenderers.get( s.getClass() ).accept( s ) );
	}

	private Pen getPen( DesignShape shape ) {
		// FIXME If the pen values change then this cache should be cleared
		// TODO Move this to the shape calcPen method
		Pen pen = shape.getValue( "pen" );
		if( pen == null ) {
			pen = new Pen();
			pen.paint( shape.calcDrawPaint() );
			pen.width( shape.calcDrawWidth() );
			pen.cap( LineCap.valueOf( shape.calcDrawCap().name() ) );
			pen.join( LineJoin.ROUND );
			pen.dashes( shape.calcDrawPattern().stream().mapToDouble( d -> d ).toArray() );
			pen.offset( 0.0 );

			shape.setValue( "pen", pen );
		}
		return pen;
	}

	private void renderArc( DesignArc arc ) {
		renderer.draw( new Arc( arc.getOrigin().getX(), arc.getOrigin().getY(), arc.getXRadius(), arc.getYRadius(), arc.calcRotate(), -arc.getStart(), arc.getExtent() ), getPen( arc ) );
	}

	private void renderCurve( DesignCurve curve ) {
		renderer.draw( new Curve(
			curve.getOrigin().getX(),
			curve.getOrigin().getY(),
			curve.getOriginControl().getX(),
			curve.getOriginControl().getY(),
			curve.getPointControl().getX(),
			curve.getPointControl().getY(),
			curve.getPoint().getX(),
			curve.getPoint().getY()
		), getPen( curve ) );
	}

	private void renderEllipse( DesignEllipse ellipse ) {
		renderer.draw( new Ellipse( ellipse.getOrigin().getX(), ellipse.getOrigin().getY(), ellipse.getXRadius(), ellipse.getYRadius(), ellipse.calcRotate() ), getPen( ellipse ) );
	}

	private void renderLine( DesignLine line ) {
		renderer.draw( new Line( CadPoints.asPoint( line.getOrigin() ), CadPoints.asPoint( line.getPoint() ) ), getPen( line ) );
	}

	private void renderMarker( DesignMarker marker ) {
		DesignPath path = marker.calcType().getPath();
		//		Path path = marker.calcType().getFxPath();
		//		path.setLayoutX( marker.getOrigin().getX() );
		//		path.setLayoutY( marker.getOrigin().getY() );
		//		path.setScaleX( marker.calcSize() );
		//		path.setScaleY( marker.calcSize() );
		// FIXME Markers should also have rotate
		renderer.draw( new Path( marker.getOrigin().getX(), marker.getOrigin().getY(), 0 ), getPen( marker ) );
	}

	private void renderPath( DesignPath path ) {}

	private void renderTextLine( DesignTextLine line ) {}

	private void renderTextArea( DesignTextArea area ) {}

	private void configureWorkplane() {
		// The workplane values are stored in the tool settings
		// However, a set of default workplane values may need to be put in the
		// asset settings because when a tool is closed, the tool settings are deleted.
		DesignWorkplane workplane = getWorkplane();
		Settings settings = getAssetSettings();

		workplane.setCoordinateSystem( CoordinateSystem.valueOf( settings.get( DesignWorkplane.COORDINATE_SYSTEM, DesignWorkplane.DEFAULT_COORDINATE_SYSTEM.name() ).toUpperCase() ) );
		workplane.setOrigin( settings.get( "workpane-origin", DesignWorkplane.DEFAULT_GRID_ORIGIN ) );

		workplane.setGridAxisVisible( settings.get( DesignWorkplane.GRID_AXIS_VISIBLE, Boolean.class, DesignWorkplane.DEFAULT_GRID_AXIS_VISIBILE ) );
		workplane.setGridAxisPaint( Paints.parseWithNullOnException( settings.get( DesignWorkplane.GRID_AXIS_PAINT, DesignWorkplane.DEFAULT_GRID_AXIS_PAINT ) ) );
		workplane.setGridAxisWidth( settings.get( DesignWorkplane.GRID_AXIS_WIDTH, DesignWorkplane.DEFAULT_GRID_AXIS_WIDTH ) );

		workplane.setMajorGridVisible( settings.get( DesignWorkplane.GRID_MAJOR_VISIBLE, Boolean.class, DesignWorkplane.DEFAULT_GRID_MAJOR_VISIBILE ) );
		workplane.setMajorGridX( settings.get( DesignWorkplane.GRID_MAJOR_X, DesignWorkplane.DEFAULT_GRID_MAJOR_SIZE ) );
		workplane.setMajorGridY( settings.get( DesignWorkplane.GRID_MAJOR_Y, DesignWorkplane.DEFAULT_GRID_MAJOR_SIZE ) );
		workplane.setMajorGridZ( settings.get( DesignWorkplane.GRID_MAJOR_Z, DesignWorkplane.DEFAULT_GRID_MAJOR_SIZE ) );
		workplane.setMajorGridPaint( Paints.parseWithNullOnException( settings.get( DesignWorkplane.GRID_MAJOR_PAINT, DesignWorkplane.DEFAULT_GRID_MAJOR_PAINT ) ) );
		workplane.setMajorGridWidth( settings.get( DesignWorkplane.GRID_MAJOR_WIDTH, DesignWorkplane.DEFAULT_GRID_MAJOR_WIDTH ) );

		workplane.setMinorGridVisible( settings.get( DesignWorkplane.GRID_MINOR_VISIBLE, Boolean.class, DesignWorkplane.DEFAULT_GRID_MINOR_VISIBILE ) );
		workplane.setMinorGridX( settings.get( DesignWorkplane.GRID_MINOR_X, DesignWorkplane.DEFAULT_GRID_MINOR_SIZE ) );
		workplane.setMinorGridY( settings.get( DesignWorkplane.GRID_MINOR_Y, DesignWorkplane.DEFAULT_GRID_MINOR_SIZE ) );
		workplane.setMinorGridZ( settings.get( DesignWorkplane.GRID_MINOR_Z, DesignWorkplane.DEFAULT_GRID_MINOR_SIZE ) );
		workplane.setMinorGridPaint( Paints.parseWithNullOnException( settings.get( DesignWorkplane.GRID_MINOR_PAINT, DesignWorkplane.DEFAULT_GRID_MINOR_PAINT ) ) );
		workplane.setMinorGridWidth( settings.get( DesignWorkplane.GRID_MINOR_WIDTH, DesignWorkplane.DEFAULT_GRID_MINOR_WIDTH ) );

		workplane.setSnapGridX( settings.get( DesignWorkplane.GRID_SNAP_X, DesignWorkplane.DEFAULT_GRID_SNAP_SIZE ) );
		workplane.setSnapGridY( settings.get( DesignWorkplane.GRID_SNAP_Y, DesignWorkplane.DEFAULT_GRID_SNAP_SIZE ) );
		workplane.setSnapGridZ( settings.get( DesignWorkplane.GRID_SNAP_Z, DesignWorkplane.DEFAULT_GRID_SNAP_SIZE ) );

		settings.register( DesignWorkplane.COORDINATE_SYSTEM, e -> workplane.setCoordinateSystem( CoordinateSystem.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		settings.register( DesignWorkplane.GRID_ORIGIN, e -> workplane.setOrigin( String.valueOf( e.getNewValue() ) ) );

		settings.register( DesignWorkplane.GRID_AXIS_VISIBLE, e -> workplane.setGridAxisVisible( Boolean.parseBoolean( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( DesignWorkplane.GRID_AXIS_PAINT, e -> workplane.setGridAxisPaint( Paints.parse( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( DesignWorkplane.GRID_AXIS_WIDTH, e -> workplane.setGridAxisWidth( String.valueOf( e.getNewValue() ) ) );

		settings.register( DesignWorkplane.GRID_MAJOR_VISIBLE, e -> workplane.setMajorGridVisible( Boolean.parseBoolean( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( DesignWorkplane.GRID_MAJOR_X, e -> workplane.setMajorGridX( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_MAJOR_Y, e -> workplane.setMajorGridY( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_MAJOR_Z, e -> workplane.setMajorGridZ( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_MAJOR_PAINT, e -> workplane.setMajorGridPaint( Paints.parse( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( DesignWorkplane.GRID_MAJOR_WIDTH, e -> workplane.setMajorGridWidth( String.valueOf( e.getNewValue() ) ) );

		settings.register( DesignWorkplane.GRID_MINOR_VISIBLE, e -> workplane.setMinorGridVisible( Boolean.parseBoolean( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( DesignWorkplane.GRID_MINOR_X, e -> workplane.setMinorGridX( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_MINOR_Y, e -> workplane.setMinorGridY( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_MINOR_Z, e -> workplane.setMinorGridZ( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_MINOR_PAINT, e -> workplane.setMinorGridPaint( Paints.parse( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( DesignWorkplane.GRID_MINOR_WIDTH, e -> workplane.setMinorGridWidth( String.valueOf( e.getNewValue() ) ) );

		settings.register( DesignWorkplane.GRID_SNAP_X, e -> workplane.setSnapGridX( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_SNAP_Y, e -> workplane.setSnapGridY( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_SNAP_Z, e -> workplane.setSnapGridZ( String.valueOf( e.getNewValue() ) ) );

		// Rerender if any workplane values change
		workplane.register( NodeEvent.VALUE_CHANGED, e -> rerender() );
	}

	private void configureSettingsFromProduct() {
		Settings productSettings = getProduct().getSettings();

		String defaultSelectSize = "2";
		String defaultSelectUnit = DesignUnit.CENTIMETER.name().toLowerCase();
		String defaultReferencePointType = DesignMarker.Type.CIRCLE.name().toLowerCase();
		String defaultReferencePointSize = "10";
		String defaultReferencePointPaint = "#808080";
		String defaultReticle = ReticleCursor.DUPLEX.getClass().getSimpleName().toLowerCase();

		double selectApertureSize = Double.parseDouble( productSettings.get( SELECT_APERTURE_SIZE, defaultSelectSize ) );
		DesignUnit selectApertureUnit = DesignUnit.valueOf( productSettings.get( SELECT_APERTURE_UNIT, defaultSelectUnit ).toUpperCase() );
		DesignMarker.Type referencePointType = DesignMarker.Type.valueOf( productSettings.get( REFERENCE_POINT_TYPE, defaultReferencePointType ).toUpperCase() );
		double referencePointSize = Double.parseDouble( productSettings.get( REFERENCE_POINT_SIZE, defaultReferencePointSize ) );
		Paint referencePointPaint = Paints.parse( productSettings.get( REFERENCE_POINT_PAINT, defaultReferencePointPaint ) );

		//		setReticle( ReticleCursor.valueOf( productSettings.get( RETICLE, defaultReticle ) ) );
		//		setSelectAperture( new DesignValue( selectApertureSize, selectApertureUnit ) );
		//		designPane.setReferencePointType( referencePointType );
		//		designPane.setReferencePointSize( referencePointSize );
		//		designPane.setReferencePointPaint( referencePointPaint );

		//		// Settings listeners
		//		productSettings.register( RETICLE, e -> setReticle( ReticleCursor.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		//		productSettings.register( SELECT_APERTURE_SIZE, e -> setSelectAperture( new DesignValue( Double.parseDouble( (String)e.getNewValue() ), getSelectAperture().getUnit() ) ) );
		//		productSettings.register( SELECT_APERTURE_UNIT, e -> setSelectAperture( new DesignValue( getSelectAperture().getValue(), DesignUnit.valueOf( ((String)e.getNewValue()).toUpperCase() ) ) ) );
		//		productSettings.register( REFERENCE_POINT_TYPE, e -> designPane.setReferencePointType( DesignMarker.Type.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		//		productSettings.register( REFERENCE_POINT_SIZE, e -> designPane.setReferencePointSize( Double.parseDouble( (String)e.getNewValue() ) ) );
		//		productSettings.register( REFERENCE_POINT_PAINT, e -> designPane.setReferencePointPaint( Paints.parse( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
	}

	private void configureSettingsFromTool() {
		Settings settings = getSettings();

		Point3D viewpoint = ParseUtil.parsePoint3D( settings.get( SETTINGS_VIEW_POINT, "0,0,0" ) );
		double rotate = Double.parseDouble( settings.get( SETTINGS_VIEW_ROTATE, "0.0" ) );
		double zoom = Double.parseDouble( settings.get( SETTINGS_VIEW_ZOOM, "1.0" ) );

		renderer.setViewpoint( viewpoint.getX(), viewpoint.getY() );
		renderer.setViewRotate( rotate );
		renderer.setZoom( zoom, zoom );
	}

}
