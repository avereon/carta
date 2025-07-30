package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.ShapePropertiesAssetType;
import com.avereon.cartesia.cursor.Reticle;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.data.map.DesignUnitMapper;
import com.avereon.cartesia.data.util.DesignPropertiesMap;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.snap.Snap;
import com.avereon.cartesia.snap.SnapGrid;
import com.avereon.cartesia.tool.*;
import com.avereon.data.IdNode;
import com.avereon.data.MultiNodeSettings;
import com.avereon.data.NodeSettings;
import com.avereon.settings.Settings;
import com.avereon.util.TypeReference;
import com.avereon.xenon.ProgramAction;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetSwitchedEvent;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.task.Task;
import com.avereon.xenon.tool.guide.GuideNode;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.zerra.color.Paints;
import com.avereon.zerra.javafx.Fx;
import com.avereon.zerra.javafx.FxUtil;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Transform;
import lombok.CustomLog;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@CustomLog
public class DesignToolV2 extends BaseDesignTool {

	// DEFAULTS

	// KEYS

	private static final String CURRENT_LAYER = "current-layer";

	// GUIDES

	private final LayersGuide layersGuide;

	//	private final ViewsGuide viewsGuide;

	//	private final PrintsGuide printsGuide;

	// RENDERER

	private static final Snap gridSnap = new SnapGrid();

	private final BaseDesignToolV2Renderer renderer;

	private final Stack<DesignPortal> portalStack;

	private final DesignPropertiesMap designPropertiesMap;

	// TOOL PROPERTIES
	// The renderer might also have some properties that should be exposed

	private final ObjectProperty<DesignValue> selectTolerance;

	// OPTIONAL TOOL PROPERTIES
	// The renderer might also have some properties that should be exposed

	// ACTIONS

	private final Map<String, ProgramAction> commandActions;

	private BooleanProperty gridSnapEnabled;

	private BooleanProperty showHotspotEnabled;

	private ChangeListener<Boolean> gridVisibleToggleHandler;

	private ChangeListener<Boolean> snapGridToggleHandler;

	private com.avereon.event.EventHandler<AssetSwitchedEvent> assetSwitchListener;

	public DesignToolV2( XenonProgramProduct product, Asset asset ) {
		super( product, asset, new BaseDesignToolV2Renderer() );

		commandActions = new ConcurrentHashMap<>();
		designPropertiesMap = new DesignPropertiesMap( product );

		layersGuide = new LayersGuide( product, this );
		//		viewsGuide = new ViewsGuide( product, this );
		//		printsGuide = new PrintsGuide( product, this );

		// Create and associate the workplane and renderer
		renderer = (BaseDesignToolV2Renderer)getRenderer();
		renderer.setApertureDrawPaint( DEFAULT_APERTURE_DRAW );
		renderer.setApertureFillPaint( DEFAULT_APERTURE_FILL );
		renderer.setPreviewDrawPaint( DEFAULT_PREVIEW_DRAW );
		renderer.setPreviewFillPaint( DEFAULT_PREVIEW_FILL );
		renderer.setSelectedDrawPaint( DEFAULT_SELECTED_DRAW );
		renderer.setSelectedFillPaint( DEFAULT_SELECTED_FILL );

		// TODO Move this to tool settings like reticle and aperture
		renderer.getWorkplane().setGridStyle( GridStyle.DOT );

		selectTolerance = new SimpleObjectProperty<>( DEFAULT_SELECT_TOLERANCE );

		portalStack = new Stack<>();

		// NOTE Settings and settings listeners should go in the ready() method
	}

