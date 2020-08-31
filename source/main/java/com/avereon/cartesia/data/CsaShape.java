package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.cartesia.tool.DesignPane;
import com.avereon.data.NodeComparator;
import com.avereon.zerra.color.Colors;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.util.List;
import java.util.Map;

public abstract class CsaShape extends DesignDrawable implements Comparable<CsaShape> {

	public static final String SHAPE = "shape";

	public static final String ORIGIN = "origin";

	public static final String SELECTED = "selected";

	static final String SHAPE_META_DATA = "shape-meta-data";

	static final String CONSTRUCTION_POINTS = "construction-points";

	private static final Color DEFAULT_SELECT_DRAW = Colors.web( "#ff00ffff" );

	private static final Color DEFAULT_SELECT_FILL = Colors.web( "#ff00ff40" );

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

	public List<Shape> generateGeometry() {
		return List.of();
	}

	public List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		return List.of();
	}

	<V extends Shape> V configureShape( V shape ) {
		shape.getProperties().put( SHAPE_META_DATA, this );

		// All these...and the listeners can probably be handled in DesignDrawable
		// FIXME Use "calculated" methods instead of direct methods
		shape.setStroke( getDrawColor() );
		shape.setStrokeWidth( getDrawWidth() );
		shape.setFill( getFillColor() );

		// Add listeners for property changes
		// FIXME Use "calculated" methods instead of direct methods
		register( DRAW_WIDTH, e -> shape.setStrokeWidth( getDrawWidth() ) );
		register( DRAW_COLOR, e -> shape.setStroke( getDrawColor() ) );
		register( FILL_COLOR, e -> shape.setFill( getFillColor() ) );

		// Selection listener
		register( CsaShape.SELECTED, e -> {
			// FIXME Use "calculated" methods instead of direct methods
			shape.setStroke( e.getNewValue() ? DEFAULT_SELECT_DRAW : getDrawColor() );
			shape.setFill( e.getNewValue() ? DEFAULT_SELECT_FILL : getFillColor() );
		} );

		return shape;
	}

	ConstructionPoint cp( DesignPane pane, DoubleProperty xProperty, DoubleProperty yProperty ) {
		ConstructionPoint cp = new ConstructionPoint();
		cp.layoutXProperty().bind( xProperty.multiply( pane.scaleXProperty() ) );
		cp.layoutYProperty().bind( yProperty.multiply( pane.scaleYProperty() ).negate() );
		return cp;
	}

}
