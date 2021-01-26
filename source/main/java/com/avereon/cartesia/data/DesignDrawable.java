package com.avereon.cartesia.data;

import com.avereon.cartesia.math.Maths;
import com.avereon.data.NodeEvent;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import com.avereon.util.Log;
import com.avereon.util.TextUtil;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.zerra.color.Paints;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;

import java.util.Map;
import java.util.Set;

public abstract class DesignDrawable extends DesignNode {

	private static final System.Logger log = Log.get();

	public static final String ORDER = "order";

	public static final String DRAW_PAINT = "draw-paint";

	public static final String DRAW_WIDTH = "draw-width";

	public static final String DRAW_CAP = "draw-cap";

	public static final String DRAW_PATTERN = "draw-pattern";

	public static final String FILL_PAINT = "fill-paint";

	private static final String VIRTUAL_LAYER = "layer";

	private static final String VIRTUAL_DRAW_PAINT_MODE = "draw-paint-mode";

	private static final String VIRTUAL_DRAW_WIDTH_MODE = "draw-width-mode";

	private static final String VIRTUAL_DRAW_PATTERN_MODE = "draw-pattern-mode";

	private static final String VIRTUAL_DRAW_CAP_MODE = "draw-cap-mode";

	private static final String VIRTUAL_FILL_PAINT_MODE = "fill-paint-mode";

	static final String MODE_CUSTOM = "custom";

	static final String MODE_LAYER = "layer";

	static final String MODE_NONE = "none";

	// TODO These defaults should only be used for layers, they should be null otherwise
	static final double DEFAULT_DRAW_WIDTH = 0.05;

	static final Color DEFAULT_DRAW_PAINT = Color.web( "0x000000ff" );

	static final StrokeLineCap DEFAULT_DRAW_CAP = StrokeLineCap.SQUARE;

	static final String DEFAULT_DRAW_PATTERN = null;

	static final Color DEFAULT_FILL_PAINT = null;

	private static final Set<String> nonCustomModes = Set.of( MODE_LAYER, MODE_NONE );

	protected SettingsPage page;

	protected DesignDrawable() {
		addModifyingKeys( DRAW_WIDTH, DRAW_PAINT, DRAW_CAP, DRAW_PATTERN, FILL_PAINT );
	}

	public DesignLayer getParentLayer() {
		return getParent();
	}

