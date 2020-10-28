package com.avereon.cartesia.tool;

import com.avereon.cartesia.CartesiaMod;
import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.DesignValue;
import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.cursor.ReticleCursor;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.util.Log;
import com.avereon.xenon.Action;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.PropertiesToolEvent;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.tool.guide.GuideNode;
import com.avereon.xenon.tool.guide.GuidedTool;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.zerra.javafx.Fx;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class DesignTool extends GuidedTool {

	public static final String RETICLE = "reticle";

	public static final String SELECT_APERTURE_RADIUS = "select-aperture-radius";

	public static final String SELECT_APERTURE_UNIT = "select-aperture-unit";

	private static final String SETTINGS_ZOOM = "zoom";

	private static final String SETTINGS_PAN = "pan";

	private static final String CURRENT_LAYER = "layer";

	private static final String VISIBLE_LAYERS = "visible-layers";

	private static final System.Logger log = Log.get();

	private final DesignToolLayersGuide layersGuide;

	private final DesignToolViewsGuide viewsGuide;

	private final DesignToolPrintsGuide printsGuide;

	private final DesignPane designPane;

	private final Pane selectPane;

	private final SelectWindow selectWindow;

	private final ObjectProperty<DesignValue> selectTolerance;

	private final ObservableList<Shape> selectedShapes;

	private final ObjectProperty<DesignLayer> currentLayer;

	private final DeleteAction deleteAction;

	private final UndoAction undoAction;

	private final RedoAction redoAction;

	private ReticleCursor reticle;

	private Point3D dragAnchor;

	private Point3D viewAnchor;

	public DesignTool( ProgramProduct product, Asset asset ) {
		super( product, asset );
		getStyleClass().add( "design-tool" );

		addStylesheet( CartesiaMod.STYLESHEET );

		this.layersGuide = new DesignToolLayersGuide( product, this );
		this.viewsGuide = new DesignToolViewsGuide( product, this );
		this.printsGuide = new DesignToolPrintsGuide( product, this );
		getGuideContext().getGuides().addAll( layersGuide, viewsGuide, printsGuide );

		this.designPane = new DesignPane();
		this.selectTolerance = new SimpleObjectProperty<>();
		this.currentLayer = new SimpleObjectProperty<>();

		this.deleteAction = new DeleteAction( product.getProgram() );
		this.undoAction = new UndoAction( product.getProgram() );
		this.redoAction = new RedoAction( product.getProgram() );

		this.selectedShapes = FXCollections.observableArrayList();
		this.selectedShapes.addListener( (ListChangeListener<? super Shape>)this::doSelectShapes );
		this.selectWindow = new SelectWindow();
		this.selectWindow.getStyleClass().add( "select" );
		this.selectPane = new Pane();
		this.selectPane.getChildren().addAll( selectWindow );

		getChildren().addAll( designPane, selectPane );

		// Initial values from settings
		setReticle( ReticleCursor.valueOf( product.getSettings().get( RETICLE, ReticleCursor.DUPLEX.getClass().getSimpleName() ).toUpperCase() ) );
		double selectApertureRadius = Double.parseDouble( product.getSettings().get( SELECT_APERTURE_RADIUS, "1.0" ) );
		DesignUnit selectApertureUnit = DesignUnit.valueOf( product.getSettings().get( SELECT_APERTURE_UNIT, DesignUnit.MILLIMETER.name() ).toUpperCase() );
		setSelectTolerance( new DesignValue( selectApertureRadius, selectApertureUnit ) );

		// Settings listeners
		product.getSettings().register( RETICLE, e -> setReticle( ReticleCursor.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );
		product
			.getSettings()
			.register( SELECT_APERTURE_RADIUS, e -> setSelectTolerance( new DesignValue( Double.parseDouble( (String)e.getNewValue() ), selectApertureUnit ) ) );
		product
			.getSettings()
			.register( SELECT_APERTURE_UNIT,
				e -> setSelectTolerance( new DesignValue( selectApertureRadius, DesignUnit.valueOf( ((String)e.getNewValue()).toUpperCase() ) ) )
			);
	}

	public final Design getDesign() {
		return getAssetModel();
	}

	public final DesignContext getDesignContext() {
		return getDesign().getDesignContext( getProduct() );
	}

	public final CommandContext getCommandContext() {
		return getDesignContext().getCommandContext();
	}

	@Deprecated
	public CommandPrompt getCommandPrompt() {
		return getDesignContext().getCommandPrompt();
	}

	public Point3D getPan() {
		return designPane == null ? Point3D.ZERO : designPane.getViewPoint();
	}

	public void setPan( Point3D point ) {
		if( designPane != null ) designPane.setViewPoint( point );
	}

	public double getZoom() {
		return designPane == null ? 1.0 : designPane.getZoom();
	}

	public void setZoom( double zoom ) {
		if( designPane != null ) designPane.setZoom( zoom );
	}

	public ReticleCursor getReticle() {
		return reticle;
	}

	public void setSelectTolerance( DesignValue tolerance ) {
		selectTolerance.set( tolerance );
	}

	public DesignValue getSelectTolerance() {
		return selectTolerance.get();
	}

	public ObjectProperty<DesignValue> selectToleranceProperty() {
		return selectTolerance;
	}

	public ObservableList<Shape> selectedShapes() {
		return selectedShapes;
	}

	public void setCurrentLayer( DesignLayer layer ) {
		currentLayer.set( layer );
	}

	public DesignLayer getCurrentLayer() {
		return currentLayer.get();
	}

	public ObjectProperty<DesignLayer> currentLayerProperty() {
		return currentLayer;
	}

	public boolean isLayerVisible( DesignLayer layer ) {
		return designPane.isLayerVisible( layer );
	}

	public void setLayerVisible( DesignLayer layer, boolean visible ) {
		designPane.setLayerVisible( layer, visible );
	}

	public List<Shape> getVisibleShapes() {
		return designPane.getVisibleShapes();
	}

	public Point3D getViewPoint() {
		return designPane.getViewPoint();
	}

	/**
	 * Change the zoom value by a factor.
	 *
	 * @param anchor
	 * @param factor
	 */
	public void zoom( Point3D anchor, double factor ) {
		Fx.run( () -> designPane.zoom( anchor, factor ) );
	}

	public Point3D mouseToWorld( double x, double y, double z ) {
		return designPane == null ? Point3D.ZERO : designPane.parentToLocal( x, y, z );
	}

	public Point3D mouseToWorld( Point3D point ) {
		return designPane == null ? Point3D.ZERO : designPane.parentToLocal( point );
	}

	public Point3D worldToMouse( double x, double y, double z ) {
		return designPane == null ? Point3D.ZERO : designPane.localToParent( x, y, z );
	}

	public Point3D worldToMouse( Point3D point ) {
		return designPane == null ? Point3D.ZERO : designPane.localToParent( point );
	}

	public void setPreview( Node preview ) {
		designPane.addPreview( preview );
	}

	public void clearPreview() {
		designPane.clearPreview();
	}

	@Override
	protected void ready( OpenAssetRequest request ) throws ToolException {
		super.ready( request );

		setTitle( getAsset().getName() );
		setGraphic( getProgram().getIconLibrary().getIcon( getProduct().getCard().getArtifact() ) );

		getAsset().getUndoManager().undoAvailableProperty().addListener( ( v, o, n ) -> undoAction.updateEnabled() );
		getAsset().getUndoManager().redoAvailableProperty().addListener( ( v, o, n ) -> redoAction.updateEnabled() );
		getAsset().register( Asset.NAME, e -> setTitle( e.getNewValue() ) );
		getAsset().register( Asset.ICON, e -> setIcon( e.getNewValue() ) );

		Design design = request.getAsset().getModel();
		designPane.loadDesign( design );
		designPane.setDpi( Screen.getPrimary().getDpi() );
		layersGuide.link( designPane );
		//viewsGuide.init( design );
		//printsGuide.init( design );

		// Keep the design pane centered when resizing
		widthProperty().addListener( ( p, o, n ) -> designPane.recenter() );
		heightProperty().addListener( ( p, o, n ) -> designPane.recenter() );

		// Get tool settings
		setPan( ParseUtil.parsePoint3D( getSettings().get( SETTINGS_PAN, "0,0,0" ) ) );
		setZoom( Double.parseDouble( getSettings().get( SETTINGS_ZOOM, "1.0" ) ) );
		design.findLayers( DesignLayer.ID, getSettings().get( CURRENT_LAYER ) ).stream().findFirst().ifPresent( this::setCurrentLayer );

		// Add pan property listener
		designPane.viewPointProperty().addListener( ( p, o, n ) -> {
			getSettings().set( SETTINGS_PAN, n.getX() + "," + n.getY() + "," + n.getZ() );
		} );

		// Add zoom property listener
		designPane.zoomProperty().addListener( ( p, o, n ) -> {
			getCoordinateStatus().updateZoom( n.doubleValue() );
			getSettings().set( SETTINGS_ZOOM, n.doubleValue() );
		} );

		// Add current layer property listener
		currentLayerProperty().addListener( ( p, o, n ) -> {
			getSettings().set( CURRENT_LAYER, n.getId() );
		} );

		addEventFilter( MouseEvent.MOUSE_MOVED, this::mouseMove );
		addEventFilter( MouseEvent.MOUSE_PRESSED, this::mousePress );
		addEventFilter( MouseEvent.MOUSE_DRAGGED, this::mouseDrag );
		addEventFilter( MouseEvent.MOUSE_RELEASED, this::mouseRelease );

		addEventFilter( MouseEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( MouseDragEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ScrollEvent.ANY, e -> getCommandContext().handle( e ) );
		addEventFilter( ZoomEvent.ANY, e -> getCommandContext().handle( e ) );

		designPane.recenter();

		if( isActive() ) activate();
	}

	@Override
	protected void guideNodesSelected( Set<GuideNode> oldNodes, Set<GuideNode> newNodes ) {
		newNodes.stream().findFirst().ifPresent( n -> doSetCurrentLayerById( n.getId() ) );
	}

	@Override
	protected void activate() throws ToolException {
		super.activate();
		registerActions();
		if( isReady() ) {
			getDesignContext().getCommandContext().setLastActiveDesignTool( this );
			registerStatusBarItems();
		}
		requestFocus();
	}

	@Override
	protected void conceal() throws ToolException {
		super.conceal();
		unregisterActions();
		if( isReady() && isLastTool() ) unregisterStatusBarItems();
	}

	private void registerStatusBarItems() {
		CommandPrompt prompt = getCommandPrompt();
		getScene().addEventHandler( KeyEvent.ANY, prompt );
		getWorkspace().getStatusBar().addLeftItems( prompt );
		getWorkspace().getStatusBar().addRightItems( getCoordinateStatus() );
	}

	private void unregisterStatusBarItems() {
		CommandPrompt prompt = getCommandPrompt();
		getScene().removeEventHandler( KeyEvent.ANY, prompt );
		getWorkspace().getStatusBar().removeLeftItems( prompt );
		getWorkspace().getStatusBar().removeRightItems( getCoordinateStatus() );
	}

	private void registerActions() {
		pushAction( "delete", deleteAction );
		pushAction( "undo", undoAction );
		pushAction( "redo", redoAction );
	}

	private void unregisterActions() {
		pullAction( "delete", deleteAction );
		pullAction( "undo", undoAction );
		pullAction( "redo", redoAction );
	}

	static DesignLayer getDesignData( DesignPane.Layer l ) {
		return (DesignLayer)l.getProperties().get( DesignShapeView.DESIGN_DATA );
	}

	static DesignShape getDesignData( Shape s ) {
		return (DesignShape)s.getParent().getProperties().get( DesignShapeView.DESIGN_DATA );
	}

	private void setReticle( ReticleCursor reticle ) {
		this.reticle = reticle;
		if( getCursor() instanceof ReticleCursor ) setCursor( reticle );
	}

	private CoordinateStatus getCoordinateStatus() {
		return getDesignContext().getCoordinateStatus();
	}

	private void mouseMove( MouseEvent event ) {
		getCoordinateStatus().updatePosition( mouseToWorld( event.getX(), event.getY(), event.getZ() ) );
	}

	private void mousePress( MouseEvent event ) {
		// Drag anchor is used by select, pan (and others)
		dragAnchor = new Point3D( event.getX(), event.getY(), 0 );
		selectWindow.hide();

		if( isPanMode( event ) ) {
			viewAnchor = designPane.getViewPoint();
		} else if( isSelectMode() ) {
			mouseSelect( event.getX(), event.getY(), event.getZ(), isSelectModifyEvent( event ) );
		}
	}

	private void mouseDrag( MouseEvent event ) {
		if( isPanMode( event ) ) {
			designPane.mousePan( viewAnchor, dragAnchor, event.getX(), event.getY() );
		} else if( isWindowSelectMode( event ) ) {
			updateSelectWindow( dragAnchor, new Point3D( event.getX(), event.getY(), event.getZ() ) );
		}
	}

	private void mouseRelease( MouseEvent event ) {
		Point3D mouse = new Point3D( event.getX(), event.getY(), event.getZ() );
		if( isSelectMode() && selectWindow.getWidth() > 0 && selectWindow.getHeight() > 0 ) windowSelect( dragAnchor, mouse, !event.isControlDown() );
		selectWindow.hide();
		dragAnchor = null;
	}

	private void updateSelectWindow( Point3D anchor, Point3D mouse ) {
		if( anchor == null ) return;
		double x = Math.min( anchor.getX(), mouse.getX() );
		double y = Math.min( anchor.getY(), mouse.getY() );
		double w = Math.abs( anchor.getX() - mouse.getX() );
		double h = Math.abs( anchor.getY() - mouse.getY() );
		Fx.run( () -> selectWindow.resizeRelocate( x, y, w, h ) );
	}

	private boolean mouseSelect( double x, double y, double z, boolean modify ) {
		if( !modify ) selectedShapes().clear();

		List<Shape> selection = designPane.apertureSelect( x, y, z, getSelectTolerance() );
		if( selection.isEmpty() ) return false;

		Shape shape = selection.get( 0 );
		boolean selected = getDesignData( shape ).isSelected();
		if( !modify || !selected ) {
			selectedShapes().add( shape );
		} else {
			selectedShapes().remove( shape );
		}

		return true;
	}

	private boolean windowSelect( Point3D a, Point3D b, boolean contains ) {
		selectedShapes().clear();
		List<Shape> selection = designPane.windowSelect( a, b, contains );
		selectedShapes().addAll( selection );
		return !selection.isEmpty();
	}

	private boolean isSelectModifyEvent( MouseEvent event ) {
		return event.isControlDown() && event.isPrimaryButtonDown();
	}

	private boolean isPanMode( MouseEvent event ) {
		return event.isShiftDown() && event.isPrimaryButtonDown() && !event.isStillSincePress();
	}

	private boolean isSelectMode() {
		return getCommandContext().isSelectMode();
	}

	private boolean isWindowSelectMode( MouseEvent event ) {
		return isSelectMode() && !event.isStillSincePress();
	}

	private void doSelectShapes( ListChangeListener.Change<? extends Shape> c ) {
		while( c.next() ) {
			c.getRemoved().stream().findFirst().map( DesignTool::getDesignData ).ifPresent( this::hidePropertiesPage );
			c.getRemoved().stream().map( DesignTool::getDesignData ).forEach( s -> s.setSelected( false ) );
			c.getAddedSubList().stream().map( DesignTool::getDesignData ).forEach( s -> s.setSelected( true ) );
			c.getAddedSubList().stream().findFirst().map( DesignTool::getDesignData ).ifPresent( this::showPropertiesPage );
		}
		deleteAction.updateEnabled();
	}

	private void doDeleteShapes( Collection<DesignShape> shapes ) {
		runTask( () -> shapes.forEach( s -> s.getParentLayer().removeShape( s ) ) );
		selectedShapes.clear();
	}

	private void doSetCurrentLayerById( String id ) {
		getDesign().findLayers( DesignLayer.ID, id ).stream().findFirst().ifPresent( currentLayer::set );
	}

	private void showPropertiesPage( DesignShape s ) {
		try {
			SettingsPage page = s.getPropertiesPage( getProduct() );
			PropertiesToolEvent event = new PropertiesToolEvent( DesignTool.this, PropertiesToolEvent.SHOW, page );
			getWorkspace().getEventBus().dispatch( event );
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}

	private void hidePropertiesPage( DesignShape s ) {
		try {
			SettingsPage page = s.getPropertiesPage( getProduct() );
			PropertiesToolEvent event = new PropertiesToolEvent( DesignTool.this, PropertiesToolEvent.HIDE, page );
			getWorkspace().getEventBus().dispatch( event );
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}

	private static class SelectWindow extends Rectangle {

		@Override
		public boolean isResizable() {
			return true;
		}

		@Override
		public void resizeRelocate( double x, double y, double w, double h ) {
			setX( x );
			setY( y );
			setWidth( w );
			setHeight( h );
			setVisible( true );
		}

		public void hide() {
			setX( 0 );
			setY( 0 );
			setWidth( 0 );
			setHeight( 0 );
			setVisible( false );
		}

	}

	private class DeleteAction extends Action {

		protected DeleteAction( Program program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return !selectedShapes().isEmpty();
		}

		@Override
		public void handle( ActionEvent event ) {
			doDeleteShapes( selectedShapes().stream().map( DesignTool::getDesignData ).collect( Collectors.toSet() ) );
		}

	}

	private class UndoAction extends Action {

		protected UndoAction( Program program ) {
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

	private class RedoAction extends Action {

		protected RedoAction( Program program ) {
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
