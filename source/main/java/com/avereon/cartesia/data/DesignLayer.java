package com.avereon.cartesia.data;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.geometry.CsaShape;
import com.avereon.data.IdNode;
import com.avereon.data.NodeComparator;
import javafx.scene.paint.Color;

import java.util.Map;
import java.util.Set;

public class DesignLayer extends IdNode implements Comparable<DesignLayer> {

	public static final String NAME = "name";

	public static final String UNIT = "unit";

	public static final String DRAW_WIDTH = "draw-width";

	public static final String DRAW_COLOR = "draw-color";

	public static final String FILL_COLOR = "fill-color";

	public static final String ORDER = "order";

	public static final String SHAPES = "shapes";

	private static final NodeComparator<DesignLayer> comparator;

	static {
		comparator = new NodeComparator<>( ORDER, NAME );
	}

	public DesignLayer() {
		defineNaturalKey( NAME );
		addModifyingKeys( NAME, DRAW_WIDTH, DRAW_COLOR, FILL_COLOR, SHAPES );
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

	public double getOrder() {
		return getValue( ORDER, 0 );
	}

	public DesignLayer setOrder( double order ) {
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

	public double getDrawWidth() {
		return getValue( DRAW_WIDTH );
	}

	public DesignLayer setDrawWidth( double width ) {
		setValue( DRAW_WIDTH, width );
		return this;
	}

	public Color getDrawColor() {
		return getValue( DRAW_COLOR );
	}

	public DesignLayer setDrawColor( Color color ) {
		setValue( DRAW_COLOR, color );
		return this;
	}

	public Color getFillColor() {
		return getValue( FILL_COLOR );
	}

	public DesignLayer setFillColor( Color color ) {
		setValue( FILL_COLOR, color );
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

	public Map<String, ?> asMap() {
		return asMap( ID, NAME );
	}

	@Override
	public int compareTo( DesignLayer that ) {
		return comparator.compare( this, that );
	}

	//	private ShapeNode shapeNode() {
	//		return computeIfAbsent( SHAPES, k -> new ShapeNode() );
	//	}

}
