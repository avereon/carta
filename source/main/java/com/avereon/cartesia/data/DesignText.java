package com.avereon.cartesia.data;

import com.avereon.cartesia.math.*;
import com.avereon.data.NodeEvent;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import com.avereon.zarra.color.Paints;
import com.avereon.zarra.font.FontUtil;
import javafx.geometry.Point3D;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import lombok.CustomLog;

import java.util.Map;

@SuppressWarnings( "UnusedReturnValue" )
@CustomLog
public class DesignText extends DesignShape implements DesignTextAttributes {

	public static final String TEXT = "text";

	public static final String ROTATE = "rotate";

	// TODO Add horizontal alignment
	// TODO Add vertical alignment
	// How about justification?
	// How about rich text support?

	private static final String VIRTUAL_TEXT_FONT_MODE = "text-font-mode";

	public DesignText() {
		this( null );
	}

	public DesignText( Point3D origin ) {
		this( origin, null );
	}

	public DesignText( Point3D origin, String text ) {
		this( origin, text, null );
	}

	public DesignText( Point3D origin, String text, Double rotate ) {
		super( origin );
		addModifyingKeys( TEXT, TEXT_FONT, ROTATE );

		setText( text );
		setRotate( rotate );

		setTextFont( MODE_LAYER );
		setFillPaint( MODE_LAYER );
		setDrawPaint( MODE_LAYER );
		setDrawWidth( MODE_LAYER );
		setDrawCap( MODE_LAYER );
		setDrawPattern( MODE_LAYER );
	}

