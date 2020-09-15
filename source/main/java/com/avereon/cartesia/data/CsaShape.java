package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.cartesia.tool.DesignPane;
import com.avereon.data.NodeComparator;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.settings.SettingsPage;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class CsaShape extends DesignDrawable implements Comparable<CsaShape> {

	public static final String SHAPE = "shape";

	public static final String ORIGIN = "origin";

	public static final String SELECTED = "selected";

	static final String SHAPE_META_DATA = "shape-meta-data";

	static final String CONSTRUCTION_POINTS = "construction-points";

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

	public CsaShape updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		setOrigin( ParseUtil.parsePoint3D( (String)map.get( ORIGIN ) ) );
		return this;
	}

	@Override
	public int compareTo( CsaShape that ) {
		return comparator.compare( this, that );
	}

	public List<Shape> generateGeometry() {
		return List.of();
	}

	public List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		return List.of();
	}

	public abstract SettingsPage getPropertiesPage( ProgramProduct product ) throws IOException;

	<V extends Shape> V configureShape( V shape ) {
		shape.getProperties().put( SHAPE_META_DATA, this );

		shape.setStrokeWidth( calcDrawWidth() );
		shape.setStroke( calcDrawColor() );
		shape.setFill( calcFillColor() );

		// Add listeners for property changes
		register( DRAW_WIDTH, e -> shape.setStrokeWidth( calcDrawWidth() ) );
		register( DRAW_COLOR, e -> shape.setStroke( calcDrawColor() ) );
		register( FILL_COLOR, e -> shape.setFill( calcFillColor() ) );

		// Selection listener
		register( CsaShape.SELECTED, e -> {
			shape.setStroke( e.getNewValue() ? calcSelectDrawColor() : calcDrawColor() );
			shape.setFill( e.getNewValue() ? calcSelectFillColor() : calcFillColor() );
		} );

		return shape;
	}

	ConstructionPoint cp( DesignPane pane, DoubleProperty xProperty, DoubleProperty yProperty ) {
		ConstructionPoint cp = new ConstructionPoint();
		cp.layoutXProperty().bind( xProperty.multiply( pane.scaleXProperty() ) );
		cp.layoutYProperty().bind( yProperty.multiply( pane.scaleYProperty() ).negate() );
		return cp;
	}

	public static CsaShape getFrom( Shape s ) {
		return (CsaShape)s.getProperties().get( DesignPane.SHAPE_META_DATA );
	}

}
