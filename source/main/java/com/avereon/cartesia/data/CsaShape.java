package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.data.NodeComparator;
import javafx.geometry.Point3D;

import java.util.Map;

public abstract class CsaShape extends DesignDrawable implements Comparable<CsaShape> {

	public static final String SHAPE = "shape";

	public static final String ORIGIN = "origin";

	public static final String SELECTED = "selected";

	public static final NodeComparator<CsaShape> comparator;

	static {
		comparator = new NodeComparator<>( ORDER );
	}

	public CsaShape() {
		addModifyingKeys( ORIGIN );
	}

	public Point3D getOrigin() {
		return getValue( ORIGIN );
	}

	public CsaShape setOrigin( Point3D origin ) {
		setValue( ORIGIN, origin );
		return this;
	}

	public boolean isSelected() {
		return getValue( SELECTED, false );
	}

	public CsaShape setSelected( boolean selected ) {
		setValue( SELECTED, selected );
		return this;
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( ORIGIN ) );
		return map;
	}

	public CsaShape updateFrom( Map<String, String> map ) {
		super.updateFrom( map );
		setOrigin( ParseUtil.parsePoint3D( map.get( ORIGIN ) ) );
		return this;
	}

	@Override
	public int compareTo( CsaShape that ) {
		return comparator.compare( this, that );
	}

}
