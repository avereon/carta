package com.avereon.cartesia.tool;

import com.avereon.event.EventHandler;
import com.avereon.product.Rb;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.RbKey;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.tool.settings.SettingsPagePanel;
import com.avereon.xenon.workpane.Workpane;
import com.avereon.zerra.javafx.Fx;
import javafx.scene.control.ScrollPane;
import lombok.CustomLog;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

@CustomLog
public class ShapePropertiesTool extends ProgramTool {

	private final ScrollPane scroller;

	private final EventHandler<ShapePropertiesToolEvent> showHandler;

	private final EventHandler<ShapePropertiesToolEvent> hideHandler;

	private static final Map<SettingsPage, SettingsPagePanel> settingsPagePanelCache;

	static {
		settingsPagePanelCache = Collections.synchronizedMap( new WeakHashMap<>() );
	}

	public ShapePropertiesTool( XenonProgramProduct product, Asset asset ) {
		super( product, asset );
		setId( "tool-properties" );

		// UI components
		scroller = new ScrollPane();
		scroller.setFitToWidth( true );
		getChildren().addAll( scroller );

		// Event handlers
		this.showHandler = this::showPage;
		this.hideHandler = this::hidePage;
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
		setTitle( Rb.text( RbKey.LABEL, "shape-properties-tool" ) );
		setGraphic( getProgram().getIconLibrary().getIcon( "shape-properties" ) );
	}

	@Override
	protected void allocate() {
		getWorkspace().getEventBus().register( ShapePropertiesToolEvent.SHOW, showHandler );
		getWorkspace().getEventBus().register( ShapePropertiesToolEvent.HIDE, hideHandler );
	}

	@Override
	protected void activate() {
		ShapePropertiesToolEvent event = getWorkspace().getEventBus().getPriorEvent( ShapePropertiesToolEvent.class );
		if( event != null && event.getEventType() == ShapePropertiesToolEvent.SHOW && isEmpty() ) showPage( event );
	}

	@Override
	protected void deallocate() {
		getWorkspace().getEventBus().unregister( ShapePropertiesToolEvent.HIDE, hideHandler );
		getWorkspace().getEventBus().unregister( ShapePropertiesToolEvent.SHOW, showHandler );
	}

	private boolean isEmpty() {
		return scroller.getContent() == null;
	}

	private void showPage( ShapePropertiesToolEvent event ) {
		Fx.run( () -> {
			SettingsPagePanel panel = settingsPagePanelCache.computeIfAbsent( event.getPage(), p -> {
				p.setSettings( event.getSettings() );
				return new SettingsPagePanel( p, getProgram().getSettingsManager().getOptionProviders() );
			} );
			event.getPage().setSettings( event.getSettings() );
			scroller.setContent( panel );
		} );
	}

	private void hidePage( ShapePropertiesToolEvent event ) {
		Fx.run( () -> scroller.setContent( null ) );
	}

}
