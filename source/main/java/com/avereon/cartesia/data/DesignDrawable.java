package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadMath;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.data.NodeEvent;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import com.avereon.util.TextUtil;
import com.avereon.xenon.tool.settings.SettingsPage;
import com.avereon.zarra.color.Paints;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import lombok.CustomLog;

import java.util.List;
import java.util.Map;
import java.util.Set;

@CustomLog
@SuppressWarnings( "UnusedReturnValue" )
public abstract class DesignDrawable extends DesignNode {

	public static final String ORDER = "order";

	public static final String DRAW_PAINT = "draw-paint";

	public static final String DRAW_WIDTH = "draw-width";

	public static final String DRAW_ALIGN = "draw-type";

	public static final String DRAW_CAP = "draw-cap";

	public static final String DRAW_PATTERN = "draw-pattern";

	public static final String FILL_PAINT = "fill-paint";

	static final String VIRTUAL_LAYER = "layer";

	static final String VIRTUAL_DRAW_PAINT_MODE = "draw-paint-mode";

	static final String VIRTUAL_DRAW_WIDTH_MODE = "draw-width-mode";

	static final String VIRTUAL_DRAW_PATTERN_MODE = "draw-pattern-mode";

	static final String VIRTUAL_DRAW_CAP_MODE = "draw-cap-mode";

	static final String VIRTUAL_FILL_PAINT_MODE = "fill-paint-mode";

	static final String MODE_CUSTOM = "custom";

	public static final String MODE_LAYER = "layer";

	public static final String SHAPE_NODE = "shape-node";

	private static final Set<String> nonCustomModes = Set.of( MODE_LAYER );

	protected SettingsPage page;

	protected DesignDrawable() {
		addModifyingKeys( ORDER, DRAW_PAINT, DRAW_WIDTH, DRAW_ALIGN, DRAW_CAP, DRAW_PATTERN, FILL_PAINT );
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
		return Paints.parseWithNullOnException( getDrawPaintWithInheritance() );
	}

	public Paint calcDrawPaintWithoutInheritance() {
		return Paints.parseWithNullOnException( getDrawPaint() );
	}

	public String getDrawPaintWithInheritance() {
		String paint = getDrawPaint();
		if( isCustomValue( paint ) ) return paint;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_DRAW_PAINT : layer.getDrawPaint();
	}

	public String getDrawPaint() {
		return getValue( DRAW_PAINT );
	}

	public DesignDrawable setDrawPaint( String paint ) {
		setValue( DRAW_PAINT, paint );
		return this;
	}

	public double calcDrawWidth() {
		return (double)getCache().computeIfAbsent( DRAW_WIDTH, (k) -> CadMath.evalNoException( getDrawWidthWithInheritance() ) );
	}

	public String getDrawWidthWithInheritance() {
		String width = getDrawWidth();
		if( isCustomValue( width ) ) return width;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_DRAW_WIDTH : layer.getDrawWidth();
	}

	public String getDrawWidth() {
		return getValue( DRAW_WIDTH );
	}

	public DesignDrawable setDrawWidth( String width ) {
		setValue( DRAW_WIDTH, width );
		return this;
	}

	public StrokeType calcDrawAlign() {
		try {
			return StrokeType.valueOf( getDrawAlignWithInheritance() );
		} catch( NullPointerException | IllegalArgumentException exception ) {
			return StrokeType.CENTERED;
		}
	}

	public String getDrawAlignWithInheritance() {
		String align = getDrawAlign();
		if( isCustomValue( align ) ) return align;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_DRAW_ALIGN : layer.getDrawAlign();
	}

	public String getDrawAlign() {
		return getValue( DRAW_ALIGN );
	}

	public DesignDrawable setDrawAlign( String align ) {
		setValue( DRAW_ALIGN, align );
		return this;
	}

	public List<Double> calcDrawPattern() {
		return CadShapes.parseDashPattern( getDrawPatternWithInheritance() );
	}

	public String getDrawPatternWithInheritance() {
		String pattern = getDrawPattern();
		if( isCustomValue( pattern ) ) return pattern;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_DRAW_PATTERN : layer.getDrawPattern();
	}

	public String getDrawPattern() {
		// Do not default to layer for this property
		return getValue( DRAW_PATTERN );
	}

	public DesignDrawable setDrawPattern( String pattern ) {
		setValue( DRAW_PATTERN, pattern );
		return this;
	}

	public StrokeLineCap calcDrawCap() {
		return StrokeLineCap.valueOf( getDrawCapWithInheritance().toUpperCase() );
	}

	public String getDrawCapWithInheritance() {
		String cap = getDrawCap();
		if( isCustomValue( cap ) ) return cap;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_DRAW_CAP : layer.getDrawCap();
	}

	public String getDrawCap() {
		return getValue( DRAW_CAP );
	}

