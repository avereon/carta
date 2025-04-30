package com.avereon.cartesia.data;

import com.avereon.cartesia.DesignUnit;
import com.avereon.cartesia.math.CadMath;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.data.IdNode;
import com.avereon.data.Node;
import com.avereon.xenon.NodeOrderNameComparator;
import com.avereon.zerra.color.Paints;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import lombok.CustomLog;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SuppressWarnings( "UnusedReturnValue" )
@CustomLog
public class DesignLayer extends DesignDrawable implements DesignTextSupport {

	public static final String NAME = "name";

	public static final String UNIT = "unit";

	// NOTE Visibility state is stored in the design tool,
	// instead of here in the data model so that each tool can have different
	// layers visible.
	//public static final String VISIBLE = "visible";

	public static final String LAYERS = "layers";

	public static final String SHAPES = "shapes";

	static final String DEFAULT_DRAW_PAINT = "#808080ff";

	static final String DEFAULT_DRAW_WIDTH = "0.05";

	static final String DEFAULT_DRAW_ALIGN = StrokeType.CENTERED.name().toLowerCase();

	static final String DEFAULT_DRAW_CAP = StrokeLineCap.ROUND.name().toLowerCase();

	static final String DEFAULT_DRAW_JOIN = StrokeLineJoin.ROUND.name().toLowerCase();

	static final String DEFAULT_DRAW_PATTERN = null;

	static final String DEFAULT_FILL_PAINT = null;

	static final String DEFAULT_TEXT_SIZE = "1";

	static final String DEFAULT_TEXT_FILL_PAINT = "#000000ff";

	static final String DEFAULT_TEXT_DRAW_PAINT = null;

	static final String DEFAULT_TEXT_DRAW_WIDTH = "0.05";

	static final String DEFAULT_TEXT_DRAW_CAP = StrokeLineCap.ROUND.name().toLowerCase();

	static final String DEFAULT_TEXT_DRAW_PATTERN = null;

	static final String DEFAULT_FONT_NAME = null;

	static final String DEFAULT_FONT_WEIGHT = FontWeight.NORMAL.name().toLowerCase();

	static final String DEFAULT_FONT_POSTURE = FontPosture.REGULAR.name().toUpperCase();

	static final String DEFAULT_FONT_UNDERLINE = Boolean.toString( false );

	static final String DEFAULT_FONT_STRIKETHROUGH = Boolean.toString( false );

