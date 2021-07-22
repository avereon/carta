package com.avereon.cartesia.data;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.math.CadMath;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.data.IdNode;
import com.avereon.data.Node;
import com.avereon.data.NodeComparator;
import com.avereon.zerra.color.Paints;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import lombok.CustomLog;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@CustomLog
public class DesignLayer extends DesignDrawable {

	public static final String NAME = "name";

	public static final String UNIT = "unit";

	// NOTE Visibility is a setting in the design tool, not here

	public static final String LAYERS = "layers";

	public static final String SHAPES = "shapes";

	static final String DEFAULT_DRAW_PAINT = "#000000ff";

	static final String DEFAULT_DRAW_WIDTH = "0.05";

	static final String DEFAULT_DRAW_CAP = StrokeLineCap.BUTT.name().toLowerCase();

	static final String DEFAULT_DRAW_PATTERN = null;

	static final String DEFAULT_FILL_PAINT = null;

	//private static final NodeComparator<DesignLayer> comparator;

	//	static {
	//		comparator = new NodeComparator<>( ORDER, NAME );
	//	}

	public DesignLayer() {
		defineNaturalKey( NAME );
		addModifyingKeys( NAME, UNIT, LAYERS, SHAPES );

		setDrawPaint( DEFAULT_DRAW_PAINT );
		setDrawWidth( DEFAULT_DRAW_WIDTH );
		setDrawPattern( DEFAULT_DRAW_PATTERN );
		setDrawCap( DEFAULT_DRAW_CAP );
		setFillPaint( DEFAULT_FILL_PAINT );

		setSetModifyFilter( SHAPES, n -> n.isNotSet( DesignShape.REFERENCE ) );
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

	public boolean isRootLayer() {
		return this == getDesign().getRootLayer();
	}

	public String getFullName() {
		if( isRootLayer() ) {
			return "/";
		} else if( getLayer().isRootLayer() ) {
			return getName();
		}
		return getLayer().getFullName() + "/" + getName();
	}

	public DesignUnit getDesignUnit() {
		return getValue( UNIT );
	}

	public DesignLayer setDesignUnit( DesignUnit unit ) {
		setValue( UNIT, unit );
		return this;
	}

	/**
	 * Deeply get all the descendent layers in this layer.
	 * @return All descendent layers of this layer
	 */
	public List<DesignLayer> getAllLayers() {
		List<DesignLayer> layers = new ArrayList<>();
		getLayers().forEach( layer -> {
			layers.add( layer );
			layers.addAll( layer.getAllLayers() );
		} );
		return layers;
	}

	public Set<DesignLayer> findLayers( String key, Object value ) {
		return getAllLayers().stream().filter( l -> Objects.equals( l.getValue( key ), value ) ).collect( Collectors.toSet() );
	}

	/**
	 * Get only the child layers of this layer, not any descendents.
	 * @return The child layers of this layer
	 */
	public List<DesignLayer> getLayers() {
		return getValueList( LAYERS, new NodeComparator<>( DesignLayer.ORDER, DesignLayer.NAME ) );
	}

	public DesignLayer addLayer( DesignLayer layer ) {
		addToSet( LAYERS, layer );
		return this;
	}

	@SuppressWarnings( "UnusedReturnValue" )
	public DesignLayer addLayerBeforeOrAfter( DesignLayer layer, DesignLayer anchor, boolean after ) {
		List<DesignLayer> layers = getLayers();

		int size = layers.size();
		int insert = anchor == null ? -1 : layers.indexOf( anchor );
		if( after ) insert++;
		if( insert < 0 || insert > size ) insert = size;
		layers.add( insert, layer );
		updateOrder( layers );
		addLayer( layer );

		return this;
	}

	@SuppressWarnings( "UnusedReturnValue" )
	private <T extends DesignDrawable> List<T> updateOrder( List<T> list ) {
		AtomicInteger counter = new AtomicInteger( 0 );
		list.forEach( i -> i.setOrder( counter.getAndIncrement() ) );
		return list;
	}

	@SuppressWarnings( "UnusedReturnValue" )
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

	@SuppressWarnings( "UnusedReturnValue" )
	public DesignLayer removeShape( DesignShape shape ) {
		removeFromSet( SHAPES, shape );
		return this;
	}

	@SuppressWarnings( "UnusedReturnValue" )
	public <T extends DesignDrawable> T addDrawable( T drawable ) {
		if( drawable instanceof DesignShape ) {
			addShape( (DesignShape)drawable );
		} else if( drawable instanceof DesignLayer ) {
			addLayer( (DesignLayer)drawable );
		}
		return drawable;
	}

	public <T extends DesignDrawable> T removeDrawable( T drawable ) {
		if( drawable instanceof DesignShape ) {
			removeShape( (DesignShape)drawable );
		} else if( drawable instanceof DesignLayer ) {
			removeLayer( (DesignLayer)drawable );
		}
		return drawable;
	}

	@Override
	public String getDrawPaint() {
		return getValue( DRAW_PAINT );
	}

	@Override
	public Paint calcDrawPaint() {
		return Paints.parseWithNullOnException( getDrawPaint() );
	}

	@Override
	public String getDrawWidth() {
		return getValue( DRAW_WIDTH, DEFAULT_DRAW_WIDTH );
	}

	@Override
	public double calcDrawWidth() {
		return CadMath.evalNoException( getDrawWidth() );
	}

	@Override
	public String getDrawPattern() {
		return getValue( DRAW_PATTERN, DEFAULT_DRAW_PATTERN );
	}

	@Override
	public List<Double> calcDrawPattern() {
		return CadShapes.parseDashPattern( getDrawPattern() );
	}

	@Override
	public String getDrawCap() {
		return getValue( DRAW_CAP, DEFAULT_DRAW_CAP );
	}

	@Override
	public StrokeLineCap calcDrawCap() {
		return StrokeLineCap.valueOf( getDrawCap().toUpperCase() );
	}

	@Override
	public String getFillPaint() {
		return getValue( FILL_PAINT );
	}

	@Override
	public Paint calcFillPaint() {
		return Paints.parseWithNullOnException( getFillPaint() );
	}

	@Override
	public Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( NAME ) );
		return map;
	}

	public Map<String, Object> asDeepMap() {
		Map<String, Object> map = new HashMap<>( asMap() );
		map.put( LAYERS, getLayers().stream().collect( Collectors.toMap( IdNode::getId, DesignLayer::asDeepMap ) ) );
		map.put( SHAPES, getShapes().stream().filter( s -> !s.isReference() ).collect( Collectors.toMap( IdNode::getId, DesignShape::asMap ) ) );
		return map;
	}

	public DesignLayer updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( NAME ) ) setName( (String)map.get( NAME ) );
		return this;
	}

	@Override
	public <T extends Node> Comparator<T> getComparator() {
		return new NodeComparator<>( ORDER, NAME );
	}

	@Override
	public String toString() {
		return super.toString( NAME );
	}

}
