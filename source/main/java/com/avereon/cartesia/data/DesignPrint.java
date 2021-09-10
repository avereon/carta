package com.avereon.cartesia.data;

import com.avereon.data.IdNode;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@CustomLog
public class DesignPrint extends DesignView {

	/**
	 * Overridden to return the specific type of this class.
	 *
	 * @param id The node id
	 * @return This instance
	 */
	public DesignPrint setId( String id ) {
		super.setId( id );
		return this;
	}

	public DesignPrint setOrder( int order ) {
		setValue( ORDER, order );
		return this;
	}

	public DesignPrint setName( String name ) {
		setValue( NAME, name );
		return this;
	}

	public DesignPrint setOrigin( Point3D origin ) {
		setValue( ORIGIN, origin );
		return this;
	}

	public DesignPrint setZoom( Double value ) {
		setValue( ZOOM, value );
		return this;
	}

	public DesignPrint setRotate( Double value ) {
		setValue( ROTATE, value );
		return this;
	}

	public DesignPrint setLayers( Collection<DesignLayer> layers ) {
		super.setLayers( layers );
		return this;
	}

	@Override
	public Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		//map.putAll( asMap( NAME, ORDER, ORIGIN, ROTATE, ZOOM ) );
		return map;
	}

	public Map<String,Object> asDeepMap() {
		Map<String, Object> map = new HashMap<>( asMap() );
		if( getLayers().size() > 0 ) map.put( LAYERS, getLayers().stream().map( IdNode::getId ).collect( Collectors.toSet()) );
		return map;
	}

	public DesignPrint updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
//		if( map.containsKey( NAME ) ) setName( (String)map.get( NAME ) );
//		if( map.containsKey( ORDER ) ) setOrder( (Integer)map.get( ORDER ) );
//		if( map.containsKey( ORIGIN ) ) setOrigin( (Point3D)map.get( ORDER ) );
//		if( map.containsKey( ROTATE ) ) setRotate( (Double)map.get( ROTATE ) );
//		if( map.containsKey( ZOOM ) ) setZoom( (Double)map.get( ZOOM ) );
		return this;
	}

}
