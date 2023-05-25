package com.avereon.cartesia.tool;

import com.avereon.event.EventHandler;
import com.avereon.product.Rb;
import com.avereon.xenon.RbKey;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.xenon.tool.settings.SettingsPanel;
import com.avereon.xenon.workpane.Workpane;
import com.avereon.zarra.javafx.Fx;
import javafx.scene.control.ScrollPane;

public class ShapePropertiesTool extends ProgramTool {

	private final ScrollPane scroller;

	private final EventHandler<ShapePropertiesToolEvent> showHandler;

	private final EventHandler<ShapePropertiesToolEvent> hideHandler;

	public ShapePropertiesTool( XenonProgramProduct product, Asset asset ) {
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
		if( event != null && event.getEventType() == ShapePropertiesToolEvent.SHOW && isEmpty() ) showPage( event.getPage() );
	}

	@Override
	protected void deallocate() {
		getWorkspace().getEventBus().unregister( ShapePropertiesToolEvent.HIDE, hideHandler );
		getWorkspace().getEventBus().unregister( ShapePropertiesToolEvent.SHOW, showHandler );
	}

	private boolean isEmpty() {
		return scroller.getContent() == null;
	}

	private void showPage( SettingsPage page ) {
		Fx.run( () -> {
			page.setOptionProviders( getProgram().getSettingsManager().getOptionProviders() );
			scroller.setContent( new SettingsPanel( page ) );
		});
	}

	private void hidePage() {
		Fx.run( () -> scroller.setContent( null ) );
	}

}
