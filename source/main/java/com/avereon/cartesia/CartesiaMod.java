package com.avereon.cartesia;

import com.avereon.cartesia.cursor.Reticle;
import com.avereon.cartesia.data.util.DesignLayerOptionProvider;
import com.avereon.cartesia.data.util.DesignUnitOptionProvider;
import com.avereon.cartesia.data.util.MarkerTypeOptionProvider;
import com.avereon.cartesia.icon.*;
import com.avereon.cartesia.rb.CartesiaHelp;
import com.avereon.cartesia.settings.FontSettingEditor;
import com.avereon.cartesia.tool.Design2dEditor;
import com.avereon.cartesia.tool.ShapePropertiesTool;
import com.avereon.cartesia.tool.design.DesignToolV2;
import com.avereon.index.Document;
import com.avereon.log.LazyEval;
import com.avereon.product.Rb;
import com.avereon.xenon.ActionProxy;
import com.avereon.xenon.Module;
import com.avereon.xenon.ToolInstanceMode;
import com.avereon.xenon.ToolRegistration;
import com.avereon.xenon.asset.type.ProgramHelpType;
import com.avereon.xenon.index.IndexService;
import com.avereon.xenon.tool.settings.SettingsPageParser;
import com.avereon.xenon.tool.settings.editor.PaintSettingEditor;
import com.avereon.zenna.icon.EyeIcon;
import com.avereon.zenna.icon.PreferencesIcon;
import com.avereon.zenna.icon.PrinterIcon;
import lombok.CustomLog;
import lombok.Getter;

import java.net.URI;
import java.net.URL;
import java.util.*;

@CustomLog
public class CartesiaMod extends Module {

	public static final String STYLESHEET = "cartesia.css";

	public static final String INDEX_ID = "cartesia";

	private Design2dAssetType design2dAssetType;

	private Design3dAssetType design3dAssetType;

	private ShapePropertiesAssetType shapePropertiesAssetType;

	@Getter
	private CommandMap commandMap;

	@Override
	public void startup() throws Exception {
		log.atDebug().log( "%s starting...", LazyEval.of( () -> getCard().getName() ) );

		super.startup();
		registerIcons();
		registerActions();

		getProgram().getAssetManager().addScheme( new CartesiaScheme( getProgram() ) );

		// Register Design2D asset type and tools
		registerAssetType( design2dAssetType = new Design2dAssetType( this ) );
		// Settings pages
		String path = "/" + getClass().getPackageName().replace( ".", "/" );
		design2dAssetType.setSettingsPages( SettingsPageParser.parse( this, path + "/design/props/design.xml", RbKey.PROPS ) );

		registerTools();

		getProgram().getSettingsManager().putSettingEditor( "font-cartesia", FontSettingEditor.class );
		getProgram().getSettingsManager().putSettingEditor( "paint-cartesia", PaintSettingEditor.class );

		getProgram().getSettingsManager().putOptionProvider( "marker-type-option-provider", new MarkerTypeOptionProvider() );
		getProgram().getSettingsManager().putOptionProvider( "design-layer-layers", new DesignLayerOptionProvider( this, true ) );
		getProgram().getSettingsManager().putOptionProvider( "design-shape-layers", new DesignLayerOptionProvider( this, false ) );
		getProgram().getSettingsManager().putOptionProvider( "design-units", new DesignUnitOptionProvider() );
		getProgram().getSettingsManager().putOptionProvider( "font-family-provider", new FontFamilyNameOptionProvider() );

		// Load the default settings
		loadDefaultSettings();

		// Register the settings pages
		registerSettingsPages();

		// Load the command map
		commandMap = new CommandMap().load( this );

		// Index the help pages
		registerHelpPages();

		// Register a listener to clear the Reticle cursor cache when the theme changes
		getProgram().getWorkspaceManager().themeIdProperty().addListener( (p,o,n) -> Reticle.clearCursorCache() );

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
		unregisterTool( design2dAssetType, DesignToolV2.class );
		unregisterAssetType( design2dAssetType );

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
		registerIcon( "layer-current", new LayerCurrentVisibleIcon() );
		registerIcon( "layer-current-hidden", new LayerCurrentHiddenIcon() );
		registerIcon( "layer-hidden", new LayerHiddenIcon() );
		registerIcon( "grid-toggle-enabled", new GridVisibleIcon() );
		registerIcon( "grid-toggle-disabled", new GridHiddenIcon() );
		registerIcon( "snap-grid-toggle-enabled", new SnapGridEnabledIcon() );
		registerIcon( "snap-grid-toggle-disabled", new SnapGridDisabledIcon() );
		registerIcon( "shape-properties", new PreferencesIcon() );
		registerIcon( "views", new EyeIcon() );
		registerIcon( "prints", new PrinterIcon() );
	}

