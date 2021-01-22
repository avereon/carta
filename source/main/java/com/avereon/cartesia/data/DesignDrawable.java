package com.avereon.cartesia.data;

import com.avereon.cartesia.math.Maths;
import com.avereon.data.NodeEvent;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import com.avereon.util.Log;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.zerra.color.Paints;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;

import java.util.Map;

public abstract class DesignDrawable extends DesignNode {

	private static final System.Logger log = Log.get();

	public static final String ORDER = "order";

	public static final String DRAW_PAINT = "draw-paint";

	public static final String DRAW_WIDTH = "draw-width";

	public static final String DRAW_PATTERN = "draw-pattern";

	public static final String DRAW_CAP = "draw-cap";

	public static final String FILL_PAINT = "fill-paint";

	private static final String VIRTUAL_LAYER = "layer";

	private static final String VIRTUAL_DRAW_PAINT_SOURCE = "draw-paint-mode";

	private static final String VIRTUAL_DRAW_WIDTH_SOURCE = "draw-width-mode";

	private static final String VIRTUAL_DRAW_PATTERN_SOURCE = "draw-pattern-mode";

	private static final String VIRTUAL_DRAW_CAP_SOURCE = "draw-cap-mode";

	private static final String VIRTUAL_FILL_PAINT_SOURCE = "fill-paint-mode";

	// TODO These defaults should only be used for layers, they should be null otherwise
	private static final double DEFAULT_DRAW_WIDTH = 0.05;

	private static final Color DEFAULT_DRAW_PAINT = Color.web( "0x000000ff" );

	private static final StrokeLineCap DEFAULT_DRAW_CAP = StrokeLineCap.SQUARE;

	private static final String DEFAULT_DRAW_PATTERN = null;

	private static final Color DEFAULT_FILL_PAINT = null;

	protected SettingsPage page;

	protected DesignDrawable() {
		addModifyingKeys( DRAW_WIDTH, DRAW_PAINT, FILL_PAINT );
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

	public double calcDrawWidth() {
		String width = getDrawWidth();
		if( width != null ) return Maths.evalNoException( width );

		// Layers with null values return the default
		if( isLayer() ) return DEFAULT_DRAW_WIDTH;

		// Use the shape parent layer to get the value
		return ((DesignLayer)getParent()).calcDrawWidth();
	}

	public String getDrawWidth() {
		return getValue( DRAW_WIDTH );
	}

	public DesignDrawable setDrawWidth( String width ) {
		setValue( DRAW_WIDTH, width );
		return this;
	}

	public Paint calcDrawPaint() {
		String paint = getDrawPaint();
		if( paint != null ) return Paints.parse( paint );

		// Layers with null values return the default
		if( isLayer() ) return DEFAULT_DRAW_PAINT;

		// Use the shape parent layer to get the value
		return ((DesignLayer)getParent()).calcDrawPaint();
	}

	public String getDrawPaint() {
		return getValue( DRAW_PAINT );
	}

	public DesignDrawable setDrawPaint( String paint ) {
		setValue( DRAW_PAINT, paint );
		return this;
	}

	public String calcDrawPattern() {
		String pattern = getDrawPaint();
		if( pattern != null ) return pattern;

		// Layers with null values return the default
		if( isLayer() ) return DEFAULT_DRAW_PATTERN;

		// Use the shape parent layer to get the value
		return ((DesignLayer)getParent()).calcDrawPattern();
	}

	public String getDrawPattern() {
		return getValue( DRAW_PATTERN );
	}

	public DesignDrawable setDrawPattern( String pattern ) {
		setValue( DRAW_PATTERN, pattern );
		return this;
	}

	public StrokeLineCap calcDrawCap() {
		String cap = getDrawCap();
		if( cap != null ) return StrokeLineCap.valueOf( cap.toUpperCase() );

		// Layers with null values return the default
		if( isLayer() ) return DEFAULT_DRAW_CAP;

		// Use the shape parent layer to get the value
		return ((DesignLayer)getParent()).calcDrawCap();
	}

	public String getDrawCap() {
		return getValue( DRAW_CAP, DEFAULT_DRAW_CAP.name().toLowerCase() );
	}

	public DesignDrawable setDrawCap( String cap ) {
		setValue( DRAW_CAP, cap );
		return this;
	}

	public Paint calcFillPaint() {
		String paint = getFillPaint();
		if( paint != null ) return Paints.parse( paint );

		// Layers with null values return the default
		if( isLayer() ) return DEFAULT_FILL_PAINT;

		// Use the shape parent layer to get the value
		return ((DesignLayer)getParent()).calcFillPaint();
	}

	public String getFillPaint() {
		return getValue( FILL_PAINT );
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
			case VIRTUAL_DRAW_PAINT_SOURCE -> (T)(getDrawPaint() == null ? "layer" : "custom");
			case VIRTUAL_DRAW_WIDTH_SOURCE -> (T)(getDrawWidth() == null ? "layer" : "custom");
			case VIRTUAL_DRAW_PATTERN_SOURCE -> (T)(getDrawPattern() == null ? "layer" : "custom");
			case VIRTUAL_DRAW_CAP_SOURCE -> (T)(getDrawCap() == null ? "layer" : "custom");
			case VIRTUAL_FILL_PAINT_SOURCE -> (T)(getFillPaint() == null ? "layer" : "custom");
			default -> super.getValue( key );
		};
	}

