package com.avereon.cartesia.data;

import com.avereon.cartesia.DesignUnit;
import com.avereon.data.IdNode;
import com.avereon.data.NodeComparator;

import java.util.*;
import java.util.stream.Collectors;

public class DesignLayer extends DesignDrawable implements Comparable<DesignLayer> {

	public static final String NAME = "name";

	public static final String UNIT = "unit";

	// NOTE Visibility is a per tool setting

	public static final String LAYERS = "layers";

	public static final String SHAPES = "shapes";

	private static final NodeComparator<DesignLayer> comparator;

	static {
		comparator = new NodeComparator<>( ORDER, NAME );
	}

	public DesignLayer() {
		defineNaturalKey( NAME );
		addModifyingKeys( NAME, UNIT, LAYERS, SHAPES );
	}

	/**
	 * Overridden to return the specific type of this class.
	 *
	 * @param id The node id
	 * @return This instance
	 */
	public DesignLayer setId( String id ) {
		super.setValue( ID, id );
		return this;
	}

	public String getName() {
		return getValue( NAME );
	}

	public DesignLayer setName( String name ) {
		setValue( NAME, name );
		return this;
	}

	public DesignUnit getDesignUnit() {
		return getValue( UNIT );
	}

	public DesignLayer setDesignUnit( DesignUnit unit ) {
		setValue( UNIT, unit );
		return this;
	}

	public List<DesignLayer> getAllLayers() {
		List<DesignLayer> layers = new ArrayList<>( getValues( LAYERS ) );
		layers.sort( new NodeComparator<>( DesignLayer.ORDER ) );
		layers.addAll( layers.stream().flatMap( l -> l.getAllLayers().stream() ).collect( Collectors.toList() ) );
		return layers;
	}

	public Set<DesignLayer> findLayers( String key, Object value ) {
		return getAllLayers().stream().filter( l -> Objects.equals( l.getValue( key ), value ) ).collect( Collectors.toSet() );
	}

	public List<DesignLayer> getLayers() {
		return getValueList( LAYERS, new NodeComparator<>( DesignLayer.ORDER ) );
	}

	public DesignLayer addLayer( DesignLayer layer ) {
		return addLayer( layer, null, false );
	}

	public DesignLayer addLayer( DesignLayer layer, DesignLayer anchor, boolean after ) {
		List<DesignLayer> layers = getLayers();

		addToSet( LAYERS, layer );
		int size = layers.size();
		int insert = anchor == null ? -1 : layers.indexOf( anchor );
		if( after ) insert++;
		if( insert < 0 || insert > size ) insert = size;
		layers.add( insert, layer );

		updateOrder( layers );

		return this;
	}

	private <T extends DesignDrawable> List<T> updateOrder( List<T> list ) {
		int index = 0;
		for( T item : list ) {
			item.setOrder( index++ );
		}
		return list;
	}

	public DesignLayer removeLayer( DesignLayer layer ) {
		removeFromSet( LAYERS, layer );
		return this;
	}

	public Set<DesignShape> getShapes() {
		return getValues( SHAPES );
	}

	public DesignLayer addShape( DesignShape shape ) {
		addToSet( SHAPES, shape );
		return this;
	}

	public DesignLayer removeShape( DesignShape shape ) {
		removeFromSet( SHAPES, shape );
		return this;
	}

	public Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( NAME ) );
		return map;
	}

	public Map<String, Object> asDeepMap() {
		Map<String, Object> map = new HashMap<>( asMap() );
		map.put( LAYERS, getLayers().stream().collect( Collectors.toMap( IdNode::getId, DesignLayer::asMap ) ) );
		map.put( SHAPES, getShapes().stream().collect( Collectors.toMap( IdNode::getId, DesignShape::asMap ) ) );
		return map;
	}

	public DesignLayer updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( NAME ) ) setName( (String)map.get( NAME ) );
		return this;
	}

	@Override
	public int compareTo( DesignLayer that ) {
		return comparator.compare( this, that );
	}

	@Override
	public String toString() {
		return super.toString( NAME );
	}

}
