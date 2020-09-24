package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.tool.DesignShapeView;
import com.avereon.data.NodeComparator;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.settings.SettingsPage;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;

import java.io.IOException;
import java.util.Map;

public abstract class DesignShape extends DesignDrawable implements Comparable<DesignShape> {

	public static final String SHAPE = "shape";

	public static final String ORIGIN = "origin";

	public static final String SELECTED = "selected";

	public static final NodeComparator<DesignShape> comparator;

	protected SettingsPage page;

	static {
		comparator = new NodeComparator<>( ORDER );
	}

	public DesignShape() {
		addModifyingKeys( ORIGIN );
	}

	public Point3D getOrigin() {
		return getValue( ORIGIN );
	}

	public DesignShape setOrigin( Point3D origin ) {
		setValue( ORIGIN, origin );
		return this;
	}

	public boolean isSelected() {
		return getValue( SELECTED, false );
	}

	public DesignShape setSelected( boolean selected ) {
		setValue( SELECTED, selected );
		return this;
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

	@Override
	public int compareTo( DesignShape that ) {
		return comparator.compare( this, that );
	}

	public abstract SettingsPage getPropertiesPage( ProgramProduct product ) throws IOException;

}
