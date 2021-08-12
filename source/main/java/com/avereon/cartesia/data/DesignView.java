package com.avereon.cartesia.data;

import com.avereon.data.NodeLink;
import javafx.geometry.Point3D;

import java.util.Collection;
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

	public static final String VIEW_ROTATE = "rotate";

	public static final String ZOOM = "zoom";

	public static final String LAYER_LINKS = "layer-links";

	public DesignView() {
		defineNaturalKey( NAME );
		addModifyingKeys( NAME, ORDER, ORIGIN, VIEW_ROTATE, ZOOM, LAYER_LINKS );
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

	public Double getViewRotate() {
		return getValue( VIEW_ROTATE );
	}

	public DesignView setViewRotate( Double value ) {
		setValue( VIEW_ROTATE, value );
		return this;
	}

	public Set<DesignLayer> getVisibleLayers() {
		Collection<NodeLink<DesignLayer>> links = getValue( LAYER_LINKS );
		return links.stream().map( NodeLink::getNode ).collect( Collectors.toSet());
	}

	public DesignView setVisibleLayers( Collection<DesignLayer> layers ) {
		addNodes( LAYER_LINKS, layers.stream().map( NodeLink::new ).collect( Collectors.toSet()) );
		return this;
	}

	public DesignView updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( NAME ) ) setName( (String)map.get( NAME ) );
		if( map.containsKey( ORDER ) ) setOrder( (Integer)map.get( ORDER ) );
		return this;
	}

}
