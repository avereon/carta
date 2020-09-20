package com.avereon.cartesia;

import com.avereon.cartesia.icon.CartesiaIcon;
import com.avereon.cartesia.icon.LayerIcon;
import com.avereon.cartesia.icon.LayersIcon;
import com.avereon.cartesia.tool.Design2dEditor;
import com.avereon.xenon.Mod;
import com.avereon.xenon.ToolRegistration;

public class CartesiaMod extends Mod {

	public static final String STYLESHEET = "cartesia.css";

	private Design2dAssetType design2dAssetType;

	private Design3dAssetType design3dAssetType;

	@Override
	public void startup() throws Exception {
		super.startup();
		registerIcon( getCard().getArtifact(), new CartesiaIcon() );
		registerIcon( "layer", new LayerIcon() );
		registerIcon( "layers", new LayersIcon() );

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

		// Load the default settings
		loadDefaultSettings();

		// Register the settings pages
		registerSettingsPages();
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

		unregisterIcon( getCard().getArtifact(), new CartesiaIcon() );
		unregisterIcon( "layer", new LayerIcon() );
		unregisterIcon( "layers", new LayersIcon() );
		super.shutdown();
	}

}
