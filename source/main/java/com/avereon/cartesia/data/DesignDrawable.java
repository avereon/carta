package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadMath;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.data.NodeEvent;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import com.avereon.util.TextUtil;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.zerra.color.Paints;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import lombok.CustomLog;

import java.util.List;
import java.util.Map;
import java.util.Set;

@CustomLog
public abstract class DesignDrawable extends DesignNode {

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

	public static final String MODE_LAYER = "layer";

	public static final String SHAPE_NODE = "shape-node";

	private static final Set<String> nonCustomModes = Set.of( MODE_LAYER );

	protected SettingsPage page;

	protected DesignDrawable() {
		addModifyingKeys( ORDER, DRAW_PAINT, DRAW_WIDTH, DRAW_CAP, DRAW_PATTERN, FILL_PAINT );

		setDrawPaint( MODE_LAYER );
		setDrawWidth( MODE_LAYER );
		setDrawCap( MODE_LAYER );
		setDrawPattern( MODE_LAYER );
		setFillPaint( MODE_LAYER );
	}

	public DesignLayer getLayer() {
		return getParent();
	}

	public int getOrder() {
		return getValue( ORDER, -1 );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setOrder( int order ) {
		setValue( ORDER, order );
		return (T)this;
	}

	public Paint calcDrawPaint() {
		String paint = getDrawPaint();
		if( paint == null ) return null;
		if( isCustomValue( paint ) ) return Paints.parseWithNullOnException( paint );

		DesignLayer layer = getLayer();
		return layer == null ? Paints.parseWithNullOnException( DesignLayer.DEFAULT_DRAW_PAINT ) : layer.calcDrawPaint();
	}

	public String getDrawPaint() {
		return getValue( DRAW_PAINT );
	}

	public DesignDrawable setDrawPaint( String paint ) {
		setValue( DRAW_PAINT, paint );
		return this;
	}

	public double calcDrawWidth() {
		String width = getDrawWidth();
		if( isCustomValue( width ) ) return CadMath.evalNoException( width );

		DesignLayer layer = getLayer();
		return layer == null ? Double.parseDouble( DesignLayer.DEFAULT_DRAW_WIDTH ) : layer.calcDrawWidth();
	}

	public String getDrawWidth() {
		return getValue( DRAW_WIDTH, MODE_LAYER );
	}

	public DesignDrawable setDrawWidth( String width ) {
		setValue( DRAW_WIDTH, width );
		return this;
	}

	public List<Double> calcDrawPattern() {
		String pattern = getDrawPattern();
		if( isCustomValue( pattern ) ) return CadShapes.parseDashPattern( pattern );

		DesignLayer layer = getLayer();
		return layer == null ? CadShapes.parseDashPattern( DesignLayer.DEFAULT_DRAW_PATTERN ) : layer.calcDrawPattern();
	}

	public String getDrawPattern() {
		// Do not default to layer for this property
		return getValue( DRAW_PATTERN );
	}

	public DesignDrawable setDrawPattern( String pattern ) {
		setValue( DRAW_PATTERN, TextUtil.isEmpty( pattern ) ? null : pattern );
		return this;
	}

	public StrokeLineCap calcDrawCap() {
		String cap = getDrawCap();
		if( isCustomValue( cap ) ) return StrokeLineCap.valueOf( cap.toUpperCase() );

		DesignLayer layer = getLayer();
		return layer == null ? StrokeLineCap.valueOf( DesignLayer.DEFAULT_DRAW_CAP.toUpperCase() ) : layer.calcDrawCap();
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
		if( paint == null ) return null;
		if( isCustomValue( paint ) ) return Paints.parseWithNullOnException( paint );

		DesignLayer layer = getLayer();
		return layer == null ? Paints.parseWithNullOnException( DesignLayer.DEFAULT_FILL_PAINT ) : layer.calcFillPaint();
	}

	public String getFillPaint() {
		// Do not default to layer for this property
		return getValue( FILL_PAINT );
	}

	public DesignDrawable setFillPaint( String paint ) {
		setValue( FILL_PAINT, paint );
		return this;
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public <T> T getValue( String key ) {
		return switch( key ) {
			case VIRTUAL_LAYER -> getLayer() == null ? null : (T)getLayer().getId();
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
		if( value != null && nonCustomModes.contains( value ) ) return value;
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

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( ORDER, DRAW_PAINT, DRAW_WIDTH, DRAW_CAP, DRAW_PATTERN, FILL_PAINT ) );
		return map;
	}

	public DesignDrawable updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );

		// Fix bad data
		String drawPattern = (String)map.get( DRAW_PATTERN );
		if( "0".equals( drawPattern ) ) drawPattern = null;
		if( "".equals( drawPattern ) ) drawPattern = null;

		if( map.containsKey( ORDER ) ) setOrder( (Integer)map.get( ORDER ) );
		setDrawPaint( map.containsKey( DRAW_PAINT ) ? (String)map.get( DRAW_PAINT ) : null );
		if( map.containsKey( DRAW_WIDTH ) ) setDrawWidth( (String)map.get( DRAW_WIDTH ) );
		if( map.containsKey( DRAW_CAP ) ) setDrawCap( (String)map.get( DRAW_CAP ) );
		if( map.containsKey( DRAW_PATTERN ) ) setDrawPattern( drawPattern );
		setFillPaint( map.containsKey( FILL_PAINT ) ? (String)map.get( FILL_PAINT ) : null );
		return this;
	}

	private <T> T changeLayer( T newValue ) {
		String newLayerId = String.valueOf( newValue );
		if( getValue( VIRTUAL_LAYER ).equals( newLayerId ) ) return newValue;

		DesignLayer oldLayer = getLayer();
		try( Txn ignored = Txn.create() ) {
			DesignLayer newLayer = getDesign().findLayerById( newLayerId );
			newLayer.addDrawable( oldLayer.removeDrawable( this ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error changing layer" );
		}

		return newValue;
	}

	<T> T changeDrawPaintMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_DRAW_PAINT_MODE );
		try( Txn ignored = Txn.create() ) {
			setDrawPaint( isCustom ? Paints.toString( calcDrawPaint() ) : String.valueOf( newValue ).toLowerCase() );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_DRAW_PAINT_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error changing draw paint" );
		}
		return newValue;
	}

	<T> T changeDrawWidthMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_DRAW_WIDTH_MODE );
		try( Txn ignored = Txn.create() ) {
			setDrawWidth( isCustom ? String.valueOf( calcDrawWidth() ) : String.valueOf( newValue ).toLowerCase() );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_DRAW_WIDTH_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error setting draw width" );
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
			log.atError().withCause( exception ).log( "Error setting draw cap" );
		}
		return newValue;
	}

	<T> T changeDrawPatternMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_DRAW_PATTERN_MODE );
		try( Txn ignored = Txn.create() ) {
			setDrawPattern( isCustom ? TextUtil.toString( calcDrawPattern(), ", " ) : String.valueOf( newValue ).toLowerCase() );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_DRAW_PATTERN_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error setting draw patter" );
		}
		return newValue;
	}

	<T> T changeFillPaintMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_FILL_PAINT_MODE );
		try( Txn ignored = Txn.create() ) {
			setFillPaint( isCustom ? Paints.toString( calcFillPaint() ) : String.valueOf( newValue ).toLowerCase() );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_FILL_PAINT_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error setting draw width" );
		}
		return newValue;
	}

}
