package com.avereon.cartesia.tool;

import com.avereon.cartesia.CartesiaMod;
import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.data.NodeEvent;
import com.avereon.marea.Pen;
import com.avereon.marea.Renderer2d;
import com.avereon.marea.fx.FxRenderer2d;
import com.avereon.marea.geom.Line;
import com.avereon.settings.Settings;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.tool.guide.GuidedTool;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.zarra.color.Paints;
import com.avereon.zarra.javafx.Fx;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

public class DesignToolNg extends GuidedTool {

	// Need the design model
	private Design design;

	// Need a place to render the model
	private final Renderer2d renderer;

	private final DesignWorkplane workplane;

	public DesignToolNg( ProgramProduct product, Asset asset ) {
		super( product, asset );
		getStyleClass().add( "design-tool" );

		addStylesheet( CartesiaMod.STYLESHEET );

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

		configureWorkplane();
		rerender();
	}

	private void rerender() {
		Fx.run( this::render );
	}

	private void render() {
		if( this.design == null ) return;
		Fx.checkFxThread();

		renderer.clear();
		for( DesignLayer layer : design.getAllLayers() ) {
			for( DesignShape shape : layer.getShapes() ) {
				if( shape instanceof DesignLine line ) {
					renderer.draw( new Line( CadPoints.asPoint( line.getOrigin() ), CadPoints.asPoint( line.getPoint() ) ), new Pen( Color.RED ) );
				}
			}
		}
	}

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
		//settings.register( DesignWorkplane.GRID_MAJOR_Z, e -> workplane.setMajorGridZ( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_MAJOR_PAINT, e -> workplane.setMajorGridPaint( Paints.parse( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( DesignWorkplane.GRID_MAJOR_WIDTH, e -> workplane.setMajorGridWidth( String.valueOf( e.getNewValue() ) ) );

		settings.register( DesignWorkplane.GRID_MINOR_VISIBLE, e -> workplane.setMinorGridVisible( Boolean.parseBoolean( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( DesignWorkplane.GRID_MINOR_X, e -> workplane.setMinorGridX( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_MINOR_Y, e -> workplane.setMinorGridY( String.valueOf( e.getNewValue() ) ) );
		//settings.register( DesignWorkplane.GRID_MINOR_Z, e -> workplane.setMinorGridZ( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_MINOR_PAINT, e -> workplane.setMinorGridPaint( Paints.parse( String.valueOf( e.getNewValue() ) ) ) );
		settings.register( DesignWorkplane.GRID_MINOR_WIDTH, e -> workplane.setMinorGridWidth( String.valueOf( e.getNewValue() ) ) );

		settings.register( DesignWorkplane.GRID_SNAP_X, e -> workplane.setSnapGridX( String.valueOf( e.getNewValue() ) ) );
		settings.register( DesignWorkplane.GRID_SNAP_Y, e -> workplane.setSnapGridY( String.valueOf( e.getNewValue() ) ) );
		//settings.register( DesignWorkplane.GRID_SNAP_Z, e -> workplane.setSnapGridZ( String.valueOf( e.getNewValue() ) ) );

		// Rebuild the grid if any workplane values change
		//workplane.register( NodeEvent.VALUE_CHANGED, e -> rebuildGridAction.update() );
	}

}