	@Override
	protected void ready( OpenAssetRequest request ) throws ToolException {
		// Don't use the superclass logic
		//super.ready( request );

		setTitle( getAsset().getName() );
		setGraphic( getProgram().getIconLibrary().getIcon( getProduct().getCard().getArtifact() ) );

		getAsset().register( Asset.NAME, e -> setTitle( e.getNewValue() ) );
		getAsset().register( Asset.ICON, e -> setIcon( e.getNewValue() ) );

		// Hide the toast message
		getToast().setVisible( false );

		// Set the renderer design
		renderer.setDesign( getDesign() );

		// Set defaults
		setCurrentLayer( getDesign().getAllLayers().getFirst() );

		// Fire the design ready event (should be done after renderer.setDesign)
		fireEvent( new DesignToolEvent( this, DesignToolEvent.DESIGN_READY ) );

		getAsset().getUndoManager().undoAvailableProperty().addListener( ( v, o, n ) -> getUndoAction().updateEnabled() );
		getAsset().getUndoManager().redoAvailableProperty().addListener( ( v, o, n ) -> getRedoAction().updateEnabled() );

		layersGuide.ready( request );
		//		viewsGuide.ready( request );
		//		printsGuide.ready( request );
		//getGuideContext().getGuides().addAll( layersGuide, viewsGuide, printsGuide );
		getGuideContext().getGuides().addAll( layersGuide );
		getGuideContext().setCurrentGuide( layersGuide );

		//		// Keep the design pane centered when resizing
		//		// These should be added before updating the pan and zoom
		//		widthProperty().addListener( ( p, o, n ) -> Fx.run( designPane::updateView ) );
		//		heightProperty().addListener( ( p, o, n ) -> Fx.run( designPane::updateView ) );
		//
		//		// NOTE What listeners should be registered before configuring things???
		//		// There are two ways of handing the initialization of properties that change
		//		// 1. Initialize the properties from settings, then add listeners afterward.
		//		//    This is safer, but does not allow the listener logic to be triggered.
		//		// 2. Add listeners, then initialize the properties. This approach has the
		//		//    benefit of triggering the listeners, but, depending on the listener,
		//		//    that could be problematic if the listener gets caught in a loop.
		//		//    Another downside to this approach is that several listeners might call
		//		//    the same logic (possibly expensive logic) causing an unnecessary
		//		//    delay.
		//
		// Workplane settings
		// The workplane values are stored in the asset settings
		// However, a set of default workplane values may need to be put in the
		// asset settings because when a tool is closed, the tool settings are deleted.
		configureWorkplane( getWorkplane(), getAssetSettings() );

		Settings productSettings = getProduct().getSettings();
		Settings settings = getSettings();
		String defaultSelectSize = String.valueOf( DEFAULT_SELECT_TOLERANCE.getValue() );
		String defaultSelectUnit = DEFAULT_SELECT_TOLERANCE.getUnit().toString().toLowerCase();
		String defaultReferencePointType = DesignMarker.Type.CIRCLE.name().toLowerCase();
		String defaultReferencePointSize = "10";
		String defaultReferencePointPaint = "#808080";
		String defaultReticle = DEFAULT_RETICLE.name().toLowerCase();

		// Get tool settings
		double selectApertureSize = Double.parseDouble( productSettings.get( SELECT_APERTURE_SIZE, defaultSelectSize ) );
		DesignUnit selectApertureUnit = DesignUnit.valueOf( DesignUnitMapper.mapNameToAbbreviation( productSettings.get( SELECT_APERTURE_UNIT, defaultSelectUnit ) ).toUpperCase() );
		DesignMarker.Type referencePointType = DesignMarker.Type.valueOf( productSettings.get( REFERENCE_POINT_TYPE, defaultReferencePointType ).toUpperCase() );
		double referencePointSize = Double.parseDouble( productSettings.get( REFERENCE_POINT_SIZE, defaultReferencePointSize ) );
		Paint referencePointPaint = Paints.parse( productSettings.get( REFERENCE_POINT_PAINT, defaultReferencePointPaint ) );

		Point3D viewPoint = ParseUtil.parsePoint3D( settings.get( SETTINGS_VIEW_POINT, "0,0,0" ) );
		double viewZoom = Double.parseDouble( settings.get( SETTINGS_VIEW_ZOOM, "1.0" ) );
		double viewRotate = Double.parseDouble( settings.get( SETTINGS_VIEW_ROTATE, "0.0" ) );
		setView( viewPoint, viewZoom, viewRotate );
		setReticle( Reticle.valueOf( productSettings.get( RETICLE, defaultReticle ).toUpperCase() ) );
		setSelectTolerance( new DesignValue( selectApertureSize, selectApertureUnit ) );
		//		designPane.setReferencePointType( referencePointType );
		//		designPane.setReferencePointSize( referencePointSize );
		//		designPane.setReferencePointPaint( referencePointPaint );

		getDesign().findLayers( DesignLayer.ID, settings.get( CURRENT_LAYER, "" ) ).stream().findFirst().ifPresent( this::setCurrentLayer );
		getDesign().findViews( DesignView.ID, settings.get( CURRENT_VIEW, "" ) ).stream().findFirst().ifPresent( this::setCurrentView );

		// Restore the list of enabled layers
		Set<String> enabledLayerIds = settings.get( ENABLED_LAYERS, new TypeReference<>() {}, Set.of() );
		getDesign().getAllLayers().forEach( l -> setLayerEnabled( l, enabledLayerIds.contains( l.getId() ) ) );

		// Restore the list of visible layers
		Set<String> visibleLayerIds = settings.get( VISIBLE_LAYERS, new TypeReference<>() {}, Set.of() );
		getDesign().getAllLayers().forEach( l -> setLayerVisible( l, visibleLayerIds.contains( l.getId() ) ) );

		// Restore the grid visible flag
		setGridVisible( Boolean.parseBoolean( settings.get( GRID_VISIBLE, DEFAULT_GRID_VISIBLE ) ) );

		// Restore the grid snap enabled flag
		setGridSnapEnabled( Boolean.parseBoolean( settings.get( GRID_SNAP_ENABLED, DEFAULT_GRID_SNAP_ENABLED ) ) );

		//		// Restore the reference view visibility
		//		setReferenceLayerVisible( Boolean.parseBoolean( settings.get( REFERENCE_LAYER_VISIBLE, Boolean.TRUE.toString() ) ) );

		// Settings listeners
		productSettings.register( RETICLE, e -> setReticle( Reticle.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		productSettings.register( SELECT_APERTURE_SIZE, e -> setSelectTolerance( new DesignValue( Double.parseDouble( (String)e.getNewValue() ), getSelectTolerance().getUnit() ) ) );
		productSettings.register( SELECT_APERTURE_UNIT, e -> setSelectTolerance( new DesignValue( getSelectTolerance().getValue(), DesignUnit.valueOf( ((String)e.getNewValue()).toUpperCase() ) ) ) );
		//productSettings.register( REFERENCE_POINT_TYPE, e -> designPane.setReferencePointType( DesignMarker.Type.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		//productSettings.register( REFERENCE_POINT_SIZE, e -> designPane.setReferencePointSize( Double.parseDouble( (String)e.getNewValue() ) ) );
		//productSettings.register( REFERENCE_POINT_PAINT, e -> designPane.setReferencePointPaint( Paints.parse( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );

		//		// Add layout bounds property listener
		//		layoutBoundsProperty().addListener( ( p, o, n ) -> doUpdateGridBounds() );

		// Add view point property listener
		renderer.viewCenterXProperty().addListener( ( p, o, n ) -> {
			getStorePreviousViewAction().request();
			Point3D vp = renderer.getViewCenter();
			settings.set( SETTINGS_VIEW_POINT, vp.getX() + "," + vp.getY() + ",0" );
			//doUpdateGridBounds();
		} );
		renderer.viewCenterYProperty().addListener( ( p, o, n ) -> {
			getStorePreviousViewAction().request();
			Point3D vp = renderer.getViewCenter();
			settings.set( SETTINGS_VIEW_POINT, vp.getX() + "," + vp.getY() + ",0" );
			//doUpdateGridBounds();
		} );

		// Add view rotate property listener
		renderer.viewRotateProperty().addListener( ( p, o, n ) -> {
			getStorePreviousViewAction().request();
			settings.set( SETTINGS_VIEW_ROTATE, n.doubleValue() );
			//doUpdateGridBounds();
		} );

		// Add view zoom property listener
		renderer.viewZoomXProperty().addListener( ( p, o, n ) -> {
			getStorePreviousViewAction().request();
			getCoordinateStatus().updateZoom( n.doubleValue() );
			settings.set( SETTINGS_VIEW_ZOOM, n.doubleValue() );
			//doUpdateGridBounds();
		} );
		renderer.viewZoomYProperty().addListener( ( p, o, n ) -> {
			getStorePreviousViewAction().request();
			getCoordinateStatus().updateZoom( n.doubleValue() );
			settings.set( SETTINGS_VIEW_ZOOM, n.doubleValue() );
			//doUpdateGridBounds();
		} );

		// Add enabled layers listener
		enabledLayers().addListener( this::doStoreEnabledLayers );

		// Add visible layers listener
		visibleLayers().addListener( this::doStoreVisibleLayers );

		// Add current layer property listener
		currentLayerProperty().addListener( ( p, o, n ) -> settings.set( CURRENT_LAYER, n.getId() ) );

		// Add selected layer property listener
		selectedLayerProperty().addListener( ( p, o, n ) -> showPropertiesPage( n ) );

		// Add current view property listener
		currentViewProperty().addListener( ( p, o, n ) -> settings.set( CURRENT_VIEW, n.getId() ) );

		// Add grid visible property listener
		gridVisible().addListener( ( p, o, n ) -> settings.set( GRID_VISIBLE, String.valueOf( n ) ) );

		// Add grid visible property listener
		gridSnapEnabled().addListener( ( p, o, n ) -> settings.set( GRID_SNAP_ENABLED, String.valueOf( n ) ) );

		//		// Add reference points visible property listener
		//		designPane.referenceLayerVisible().addListener( ( p, o, n ) -> settings.set( REFERENCE_LAYER_VISIBLE, String.valueOf( n ) ) );

		// Update the design context when the mouse moves
		addEventFilter( MouseEvent.MOUSE_MOVED, e -> getDesignContext().setMouse( e ) );

		getDesignContext().getPreviewShapes().addListener( this::onPreviewShapesChanged );
		getDesignContext().getSelectedShapes().addListener( this::onSelectedShapesChanged );

		// Update the select aperture when the mouse moves
		addEventFilter(
			MouseEvent.MOUSE_MOVED, e -> {
				if( getCommandContext().isEmptyMode() ) {
					setSelectAperture( new Point3D( e.getX(), e.getY(), e.getZ() ), new Point3D( e.getX(), e.getY(), e.getZ() ) );
				}
			}
		);

		//addEventFilter( KeyEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( MouseEvent.ANY, e -> getCommandContext().handle( e ) );
		//addEventFilter( MouseDragEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ScrollEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ZoomEvent.ANY, e -> getCommandContext().handle( e ) );

		getCoordinateStatus().updateZoom( getViewZoom() );
		//		designPane.updateView();
		//		doUpdateGridBounds();

		// Now that all the listeners are set up:

		// TODO Should selected layer be stored in the tool settings?
		if( getSelectedLayer() == null ) setSelectedLayer( getCurrentLayer() );

		// Request the initial geometry render
		getRenderer().setVisible( true );
		renderer.render();
	}

	@Override
	protected void guideNodesSelected( Set<GuideNode> oldNodes, Set<GuideNode> newNodes ) {
		if( getCurrentGuide() == layersGuide ) {
			newNodes.stream().findFirst().ifPresent( n -> doSetSelectedLayerById( n.getId() ) );
			//		} else if( getCurrentGuide() == viewsGuide ) {
			//			newNodes.stream().findFirst().ifPresent( n -> doSetCurrentViewById( n.getId() ) );
			//		} else if( getCurrentGuide() == printsGuide ) {
			//			newNodes.stream().findFirst().ifPresent( n -> doSetCurrentPrintById( n.getId() ) );
		}
	}

	@Override
	protected void guideFocusChanged( boolean focused, Set<GuideNode> nodes ) {}

	@Override
	public Point3D getViewCenter() {
		return renderer.getViewCenter();
	}

	@Override
	public void setViewCenter( Point3D point ) {
		renderer.setViewCenter( point );
	}

	public DoubleProperty viewpointXProperty() {
		return renderer.viewCenterXProperty();
	}

	public DoubleProperty viewpointYProperty() {
		return renderer.viewCenterYProperty();
	}

	@Override
	public double getDpi() {
		return renderer.getDpiX();
	}

	@Override
	public void setDpi( double dpi ) {
		renderer.setDpi( dpi, dpi );
	}

	@Override
	public double getViewZoom() {
		return renderer.getViewZoomX();
	}

	@Override
	public void setViewZoom( double viewZoom ) {
		renderer.setViewZoom( viewZoom );
	}

	@Override
	public double getViewRotate() {
		return renderer.getViewRotate();
	}

	@Override
	public void setViewRotate( double angle ) {
		renderer.setViewRotate( angle );
	}

	@Override
	public void setView( DesignPortal portal ) {
		setView( portal.center(), portal.zoom(), portal.rotate() );
	}

	@Override
	public void setView( Point3D center, double zoom ) {
		setView( center, zoom, getViewRotate() );
	}

	@Override
	public void setView( Point3D center, double zoom, double rotate ) {
		renderer.setViewCenter( center );
		renderer.setViewZoom( zoom );
		renderer.setViewRotate( rotate );
	}

	@Override
	public void setSelectTolerance( DesignValue aperture ) {
		selectTolerance().set( aperture );
	}

	@Override
	public DesignValue getSelectTolerance() {
		return selectTolerance.get();
	}

	@Override
	public ObjectProperty<DesignValue> selectTolerance() {
		return selectTolerance;
	}

	public ObjectProperty<DesignShape> selectAperture() {
		return renderer.selectAperture();
	}

	@Override
	public Point3D nearestReferencePoint( Collection<DesignShape> shapes, Point3D point ) {
		// Use screen coordinates to determine "nearest" since that is what the user sees

		// Convert the world-point to screen-coordinates
		Point3D mouse = worldToScreen( point );

		// Go through all the reference points, convert them to screen coordinates and find the nearest
		double distance;
		double minDistance = Double.MAX_VALUE;
		Point3D nearest = CadPoints.NONE;

		for( DesignShape shape : shapes ) {
			if( shape == null || shape.isPreview() ) continue;

			List<Point3D> referencePoints = shape.getReferencePoints();
			for( Point3D cp : referencePoints ) {
				distance = mouse.distance( worldToScreen( cp ) );
				if( distance < minDistance ) {
					nearest = cp;
					minDistance = distance;
				}
			}
		}

		return nearest;
	}

	public boolean isCurrentLayer( DesignLayer layer ) {
		return getCurrentLayer() == layer;
	}

	@Override
	public boolean isLayerVisible( DesignLayer layer ) {
		return renderer.isLayerVisible( layer );
	}

	@Override
	public void setLayerVisible( DesignLayer layer, boolean visible ) {
		if( layer == null ) {
			log.atError( new NullPointerException( "Specified layer is null" ) ).log( "Cannot set the visibility of a null layer" );
			return;
		}
		if( visible ) {
			if( !renderer.visibleLayers().contains( layer ) ) renderer.visibleLayers().add( layer );
		} else {
			renderer.visibleLayers().remove( layer );
		}
	}

	@Override
	public List<DesignLayer> getVisibleLayers() {
		return new ArrayList<>( renderer.visibleLayers() );
	}

	public ObservableList<DesignLayer> visibleLayers() {
		return renderer.visibleLayers();
	}

	public List<DesignLayer> getEnabledLayers() {
		return new ArrayList<>( renderer.enabledLayers() );
	}

	public ObservableList<DesignLayer> enabledLayers() {
		return renderer.enabledLayers();
	}

	public void setLayerEnabled( DesignLayer layer, boolean visible ) {
		if( visible ) {
			renderer.enabledLayers().add( layer );
		} else {
			renderer.enabledLayers().remove( layer );
		}
	}

	// FIXME Should this be converted to getter/setter?
	@Override
	public DesignLayer getPreviewLayer() {
		return renderer.getPreviewLayer();
	}

	// FIXME Should this be converted to getter/setter?
	@Override
	public DesignLayer getReferenceLayer() {
		return renderer.getReferenceLayer();
	}

	@Override
	public List<DesignShape> getVisibleShapes() {
		return renderer.getVisibleShapes();
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
		Fx.run( () -> renderer.zoom( anchor, factor ) );
	}

	public Point3D scaleScreenToWorld( Point3D point ) {
		// FIXME What happens when the view is rotated
		double scaleX = renderer.getInternalScaleX();
		double scaleY = renderer.getInternalScaleY();
		return new Point3D( point.getX() / scaleX, point.getY() / scaleY, point.getZ() );
	}

	public Point3D scaleWorldToScreen( Point3D point ) {
		// FIXME What happens when the view is rotated
		double scaleX = renderer.getInternalScaleX();
		double scaleY = renderer.getInternalScaleY();
		return new Point3D( point.getX() * scaleX, point.getY() * scaleY, point.getZ() );
	}

	@Override
	public Point3D screenToWorkplane( Point3D point ) {
		return screenToWorkplane( point.getX(), point.getY(), point.getZ() );
	}

	@Override
	public Point3D screenToWorkplane( double x, double y, double z ) {
		Point3D worldPoint = screenToWorld( x, y, z );
		return isGridSnapEnabled() ? gridSnap.snap( this, worldPoint ) : worldPoint;
	}

	@Override
	public Point3D snapToGrid( Point3D point ) {
		return isGridSnapEnabled() ? gridSnap.snap( this, point ) : point;
	}

	@Override
	public Point3D snapToGrid( double x, double y, double z ) {
		return snapToGrid( new Point3D( x, y, z ) );
	}

	@Override
	public Transform getWorldToScreenTransform() {
		return renderer == null ? Fx.IDENTITY_TRANSFORM : renderer.getWorldToScreenTransform();
	}

	@Override
	public Point2D worldToScreen( double x, double y ) {
		return renderer == null ? Point2D.ZERO : renderer.localToParent( x, y );
	}

	@Override
	public Point2D worldToScreen( Point2D point ) {
		return renderer == null ? Point2D.ZERO : renderer.localToParent( point );
	}

	@Override
	public Point3D worldToScreen( double x, double y, double z ) {
		return renderer == null ? Point3D.ZERO : renderer.localToParent( x, y, z );
	}

	@Override
	public Point3D worldToScreen( Point3D point ) {
		return renderer == null ? Point3D.ZERO : renderer.localToParent( point );
	}

	@Override
	public Bounds worldToScreen( Bounds bounds ) {
		return renderer == null ? Fx.EMPTY_BOUNDS : renderer.localToParent( bounds );
	}

	@Override
	public Transform getScreenToWorldTransform() {
		return renderer == null ? Fx.IDENTITY_TRANSFORM : renderer.getScreenToWorldTransform();
	}

	@Override
	public Point2D screenToWorld( double x, double y ) {
		return renderer == null ? Point2D.ZERO : renderer.parentToLocal( x, y );
	}

	@Override
	public Point2D screenToWorld( Point2D point ) {
		return renderer == null ? Point2D.ZERO : renderer.parentToLocal( point );
	}

	@Override
	public Point3D screenToWorld( double x, double y, double z ) {
		return renderer == null ? Point3D.ZERO : renderer.parentToLocal( x, y, z );
	}

	@Override
	public Point3D screenToWorld( Point3D point ) {
		return renderer == null ? Point3D.ZERO : renderer.parentToLocal( point );
	}

	@Override
	public Bounds screenToWorld( Bounds bounds ) {
		return renderer == null ? Fx.EMPTY_BOUNDS : renderer.parentToLocal( bounds );
	}

	@Override
	public boolean isGridVisible() {
		return renderer.isGridVisible();
	}

	@Override
	public void setGridVisible( boolean visible ) {
		renderer.setGridVisible( visible );
	}

	@Override
	public BooleanProperty gridVisible() {
		return renderer.gridVisible();
	}

	@Override
	public boolean isGridSnapEnabled() {
		return gridSnapEnabled == null ? DEFAULT_GRID_SNAP_ENABLED : gridSnapEnabled().get();
	}

	@Override
	public void setGridSnapEnabled( boolean enabled ) {
		gridSnapEnabled().set( enabled );
	}

	@Override
	public BooleanProperty gridSnapEnabled() {
		if( gridSnapEnabled == null ) gridSnapEnabled = new SimpleBooleanProperty( DEFAULT_GRID_SNAP_ENABLED );
		return gridSnapEnabled;
	}

	public boolean isShowHotspotEnabled() {
		return showHotspotEnabled == null ? DEFAULT_SHOW_HOTSPOT_ENABLED : showHotspotEnabled().get();
	}

	public void setShowHotspotEnabled( boolean enabled ) {
		showHotspotEnabled.set( enabled );
	}

	public BooleanProperty showHotspotEnabled() {
		if( showHotspotEnabled == null ) showHotspotEnabled = new SimpleBooleanProperty( DEFAULT_SHOW_HOTSPOT_ENABLED );
		return showHotspotEnabled;
	}

	/**
	 * Set the select aperture window. Points are specified in screen coordinates.
	 *
	 * @param anchor The anchor point
	 * @param mouse The mouse point
	 */
	@Override
	public void setSelectAperture( Point3D anchor, Point3D mouse ) {
		if( anchor == null || mouse == null ) {
			renderer.setSelectAperture( null );
			return;
		}

		// Set the select aperture
		DesignShape selectAperture;
		if( anchor.equals( mouse ) ) {
			if( isShowHotspotEnabled() ) {
				double size = renderer.realToScreen( getSelectTolerance() );
				selectAperture = new DesignEllipse( mouse, size );
			} else {
				selectAperture = null;
			}
		} else {
			Bounds box = FxUtil.bounds( anchor, mouse );
			selectAperture = new DesignBox( box );
		}

		renderer.setSelectAperture( selectAperture );
	}

	@Override
	public List<DesignShape> screenPointSyncFindOne( Point3D mouse ) {
		// This is a finding operation
		return screenPointFind( mouse ).stream().findFirst().stream().collect( Collectors.toList() );
	}

	@Override
	public List<DesignShape> worldPointSyncFindOne( Point3D point ) {
		// This is a finding operation
		return worldPointFind( point ).stream().findFirst().stream().collect( Collectors.toList() );
	}

	@Override
	public List<DesignShape> screenPointSyncFindAll( Point3D mouse ) {
		// This is a finding operation
		return screenPointFind( mouse );
	}

	@Override
	public List<DesignShape> worldPointSyncFindAll( Point3D point ) {
		// This is a finding operation
		return worldPointFind( point );
	}

	@Override
	public List<DesignShape> screenPointSyncSelect( Point3D mouse ) {
		// This is a selecting operation
		screenPointSelect( mouse );
		return new ArrayList<>( getSelectedShapes() );
	}

	@Override
	public List<DesignShape> worldPointSyncSelect( Point3D point ) {
		// This is a selecting operation
		worldPointSelect( point );
		return new ArrayList<>( getSelectedShapes() );
	}

	@Override
	public void screenPointSelect( Point3D mouse ) {
		screenPointSelect( mouse, false );
	}

	@Override
	public void screenPointSelect( Point3D mouse, boolean toggle ) {
		worldPointSelect( renderer.parentToLocal( mouse ), toggle );
	}

	@Override
	public void screenWindowSelect( Point3D a, Point3D b, boolean intersect, boolean toggle ) {
		worldWindowSelect( renderer.parentToLocal( a ), renderer.parentToLocal( b ), intersect, toggle );
	}

	@Override
	public void worldPointSelect( Point3D point ) {
		worldPointSelect( point, false );
	}

	private List<DesignShape> screenPointFind( Point3D mouse ) {
		return worldPointFind( renderer.parentToLocal( mouse ) );
	}

	private List<DesignShape> worldPointFind( Point3D point ) {
		return renderer.worldPointFind( point, getSelectTolerance() );
	}

	@Override
	public void worldPointSelect( Point3D point, boolean toggle ) {
		List<DesignShape> shapes = worldPointFind( point );

		if( shapes.isEmpty() ) {
			setSelectedShapes( shapes, toggle );
		} else {
			setSelectedShapes( List.of( shapes.getFirst() ), toggle );
		}
	}

	public void worldWindowSelect( Point3D a, Point3D b, boolean intersect, boolean toggle ) {
		setSelectedShapes( renderer.worldWindowFind( a, b, intersect ), toggle );
	}

	private void setSelectedShapes( List<DesignShape> shapes, boolean toggle ) {
		ObservableList<DesignShape> selectedShapes = getDesignContext().getSelectedShapes();
		if( toggle ) {
			shapes.forEach( shape -> {
				if( shape.isSelected() ) {
					selectedShapes.remove( shape );
				} else {
					selectedShapes.add( shape );
				}
			} );
		} else {
			selectedShapes.setAll( shapes );
		}
	}

	@Override
	public void clearSelectedShapes() {
		getDesignContext().getSelectedShapes().clear();
	}

	/**
	 * Get a copy of the selected shapes list. The returned list is safe to modify
	 * and will not affect the internal selected shapes list.
	 *
	 * @return A copy of the selected shapes list.
	 */
	@Override
	public List<DesignShape> getSelectedShapes() {
		return new ArrayList<>( getDesignContext().getSelectedShapes() );
	}

	@Override
	public DesignPortal getPriorPortal() {
		// Remove the current portal
		if( !portalStack.isEmpty() ) portalStack.pop();

		// Return the prior portal
		return portalStack.isEmpty() ? DesignPortal.DEFAULT : portalStack.pop();
	}

	private void configureWorkplane( Workplane workplane, Settings settings ) {
		workplane.setGridSystem( Grid.valueOf( settings.get( Workplane.GRID_SYSTEM, Workplane.DEFAULT_GRID_SYSTEM.name() ).toUpperCase() ) );
		workplane.setOrigin( settings.get( Workplane.WORKPANE_ORIGIN, Workplane.DEFAULT_ORIGIN ) );

		workplane.setGridAxisVisible( settings.get( Workplane.GRID_AXIS_VISIBLE, Boolean.class, Workplane.DEFAULT_GRID_AXIS_VISIBLE ) );
		workplane.setGridAxisPaint( settings.get( Workplane.GRID_AXIS_PAINT, Workplane.DEFAULT_GRID_AXIS_PAINT ) );
		workplane.setGridAxisWidth( settings.get( Workplane.GRID_AXIS_WIDTH, Workplane.DEFAULT_GRID_AXIS_WIDTH ) );

		workplane.setMajorGridVisible( settings.get( Workplane.GRID_MAJOR_VISIBLE, Boolean.class, Workplane.DEFAULT_GRID_MAJOR_VISIBLE ) );
		workplane.setMajorGridX( settings.get( Workplane.GRID_MAJOR_X, Workplane.DEFAULT_GRID_MAJOR_SIZE ) );
		workplane.setMajorGridY( settings.get( Workplane.GRID_MAJOR_Y, Workplane.DEFAULT_GRID_MAJOR_SIZE ) );
		workplane.setMajorGridPaint( settings.get( Workplane.GRID_MAJOR_PAINT, Workplane.DEFAULT_GRID_MAJOR_PAINT ) );
		workplane.setMajorGridWidth( settings.get( Workplane.GRID_MAJOR_WIDTH, Workplane.DEFAULT_GRID_MAJOR_WIDTH ) );

		workplane.setMinorGridVisible( settings.get( Workplane.GRID_MINOR_VISIBLE, Boolean.class, Workplane.DEFAULT_GRID_MINOR_VISIBLE ) );
		workplane.setMinorGridX( settings.get( Workplane.GRID_MINOR_X, Workplane.DEFAULT_GRID_MINOR_SIZE ) );
		workplane.setMinorGridY( settings.get( Workplane.GRID_MINOR_Y, Workplane.DEFAULT_GRID_MINOR_SIZE ) );
		workplane.setMinorGridPaint( settings.get( Workplane.GRID_MINOR_PAINT, Workplane.DEFAULT_GRID_MINOR_PAINT ) );
		workplane.setMinorGridWidth( settings.get( Workplane.GRID_MINOR_WIDTH, Workplane.DEFAULT_GRID_MINOR_WIDTH ) );

		workplane.setSnapGridX( settings.get( Workplane.GRID_SNAP_X, Workplane.DEFAULT_GRID_SNAP_SIZE ) );
		workplane.setSnapGridY( settings.get( Workplane.GRID_SNAP_Y, Workplane.DEFAULT_GRID_SNAP_SIZE ) );

		settings.register( Workplane.GRID_SYSTEM, e -> workplane.setGridSystem( Grid.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		settings.register( Workplane.GRID_ORIGIN, e -> workplane.setOrigin( String.valueOf( e.getNewValue() ) ) );

		settings.register( Workplane.GRID_AXIS_VISIBLE, e -> workplane.setGridAxisVisible( Boolean.parseBoolean( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( Workplane.GRID_AXIS_PAINT, e -> workplane.setGridAxisPaint( String.valueOf( e.getNewValue() ) ) );
		settings.register( Workplane.GRID_AXIS_WIDTH, e -> workplane.setGridAxisWidth( String.valueOf( e.getNewValue() ) ) );

		settings.register( Workplane.GRID_MAJOR_VISIBLE, e -> workplane.setMajorGridVisible( Boolean.parseBoolean( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( Workplane.GRID_MAJOR_X, e -> workplane.setMajorGridX( String.valueOf( e.getNewValue() ) ) );
		settings.register( Workplane.GRID_MAJOR_Y, e -> workplane.setMajorGridY( String.valueOf( e.getNewValue() ) ) );
		//settings.register( DesignWorkplane.GRID_MAJOR_Z, e -> workplane.setMajorGridZ( String.valueOf( e.getNewValue() ) ) );
		settings.register( Workplane.GRID_MAJOR_PAINT, e -> workplane.setMajorGridPaint( String.valueOf( e.getNewValue() ) ) );
		settings.register( Workplane.GRID_MAJOR_WIDTH, e -> workplane.setMajorGridWidth( String.valueOf( e.getNewValue() ) ) );

		settings.register( Workplane.GRID_MINOR_VISIBLE, e -> workplane.setMinorGridVisible( Boolean.parseBoolean( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( Workplane.GRID_MINOR_X, e -> workplane.setMinorGridX( String.valueOf( e.getNewValue() ) ) );
		settings.register( Workplane.GRID_MINOR_Y, e -> workplane.setMinorGridY( String.valueOf( e.getNewValue() ) ) );
		//settings.register( DesignWorkplane.GRID_MINOR_Z, e -> workplane.setMinorGridZ( String.valueOf( e.getNewValue() ) ) );
		settings.register( Workplane.GRID_MINOR_PAINT, e -> workplane.setMinorGridPaint( String.valueOf( e.getNewValue() ) ) );
		settings.register( Workplane.GRID_MINOR_WIDTH, e -> workplane.setMinorGridWidth( String.valueOf( e.getNewValue() ) ) );

		settings.register( Workplane.GRID_SNAP_X, e -> workplane.setSnapGridX( String.valueOf( e.getNewValue() ) ) );
		settings.register( Workplane.GRID_SNAP_Y, e -> workplane.setSnapGridY( String.valueOf( e.getNewValue() ) ) );
		//settings.register( DesignWorkplane.GRID_SNAP_Z, e -> workplane.setSnapGridZ( String.valueOf( e.getNewValue() ) ) );

		// Rebuild the grid if any workplane values change
		//workplane.register( NodeEvent.VALUE_CHANGED, e -> rebuildGridAction.request() );
	}

	private void capturePreviousPortal() {
		portalStack.push( new DesignPortal( getViewCenter(), getViewZoom(), getViewRotate() ) );
	}

	public void showCommandPrompt() {
		Fx.run( this::registerStatusBarItems );
		Fx.run( this::requestFocus );
	}

	public BaseDesignRenderer getScreenDesignRenderer() {
		return renderer;
	}

	public Class<? extends BaseDesignRenderer> getPrintDesignRendererClass() {
		return BaseDesignToolV2Renderer.class;
	}

	private List<DesignLayer> getFilteredLayers( Predicate<? super DesignLayer> filter ) {
		return getDesign().getAllLayers().stream().filter( filter ).collect( Collectors.toList() );
	}

	private void doSetSelectedLayerById( String id ) {
		getDesign().findLayerById( id ).ifPresent( this::setSelectedLayer );
		log.atConfig().log( "Selected layer: %s", id );
	}

	private void doSetCurrentLayerById( String id ) {
		getDesign().findLayerById( id ).ifPresent( y -> {
			currentLayerProperty().set( y );
			showPropertiesPage( y );
		} );
	}

	private void doSetCurrentViewById( String id ) {
		getDesign().findViewById( id ).ifPresent( v -> {
			currentViewProperty().set( v );
			//renderer.setView( v.getLayers(), v.getOrigin(), v.getZoom(), v.getRotate() );
			//showPropertiesPage( v );
		} );
	}

	private void doSetCurrentPrintById( String id ) {
		// TODO Implement DesignTool.doSetCurrentPrintById()
	}

	private void doStoreEnabledLayers( ListChangeListener.Change<? extends DesignLayer> c ) {
		c.next();
		getSettings().set( ENABLED_LAYERS, c.getList().stream().map( IdNode::getId ).collect( Collectors.toSet() ) );
	}

	private void doStoreVisibleLayers( ListChangeListener.Change<? extends DesignLayer> c ) {
		c.next();
		getSettings().set( VISIBLE_LAYERS, c.getList().stream().map( IdNode::getId ).collect( Collectors.toSet() ) );
	}

	private void onPreviewShapesChanged( ListChangeListener.Change<? extends DesignShape> c ) {
		// Set the preview property of the shape
		while( c.next() ) {
			c.getRemoved().forEach( s -> s.setSelected( false ) );
			c.getAddedSubList().forEach( s -> s.setSelected( true ) );
		}

		// Request a render
		renderer.render();
	}

	private void onSelectedShapesChanged( ListChangeListener.Change<? extends DesignShape> c ) {
		while( c.next() ) {
			c.getRemoved().forEach( s -> s.setSelected( false ) );
			c.getAddedSubList().forEach( s -> s.setSelected( true ) );

			int size = c.getList().size();

			if( size == 0 ) {
				showPropertiesPage( getSelectedLayer() );
			} else if( size == 1 ) {
				c.getList().stream().findFirst().ifPresent( this::showPropertiesPage );
			} else {
				// If all selected shapes are of the same type then show the properties page for that type
				Class<? extends DesignDrawable> type = c.getList().getFirst().getClass();

				// Otherwise show the general DesignShape properties page
				for( DesignShape shape : c.getList() ) {
					if( shape.getClass() != type ) {
						type = DesignShape.class;
						break;
					}
				}

				showPropertiesPage( new MultiNodeSettings( c.getList() ), type );
			}
		}

		// Request a render
		renderer.render();

		getDeleteAction().updateEnabled();
	}

	private void showPropertiesPage( DesignDrawable drawable ) {
		if( drawable != null ) {
			// Wrap the drawable in a data node settings object
			NodeSettings wrapper = new NodeSettings( drawable );

			// Show the properties page for the drawable
			showPropertiesPage( wrapper, drawable.getClass() );
		}
	}

	private void showPropertiesPage( Settings settings, Class<? extends DesignDrawable> type ) {
		SettingsPage page = designPropertiesMap.getSettingsPage( type );
		if( page != null ) {
			// Switch to a task thread to get the tool
			getProgram().getTaskManager().submit( Task.of( () -> {
				try {
					// Open the tool but don't make it the active tool
					getProgram().getAssetManager().openAsset( ShapePropertiesAssetType.URI, getWorkpane(), true, false ).get();

					// Fire the event on the FX thread
					Fx.run( () -> getWorkspace().getEventBus().dispatch( new ShapePropertiesToolEvent( this, ShapePropertiesToolEvent.SHOW, page, settings ) ) );
				} catch( Exception exception ) {
					log.atWarn( exception ).log();
				}
			} ) );
		} else {
			log.atError().log( "Unable to find properties page for %s", type.getName() );
		}
	}

	private void hidePropertiesPage() {
		getWorkspace().getEventBus().dispatch( new ShapePropertiesToolEvent( this, ShapePropertiesToolEvent.HIDE ) );
	}

}