	public String getText() {
		return getValue( TEXT );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignText> T setText( String value ) {
		setValue( TEXT, value );
		return (T)this;
	}

	public double calcTextSize() {
		return CadMath.evalNoException( getTextSizeWithInheritance() );
	}

	public String getTextSizeWithInheritance() {
		String width = getTextSize();
		if( isCustomValue( width ) ) return width;

		DesignLayer layer = getLayer();
		return layer == null ? DEFAULT_TEXT_SIZE : layer.getTextSize();
	}

	public String getTextSize() {
		return getValue (TEXT_SIZE, MODE_LAYER );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignText> T setTextSize( String value ) {
		setValue( TEXT_SIZE, value );
		return (T)this;
	}

	@Override
	public Paint calcDrawPaint() {
		// FIXME Allow user to override
		return Paints.parseWithNullOnException( getLayer().getTextDrawPaint() );
	}

	@Override
	public Paint calcFillPaint() {
		// FIXME Allow user to override
		return Paints.parseWithNullOnException( getLayer().getTextFillPaint() );
	}

	public Font calcTextFont() {
		// FIXME Use the font attributes to calculate a font
		return FontUtil.decode( getTextFontWithInheritance() );
	}

	@Deprecated
	public String getTextFontWithInheritance() {
		String font = getTextFont();
		if( isCustomValue( font ) ) return font;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_TEXT_FONT : layer.getTextFont();
	}

	@Deprecated
	public String getTextFont() {
		return getValue( TEXT_FONT, DesignLayer.DEFAULT_TEXT_FONT );
	}

	@SuppressWarnings( "unchecked" )
	@Deprecated
	public <T extends DesignDrawable> T setTextFont( String value ) {
		setValue( TEXT_FONT, value );
		return (T)this;
	}

	//	public Paint calcTextFillPaint() {
	//		return Paints.parseWithNullOnException( getTextFillPaintWithInheritance() );
	//	}
	//
	//	public String getTextFillPaintWithInheritance() {
	//		String paint = getTextFillPaint();
	//		if( paint == null || isCustomValue( paint ) ) return paint;
	//
	//		DesignLayer layer = getLayer();
	//		return layer == null ? DesignLayer.DEFAULT_TEXT_FILL_PAINT : layer.getTextFillPaint();
	//	}
	//
	//	public String getTextFillPaint() {
	//		// Do not default to layer for this property
	//		return getValue( TEXT_FILL_PAINT );
	//	}
	//
	//	public DesignDrawable setTextFillPaint( String paint ) {
	//		setValue( TEXT_FILL_PAINT, paint );
	//		return this;
	//	}
	//
	//	public Paint calcTextDrawPaint() {
	//		return Paints.parseWithNullOnException( getTextDrawPaintWithInheritance() );
	//	}
	//
	//	public String getTextDrawPaintWithInheritance() {
	//		String paint = getTextDrawPaint();
	//		if( paint == null || isCustomValue( paint ) ) return paint;
	//
	//		DesignLayer layer = getLayer();
	//		return layer == null ? DesignLayer.DEFAULT_TEXT_DRAW_PAINT : layer.getTextDrawPaint();
	//	}
	//
	//	public String getTextDrawPaint() {
	//		return getValue( TEXT_DRAW_PAINT );
	//	}
	//
	//	public DesignDrawable setTextDrawPaint( String paint ) {
	//		setValue( TEXT_DRAW_PAINT, paint );
	//		return this;
	//	}
	//
	//	public double calcTextDrawWidth() {
	//		return CadMath.evalNoException( getTextDrawWidthWithInheritance() );
	//	}
	//
	//	public String getTextDrawWidthWithInheritance() {
	//		String width = getTextDrawWidth();
	//		if( isCustomValue( width ) ) return width;
	//
	//		DesignLayer layer = getLayer();
	//		return layer == null ? DesignLayer.DEFAULT_TEXT_DRAW_WIDTH : layer.getTextDrawWidth();
	//	}
	//
	//	public String getTextDrawWidth() {
	//		return getValue( TEXT_DRAW_WIDTH, MODE_LAYER );
	//	}
	//
	//	public DesignDrawable setTextDrawWidth( String width ) {
	//		setValue( TEXT_DRAW_WIDTH, width );
	//		return this;
	//	}
	//
	//	public List<Double> calcTextDrawPattern() {
	//		return CadShapes.parseDashPattern( getTextDrawPatternWithInheritance() );
	//	}
	//
	//	public String getTextDrawPatternWithInheritance() {
	//		String pattern = getTextDrawPattern();
	//		if( isCustomValue( pattern ) ) return pattern;
	//
	//		DesignLayer layer = getLayer();
	//		return layer == null ? DesignLayer.DEFAULT_TEXT_DRAW_PATTERN : layer.getTextDrawPattern();
	//	}
	//
	//	public String getTextDrawPattern() {
	//		// Do not default to layer for this property
	//		return getValue( TEXT_DRAW_PATTERN );
	//	}
	//
	//	public DesignDrawable setTextDrawPattern( String pattern ) {
	//		setValue( TEXT_DRAW_PATTERN, TextUtil.isEmpty( pattern ) ? null : pattern );
	//		return this;
	//	}
	//
	//	public StrokeLineCap calcTextDrawCap() {
	//		return StrokeLineCap.valueOf( getTextDrawCapWithInheritance().toUpperCase() );
	//	}
	//
	//	public String getTextDrawCapWithInheritance() {
	//		String cap = getTextDrawCap();
	//		if( isCustomValue( cap ) ) return cap;
	//
	//		DesignLayer layer = getLayer();
	//		return layer == null ? DesignLayer.DEFAULT_TEXT_DRAW_CAP : layer.getTextDrawCap();
	//	}
	//
	//	public String getTextDrawCap() {
	//		return getValue( TEXT_DRAW_CAP, MODE_LAYER );
	//	}
	//
	//	public DesignDrawable setTextDrawCap( String cap ) {
	//		setValue( TEXT_DRAW_CAP, cap );
	//		return this;
	//	}

	public double calcRotate() {
		return hasKey( ROTATE ) ? getRotate() : 0.0;
	}

	public Double getRotate() {
		return getValue( ROTATE );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignText> T setRotate( Double value ) {
		if( value != null && CadGeometry.areSameAngle360( 0.0, value ) ) value = null;
		setValue( ROTATE, value );
		return (T)this;
	}

	public CadOrientation getOrientation() {
		return calcOrientation( getOrigin(), calcRotate() );
	}

	public static CadOrientation calcOrientation( Point3D center, double rotate ) {
		return new CadOrientation( center, CadPoints.UNIT_Z, CadGeometry.rotate360( CadPoints.UNIT_Y, rotate ) );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public <T> T getValue( String key ) {
		return switch( key ) {
			case VIRTUAL_TEXT_FONT_MODE -> (T)(getValueMode( getTextFont() ));
			//			case VIRTUAL_TEXT_FONT_MODE -> (T)(getValueMode( getTextFont() ));
			//			case VIRTUAL_TEXT_FILL_PAINT_MODE -> (T)(getValueMode( getTextFillPaint() ));
			//			case VIRTUAL_TEXT_DRAW_PAINT_MODE -> (T)(getValueMode( getTextDrawPaint() ));
			//			case VIRTUAL_TEXT_DRAW_WIDTH_MODE -> (T)(getValueMode( getTextDrawWidth() ));
			//			case VIRTUAL_TEXT_DRAW_PATTERN_MODE -> (T)(getValueMode( getTextDrawPattern() ));
			//			case VIRTUAL_TEXT_DRAW_CAP_MODE -> (T)(getValueMode( getTextDrawCap() ));

			default -> super.getValue( key );
		};
	}

	@Override
	public <T> T setValue( String key, T newValue ) {
		return switch( key ) {
			case VIRTUAL_TEXT_FONT_MODE -> changeTextFontMode( newValue );
			//			case VIRTUAL_TEXT_FILL_PAINT_MODE -> changeTextFillPaintMode( newValue );
			//			case VIRTUAL_TEXT_DRAW_PAINT_MODE -> changeTextDrawPaintMode( newValue );
			//			case VIRTUAL_TEXT_DRAW_WIDTH_MODE -> changeTextDrawWidthMode( newValue );
			//			case VIRTUAL_TEXT_DRAW_PATTERN_MODE -> changeTextDrawPatternMode( newValue );
			//			case VIRTUAL_TEXT_DRAW_CAP_MODE -> changeTextDrawCapMode( newValue );

			default -> super.setValue( key, newValue );
		};
	}

	@Override
	public double distanceTo( Point3D point ) {
		return CadGeometry.distance( getOrigin(), point );
	}

	@Override
	public double pathLength() {
		// Text does not have a path length
		return Double.NaN;
	}

	@Override
	public Map<String, Object> getInformation() {
		return Map.of( ORIGIN, getOrigin(), TEXT, getText(), TEXT_FONT, getTextFont(), ROTATE, getRotate() );
	}

	@Override
	public DesignText cloneShape() {
		return new DesignText().copyFrom( this, true );
	}

	@Override
	public void apply( CadTransform transform ) {
		double oldRotate = CadGeometry.angle360( getOrientation().getRotate() );
		CadOrientation newPose = getOrientation().clone().transform( transform );
		double newRotate = CadGeometry.angle360( newPose.getRotate() );
		double dRotate = newRotate - oldRotate;
		double rotate = calcRotate() + dRotate;

		try( Txn ignored = Txn.create() ) {
			setOrigin( transform.apply( getOrigin() ) );
			setRotate( rotate );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to apply transform" );
		}
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, TEXT );
		map.putAll( asMap( TEXT, TEXT_FONT, ROTATE ) );
		return map;
	}

	@Override
	public DesignText updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( TEXT ) ) setText( (String)map.get( TEXT ) );
		if( map.containsKey( TEXT_FONT ) ) setTextFont( (String)map.get( TEXT_FONT ) );
		if( map.containsKey( ROTATE ) ) setRotate( (Double)map.get( ROTATE ) );
		return this;
	}

	@Override
	public DesignShape updateFrom( DesignShape shape ) {
		super.updateFrom( shape );
		if( !(shape instanceof DesignText text) ) return this;

		try( Txn ignore = Txn.create() ) {
			this.setRotate( text.getRotate() );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to update curve" );
		}

		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, TEXT, TEXT_FONT, ROTATE );
	}

