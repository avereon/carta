package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.data.Node;
import com.avereon.data.NodeComparator;
import javafx.geometry.Point3D;

import java.util.Comparator;
import java.util.Map;

public abstract class DesignShape extends DesignDrawable {

	public static final DesignShape NONE = new DesignShape() {}.setId( "NONE" );

	public static final String SHAPE = "shape";

	public static final String ORIGIN = "origin";

	public static final String SELECTED = "selected";

	public static final String PREVIEW = "preview";

	public DesignShape() {
		this( null );
	}

	public DesignShape( Point3D origin ) {
		addModifyingKeys( ORIGIN );
		setOrigin( origin );
	}

	public Point3D getOrigin() {
		return getValue( ORIGIN );
	}

	public <T extends DesignShape> T setOrigin( Point3D origin ) {
		setValue( ORIGIN, origin );
		return (T)this;
	}

	public boolean isPreview() {
		return getValue( PREVIEW, false );
	}

	public DesignShape setPreview( boolean preview ) {
		setValue( PREVIEW, preview ? true : null );
		return this;
	}

	public boolean isSelected() {
		return getValue( SELECTED, false );
	}

	public DesignShape setSelected( boolean selected ) {
		setValue( SELECTED, selected ? true : null );
		return this;
	}

	public double distanceTo( Point3D point ) {
		return Double.NaN;
	}

	public DesignShape clone() {
		DesignShape shape = cloneShape();
		this.getLayer().addShape( shape );
		return shape;
	}

	protected DesignShape cloneShape() {
		throw new UnsupportedOperationException();
	}

	public void apply( CadTransform transform ) {
		throw new UnsupportedOperationException();
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( ORIGIN ) );
		return map;
	}

	public DesignShape updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		setOrigin( ParseUtil.parsePoint3D( (String)map.get( ORIGIN ) ) );
		return this;
	}

	public DesignShape updateFrom( DesignShape shape ) {
		return this;
	}

	@Override
	public <T extends Node> Comparator<T> getComparator() {
		return new NodeComparator<>( ORDER );
	}

}
