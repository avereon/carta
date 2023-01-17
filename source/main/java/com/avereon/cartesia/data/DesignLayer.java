package com.avereon.cartesia.data;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.math.CadMath;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.data.IdNode;
import com.avereon.data.Node;
import com.avereon.data.NodeComparator;
import com.avereon.zarra.color.Paints;
import com.avereon.zarra.font.FontUtil;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import lombok.CustomLog;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SuppressWarnings( "UnusedReturnValue" )
@CustomLog
public class DesignLayer extends DesignDrawable {

	public static final String NAME = "name";

	public static final String UNIT = "unit";

	public static final String TEXT_FONT = "text-font";

	public static final String TEXT_FILL_PAINT = "text-fill-paint";

	public static final String TEXT_DRAW_PAINT = "text-draw-paint";

	public static final String TEXT_DRAW_WIDTH = "text-draw-width";

	public static final String TEXT_DRAW_CAP = "text-draw-cap";

	public static final String TEXT_DRAW_PATTERN = "text-draw-pattern";

	// NOTE Visibility state is stored in the design tool,
	// instead of here in the data model so that each tool can have different
	// layers visible.
	//public static final String VISIBLE = "visible";

	public static final String LAYERS = "layers";

	public static final String SHAPES = "shapes";

	static final String DEFAULT_DRAW_PAINT = "#000000ff";

	static final String DEFAULT_DRAW_WIDTH = "0.05";

	static final String DEFAULT_DRAW_CAP = StrokeLineCap.ROUND.name().toLowerCase();

	static final String DEFAULT_DRAW_PATTERN = null;

	static final String DEFAULT_FILL_PAINT = null;

	static final String DEFAULT_TEXT_FONT = "System|Regular|1.0";

	static final String DEFAULT_TEXT_FILL_PAINT = "#000000ff";

	static final String DEFAULT_TEXT_DRAW_PAINT = null;

	static final String DEFAULT_TEXT_DRAW_WIDTH = "0.05";

	static final String DEFAULT_TEXT_DRAW_CAP = StrokeLineCap.ROUND.name().toLowerCase();

	static final String DEFAULT_TEXT_DRAW_PATTERN = null;

	public DesignLayer() {
		defineNaturalKey( NAME );
		addModifyingKeys( NAME, UNIT, TEXT_FONT, TEXT_FILL_PAINT, TEXT_DRAW_PAINT, TEXT_DRAW_WIDTH, TEXT_DRAW_CAP, TEXT_DRAW_PATTERN, LAYERS, SHAPES );

		setDrawPaint( DEFAULT_DRAW_PAINT );
		setDrawWidth( DEFAULT_DRAW_WIDTH );
		setDrawPattern( DEFAULT_DRAW_PATTERN );
		setDrawCap( DEFAULT_DRAW_CAP );
		setFillPaint( DEFAULT_FILL_PAINT );

		setTextFont( DEFAULT_TEXT_FONT );
		setTextFillPaint( DEFAULT_TEXT_FILL_PAINT );
		setTextDrawPaint( DEFAULT_TEXT_DRAW_PAINT );
		setTextDrawWidth( DEFAULT_TEXT_DRAW_WIDTH );
		setTextDrawPattern( DEFAULT_TEXT_DRAW_PATTERN );
		setTextDrawCap( DEFAULT_TEXT_DRAW_CAP );

		setSetModifyFilter( SHAPES, n -> n.isNotSet( DesignShape.REFERENCE ) );
	}