	private void registerActions() {
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

	private void registerTools() {
		getProgram().getToolManager().addToolAlias( "com.avereon.cartesia.tool.DesignToolV1", DesignToolV2.class );
		getProgram().getToolManager().addToolAlias( "com.avereon.cartesia.tool.design.FxRenderDesignTool", DesignToolV2.class );
		getProgram().getToolManager().addToolAlias( "com.avereon.cartesia.tool.design.FxShapeDesignTool", DesignToolV2.class );

		// Default tool registration
		ToolRegistration designToolRegistration = new ToolRegistration( this, DesignToolV2.class );
		designToolRegistration.setName( Rb.text( RbKey.LABEL, "design-2d-editor" ) );
		registerTool( design2dAssetType, designToolRegistration );
		// Other tool registrations
		ToolRegistration design2dEditorRegistration = new ToolRegistration( this, Design2dEditor.class );
		design2dEditorRegistration.setName( Rb.text( RbKey.LABEL, "design-2d-editor" ) + " (Deprecated)" );
		registerTool( design2dAssetType, design2dEditorRegistration );

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
	}

	private void unregisterIcons() {
		unregisterIcon( "prints", new PrinterIcon() );
		unregisterIcon( "views", new EyeIcon() );
		unregisterIcon( "shape-properties", new PreferencesIcon() );
		unregisterIcon( "snap-grid-toggle-disabled", new SnapGridDisabledIcon() );
		unregisterIcon( "snap-grid-toggle-enabled", new SnapGridEnabledIcon() );
		unregisterIcon( "grid-toggle-disabled", new GridHiddenIcon() );
		unregisterIcon( "grid-toggle-enabled", new GridVisibleIcon() );
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
		unregisterIcon( getCard().getArtifact(), new CartesiaIcon() );
	}

	private void registerHelpPages() {
		// FIXME Tag names are language specific

		getProgram().getIndexService().submit( INDEX_ID, createIndexableDocument( "/docs/manual/introduction" ) );
		getProgram().getIndexService().submit( INDEX_ID, createIndexableDocument( "/docs/manual/relative-coordinates" ) );
		getProgram().getIndexService().submit( INDEX_ID, createIndexableDocument( "/docs/manual/selecting-geometry" ) );

		registerCommandHelpPages();
	}

	/**
	 * Create an indexable document from a resource path.
	 *
	 * @param resourcePath The resource path without language or suffix
	 * @return The indexable document
	 */
	private Document createIndexableDocument( String resourcePath ) {
		return createIndexableDocument( "document", resourcePath, Map.of(), List.of(), null );
	}

	private Document createIndexableDocument(
		String icon, String resourcePath, Map<String, String> values, List<String> tags, String defaultContent
	) {
		// Create the document URI
		String modKey = getCard().getProductKey();
		URI uri = URI.create( ProgramHelpType.URI + ":/" + modKey + resourcePath );

		Map<String, String> replacementValues = new HashMap<>( values );
		replacementValues.put( "module.name", getCard().getName() );
		replacementValues.put( "module.version", getCard().getVersion() );
		replacementValues.put( "module.release", getCard().getRelease().toHumanString() );

		Document document = new Document();
		document.mediaType( Document.MediaType.HTML );
		document.uri( uri );
		document.icon( icon );
		document.title( null );
		document.content( null );
		document.values( replacementValues );
		document.tags( tags );
		document.properties().put( IndexService.STORE_CONTENT, true );

		try {
			// Create the resource content URL
			URL url = (URL)ResourceBundle.getBundle( CartesiaHelp.class.getName() ).getObject( resourcePath );
			document.url( url );
			log.atDebug().log( "Resource found: %s", url );
		} catch( MissingResourceException exception ) {
			//log.atConfig().log( "Resource not found: %s", resourcePath );
			if( defaultContent != null ) document.content( defaultContent );
		}

		return document;
	}

	private void registerCommandHelpPages() {
		Map<String, CommandMetadata> commands = getCommandMap().getAll();
		for( CommandMetadata command : commands.values() ) {
			ActionProxy action = getProgram().getActionLibrary().getAction( command.getAction() );
			String resourcePath = "/docs/manual/commands/" + command.getAction();
			String icon = action.getIcon();

			String actionName = Objects.requireNonNullElse( command.getName(), "" );
			String actionCommand = Objects.requireNonNullElse( command.getCommand(), "--" ).toUpperCase();
			Map<String, String> values = Map.of( "action.name", actionName, "action.command", actionCommand );

			String title = actionCommand + " - " + actionName;

			StringBuilder defaultContent = new StringBuilder( "<html>" );
			defaultContent.append( "<head>" );
			defaultContent.append( "<title>" ).append( title ).append( "</title>" );
			defaultContent.append( "</head>" );
			defaultContent.append( "<h1>" ).append( actionName ).append( "</h1>" );
			defaultContent.append( "<h2>" ).append( actionCommand ).append( "</h2>" );
			defaultContent.append( "</body></html>" );

			Document document = createIndexableDocument( icon, resourcePath, values, command.getTags(), defaultContent.toString() );
			getProgram().getIndexService().submit( INDEX_ID, document );
		}
	}

	private void unregisterIndexes() {
		getProgram().getIndexService().removeIndex( INDEX_ID );
	}

}
