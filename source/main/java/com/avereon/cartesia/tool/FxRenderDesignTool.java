package com.avereon.cartesia.tool;

import com.avereon.cartesia.CartesiaMod;
import com.avereon.cartesia.CommandMap;
import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.cursor.Reticle;
import com.avereon.cartesia.cursor.ReticleCursor;
import com.avereon.cartesia.data.*;
import com.avereon.data.NodeSettings;
import com.avereon.settings.Settings;
import com.avereon.util.DelayedAction;
import com.avereon.xenon.ProgramAction;
import com.avereon.xenon.PropertiesToolEvent;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.asset.type.PropertiesType;
import com.avereon.xenon.task.Task;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.xenon.workspace.Workspace;
import com.avereon.zarra.javafx.Fx;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import lombok.CustomLog;

import java.util.Collection;
import java.util.List;

@CustomLog
public class FxRenderDesignTool extends BaseDesignTool {

	// FIXME Possibly rename this to aim?
	public static final Point3D DEFAULT_VIEWPOINT = Point3D.ZERO;

	public static final double DEFAULT_ZOOM = 1.0;

	public static final double DEFAULT_ROTATE = 0.0;

	public static final Reticle DEFAULT_RETICLE = Reticle.CROSSHAIR;

	// RENDERER

	private final Label toast;

	private final DesignRenderer renderer;

	private final DesignWorkplane workplane;

	// PROPERTIES

	private final ObjectProperty<Point3D> viewpointProperty;

	private final DoubleProperty viewZoomProperty;

	private final DoubleProperty viewRotateProperty;

	private final ObjectProperty<Reticle> reticleProperty;

	// ACTIONS

	private final PrintAction printAction;

	private final PropertiesAction propertiesAction;

	private final DeleteAction deleteAction;

	private final UndoAction undoAction;

	private final RedoAction redoAction;

	private final DelayedAction rebuildGridAction;

	public FxRenderDesignTool( XenonProgramProduct product, Asset asset ) {
		super( product, asset );
		addStylesheet( CartesiaMod.STYLESHEET );
		getStyleClass().add( "design-tool" );

		this.renderer = new DesignRenderer();
		this.workplane = new DesignWorkplane();

		viewpointProperty = new SimpleObjectProperty<>( DEFAULT_VIEWPOINT );
		viewZoomProperty = new SimpleDoubleProperty( DEFAULT_ZOOM );
		viewRotateProperty = new SimpleDoubleProperty( DEFAULT_ROTATE );
		reticleProperty = new SimpleObjectProperty<>( DEFAULT_RETICLE );

		this.printAction = new PrintAction( product.getProgram() );
		this.propertiesAction = new PropertiesAction( product.getProgram() );
		this.deleteAction = new DeleteAction( product.getProgram() );
		this.undoAction = new UndoAction( product.getProgram() );
		this.redoAction = new RedoAction( product.getProgram() );

		this.rebuildGridAction = new DelayedAction( getProgram().getTaskManager().getExecutor(), this::doRebuildGrid );
		this.rebuildGridAction.setMinTriggerLimit( 100 );
		this.rebuildGridAction.setMaxTriggerLimit( 500 );

		// Create the toast label
		this.toast = new Label( "Loading..." );

		// Add the components to the parent
		StackPane stack = new StackPane( renderer, toast );
		stack.setAlignment( Pos.CENTER );
		getChildren().addAll( stack );

		// NOTE Settings and settings listeners should go in the ready() method
	}

