package com.avereon.cartesia;

import com.avereon.cartesia.data.DesignLayerOptionProvider;
import com.avereon.cartesia.icon.*;
import com.avereon.cartesia.tool.Design2dEditor;
import com.avereon.xenon.Mod;
import com.avereon.xenon.ToolRegistration;
import com.avereon.zenna.icon.PauseIcon;

public class CartesiaMod extends Mod {

	public static final String STYLESHEET = "cartesia.css";

	private Design2dAssetType design2dAssetType;

	private Design3dAssetType design3dAssetType;

	@Override
	public void startup() throws Exception {
		super.startup();
		registerIcon( getCard().getArtifact(), new CartesiaIcon() );
		registerIcon( "layer", new LayerVisibleIcon() );
		registerIcon( "layers", new LayersIcon() );
		registerIcon( "layer-hidden", new LayerHiddenIcon() );

		registerIcon( "snap-grid-toggle-enabled", new SnapGridIcon() );
		registerIcon( "snap-grid-toggle-disabled", new PauseIcon() );

		registerAction( this.rb(), "snap-grid-toggle" );

		// Register Design2D
		registerAssetType( design2dAssetType = new Design2dAssetType( this ) );
		ToolRegistration design2dEditorRegistration = new ToolRegistration( this, Design2dEditor.class );
		design2dEditorRegistration.setName( "Design 2D Editor" );
		registerTool( design2dAssetType, design2dEditorRegistration );

		// Register Design3D
		//registerAssetType( design3dAssetType = new Design3dAssetType( this ) );
		//ToolRegistration design3dEditorRegistration = new ToolRegistration( this, Design3dEditor.class );
		//design3dEditorRegistration.setName( "Design 3D Editor" );
		//registerTool( design3dAssetType, design3dEditorRegistration );

		getProgram().getSettingsManager().putOptionProvider( "design-layers", new DesignLayerOptionProvider( this ) );

		// Load the default settings
		loadDefaultSettings();

		// Register the settings pages
		registerSettingsPages();

		CommandMap.load( this );
	}

	@Override
	public void shutdown() throws Exception {
		// Unregister the settings pages
		unregisterSettingsPages();

		// Unregister Design3D
		//unregisterTool( design3dAssetType, Design3dEditor.class );
		//unregisterAssetType( design3dAssetType );

		// Unregister Design2D
		unregisterTool( design2dAssetType, Design2dEditor.class );
		unregisterAssetType( design2dAssetType );

		unregisterAction( "snap-grid-toggle" );

		unregisterIcon( "snap-grid-toggle-disabled", new PauseIcon() );
		unregisterIcon( "snap-grid-toggle-enabled", new SnapGridIcon() );
		unregisterIcon( "layer-hidden", new LayerHiddenIcon() );
		unregisterIcon( "layers", new LayersIcon() );
		unregisterIcon( "layer", new LayerVisibleIcon() );
		unregisterIcon( getCard().getArtifact(), new CartesiaIcon() );
		super.shutdown();
	}

}
