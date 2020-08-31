package com.avereon.cartesia.data;

import com.avereon.cartesia.DesignUnit;
import com.avereon.data.IdNode;
import com.avereon.data.NodeComparator;

import java.util.*;
import java.util.stream.Collectors;

public class DesignLayer extends DesignDrawable implements Comparable<DesignLayer> {

	public static final String NAME = "name";

	public static final String UNIT = "unit";

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
		layers.addAll( layers.stream().flatMap( l -> l.getAllLayers().stream() ).collect( Collectors.toList()) );
		return layers;
	}

	public List<DesignLayer> getLayers() {
		return getValueList( LAYERS, new NodeComparator<>( DesignLayer.ORDER ) );
	}

	public DesignLayer addLayer( DesignLayer layer ) {
		addToSet( LAYERS, layer );
		return this;
	}

	public DesignLayer removeLayer( DesignLayer layer ) {
		removeFromSet( LAYERS, layer );
		return this;
	}

	public Set<CsaShape> getShapes() {
		return getValues( SHAPES );
	}

	public DesignLayer addShape( CsaShape shape ) {
		addToSet( SHAPES, shape );
		return this;
	}

	public DesignLayer removeShape( CsaShape shape ) {
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
		map.put( SHAPES, getShapes().stream().collect( Collectors.toMap( IdNode::getId, CsaShape::asMap ) ) );
		return map;
	}

	public DesignLayer updateFrom( Map<String, String> map ) {
		super.updateFrom( map );
		if( map.containsKey( NAME ) ) setName( map.get( NAME ) );
		return this;
	}

	@Override
	public int compareTo( DesignLayer that ) {
		return comparator.compare( this, that );
	}

}
