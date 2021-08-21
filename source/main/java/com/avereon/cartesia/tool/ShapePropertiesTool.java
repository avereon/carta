package com.avereon.cartesia.tool;

import com.avereon.event.EventHandler;
import com.avereon.product.Rb;
import com.avereon.xenon.BundleKey;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.PropertiesToolEvent;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.tool.settings.SettingsPanel;
import com.avereon.xenon.workpane.Workpane;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.control.ScrollPane;

public class ShapePropertiesTool extends ProgramTool {

	private final ScrollPane scroller;

	private final EventHandler<ShapePropertiesToolEvent> showHandler;

	private final EventHandler<ShapePropertiesToolEvent> hideHandler;

	public ShapePropertiesTool( ProgramProduct product, Asset asset ) {
		super( product, asset );
		setId( "tool-properties" );

		scroller = new ScrollPane();
		scroller.setFitToWidth( true );
		getChildren().addAll( scroller );
		this.showHandler = e -> Fx.run( () -> showPage( e.getPage() ) );
		this.hideHandler = e -> Fx.run( this::hidePage );
	}

	@Override
	public Workpane.Placement getPlacement() {
		return Workpane.Placement.DOCK_RIGHT;
	}

	@Override
	public boolean changeCurrentAsset() {
		return false;
	}

	@Override
	protected void ready( OpenAssetRequest request ) {
		setTitle( Rb.text( BundleKey.TOOL, "properties-name" ) );
		setGraphic( getProgram().getIconLibrary().getIcon( "properties" ) );
	}

	@Override
	protected void allocate() {
		getWorkspace().getEventBus().register( ShapePropertiesToolEvent.SHOW, showHandler );
		getWorkspace().getEventBus().register( ShapePropertiesToolEvent.HIDE, hideHandler );
	}

	@Override
	protected void activate() {
		PropertiesToolEvent event = getWorkspace().getEventBus().getPriorEvent( PropertiesToolEvent.class );
		if( event != null && event.getEventType() == PropertiesToolEvent.SHOW && isEmpty() ) showPage( event.getPage() );
	}

	@Override
	protected void deallocate() {
		getWorkspace().getEventBus().unregister( ShapePropertiesToolEvent.HIDE, hideHandler );
		getWorkspace().getEventBus().unregister( ShapePropertiesToolEvent.SHOW, showHandler );
	}

	private boolean isEmpty() {
		return scroller.getContent() == null;
	}

	public void showPage( SettingsPage page ) {
		Fx.run( () -> {
			page.setOptionProviders( getProgram().getSettingsManager().getOptionProviders() );
			scroller.setContent( new SettingsPanel( page ) );
		});
	}

	public void hidePage() {
		Fx.run( () -> scroller.setContent( null ) );
	}

}