package com.avereon.cartesia.data;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.tool.DesignContext;
import com.avereon.data.IdNode;
import com.avereon.util.Log;
import com.avereon.xenon.ProgramProduct;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Design extends IdNode {

	public static final DesignUnit DEFAULT_DESIGN_UNIT = DesignUnit.CENTIMETER;

	public static final String NAME = "name";

	public static final String UNIT = "unit";

	public static final String ROOT_LAYER = "root-layer";

	@Deprecated
	public static final String CURRENT_LAYER = "current-layer";

	public static final String VIEWS = "views";

	private static final System.Logger log = Log.get();

	private DesignContext context;

	public Design() {
		addModifyingKeys( NAME, UNIT, ROOT_LAYER );

		// Read-only values
		setValue( ROOT_LAYER, new DesignLayer() );
		defineReadOnly( ROOT_LAYER );

		// Default values
		setDesignUnit( DEFAULT_DESIGN_UNIT );
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

	public synchronized DesignContext getDesignContext( ProgramProduct product ) {
		if( context == null ) context = new DesignContext( product, this );
		return context;
	}

	public DesignLayer getRootLayer() {
		return getValue( ROOT_LAYER );
	}

	public DesignLayer findLayerById( String id ) {
		for( DesignLayer layer : getAllLayersAndRoot() ) {
			if( layer.getId().equals( id ) ) return layer;
		}
		return null;
	}

	public Set<DesignLayer> findLayers( String key, Object value ) {
		return getRootLayer().findLayers( key, value );
	}

	public List<DesignLayer> getAllLayersAndRoot() {
		List<DesignLayer> layers = new ArrayList<>();
		layers.add( getRootLayer() );
		layers.addAll( getRootLayer().getAllLayers() );
		return layers;
	}

	public List<DesignLayer> getAllLayers() {
		return getRootLayer().getAllLayers();
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
		map.put( DesignLayer.LAYERS, getRootLayer().getLayers().stream().collect( Collectors.toMap( IdNode::getId, DesignLayer::asDeepMap ) ) );
		return map;
	}

	public Design updateFrom( Map<String, Object> map ) {
		map.computeIfPresent( DesignLayer.ID, ( k, v ) -> setId( String.valueOf( v ) ) );
		map.computeIfPresent( DesignLayer.NAME, ( k, v ) -> setName( String.valueOf( v ) ) );
		return this;
	}

}