	public int getOrder() {
		return getValue( ORDER, 0 );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setOrder( int order ) {
		setValue( ORDER, order );
		return (T)this;
	}

	public Paint calcDrawPaint() {
		String paint = getDrawPaint();
		if( isCustomValue( paint ) ) return Paints.parse( paint );

		// Layers with null values return the default
		if( isLayer() ) return DEFAULT_DRAW_PAINT;

		// Use the shape parent layer to get the value
		return ((DesignLayer)getParent()).calcDrawPaint();
	}

	public String getDrawPaint() {
		return getValue( DRAW_PAINT, MODE_LAYER );
	}

	public DesignDrawable setDrawPaint( String paint ) {
		setValue( DRAW_PAINT, paint );
		return this;
	}

	public double calcDrawWidth() {
		String width = getDrawWidth();
		if( isCustomValue( width ) ) return Maths.evalNoException( width );

		// Layers with null values return the default
		if( isLayer() ) return DEFAULT_DRAW_WIDTH;

		// Use the shape parent layer to get the value
		return ((DesignLayer)getParent()).calcDrawWidth();
	}

	public String getDrawWidth() {
		return getValue( DRAW_WIDTH, MODE_LAYER );
	}

	public DesignDrawable setDrawWidth( String width ) {
		setValue( DRAW_WIDTH, width );
		return this;
	}

	public String calcDrawPattern() {
		String pattern = getDrawPattern();
		if( isCustomValue( pattern ) ) return pattern;

		// Layers with null values return the default
		if( isLayer() ) return DEFAULT_DRAW_PATTERN;

		// Use the shape parent layer to get the value
		return ((DesignLayer)getParent()).calcDrawPattern();
	}

	public String getDrawPattern() {
		return getValue( DRAW_PATTERN, MODE_LAYER );
	}

	public DesignDrawable setDrawPattern( String pattern ) {
		setValue( DRAW_PATTERN, pattern );
		return this;
	}

	public StrokeLineCap calcDrawCap() {
		String cap = getDrawCap();
		if( isCustomValue( cap ) ) return StrokeLineCap.valueOf( cap.toUpperCase() );

		// Layers with null values return the default
		if( isLayer() ) return DEFAULT_DRAW_CAP;

		// Use the shape parent layer to get the value
		return ((DesignLayer)getParent()).calcDrawCap();
	}

	public String getDrawCap() {
		return getValue( DRAW_CAP, MODE_LAYER );
	}

	public DesignDrawable setDrawCap( String cap ) {
		setValue( DRAW_CAP, cap );
		return this;
	}

	public Paint calcFillPaint() {
		String paint = getFillPaint();
		if( isCustomValue( paint ) ) return Paints.parse( paint );

		// Layers with null values return the default
		if( isLayer() ) return DEFAULT_FILL_PAINT;

		// Use the shape parent layer to get the value
		return ((DesignLayer)getParent()).calcFillPaint();
	}

	public String getFillPaint() {
		return getValue( FILL_PAINT, MODE_LAYER );
	}

	public DesignDrawable setFillPaint( String color ) {
		setValue( FILL_PAINT, color );
		return this;
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public <T> T getValue( String key ) {
		return switch( key ) {
			case VIRTUAL_LAYER -> (T)getParentLayer().getId();
			case VIRTUAL_DRAW_PAINT_MODE -> (T)(getValueMode( getDrawPaint() ));
			case VIRTUAL_DRAW_WIDTH_MODE -> (T)(getValueMode( getDrawWidth() ));
			case VIRTUAL_DRAW_PATTERN_MODE -> (T)(getValueMode( getDrawPattern() ));
			case VIRTUAL_DRAW_CAP_MODE -> (T)(getValueMode( getDrawCap() ));
			case VIRTUAL_FILL_PAINT_MODE -> (T)(getValueMode( getFillPaint() ));
			default -> super.getValue( key );
		};
	}

	boolean isCustomValue( String value ) {
		return getValueMode( value ).equals( MODE_CUSTOM );
	}

	String getValueMode( String value ) {
		if( value == null ) return MODE_LAYER;
		if( nonCustomModes.contains( value ) ) return value;
		return MODE_CUSTOM;
	}

	@Override
	public <T> T setValue( String key, T newValue ) {
		if( TextUtil.areEqual( key, VIRTUAL_LAYER ) ) return changeLayer( newValue );

		switch( key ) {
			case VIRTUAL_DRAW_PAINT_MODE -> changeDrawPaintMode( newValue );
			case VIRTUAL_DRAW_WIDTH_MODE -> changeDrawWidthMode( newValue );
			case VIRTUAL_DRAW_PATTERN_MODE -> changeDrawPatternMode( newValue );
			case VIRTUAL_DRAW_CAP_MODE -> changeDrawCapMode( newValue );
			case VIRTUAL_FILL_PAINT_MODE -> changeFillPaintMode( newValue );
		}

		return super.setValue( key, newValue );
	}

	public DesignDrawable updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );

		// Old keys
		if( map.containsKey( "draw-color" ) ) setDrawPaint( (String)map.get( "draw-color" ) );
		if( map.containsKey( "fill-color" ) ) setFillPaint( (String)map.get( "fill-color" ) );

		if( map.containsKey( ORDER ) ) setOrder( (Integer)map.get( ORDER ) );
		if( map.containsKey( DRAW_WIDTH ) ) setDrawWidth( (String)map.get( DRAW_WIDTH ) );
		if( map.containsKey( DRAW_PAINT ) ) setDrawPaint( (String)map.get( DRAW_PAINT ) );
		if( map.containsKey( DRAW_CAP ) ) setDrawCap( (String)map.get( DRAW_CAP ) );
		if( map.containsKey( DRAW_PATTERN ) ) setDrawPattern( (String)map.get( DRAW_PATTERN ) );
		if( map.containsKey( FILL_PAINT ) ) setFillPaint( (String)map.get( FILL_PAINT ) );
		return this;
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( ORDER, DRAW_PAINT, DRAW_WIDTH, DRAW_CAP, DRAW_PATTERN, FILL_PAINT ) );
		return map;
	}

	private boolean isLayer() {
		return this instanceof DesignLayer;
	}

	private <T> T changeLayer( T newValue ) {
		String newLayerId = String.valueOf( newValue );
		if( getValue( VIRTUAL_LAYER ).equals( newLayerId ) ) return newValue;

		DesignLayer oldLayer = getParentLayer();
		try {
			Txn.create();
			DesignLayer newLayer = getDesign().findLayerById( newLayerId );
			newLayer.addDrawable( oldLayer.removeDrawable( this ) );
			Txn.commit();
		} catch( TxnException exception ) {
			log.log( Log.ERROR, "Error changing layer", exception );
		}

		return newValue;
	}

	<T> T changeDrawPaintMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_DRAW_PAINT_MODE );
		try {
			Txn.create();
			setDrawPaint( isCustom ? Paints.toString( calcDrawPaint() ) : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_DRAW_PAINT_MODE, oldValue, newValue ) ) );
			Txn.commit();
		} catch( TxnException exception ) {
			log.log( Log.ERROR, "Error changing draw paint", exception );
		}
		return newValue;
	}

	<T> T changeDrawWidthMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_DRAW_WIDTH_MODE );
		try {
			Txn.create();
			setDrawWidth( isCustom ? String.valueOf( calcDrawWidth() ) : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_DRAW_WIDTH_MODE, oldValue, newValue ) ) );
			Txn.commit();
		} catch( TxnException exception ) {
			log.log( Log.ERROR, "Error setting draw width", exception );
		}
		return newValue;
	}

	<T> T changeDrawCapMode( final T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( getValueMode( String.valueOf( newValue ) ) );

		String oldValue = getValue( VIRTUAL_DRAW_CAP_MODE );
		try( Txn ignored = Txn.create() ) {
			setDrawCap( isCustom ? calcDrawCap().name().toLowerCase() : String.valueOf( newValue ).toLowerCase() );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_DRAW_CAP_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.log( Log.ERROR, "Error setting draw cap", exception );
		}
		return newValue;
	}

	<T> T changeDrawPatternMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_DRAW_PATTERN_MODE );
		try {
			Txn.create();
			setDrawPattern( isCustom ? calcDrawPattern() : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_DRAW_PATTERN_MODE, oldValue, newValue ) ) );
			Txn.commit();
		} catch( TxnException exception ) {
			log.log( Log.ERROR, "Error setting draw patter", exception );
		}
		return newValue;
	}

	<T> T changeFillPaintMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_FILL_PAINT_MODE );
		try {
			Txn.create();
			setFillPaint( isCustom ? Paints.toString( calcFillPaint() ) : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_FILL_PAINT_MODE, oldValue, newValue ) ) );
			Txn.commit();
		} catch( TxnException exception ) {
			log.log( Log.ERROR, "Error setting draw width", exception );
		}
		return newValue;
	}

}
