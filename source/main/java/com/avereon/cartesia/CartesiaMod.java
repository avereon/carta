package com.avereon.cartesia;

import com.avereon.cartesia.data.util.DesignLayerOptionProvider;
import com.avereon.cartesia.data.util.DesignUnitOptionProvider;
import com.avereon.cartesia.data.util.MarkerTypeOptionProvider;
import com.avereon.cartesia.icon.*;
import com.avereon.cartesia.rb.CartesiaHelp;
import com.avereon.cartesia.tool.Design2dEditor;
import com.avereon.cartesia.tool.ShapePropertiesTool;
import com.avereon.cartesia.tool.FxRenderDesignTool;
import com.avereon.index.Document;
import com.avereon.log.LazyEval;
import com.avereon.product.Rb;
import com.avereon.xenon.ActionProxy;
import com.avereon.xenon.Mod;
import com.avereon.xenon.ToolInstanceMode;
import com.avereon.xenon.ToolRegistration;
import com.avereon.xenon.asset.type.ProgramHelpType;
import com.avereon.xenon.tool.settings.SettingsPageParser;
import com.avereon.zarra.image.BrokenIcon;
import com.avereon.zenna.icon.PreferencesIcon;
import com.avereon.zenna.icon.PrinterIcon;
import lombok.CustomLog;

import java.net.URI;
import java.net.URL;
import java.util.*;

@CustomLog
public class CartesiaMod extends Mod {

	public static final String STYLESHEET = "cartesia.css";

	public static final String INDEX_ID = "cartesia";

	private Design2dAssetType design2dAssetType;

	private Design3dAssetType design3dAssetType;

	private ShapePropertiesAssetType shapePropertiesAssetType;

	@Override
	public void startup() throws Exception {
		log.atFine().log( "%s starting...", LazyEval.of( () -> getCard().getName() ) );

		super.startup();
		registerIcons();
		registerActions();

		getProgram().getAssetManager().addScheme( new CartesiaScheme( getProgram() ) );

		// Register Design2D asset type and tools
		registerAssetType( design2dAssetType = new Design2dAssetType( this ) );
		// Settings pages
		String path = "/" + getClass().getPackageName().replace( ".", "/" );
		design2dAssetType.setSettingsPages( SettingsPageParser.parse( this, path + "/design/props/design.xml", RbKey.PROPS ) );
		// Default tool registration
		ToolRegistration design2dEditorRegistration = new ToolRegistration( this, Design2dEditor.class );
		design2dEditorRegistration.setName( Rb.text( RbKey.LABEL, "design-2d-editor" ) );
		registerTool( design2dAssetType, design2dEditorRegistration );
		// Other tool registrations
		ToolRegistration designToolRegistration = new ToolRegistration( this, FxRenderDesignTool.class );
		designToolRegistration.setName( Rb.text( RbKey.LABEL, "design-2d-editor" ) );
		registerTool( design2dAssetType, designToolRegistration );

		// Register Design3D asset type and tools
		//registerAssetType( design3dAssetType = new Design3dAssetType( this ) );
		// Tool registration
		//ToolRegistration design3dEditorRegistration = new ToolRegistration( this, Design3dEditor.class );
		//design3dEditorRegistration.setName( Rb.text(RbKey.LABEL, "design-3d-editor") );
		//registerTool( design3dAssetType, design3dEditorRegistration );

		// Register ShapeProperties asset type and tools
		registerAssetType( shapePropertiesAssetType = new ShapePropertiesAssetType( this ) );
		ToolRegistration shapePropertiesRegistration = new ToolRegistration( this, ShapePropertiesTool.class );
		shapePropertiesRegistration.setName( Rb.text( RbKey.LABEL, "shape-properties-tool" ) );
		shapePropertiesRegistration.setInstanceMode( ToolInstanceMode.SINGLETON );
		registerTool( shapePropertiesAssetType, shapePropertiesRegistration );

		getProgram().getSettingsManager().putOptionProvider( "marker-type-option-provider", new MarkerTypeOptionProvider() );
		getProgram().getSettingsManager().putOptionProvider( "design-layer-layers", new DesignLayerOptionProvider( this, true ) );
		getProgram().getSettingsManager().putOptionProvider( "design-shape-layers", new DesignLayerOptionProvider( this, false ) );
		getProgram().getSettingsManager().putOptionProvider( "design-units", new DesignUnitOptionProvider() );

		// Load the default settings
		loadDefaultSettings();

		// Register the settings pages
		registerSettingsPages();

		// Load the command map
		CommandMap.load( this );

		// Index the help pages
		registerHelpPages();

		log.atInfo().log( "%s started.", LazyEval.of( () -> getCard().getName() ) );
	}

	@Override
	public void shutdown() throws Exception {
		log.atFine().log( "%s stopping...", LazyEval.of( () -> getCard().getName() ) );

		// Unregister the module indexes
		unregisterIndexes();

		// Unregister the settings pages
		unregisterSettingsPages();

		// Unregister ShapeProperties
		unregisterTool( shapePropertiesAssetType, ShapePropertiesTool.class );
		unregisterAssetType( shapePropertiesAssetType );

		// Unregister Design3D
		//unregisterTool( design3dAssetType, Design3dEditor.class );
		//unregisterAssetType( design3dAssetType );

		// Unregister Design2D
		unregisterTool( design2dAssetType, Design2dEditor.class );
		unregisterAssetType( design2dAssetType );

		unregisterActions();
		unregisterIcons();
		super.shutdown();

		log.atInfo().log( "%s stopped.", LazyEval.of( () -> getCard().getName() ) );
	}