	@Override
	protected void ready( OpenAssetRequest request ) throws ToolException {
		super.ready( request );

		setTitle( getAsset().getName() );
		setGraphic( getProgram().getIconLibrary().getIcon( getProduct().getCard().getArtifact() ) );

		//		getAsset().getUndoManager().undoAvailableProperty().addListener( ( v, o, n ) -> undoAction.updateEnabled() );
		//		getAsset().getUndoManager().redoAvailableProperty().addListener( ( v, o, n ) -> redoAction.updateEnabled() );
		getAsset().register( Asset.NAME, e -> setTitle( e.getNewValue() ) );
		getAsset().register( Asset.ICON, e -> setIcon( e.getNewValue() ) );

		toast.setVisible( false );

		Design design = request.getAsset().getModel();
		renderer.setDesign( design );

		// FIXME Is this correct? Or should this be in display()
		design.getDesignContext( getProduct() ).getCommandContext().setTool( this );

		//		// Link the guides before loading the design
		//		layersGuide.link();
		//		viewsGuide.link();
		//		printsGuide.link();
		//
		//		Fx.run( () -> {
		//			designPane.setDpi( Screen.getPrimary().getDpi() );
		//			designPane.setDesign( design );
		//		} );
		//
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
		//		// Workplane settings
		//		configureWorkplane();
		//
		//		Settings productSettings = getProduct().getSettings();
		//		Settings settings = getSettings();
		//		String defaultSelectSize = "2";
		//		String defaultSelectUnit = DesignUnit.CENTIMETER.name().toLowerCase();
		//		String defaultReferencePointType = DesignMarker.Type.CIRCLE.name().toLowerCase();
		//		String defaultReferencePointSize = "10";
		//		String defaultReferencePointPaint = "#808080";
		//		String defaultReticle = Reticle.DUPLEX.name().toLowerCase();
		//
		//		// Get tool settings
		//		double selectApertureSize = Double.parseDouble( productSettings.get( SELECT_APERTURE_SIZE, defaultSelectSize ) );
		//		DesignUnit selectApertureUnit = DesignUnit.valueOf( productSettings.get( SELECT_APERTURE_UNIT, defaultSelectUnit ).toUpperCase() );
		//		DesignMarker.Type referencePointType = DesignMarker.Type.valueOf( productSettings.get( REFERENCE_POINT_TYPE, defaultReferencePointType ).toUpperCase() );
		//		double referencePointSize = Double.parseDouble( productSettings.get( REFERENCE_POINT_SIZE, defaultReferencePointSize ) );
		//		Paint referencePointPaint = Paints.parse( productSettings.get( REFERENCE_POINT_PAINT, defaultReferencePointPaint ) );
		//
		//		Point3D viewPoint = ParseUtil.parsePoint3D( settings.get( SETTINGS_VIEW_POINT, "0,0,0" ) );
		//		double viewZoom = Double.parseDouble( settings.get( SETTINGS_VIEW_ZOOM, "1.0" ) );
		//		double viewRotate = Double.parseDouble( settings.get( SETTINGS_VIEW_ROTATE, "0.0" ) );
		//		setView( viewPoint, viewZoom, viewRotate );
		//		setReticle( Reticle.valueOf( productSettings.get( RETICLE, defaultReticle ).toUpperCase() ) );
		//		setSelectAperture( new DesignValue( selectApertureSize, selectApertureUnit ) );
		//		designPane.setReferencePointType( referencePointType );
		//		designPane.setReferencePointSize( referencePointSize );
		//		designPane.setReferencePointPaint( referencePointPaint );
		//
		//		design.findLayers( DesignLayer.ID, settings.get( CURRENT_LAYER, "" ) ).stream().findFirst().ifPresent( this::setCurrentLayer );
		//		design.findViews( DesignView.ID, settings.get( CURRENT_VIEW, "" ) ).stream().findFirst().ifPresent( this::setCurrentView );
		//
		//		// Restore the list of enabled layers
		//		Set<String> enabledLayerIds = settings.get( ENABLED_LAYERS, new TypeReference<>() {}, Set.of() );
		//		design.getAllLayers().forEach( l -> designPane.setLayerEnabled( l, enabledLayerIds.contains( l.getId() ) ) );
		//
		//		// Restore the list of visible layers
		//		Set<String> visibleLayerIds = settings.get( VISIBLE_LAYERS, new TypeReference<>() {}, Set.of() );
		//		design.getAllLayers().forEach( l -> designPane.setLayerVisible( l, visibleLayerIds.contains( l.getId() ) ) );
		//
		//		// Restore the grid visible flag
		//		setGridVisible( Boolean.parseBoolean( settings.get( GRID_VISIBLE, DEFAULT_GRID_VISIBLE ) ) );
		//
		//		// Restore the grid snap enabled flag
		//		setGridSnapEnabled( Boolean.parseBoolean( settings.get( GRID_SNAP_ENABLED, DEFAULT_GRID_SNAP_ENABLED ) ) );
		//
		//		// Restore the reference view visibility
		//		setReferenceLayerVisible( Boolean.parseBoolean( settings.get( REFERENCE_LAYER_VISIBLE, Boolean.TRUE.toString() ) ) );
		//
		//		// Settings listeners
		//		productSettings.register( RETICLE, e -> setReticle( Reticle.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		//		productSettings.register( SELECT_APERTURE_SIZE, e -> setSelectAperture( new DesignValue( Double.parseDouble( (String)e.getNewValue() ), getSelectAperture().getUnit() ) ) );
		//		productSettings.register( SELECT_APERTURE_UNIT, e -> setSelectAperture( new DesignValue( getSelectAperture().getValue(), DesignUnit.valueOf( ((String)e.getNewValue()).toUpperCase() ) ) ) );
		//		productSettings.register( REFERENCE_POINT_TYPE, e -> designPane.setReferencePointType( DesignMarker.Type.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		//		productSettings.register( REFERENCE_POINT_SIZE, e -> designPane.setReferencePointSize( Double.parseDouble( (String)e.getNewValue() ) ) );
		//		productSettings.register( REFERENCE_POINT_PAINT, e -> designPane.setReferencePointPaint( Paints.parse( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		//
		//		// Add layout bounds property listener
		//		layoutBoundsProperty().addListener( ( p, o, n ) -> doUpdateGridBounds() );
		//
		//		// Add view point property listener
		//		designPane.viewPointProperty().addListener( ( p, o, n ) -> {
		//			storePreviousViewAction.request();
		//			settings.set( SETTINGS_VIEW_POINT, n.getX() + "," + n.getY() + "," + n.getZ() );
		//			doUpdateGridBounds();
		//		} );
		//
		//		// Add view rotate property listener
		//		designPane.viewRotateProperty().addListener( ( p, o, n ) -> {
		//			storePreviousViewAction.request();
		//			settings.set( SETTINGS_VIEW_ROTATE, n.doubleValue() );
		//			doUpdateGridBounds();
		//		} );
		//
		//		// Add view zoom property listener
		//		designPane.zoomProperty().addListener( ( p, o, n ) -> {
		//			storePreviousViewAction.request();
		//			getCoordinateStatus().updateZoom( n.doubleValue() );
		//			settings.set( SETTINGS_VIEW_ZOOM, n.doubleValue() );
		//			doUpdateGridBounds();
		//		} );
		//
		//		// Add enabled layers listener
		//		designPane.enabledLayersProperty().addListener( this::doStoreEnabledLayers );
		//
		//		// Add visible layers listener
		//		designPane.visibleLayersProperty().addListener( this::doStoreVisibleLayers );
		//
		//		// Add current layer property listener
		//		currentLayerProperty().addListener( ( p, o, n ) -> settings.set( CURRENT_LAYER, n.getId() ) );
		//
		//		// Add current view property listener
		//		currentViewProperty().addListener( ( p, o, n ) -> settings.set( CURRENT_VIEW, n.getId() ) );
		//
		//		// Add grid visible property listener
		//		gridVisible().addListener( ( p, o, n ) -> settings.set( GRID_VISIBLE, String.valueOf( n ) ) );
		//
		//		// Add grid visible property listener
		//		gridSnapEnabled().addListener( ( p, o, n ) -> settings.set( GRID_SNAP_ENABLED, String.valueOf( n ) ) );
		//
		//		// Add reference points visible property listener
		//		designPane.referenceLayerVisible().addListener( ( p, o, n ) -> settings.set( REFERENCE_LAYER_VISIBLE, String.valueOf( n ) ) );
		//
		//		addEventFilter( MouseEvent.MOUSE_MOVED, e -> getDesignContext().setMouse( e ) );
		//
		//		//addEventFilter( KeyEvent.ANY, e -> getCommandContext().handle( e ) );
		//		addEventFilter( MouseEvent.ANY, e -> getCommandContext().handle( e ) );
		//		addEventFilter( MouseDragEvent.ANY, e -> getCommandContext().handle( e ) );
		//		addEventFilter( ScrollEvent.ANY, e -> getCommandContext().handle( e ) );
		//		addEventFilter( ZoomEvent.ANY, e -> getCommandContext().handle( e ) );
		//
		//		getCoordinateStatus().updateZoom( getZoom() );
		//		designPane.updateView();
		//		doUpdateGridBounds();

		// Get the reticle setting and bind the setting to the reticleProperty

		// Update the cursor if the reticle changes and cursor is currently a reticle
		reticleProperty.addListener( ( p, o, n ) -> {
			if( getCursor() instanceof ReticleCursor ) setCursor( n.getCursor( getProgram() ) );
		} );

		// Request the initial geometry render
		renderer.render();
	}

