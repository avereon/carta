package com.avereon.cartesia;

import com.avereon.cartesia.data.DesignLayerOptionProvider;
import com.avereon.cartesia.data.PointTypeOptionProvider;
import com.avereon.cartesia.icon.*;
import com.avereon.cartesia.tool.Design2dEditor;
import com.avereon.xenon.Mod;
import com.avereon.xenon.ToolRegistration;
import com.avereon.zerra.image.BrokenIcon;

public class CartesiaMod extends Mod {

	public static final String STYLESHEET = "cartesia.css";

	private Design2dAssetType design2dAssetType;

	private Design3dAssetType design3dAssetType;

	@Override
	public void startup() throws Exception {
		super.startup();
		registerIcon( getCard().getArtifact(), new CartesiaIcon() );
		registerIcon( "tool", new BrokenIcon() );
		registerIcon( "draw", new PencilIcon() );
		registerIcon( "arc-2", new Arc2Icon() );
		registerIcon( "arc-3", new Arc3Icon() );
		registerIcon( "circle-2", new Circle2Icon() );
		registerIcon( "circle-3", new Circle3Icon() );
		registerIcon( "curve-3", new Curve3Icon() );
		registerIcon( "curve-4", new Curve4Icon() );
		registerIcon( "ellipse-3", new Ellipse3Icon() );
		registerIcon( "ellipse-arc-5", new EllipseArc5Icon() );
		registerIcon( "line-2", new Line2Icon() );
		registerIcon( "line-perpendicular", new LinePerpendicularIcon() );
		registerIcon( "marker", new MarkerIcon() );
		registerIcon( "path", new PathIcon() );
		registerIcon( "layer", new LayerVisibleIcon() );
		registerIcon( "layers", new LayersIcon() );
		registerIcon( "layer-hidden", new LayerHiddenIcon() );
		registerIcon( "grid-toggle-enabled", new GridIcon( true ) );
		registerIcon( "grid-toggle-disabled", new GridIcon( false ) );
		registerIcon( "snap-grid-toggle-enabled", new SnapGridIcon( true ) );
		registerIcon( "snap-grid-toggle-disabled", new SnapGridIcon( false ) );

		registerAction( this, "tool" );
		registerAction( this, "draw" );
		registerAction( this, "marker" );
		registerAction( this, "line" );
		registerAction( this, "circle" );
		//registerAction( this, "arc" );
		registerAction( this, "ellipse" );
		//registerAction( this, "ellipse-arc" );
		registerAction( this, "curve" );
		registerAction( this, "draw-arc-2" );
		registerAction( this, "draw-arc-3" );
		registerAction( this, "draw-circle-2" );
		registerAction( this, "draw-circle-3" );
		//registerAction( this, "draw-curve-3" );
		registerAction( this, "draw-curve-4" );
		registerAction( this, "draw-ellipse-3" );
		registerAction( this, "draw-ellipse-arc-5" );
		registerAction( this, "draw-line-2" );
		registerAction( this, "draw-line-perpendicular" );
		registerAction( this, "draw-marker" );
		registerAction( this, "draw-path" );

		registerAction( this, "snap-grid-toggle" );
		registerAction( this, "grid-toggle" );

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

		getProgram().getSettingsManager().putOptionProvider( "point-type-option-provider", new PointTypeOptionProvider( this ) );
		getProgram().getSettingsManager().putOptionProvider( "design-layer-layers", new DesignLayerOptionProvider( this, true ) );
		getProgram().getSettingsManager().putOptionProvider( "design-shape-layers", new DesignLayerOptionProvider( this, false ) );

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

		unregisterAction( "grid-toggle" );
		unregisterAction( "snap-grid-toggle" );

		unregisterAction( "draw-path" );
		unregisterAction( "draw-marker" );
		unregisterAction( "draw-line-perpendicular" );
		unregisterAction( "draw-line-2" );
		unregisterAction( "draw-ellipse-arc-5" );
		unregisterAction( "draw-ellipse-3" );
		unregisterAction( "draw-curve-4" );
		unregisterAction( "draw-circle-3" );
		unregisterAction( "draw-circle-2" );
		unregisterAction( "draw-arc-3" );
		unregisterAction( "draw-arc-2" );
		unregisterAction( "curve" );
		unregisterAction( "ellipse" );
		unregisterAction( "circle" );
		unregisterAction( "line" );
		unregisterAction( "marker" );
		unregisterAction( "draw" );
		unregisterAction( "tool" );

		unregisterIcon( "snap-grid-toggle-disabled", new SnapGridIcon() );
		unregisterIcon( "snap-grid-toggle-enabled", new SnapGridIcon() );
		unregisterIcon( "grid-toggle-disabled", new GridIcon() );
		unregisterIcon( "grid-toggle-enabled", new GridIcon() );
		unregisterIcon( "layer-hidden", new LayerHiddenIcon() );
		unregisterIcon( "layers", new LayersIcon() );
		unregisterIcon( "layer", new LayerVisibleIcon() );
		unregisterIcon( "path", new PathIcon() );
		unregisterIcon( "marker", new MarkerIcon() );
		unregisterIcon( "line-perpendicular", new LinePerpendicularIcon() );
		unregisterIcon( "line-2", new Line2Icon() );
		unregisterIcon( "ellipse-arc-5", new EllipseArc5Icon() );
		unregisterIcon( "ellipse-3", new Ellipse3Icon() );
		unregisterIcon( "curve-4", new Curve4Icon() );
		unregisterIcon( "curve-3", new Curve3Icon() );
		unregisterIcon( "circle-3", new Circle3Icon() );
		unregisterIcon( "circle-2", new Circle2Icon() );
		unregisterIcon( "arc-3", new Arc3Icon() );
		unregisterIcon( "arc-2", new Arc2Icon() );
		unregisterIcon( "draw", new PencilIcon() );
		unregisterIcon( "tool", new BrokenIcon() );
		unregisterIcon( getCard().getArtifact(), new CartesiaIcon() );
		super.shutdown();
	}

}