	public DesignLayer() {
		defineNaturalKey( NAME );
		addModifyingKeys( NAME,
			UNIT,
			FILL_PAINT,
			DRAW_PAINT,
			DRAW_WIDTH,
			DRAW_CAP,
			DRAW_PATTERN,
			TEXT_FILL_PAINT,
			TEXT_DRAW_PAINT,
			TEXT_DRAW_WIDTH,
			TEXT_DRAW_CAP,
			TEXT_DRAW_PATTERN,
			TEXT_SIZE,
			FONT_NAME,
			FONT_WEIGHT,
			FONT_POSTURE,
			FONT_UNDERLINE,
			FONT_STRIKETHROUGH,
			LAYERS,
			SHAPES
		);

		setDrawPaint( DEFAULT_DRAW_PAINT );
		setDrawWidth( DEFAULT_DRAW_WIDTH );
		setDrawPattern( DEFAULT_DRAW_PATTERN );
		setDrawCap( DEFAULT_DRAW_CAP );
		setFillPaint( DEFAULT_FILL_PAINT );

		setTextSize( DEFAULT_TEXT_SIZE );
		setTextFillPaint( DEFAULT_TEXT_FILL_PAINT );
		setTextDrawPaint( DEFAULT_TEXT_DRAW_PAINT );
		setTextDrawWidth( DEFAULT_TEXT_DRAW_WIDTH );
		setTextDrawPattern( DEFAULT_TEXT_DRAW_PATTERN );
		setTextDrawCap( DEFAULT_TEXT_DRAW_CAP );

		setFontName( DEFAULT_FONT_NAME );
		setFontWeight( DEFAULT_FONT_WEIGHT );
		setFontPosture( DEFAULT_FONT_POSTURE );
		setFontUnderline( DEFAULT_FONT_UNDERLINE );
		setFontStrikethrough( DEFAULT_FONT_STRIKETHROUGH );
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
		return getValueList( LAYERS, getNaturalComparator() );
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
	public DesignLayer removeLayer( DesignLayer layer ) {
		removeFromSet( LAYERS, layer );

		getDesign().ifPresent( d -> {
			d.getViews().forEach( v -> v.removeLayer( layer ) );
			d.getPrints().forEach( p -> p.removeLayer( layer ) );
		} );

		return this;
	}

	@Deprecated
	public Set<DesignShape> getShapeSet() {
		return getValues( SHAPES );
	}

	public List<DesignShape> getShapes() {
		return getValueList( SHAPES, DesignShape.getComparator() );
	}

	public DesignLayer addShape( DesignShape shape ) {
		addToSet( SHAPES, shape );
		return this;
	}

	public DesignLayer addShapeBeforeOrAfter( DesignShape shape, DesignShape anchor, boolean after ) {
		List<DesignShape> shapes = new ArrayList<>( getShapes() );
		int insert = anchor == null ? -1 : shapes.indexOf( anchor );
		if( after ) insert++;
		int size = shapes.size();
		if( insert < 0 || insert > size ) insert = size;
		shapes.add( insert, shape );
		updateOrder( shapes );

		addShape( shape );

		return this;
	}

	@SuppressWarnings( "UnusedReturnValue" )
	public DesignLayer removeShape( DesignShape shape ) {
		removeFromSet( SHAPES, shape );
		return this;
	}

	public DesignLayer addShapes( Collection<DesignShape> shapes ) {
		addToSet( SHAPES, shapes );
		return this;
	}

	public DesignLayer removeShapes( Collection<DesignShape> shapes ) {
		removeFromSet( SHAPES, shapes );
		return this;
	}

	public DesignLayer clearShapes() {
		removeFromSet( SHAPES, new HashSet<>( getShapeSet() ) );
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
		return getValue( DRAW_PATTERN );
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

	// Text size
	public double calcTextSize() {
		String value = getTextSize();
		return CadMath.evalNoException( value == null ? DEFAULT_TEXT_SIZE : value );
	}

	public String getTextSize() {
		return getValue( TEXT_SIZE );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setTextSize( String value ) {
		setValue( TEXT_SIZE, value );
		return (T)this;
	}

	// Font name
	public String calcFontName() {
		String value = getFontName();
		return value == null ? DEFAULT_FONT_NAME : value;
	}

	public String getFontName() {
		return getValue( FONT_NAME );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setFontName( String value ) {
		setValue( FONT_NAME, value );
		return (T)this;
	}

	// Font weight
	public FontWeight calcFontWeight() {
		String value = getFontWeight();
		return FontWeight.valueOf( (value == null ? DEFAULT_FONT_WEIGHT : value).toUpperCase() );
	}

	public String getFontWeight() {
		return getValue( FONT_WEIGHT );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setFontWeight( String value ) {
		setValue( FONT_WEIGHT, value );
		return (T)this;
	}

	// Font posture
	public FontPosture calcFontPosture() {
		String value = getFontPosture();
		return FontPosture.valueOf( (value == null ? DEFAULT_FONT_POSTURE : value).toUpperCase() );
	}

	public String getFontPosture() {
		return getValue( FONT_POSTURE );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setFontPosture( String value ) {
		setValue( FONT_POSTURE, value );
		return (T)this;
	}

	// Font underline
	public boolean calcFontUnderline() {
		return Boolean.parseBoolean( getFontUnderline() );
	}

	public String getFontUnderline() {
		return getValue( FONT_UNDERLINE );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setFontUnderline( String value ) {
		setValue( FONT_UNDERLINE, value );
		return (T)this;
	}

	// Font strikethrough
	public boolean calcFontStrikethrough() {
		return Boolean.parseBoolean( getFontStrikethrough() );
	}

	public String getFontStrikethrough() {
		return getValue( FONT_STRIKETHROUGH );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setFontStrikethrough( String value ) {
		setValue( FONT_STRIKETHROUGH, value );
		return (T)this;
	}

	@Override
	public Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( NAME,
			TEXT_FILL_PAINT,
			TEXT_DRAW_PAINT,
			TEXT_DRAW_WIDTH,
			TEXT_DRAW_CAP,
			TEXT_DRAW_PATTERN,
			TEXT_SIZE,
			FONT_NAME,
			FONT_WEIGHT,
			FONT_POSTURE,
			FONT_UNDERLINE,
			FONT_STRIKETHROUGH
		) );
		return map;
	}

	public Map<String, Object> asDeepMap() {
		Map<String, Object> map = new HashMap<>( asMap() );
		map.put( LAYERS, getLayers().stream().collect( Collectors.toMap( IdNode::getId, DesignLayer::asDeepMap ) ) );
		map.put( SHAPES, getShapeSet().stream().filter( s -> !s.isPreview() ).collect( Collectors.toMap( IdNode::getId, DesignShape::asMap ) ) );
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
		setTextFillPaint( map.containsKey( TEXT_FILL_PAINT ) ? (String)map.get( TEXT_FILL_PAINT ) : null );
		setTextDrawPaint( map.containsKey( TEXT_DRAW_PAINT ) ? (String)map.get( TEXT_DRAW_PAINT ) : null );
		if( map.containsKey( TEXT_DRAW_WIDTH ) ) setTextDrawWidth( (String)map.get( TEXT_DRAW_WIDTH ) );
		if( map.containsKey( TEXT_DRAW_CAP ) ) setTextDrawCap( (String)map.get( TEXT_DRAW_CAP ) );
		if( map.containsKey( TEXT_DRAW_PATTERN ) ) setTextDrawPattern( textDrawPattern );

		if( map.containsKey( TEXT_SIZE ) ) setTextSize( (String)map.get( TEXT_SIZE ) );
		if( map.containsKey( FONT_NAME ) ) setFontName( (String)map.get( FONT_NAME ) );
		if( map.containsKey( FONT_WEIGHT ) ) setFontWeight( (String)map.get( FONT_WEIGHT ) );
		if( map.containsKey( FONT_POSTURE ) ) setFontPosture( (String)map.get( FONT_POSTURE ) );
		if( map.containsKey( FONT_UNDERLINE ) ) setFontUnderline( String.valueOf( map.get( FONT_UNDERLINE ) ) );
		if( map.containsKey( FONT_STRIKETHROUGH ) ) setFontStrikethrough( String.valueOf( map.get( FONT_STRIKETHROUGH ) ) );

		return this;
	}

	@Override
	public <T extends Node> Comparator<T> getNaturalComparator() {
		return new NodeOrderNameComparator<>();
	}

	@Override
	public String toString() {
		return super.toString( NAME );
	}

	@Override
	protected void invalidateCache( String key ) {
		super.invalidateCache( key );
		getLayers().forEach( l -> l.invalidateCache( key ) );
		getShapes().forEach( s -> s.invalidateCache( key ) );
	}

	private <T extends DesignDrawable> List<T> updateOrder( List<T> list ) {
		AtomicInteger counter = new AtomicInteger( 0 );
		list.forEach( i -> i.setOrder( counter.getAndIncrement() ) );
		return list;
	}

}
