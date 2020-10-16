package com.avereon.cartesia.data;

import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.DefaultCommandProcessor;
import com.avereon.cartesia.DesignUnit;
import com.avereon.data.IdNode;
import com.avereon.data.NodeLink;
import com.avereon.util.Log;
import com.avereon.xenon.ProgramProduct;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Design extends IdNode {

	public static final DesignUnit DEFAULT_DESIGN_UNIT = DesignUnit.CENTIMETER;

	public static final String NAME = "name";

	public static final String UNIT = "unit";

	public static final String ROOT_LAYER = "root-layer";

	public static final String CURRENT_LAYER = "current-layer";

	public static final String VIEWS = "views";

	private static final System.Logger log = Log.get();

	@Deprecated
	private final CommandProcessor commandProcessor;

	private DesignContext context;

	public Design() {
		addModifyingKeys( NAME, UNIT, ROOT_LAYER );

		// Read-only values
		setValue( ROOT_LAYER, new DesignLayer() );
		defineReadOnly( ROOT_LAYER );

		// Default values
		setDesignUnit( DEFAULT_DESIGN_UNIT );

		this.commandProcessor = new DefaultCommandProcessor();
	}

	public String getName() {
		return getValue( NAME );
	}

	public Design setName( String name ) {
		setValue( NAME, name );
		return this;
	}

	public DesignUnit getDesignUnit() {
		return getValue( UNIT );
	}

	public Design setDesignUnit( DesignUnit unit ) {
		setValue( UNIT, unit );
		return this;
	}

	public DesignContext getDesignContext( ProgramProduct product ) {
		if( context == null ) context = new DesignContext( product, this );
		return context;
	}

	public Design setDesignContext( DesignContext context ) {
		if( this.context == null ) this.context = context;
		return this;
	}

	public DesignLayer getRootLayer() {
		return getValue( ROOT_LAYER );
	}

	// TODO Finish removing this method
	@Deprecated
	public CommandProcessor getCommandProcessor() {
		return commandProcessor;
	}

	// TODO Finish removing this method
	@Deprecated
	public DesignLayer getCurrentLayer() {
		// Current layer is a node link so the layer doesn't get removed from the layer tree
		NodeLink<DesignLayer> link = getValue( CURRENT_LAYER );
		return link == null ? null : link.getNode();
	}

	// TODO Finish removing this method
	@Deprecated
	public Design setCurrentLayer( DesignLayer layer ) {
		if( !getAllLayers().contains( layer ) ) throw new IllegalArgumentException( "Layer does not belong to this design" );
		// Current layer is a node link so the layer doesn't get removed from the layer tree
		setValue( CURRENT_LAYER, new NodeLink<>( Objects.requireNonNull( layer ) ) );
		return this;
	}

	public Set<DesignLayer> findLayers( String key, Object value ) {
		return getRootLayer().findLayers( key, value );
	}

	public Set<DesignLayer> getAllLayers() {
		Set<DesignLayer> layers = new HashSet<>( getRootLayer().getAllLayers() );
		layers.addAll( layers.stream().flatMap( l -> l.getAllLayers().stream() ).collect( Collectors.toSet() ) );
		return layers;
	}

	public Set<DesignView> getViews() {
		return getValues( VIEWS );
	}

	public Design addView( DesignView view ) {
		addToSet( VIEWS, view );
		return this;
	}

	public Design removeView( DesignView view ) {
		removeFromSet( VIEWS, view );
		return this;
	}

	public void clearSelected() {
		getAllLayers().stream().flatMap( l -> l.getShapes().stream() ).forEach( s -> s.setSelected( false ) );
	}

	public Map<String, ?> asMap() {
		return asMap( ID, NAME );
	}

	public Map<String, Object> asDeepMap() {
		Map<String, Object> map = new HashMap<>( asMap() );
		map.put( CURRENT_LAYER, getCurrentLayer().getId() );
		map.put( DesignLayer.LAYERS, getRootLayer().getLayers().stream().collect( Collectors.toMap( IdNode::getId, DesignLayer::asDeepMap ) ) );
		return map;
	}

	public Design updateFrom( Map<String, Object> map ) {
		map.computeIfPresent( DesignLayer.ID, ( k, v ) -> setId( String.valueOf( v ) ) );
		map.computeIfPresent( DesignLayer.NAME, ( k, v ) -> setName( String.valueOf( v ) ) );
		return this;
	}

}