	@Override
	public Point3D getViewPoint() {
		return viewpointProperty.get();
	}

	@Override
	public void setViewPoint( Point3D point ) {
		viewpointProperty.set( point );
	}

	public ObjectProperty<Point3D> viewpointProperty() {
		return viewpointProperty;
	}

	@Override
	public double getZoom() {
		return viewZoomProperty.get();
	}

	@Override
	public void setZoom( double zoom ) {
		viewZoomProperty.set( zoom );
	}

	public DoubleProperty viewZoomProperty() {
		return viewZoomProperty;
	}

	@Override
	public double getViewRotate() {
		return viewRotateProperty.get();
	}

	@Override
	public void setViewRotate( double angle ) {
		viewRotateProperty.set( angle );
	}

	public DoubleProperty viewRotateProperty() {
		return viewRotateProperty;
	}

	@Override
	public final ReticleCursor getReticleCursor() {
		return getReticle().getCursor( getProgram() );
	}

	private Reticle getReticle() {
		return reticleProperty.get();
	}

	private void setReticle( Reticle reticle ) {
		reticleProperty.set( reticle );
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

	private void doRebuildGrid() {
		if( !isGridVisible() ) return;

		//		getProgram().getTaskManager().submit( Task.of( "Rebuild grid", () -> {
		//			try {
		//				//List<Shape> grid = getCoordinateSystem().getGridLines( getWorkplane() );
		//				List<Shape2d> grid = List.of();
		//				Fx.run( () -> renderer.setGrid( grid ) );
		//			} catch( Exception exception ) {
		//				log.atError().withCause( exception ).log( "Error creating grid" );
		//			}
		//		} ) );
	}

	private class PrintAction extends ProgramAction {

		protected PrintAction( Xenon program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public void handle( ActionEvent event ) {
			getProgram().getTaskManager().submit( new DesignPrintTask( getProgram(), FxRenderDesignTool.this, getAsset(), (DesignPrint)null ) );
			//getProgram().getTaskManager().submit( new DesignAwtPrintTask( getProgram(), FxRenderDesignTool.this, getAsset(), (DesignPrint)null ) );
		}

	}

	private class PropertiesAction extends ProgramAction {

		protected PropertiesAction( Xenon program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public void handle( ActionEvent event ) {
			// Get the settings pages for the asset type
			Asset asset = getAsset();
			SettingsPage designSettingsPage = asset.getType().getSettingsPages().get( "asset" );
			SettingsPage assetSettingsPage = asset.getType().getSettingsPages().get( "grid" );

			Settings designSettings = new NodeSettings( getAsset().getModel() );
			Settings assetSettings = getAssetSettings();

			// Set the settings for the pages
			designSettingsPage.setSettings( designSettings );
			assetSettingsPage.setSettings( assetSettings );

			// Switch to a task thread to get the tool
			getProgram().getTaskManager().submit( Task.of( () -> {
				try {
					// Show the properties tool
					getProgram().getAssetManager().openAsset( PropertiesType.URI ).get();

					// Fire the show request on the workspace event bus
					Workspace workspace = getProgram().getWorkspaceManager().getActiveWorkspace();
					Fx.run( () -> workspace.getEventBus().dispatch( new PropertiesToolEvent( PropertiesAction.this, PropertiesToolEvent.SHOW, designSettingsPage, assetSettingsPage ) ) );
				} catch( Exception exception ) {
					log.atError( exception ).log();
				}
			} ) );
		}

	}

	private class DeleteAction extends ProgramAction {

		protected DeleteAction( Xenon program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return !selectedShapes().isEmpty();
		}

		@Override
		public void handle( ActionEvent event ) {
			getCommandContext().command( CommandMap.getActionCommand( "delete" ).getCommand() );
		}

	}

	private class UndoAction extends ProgramAction {

		protected UndoAction( Xenon program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return getAsset().getUndoManager().isUndoAvailable();
		}

		@Override
		public void handle( ActionEvent event ) {
			getAsset().getUndoManager().undo();
		}

	}

	private class RedoAction extends ProgramAction {

		protected RedoAction( Xenon program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return getAsset().getUndoManager().isRedoAvailable();
		}

		@Override
		public void handle( ActionEvent event ) {
			getAsset().getUndoManager().redo();
		}

	}

}