	public DesignDrawable setDrawCap( String cap ) {
		setValue( DRAW_CAP, cap );
		return this;
	}

	public Paint calcFillPaint() {
		String value = getFillPaintWithInheritance();
		return Paints.parseWithNullOnException( value == null ? DesignLayer.DEFAULT_FILL_PAINT : value );
	}

	public String getFillPaintWithInheritance() {
		String paint = getFillPaint();
		if( isCustomValue( paint ) ) return paint;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_FILL_PAINT : layer.getFillPaint();
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

	<T> boolean isCustomValue( T value ) {
		return getValueMode( value ).equals( MODE_CUSTOM );
	}

	<T> String getValueMode( T value ) {
		if( value == null ) return MODE_LAYER;
		String mode = String.valueOf( value ).toLowerCase();
		if( nonCustomModes.contains( mode ) ) return mode;
		return MODE_CUSTOM;
	}

	@Override
	public <T> T setValue( String key, T newValue ) {
		return switch( key ) {
			case VIRTUAL_LAYER -> changeLayer( newValue );
			case VIRTUAL_DRAW_PAINT_MODE -> changeDrawPaintMode( newValue );
			case VIRTUAL_DRAW_WIDTH_MODE -> changeDrawWidthMode( newValue );
			case VIRTUAL_DRAW_PATTERN_MODE -> changeDrawPatternMode( newValue );
			case VIRTUAL_DRAW_CAP_MODE -> changeDrawCapMode( newValue );
			case VIRTUAL_FILL_PAINT_MODE -> changeFillPaintMode( newValue );
			default -> super.setValue( key, newValue );
		};
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( ORDER, DRAW_PAINT, DRAW_WIDTH, DRAW_ALIGN, DRAW_CAP, DRAW_PATTERN, FILL_PAINT ) );
		return map;
	}

	public DesignDrawable updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );

		// Fix pattern data
		String drawPattern = (String)map.get( DRAW_PATTERN );
		if( "0".equals( drawPattern ) ) drawPattern = null;
		if( "".equals( drawPattern ) ) drawPattern = null;

		if( map.containsKey( ORDER ) ) setOrder( (Integer)map.get( ORDER ) );

		setDrawPaint( map.containsKey( DRAW_PAINT ) ? (String)map.get( DRAW_PAINT ) : null );
		if( map.containsKey( DRAW_WIDTH ) ) setDrawWidth( (String)map.get( DRAW_WIDTH ) );
		if( map.containsKey( DRAW_ALIGN ) ) setDrawAlign( (String)map.get( DRAW_ALIGN ) );
		if( map.containsKey( DRAW_CAP ) ) setDrawCap( (String)map.get( DRAW_CAP ) );
		if( map.containsKey( DRAW_PATTERN ) ) setDrawPattern( drawPattern );
		setFillPaint( map.containsKey( FILL_PAINT ) ? (String)map.get( FILL_PAINT ) : null );

		return this;
	}

	private <T> T changeLayer( T newValue ) {
		String newLayerId = String.valueOf( newValue );
		if( getValue( VIRTUAL_LAYER ).equals( newLayerId ) ) return newValue;

		final DesignLayer oldLayer = getLayer();
		Txn.run( () -> getDesign().ifPresent( d -> {
			DesignLayer newLayer = d.getLayerById( newLayerId );
			if( newLayer != null ) newLayer.addDrawable( oldLayer.removeDrawable( this ) );
		} ) );

		return newValue;
	}

	<T> T changeDrawPaintMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_DRAW_PAINT_MODE );
		try( Txn ignored = Txn.create() ) {
			setDrawPaint( isCustom ? getDrawPaintWithInheritance() : String.valueOf( newValue ).toLowerCase() );
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
			setDrawWidth( isCustom ? getDrawWidthWithInheritance() : String.valueOf( newValue ).toLowerCase() );
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
			setDrawCap( isCustom ? getDrawCapWithInheritance() : String.valueOf( newValue ).toLowerCase() );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_DRAW_CAP_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error setting draw cap" );
		}
		return newValue;
	}

	<T> T changeDrawPatternMode( T newValue ) {
		boolean isCustom = isCustomValue( newValue );

		String oldValue = getValue( VIRTUAL_DRAW_PATTERN_MODE );
		try( Txn ignored = Txn.create() ) {
			setDrawPattern( isCustom ? TextUtil.nullToEmpty( getDrawPatternWithInheritance() ) : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_DRAW_PATTERN_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error setting draw pattern" );
		}
		return newValue;
	}

	<T> T changeFillPaintMode( T newValue ) {
		boolean isCustom = isCustomValue( newValue );

		String oldValue = getValue( VIRTUAL_FILL_PAINT_MODE );
		try( Txn ignored = Txn.create() ) {
			setFillPaint( isCustom ? getFillPaintWithInheritance() : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_FILL_PAINT_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error setting fill paint" );
		}
		return newValue;
	}

}
