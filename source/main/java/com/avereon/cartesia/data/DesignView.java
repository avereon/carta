package com.avereon.cartesia.data;

import com.avereon.data.IdNode;
import com.avereon.data.NodeLink;
import javafx.geometry.Point3D;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class to represent arbitrary selections of layers to provide different
 * "views" of the design.
 */
public class DesignView extends DesignNode {

	public static final String NAME = "name";

	public static final String ORDER = "order";

	public static final String ORIGIN = "origin";

	public static final String ROTATE = "rotate";

	public static final String ZOOM = "zoom";

	public static final String LAYERS = "layers";

	public DesignView() {
		defineNaturalKey( NAME );
		addModifyingKeys( NAME, ORDER, ORIGIN, ROTATE, ZOOM, LAYERS );
	}

	/**
	 * Overridden to return the specific type of this class.
	 *
	 * @param id The node id
	 * @return This instance
	 */
	@SuppressWarnings( "unchecked" )
	public DesignView setId( String id ) {
		super.setId( id );
		return this;
	}

	public int getOrder() {
		return getValue( ORDER, -1 );
	}

	public DesignView setOrder( int order ) {
		setValue( ORDER, order );
		return this;
	}

	public String getName() {
		return getValue( NAME );
	}

	public DesignView setName( String name ) {
		setValue( NAME, name );
		return this;
	}

	public Point3D getOrigin() {
		return getValue( ORIGIN );
	}

	public DesignView setOrigin( Point3D origin ) {
		setValue( ORIGIN, origin );
		return this;
	}

	public Double getZoom() {
		return getValue( ZOOM );
	}

	public DesignView setZoom( Double value ) {
		setValue( ZOOM, value );
		return this;
	}

	public Double getRotate() {
		return getValue( ROTATE );
	}

	public DesignView setRotate( Double value ) {
		setValue( ROTATE, value );
		return this;
	}

	public Set<DesignLayer> getLayers() {
		// Use node links for the layers
		Collection<NodeLink<DesignLayer>> links = getValues( LAYERS );
		return links.stream().map( NodeLink::getNode ).collect( Collectors.toSet() );
	}

	public DesignView setLayers( Collection<DesignLayer> layers ) {
		// Use node links for the layers
		layers.stream().map( NodeLink::new ).forEach( l -> addToSet( LAYERS,l ) );
		return this;
	}

	@Override
	public Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( NAME, ORDER, ORIGIN, ROTATE, ZOOM ) );
		return map;
	}

	public Map<String,Object> asDeepMap() {
		Map<String, Object> map = new HashMap<>( asMap() );
		if( getLayers().size() > 0 ) map.put( LAYERS, getLayers().stream().map( IdNode::getId ).collect( Collectors.toSet()) );
		return map;
	}

	public DesignView updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( NAME ) ) setName( (String)map.get( NAME ) );
		if( map.containsKey( ORDER ) ) setOrder( (Integer)map.get( ORDER ) );
		if( map.containsKey( ORIGIN ) ) setOrigin( (Point3D)map.get( ORDER ) );
		if( map.containsKey( ROTATE ) ) setRotate( (Double)map.get( ROTATE ) );
		if( map.containsKey( ZOOM ) ) setZoom( (Double)map.get( ZOOM ) );
		return this;
	}

}