	@Override
	public <T> T setValue( String key, T newValue ) {
		return switch( key ) {
			case VIRTUAL_LAYER -> changeLayer( newValue );
			case VIRTUAL_DRAW_PAINT_SOURCE -> changeDrawPaint( newValue );
			case VIRTUAL_DRAW_WIDTH_SOURCE -> changeDrawWidth( newValue );
			case VIRTUAL_DRAW_PATTERN_SOURCE -> changeDrawPattern( newValue );
			case VIRTUAL_DRAW_CAP_SOURCE -> changeDrawCap( newValue );
			case VIRTUAL_FILL_PAINT_SOURCE -> changeFillPaint( newValue );
			default -> super.setValue( key, newValue );
		};
	}

	public DesignDrawable updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );

		// Old keys
		if( map.containsKey( "draw-color" ) ) setDrawPaint( (String)map.get( "draw-color" ) );
		if( map.containsKey( "fill-color" ) ) setFillPaint( (String)map.get( "fill-color" ) );

		if( map.containsKey( ORDER ) ) setOrder( (Integer)map.get( ORDER ) );
		if( map.containsKey( DRAW_WIDTH ) ) setDrawWidth( (String)map.get( DRAW_WIDTH ) );
		if( map.containsKey( DRAW_PAINT ) ) setDrawPaint( (String)map.get( DRAW_PAINT ) );
		if( map.containsKey( FILL_PAINT ) ) setFillPaint( (String)map.get( FILL_PAINT ) );
		return this;
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( ORDER, DRAW_WIDTH, DRAW_PAINT, FILL_PAINT ) );
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

	private <T> T changeDrawPaint( T newValue ) {
		boolean isCustom = "custom".equals( newValue );

		String oldValue = getValue( VIRTUAL_DRAW_PAINT_SOURCE );
		try {
			Txn.create();
			setDrawPaint( isCustom ? Paints.toString( DEFAULT_DRAW_PAINT ) : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_DRAW_PAINT_SOURCE, oldValue, newValue ) ) );
			Txn.commit();
		} catch( TxnException exception ) {
			log.log( Log.ERROR, "Error changing draw width", exception );
		}
		return newValue;
	}

	private <T> T changeDrawWidth( T newValue ) {
		boolean isCustom = "custom".equals( newValue );

		String oldValue = getValue( VIRTUAL_DRAW_WIDTH_SOURCE );
		try {
			Txn.create();
			setDrawWidth( isCustom ? String.valueOf( DEFAULT_DRAW_WIDTH ) : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_DRAW_WIDTH_SOURCE, oldValue, newValue ) ) );
			Txn.commit();
		} catch( TxnException exception ) {
			log.log( Log.ERROR, "Error setting draw width", exception );
		}
		return newValue;
	}

	private <T> T changeDrawPattern( T newValue ) {
		boolean isCustom = "custom".equals( newValue );

		String oldValue = getValue( VIRTUAL_DRAW_PATTERN_SOURCE );
		try {
			Txn.create();
			setDrawPattern( isCustom ? DEFAULT_DRAW_PATTERN : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_DRAW_PATTERN_SOURCE, oldValue, newValue ) ) );
			Txn.commit();
		} catch( TxnException exception ) {
			log.log( Log.ERROR, "Error setting draw width", exception );
		}
		return newValue;
	}

	private <T> T changeDrawCap( T newValue ) {
		boolean isCustom = "custom".equals( newValue );

		String oldValue = getValue( VIRTUAL_DRAW_CAP_SOURCE );
		try {
			Txn.create();
			setDrawCap( isCustom ? DEFAULT_DRAW_CAP.name().toLowerCase() : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_DRAW_CAP_SOURCE, oldValue, newValue ) ) );
			Txn.commit();
		} catch( TxnException exception ) {
			log.log( Log.ERROR, "Error setting draw width", exception );
		}
		return newValue;
	}

	private <T> T changeFillPaint( T newValue ) {
		boolean isCustom = "custom".equals( newValue );

		String oldValue = getValue( VIRTUAL_FILL_PAINT_SOURCE );
		try {
			Txn.create();
			setFillPaint( isCustom ? Paints.toString( DEFAULT_FILL_PAINT ) : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_FILL_PAINT_SOURCE, oldValue, newValue ) ) );
			Txn.commit();
		} catch( TxnException exception ) {
			log.log( Log.ERROR, "Error setting draw width", exception );
		}
		return newValue;
	}

}