	/**
	 * Overridden to return the specific type of this class.
	 *
	 * @param id The node id
	 * @return This instance
	 */
	@SuppressWarnings( "unchecked" )
	public DesignLayer setId( String id ) {
		super.setId( id );
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
		return getDesign().filter( design -> this == design.getLayers() ).isPresent();
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
	 *
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
	 *
	 * @return The child layers of this layer
	 */
	public List<DesignLayer> getLayers() {
		return getValueList( LAYERS, getComparator() );
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

		getDesign().ifPresent( d -> {
			d.getViews().forEach( v -> v.removeLayer( layer ) );
			d.getPrints().forEach( p -> p.removeLayer( layer ) );
		} );

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
		return getValue( DRAW_PAINT, DEFAULT_DRAW_PAINT );
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
		return getValue( FILL_PAINT, DEFAULT_FILL_PAINT );
	}

	@Override
	public Paint calcFillPaint() {
		return Paints.parseWithNullOnException( getFillPaint() );
	}

	public String getTextFont() {
		return getValue( TEXT_FONT, DEFAULT_TEXT_FONT );
	}

	public Font calcTextFont() {
		return FontUtil.decode( getTextFont() );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setTextFont( String value ) {
		setValue( TEXT_FONT, value );
		return (T)this;
	}

	public String getTextFillPaint() {
		return getValue( TEXT_FILL_PAINT, DEFAULT_TEXT_FILL_PAINT );
	}

	public Paint calcTextFillPaint() {
		return Paints.parseWithNullOnException( getTextFillPaint() );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setTextFillPaint( String value ) {
		setValue( TEXT_FILL_PAINT, value );
		return (T)this;
	}

	public String getTextDrawPaint() {
		return getValue( TEXT_DRAW_PAINT, DEFAULT_TEXT_DRAW_PAINT );
	}

	public Paint calcTextDrawPaint() {
		return Paints.parseWithNullOnException( getTextDrawPaint() );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setTextDrawPaint( String value ) {
		setValue( TEXT_DRAW_PAINT, value );
		return (T)this;
	}

	public String getTextDrawWidth() {
		return getValue( TEXT_DRAW_WIDTH, DEFAULT_TEXT_DRAW_WIDTH );
	}

	public double calcTextDrawWidth() {
		return CadMath.evalNoException( getTextDrawWidth() );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setTextDrawWidth( String value ) {
		setValue( TEXT_DRAW_WIDTH, value );
		return (T)this;
	}

	public String getTextDrawPattern() {
		return getValue( TEXT_DRAW_PATTERN, DEFAULT_TEXT_DRAW_PATTERN );
	}

	public List<Double> calcTextDrawPattern() {
		return CadShapes.parseDashPattern( getTextDrawPattern() );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setTextDrawPattern( String value ) {
		setValue( TEXT_DRAW_PATTERN, value );
		return (T)this;
	}

	public String getTextDrawCap() {
		return getValue( TEXT_DRAW_CAP, DEFAULT_TEXT_DRAW_CAP );
	}

	public StrokeLineCap calcTextDrawCap() {
		return StrokeLineCap.valueOf( getTextDrawCap().toUpperCase() );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setTextDrawCap( String value ) {
		setValue( TEXT_DRAW_CAP, value );
		return (T)this;
	}

	@Override
	public Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( NAME, TEXT_FONT, TEXT_FILL_PAINT, TEXT_DRAW_PAINT, TEXT_DRAW_WIDTH, TEXT_DRAW_CAP, TEXT_DRAW_PATTERN ) );

		//		if( Objects.equals( getTextDrawPaint(), "none" ) ) map.remove( TEXT_DRAW_PAINT ); // This one
		//		if( Objects.equals( getTextDrawPattern(), "null" ) ) map.remove( TEXT_DRAW_PATTERN ); // This one

		//		if( Objects.equals( getTextFillPaint(), "none" ) ) map.remove( TEXT_FILL_PAINT );
		//		if( Objects.equals( getTextDrawPaint(), "none" ) ) map.remove( TEXT_DRAW_PAINT );

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

		// Fix pattern data
		String textDrawPattern = (String)map.get( TEXT_DRAW_PATTERN );
		if( "0".equals( textDrawPattern ) ) textDrawPattern = null;
		if( "".equals( textDrawPattern ) ) textDrawPattern = null;

		if( map.containsKey( NAME ) ) setName( (String)map.get( NAME ) );

		// Text
		if( map.containsKey( TEXT_FONT ) ) setTextFont( (String)map.get( TEXT_FONT ) );
		setTextFillPaint( map.containsKey( TEXT_FILL_PAINT ) ? (String)map.get( TEXT_FILL_PAINT ) : null );
		setTextDrawPaint( map.containsKey( TEXT_DRAW_PAINT ) ? (String)map.get( TEXT_DRAW_PAINT ) : null );
		if( map.containsKey( TEXT_DRAW_WIDTH ) ) setTextDrawWidth( (String)map.get( TEXT_DRAW_WIDTH ) );
		if( map.containsKey( TEXT_DRAW_CAP ) ) setTextDrawCap( (String)map.get( TEXT_DRAW_CAP ) );
		if( map.containsKey( TEXT_DRAW_PATTERN ) ) setTextDrawPattern( textDrawPattern );

		return this;
	}

	@Override
	public <T extends Node> Comparator<T> getComparator() {
		Comparator<T> byOrder = Comparator.comparingInt( o ->  o.getValue( ORDER ) );
		Comparator<T> byName = Comparator.comparing( o -> o.getValue( NAME ) );
		return byOrder.thenComparing( byName );
	}

	@Override
	public String toString() {
		return super.toString( NAME );
	}

}