	<T> T changeTextFontMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_TEXT_FONT_MODE );
		try( Txn ignored = Txn.create() ) {
			setTextFont( String.valueOf( newValue ) );
			setTextFont( isCustom ? getTextFontWithInheritance() : String.valueOf( newValue ) );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_TEXT_FONT_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error setting text font" );
		}
		return newValue;
	}

	//	<T> T changeTextFillPaintMode( T newValue ) {
	//		boolean isCustom = MODE_CUSTOM.equals( newValue );
	//
	//		String oldValue = getValue( VIRTUAL_TEXT_FILL_PAINT_MODE );
	//		try( Txn ignored = Txn.create() ) {
	//			setTextFillPaint( isCustom ? getTextFillPaintWithInheritance() : String.valueOf( newValue ).toLowerCase() );
	//			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_TEXT_FILL_PAINT_MODE, oldValue, newValue ) ) );
	//		} catch( TxnException exception ) {
	//			log.atError().withCause( exception ).log( "Error setting text fill paint" );
	//		}
	//		return newValue;
	//	}
	//
	//	<T> T changeTextDrawPaintMode( T newValue ) {
	//		boolean isCustom = MODE_CUSTOM.equals( newValue );
	//
	//		String oldValue = getValue( VIRTUAL_TEXT_DRAW_PAINT_MODE );
	//		try( Txn ignored = Txn.create() ) {
	//			setTextDrawPaint( isCustom ? getTextDrawPaintWithInheritance() : String.valueOf( newValue ).toLowerCase() );
	//			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_TEXT_DRAW_PAINT_MODE, oldValue, newValue ) ) );
	//		} catch( TxnException exception ) {
	//			log.atError().withCause( exception ).log( "Error changing text draw paint" );
	//		}
	//		return newValue;
	//	}
	//
	//	<T> T changeTextDrawWidthMode( T newValue ) {
	//		boolean isCustom = MODE_CUSTOM.equals( newValue );
	//
	//		String oldValue = getValue( VIRTUAL_TEXT_DRAW_WIDTH_MODE );
	//		try( Txn ignored = Txn.create() ) {
	//			setTextDrawWidth( isCustom ? getTextDrawWidthWithInheritance() : String.valueOf( newValue ).toLowerCase() );
	//			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_TEXT_DRAW_WIDTH_MODE, oldValue, newValue ) ) );
	//		} catch( TxnException exception ) {
	//			log.atError().withCause( exception ).log( "Error setting text draw width" );
	//		}
	//		return newValue;
	//	}
	//
	//	<T> T changeTextDrawCapMode( final T newValue ) {
	//		boolean isCustom = MODE_CUSTOM.equals( getValueMode( String.valueOf( newValue ) ) );
	//
	//		String oldValue = getValue( VIRTUAL_TEXT_DRAW_CAP_MODE );
	//		try( Txn ignored = Txn.create() ) {
	//			setTextDrawCap( isCustom ? getTextDrawCapWithInheritance() : String.valueOf( newValue ).toLowerCase() );
	//			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_TEXT_DRAW_CAP_MODE, oldValue, newValue ) ) );
	//		} catch( TxnException exception ) {
	//			log.atError().withCause( exception ).log( "Error setting text draw cap" );
	//		}
	//		return newValue;
	//	}
	//
	//	<T> T changeTextDrawPatternMode( T newValue ) {
	//		boolean isCustom = MODE_CUSTOM.equals( newValue );
	//
	//		String oldValue = getValue( VIRTUAL_TEXT_DRAW_PATTERN_MODE );
	//		try( Txn ignored = Txn.create() ) {
	//			setTextDrawPattern( isCustom ? getTextDrawPatternWithInheritance() : String.valueOf( newValue ).toLowerCase() );
	//			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_TEXT_DRAW_PATTERN_MODE, oldValue, newValue ) ) );
	//		} catch( TxnException exception ) {
	//			log.atError().withCause( exception ).log( "Error setting text draw pattern" );
	//		}
	//		return newValue;
	//	}

}