	private void registerIcons() {
		registerIcon( getCard().getArtifact(), new CartesiaIcon() );
		registerIcon( "tool", new BrokenIcon() );
		registerIcon( "draw", new PencilIcon() );
		registerIcon( "arc-2", new Arc2Icon() );
		registerIcon( "arc-3", new Arc3Icon() );
		registerIcon( "circle-2", new Circle2Icon() );
		registerIcon( "circle-3", new Circle3Icon() );
		registerIcon( "circle-diameter-2", new CircleDiameter2Icon() );
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
		registerIcon( "shape-properties", new PreferencesIcon() );
		registerIcon( "views", new ViewIcon() );
		registerIcon( "view", new ViewIcon() );
		registerIcon( "prints", new PrinterIcon() );
	}

	private void registerActions() {
		registerAction( this, "tool" );
		registerAction( this, "layer" );
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

		registerAction( this, "measure" );
	}

	private void unregisterActions() {
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
		unregisterAction( "layer" );
		unregisterAction( "tool" );
	}

	private void unregisterIcons() {
		unregisterIcon( "prints", new PrinterIcon() );
		unregisterIcon( "view", new ViewIcon() );
		unregisterIcon( "views", new ViewIcon() );
		unregisterIcon( "shape-properties", new PreferencesIcon() );
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
		unregisterIcon( "circle-diameter-2", new CircleDiameter2Icon() );
		unregisterIcon( "circle-3", new Circle3Icon() );
		unregisterIcon( "circle-2", new Circle2Icon() );
		unregisterIcon( "arc-3", new Arc3Icon() );
		unregisterIcon( "arc-2", new Arc2Icon() );
		unregisterIcon( "draw", new PencilIcon() );
		unregisterIcon( "tool", new BrokenIcon() );
		unregisterIcon( getCard().getArtifact(), new CartesiaIcon() );
	}

	private void registerHelpPages() {
		// FIXME Tag names are language specific
		getProgram().getIndexService().submit( INDEX_ID, createIndexableDocument( "document", "/docs/manual/introduction", Map.of() ) );
		getProgram().getIndexService().submit( INDEX_ID, createIndexableDocument( "document", "/docs/manual/relative-coordinates", Map.of() ) );
		getProgram().getIndexService().submit( INDEX_ID, createIndexableDocument( "document", "/docs/manual/selecting-geometry", Map.of() ) );

		registerCommandHelpPages();
	}

	private Document createIndexableDocument( String icon, String resourcePath, Map<String, String> values ) {
		return createIndexableDocument( icon, "", resourcePath, values, List.of(), "" );
	}

	private Document createIndexableDocument( String icon, String title, String resourcePath, Map<String, String> values, List<String> tags ) {
		return createIndexableDocument( icon, title, resourcePath, values, tags, "" );
	}

	private Document createIndexableDocument( String icon, String title, String resourcePath, Map<String, String> values, List<String> tags, String defaultContent ) {
		// Create the document URI
		String modKey = getCard().getProductKey();
		URI uri = URI.create( ProgramHelpType.SCHEME + ":/" + modKey + resourcePath );

		Map<String, String> replacementValues = new HashMap<>( values );
		replacementValues.put( "module.name", getCard().getName() );
		replacementValues.put( "module.version", getCard().getVersion() );
		replacementValues.put( "module.release", getCard().getRelease().toHumanString() );

		try {
			// Create the resource content URL
			URL url = (URL)ResourceBundle.getBundle( CartesiaHelp.class.getName() ).getObject( resourcePath );

			return new Document( uri, icon, title ).url( url ).values( replacementValues ).tags( tags ).store( true );
		} catch( MissingResourceException exception ) {
			log.atWarn().log( "Resource not found: path=%s", resourcePath );
			if( defaultContent != null ) return new Document( uri, icon, title ).content( defaultContent ).values( replacementValues ).tags( tags ).store( true );
		}

		return null;
	}

	private void registerCommandHelpPages() {
		Map<String, CommandMetadata> commands = CommandMap.getAll();
		for( CommandMetadata command : commands.values() ) {
			ActionProxy action = getProgram().getActionLibrary().getAction( command.getAction() );
			String resourcePath = "/docs/manual/commands/" + command.getAction();
			String icon = action.getIcon();
			String title = command.getName();

			String actionName = Objects.requireNonNullElse( command.getName(), "" );
			String actionCommand = Objects.requireNonNullElse( command.getCommand(), "--" ).toUpperCase();
			Map<String, String> values = Map.of( "action.name", actionName, "action.command", actionCommand );

			StringBuilder defaultContent = new StringBuilder( "<html>" );
			defaultContent.append( "<head>" );
			defaultContent.append( "<title>" ).append( actionName ).append( "</title>" );
			defaultContent.append( "</head>" );
			defaultContent.append( "<h1>" ).append( actionName ).append( "</h1>" );
			defaultContent.append( "<h2>" ).append( actionCommand ).append( "</h2>" );
			defaultContent.append( "</body></html>" );

			String documentTitle = actionCommand + " - " + title;
			Document document = createIndexableDocument( icon, documentTitle, resourcePath, values, command.getTags(), defaultContent.toString() );
			if( document != null ) getProgram().getIndexService().submit( INDEX_ID, document );
		}
	}

	private void unregisterIndexes() {
		getProgram().getIndexService().removeIndex( INDEX_ID );
	}

}
