package com.avereon.cartesia.data;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.geometry.CsaShape;
import com.avereon.data.IdNode;
import com.avereon.data.NodeComparator;
import com.avereon.util.NumberUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DesignLayer extends DesignDraw implements Comparable<DesignLayer> {

	public static final String NAME = "name";

	public static final String UNIT = "unit";

	public static final String ORDER = "order";

	public static final String SHAPES = "shapes";

	private static final NodeComparator<DesignLayer> comparator;

	static {
		comparator = new NodeComparator<>( ORDER, NAME );
	}

	public DesignLayer() {
		defineNaturalKey( NAME );
		addModifyingKeys( NAME, SHAPES );
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

	public int getOrder() {
		return getValue( ORDER, 0 );
	}

	public DesignLayer setOrder( int order ) {
		setValue( ORDER, order );
		return this;
	}

	public DesignUnit getDesignUnit() {
		return getValue( UNIT );
	}

	public DesignLayer setDesignUnit( DesignUnit unit ) {
		setValue( UNIT, unit );
		return this;
	}

	public Set<CsaShape> getShapes() {
		return getValues( SHAPES );
	}

	public void addShape( CsaShape shape ) {
		addToSet( SHAPES, shape );
	}

	public void removeShape( CsaShape shape ) {
		removeFromSet( SHAPES, shape );
	}

	public Map<String, String> asMap() {
		Map<String, String> map = super.asMap();
		map.putAll( asMap( NAME, ORDER ) );
		return map;
	}

	public Map<String, Object> asDeepMap() {
		Map<String, Object> map = new HashMap<>( asMap() );
		map.put( SHAPES, getShapes().stream().collect( Collectors.toMap( IdNode::getId, CsaShape::asMap ) ) );
		return map;
	}

	public DesignLayer updateFrom( Map<String, Object> map ) {
		setId( String.valueOf( map.get( DesignLayer.ID ) ) );
		map.computeIfPresent( DesignLayer.ID, ( k, v ) -> setId( String.valueOf( v ) ) );
		map.computeIfPresent( DesignLayer.NAME, ( k, v ) -> setName( String.valueOf( v ) ) );
		map.computeIfPresent( DesignLayer.ORDER, ( k, v ) -> NumberUtil.parseInt( v, -1 ) );

		// TODO Restore the other attributes
		return this;
	}

	@Override
	public int compareTo( DesignLayer that ) {
		return comparator.compare( this, that );
	}

	//	private ShapeNode shapeNode() {
	//		return computeIfAbsent( SHAPES, k -> new ShapeNode() );
	//	}

}
