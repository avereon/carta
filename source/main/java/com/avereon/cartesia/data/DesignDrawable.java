package com.avereon.cartesia.data;

import com.avereon.cartesia.math.MathEx;
import com.avereon.zerra.color.Colors;
import javafx.scene.paint.Color;

import java.util.Map;

public abstract class DesignDrawable extends DesignNode {

	public static final String ORDER = "order";

	public static final String DRAW_WIDTH = "draw-width";

	public static final String DRAW_COLOR = "draw-color";

	public static final String FILL_COLOR = "fill-color";

	private static final double DEFAULT_DRAW_WIDTH = 1.0;

	private static final Color DEFAULT_DRAW_COLOR = Color.web( "0x000000ff" );

	private static final Color DEFAULT_FILL_COLOR = Color.web( "0x202030ff" );

	private static final Color DEFAULT_SELECT_DRAW_COLOR = Colors.web( "#ff00ffff" );

	private static final Color DEFAULT_SELECT_FILL_COLOR = Colors.web( "#ff00ff40" );

	protected DesignDrawable() {
		addModifyingKeys( DRAW_WIDTH, DRAW_COLOR, FILL_COLOR );
	}

	public int getOrder() {
		return getValue( ORDER, 0 );
	}

	public DesignDrawable setOrder( int order ) {
		setValue( ORDER, order );
		return this;
	}

	public double calcDrawWidth() {
		String width = getDrawWidth();
		if( width != null ) return MathEx.eval( width );
		if( this instanceof DesignLayer ) return DEFAULT_DRAW_WIDTH;
		DesignNode parent = getParent();
		if( parent instanceof DesignLayer ) return ((DesignLayer)parent).calcDrawWidth();
		return Double.NaN;
	}

	public String getDrawWidth() {
		return getValue( DRAW_WIDTH );
	}

	public DesignDrawable setDrawWidth( String width ) {
		setValue( DRAW_WIDTH, width );
		return this;
	}

	public Color calcDrawColor() {
		String color = getDrawColor();
		if( color != null ) return Colors.web( color );
		if( this instanceof DesignLayer ) return DEFAULT_DRAW_COLOR;
		DesignNode parent = getParent();
		if( parent instanceof DesignLayer ) return ((DesignLayer)parent).calcDrawColor();
		return DEFAULT_DRAW_COLOR;
	}

	public String getDrawColor() {
		return getValue( DRAW_COLOR );
	}

	public DesignDrawable setDrawColor( String color ) {
		setValue( DRAW_COLOR, color );
		return this;
	}

	public Color calcFillColor() {
		String color = getFillColor();
		if( color != null ) return Colors.web( color );
		if( this instanceof DesignLayer ) return DEFAULT_FILL_COLOR;
		DesignNode parent = getParent();
		if( parent instanceof DesignLayer ) return ((DesignLayer)parent).calcFillColor();
		return DEFAULT_FILL_COLOR;
	}

	public String getFillColor() {
		return getValue( FILL_COLOR );
	}

	public DesignDrawable setFillColor( String color ) {
		setValue( FILL_COLOR, color );
		return this;
	}

	public Color calcSelectDrawColor() {
		return DEFAULT_SELECT_DRAW_COLOR;
	}

	public Color calcSelectFillColor() {
		return DEFAULT_SELECT_FILL_COLOR;
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( ORDER, DRAW_WIDTH, DRAW_COLOR, FILL_COLOR ) );
		return map;
	}

	public DesignDrawable updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( ORDER ) ) setOrder( (Integer)map.get( ORDER ) );
		if( map.containsKey( DRAW_WIDTH ) ) setDrawWidth( (String)map.get( DRAW_WIDTH ) );
		if( map.containsKey( DRAW_COLOR ) ) setDrawColor( (String)map.get( DRAW_COLOR ) );
		if( map.containsKey( FILL_COLOR ) ) setFillColor( (String)map.get( FILL_COLOR ) );
		return this;
	}

}
