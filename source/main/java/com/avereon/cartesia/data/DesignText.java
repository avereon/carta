package com.avereon.cartesia.data;

import com.avereon.cartesia.math.*;
import com.avereon.data.NodeEvent;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import com.avereon.zerra.font.FontMetrics;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import lombok.CustomLog;

import java.util.List;
import java.util.Map;

@SuppressWarnings( "UnusedReturnValue" )
@CustomLog
public class DesignText extends DesignShape implements DesignTextSupport {

	public static final String TEXT = "text";

	public static final String DEFAULT_ROTATE = String.valueOf( 0.0 );

	// TODO Add horizontal alignment
	// TODO Add vertical alignment
	// How about justification?
	// How about rich text support?

	private static final String VIRTUAL_TEXT_SIZE_MODE = "text-size-mode";

	private static final String VIRTUAL_FONT_NAME_MODE = "font-name-mode";

	private static final String VIRTUAL_FONT_WEIGHT_MODE = "font-weight-mode";

	private static final String VIRTUAL_FONT_POSTURE_MODE = "font-posture-mode";

	private static final String VIRTUAL_FONT_UNDERLINE_MODE = "font-underline-mode";

	private static final String VIRTUAL_FONT_STRIKETHROUGH_MODE = "font-strikethrough-mode";

	private Font cachedFont;

	private Bounds cachedTextBounds;

	public DesignText() {
		this( null );
	}

	public DesignText( Point3D origin ) {
		this( origin, null );
	}

	public DesignText( Point3D origin, String text ) {
		this( origin, text, null );
	}

	public DesignText( Point3D origin, String text, String rotate ) {
		super( origin );
		addModifyingKeys( TEXT, TEXT_SIZE, FONT_NAME, FONT_WEIGHT, FONT_POSTURE, FONT_UNDERLINE, FONT_STRIKETHROUGH );

		setText( text );
		setRotate( rotate );
	}

	@Override
	public DesignShape.Type getType() {
		return DesignShape.Type.TEXT;
	}

	public String getText() {
		return getValue( TEXT );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignText> T setText( String value ) {
		setValue( TEXT, value );

		cachedTextBounds = null;

		return (T)this;
	}

	// Text size

	public double calcTextSize() {
		return CadMath.eval( getTextSizeWithInheritance() );
	}

	public String getTextSizeWithInheritance() {
		String value = getTextSize();
		if( isCustomValue( value ) ) return value;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_TEXT_SIZE : layer.getTextSize();
	}

	public String getTextSize() {
		return getValue( TEXT_SIZE );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setTextSize( String value ) {
		setValue( TEXT_SIZE, value );

		cachedTextBounds = null;
		cachedFont = null;

		return (T)this;
	}

	// Font name
	public String calcFontName() {
		return getFontNameWithInheritance();
	}

	public String getFontNameWithInheritance() {
		String value = getFontName();
		if( isCustomValue( value ) ) return value;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_FONT_NAME : layer.getFontName();
	}

	public String getFontName() {
		return getValue( FONT_NAME );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setFontName( String value ) {
		setValue( FONT_NAME, value );

		cachedTextBounds = null;
		cachedFont = null;

		return (T)this;
	}

	// Font weight
	public FontWeight calcFontWeight() {
		return FontWeight.valueOf( getFontWeightWithInheritance().toUpperCase() );
	}

	public String getFontWeightWithInheritance() {
		String value = getFontWeight();
		if( isCustomValue( value ) ) return value;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_FONT_WEIGHT : layer.getFontWeight();
	}

	public String getFontWeight() {
		return getValue( FONT_WEIGHT );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setFontWeight( String value ) {
		setValue( FONT_WEIGHT, value );

		cachedTextBounds = null;
		cachedFont = null;

		return (T)this;
	}

	// Font posture
	public FontPosture calcFontPosture() {
		return FontPosture.valueOf( getFontPostureWithInheritance().toUpperCase() );
	}

	public String getFontPostureWithInheritance() {
		String value = getFontPosture();
		if( isCustomValue( value ) ) return value;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_FONT_POSTURE : layer.getFontPosture();
	}

	public String getFontPosture() {
		return getValue( FONT_POSTURE );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setFontPosture( String value ) {
		setValue( FONT_POSTURE, value );

		cachedTextBounds = null;
		cachedFont = null;

		return (T)this;
	}

	// Font underline
	public boolean calcFontUnderline() {
		return Boolean.parseBoolean( getFontUnderlineWithInheritance() );
	}

	public String getFontUnderlineWithInheritance() {
		String value = getFontUnderline();
		if( isCustomValue( value ) ) return value;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_FONT_UNDERLINE : layer.getFontUnderline();
	}

	public String getFontUnderline() {
		return getValue( FONT_UNDERLINE );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setFontUnderline( String value ) {
		setValue( FONT_UNDERLINE, value );

		cachedTextBounds = null;
		cachedFont = null;

		return (T)this;
	}

	// Font strikethrough
	public boolean calcFontStrikethrough() {
		return Boolean.parseBoolean( getFontStrikethroughWithInheritance() );
	}

	public String getFontStrikethroughWithInheritance() {
		String value = getFontStrikethrough();
		if( isCustomValue( value ) ) return value;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_FONT_STRIKETHROUGH : layer.getFontStrikethrough();
	}

	public String getFontStrikethrough() {
		return getValue( FONT_STRIKETHROUGH );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignDrawable> T setFontStrikethrough( String value ) {
		setValue( FONT_STRIKETHROUGH, value );

		cachedTextBounds = null;
		cachedFont = null;

		return (T)this;
	}

	@Override
	public String getDrawPaintWithInheritance() {
		String value = getDrawPaint();
		if( isCustomValue( value ) ) return value;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_TEXT_DRAW_PAINT : layer.getTextDrawPaint();
	}

	@Override
	public String getDrawWidthWithInheritance() {
		String width = getDrawWidth();
		if( isCustomValue( width ) ) return width;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_TEXT_DRAW_WIDTH : layer.getTextDrawWidth();
	}

	@Override
	public String getDashPatternWithInheritance() {
		String pattern = getDashPattern();
		if( isCustomValue( pattern ) ) return pattern;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_TEXT_DASH_PATTERN : layer.getTextDrawPattern();
	}

	@Override
	public String getDrawCapWithInheritance() {
		String cap = getDrawCap();
		if( isCustomValue( cap ) ) return cap;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_TEXT_DRAW_CAP : layer.getTextDrawCap();
	}

	@Override
	public String getFillPaintWithInheritance() {
		String value = getFillPaint();
		if( isCustomValue( value ) ) return value;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_TEXT_FILL_PAINT : layer.getTextFillPaint();
	}

	public Font calcFont() {
		if( cachedFont == null ) cachedFont = Font.font( calcFontName(), calcFontWeight(), calcFontPosture(), calcTextSize() );
		return cachedFont;
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
			case VIRTUAL_LAYER -> getLayer() == null ? null : (T)getLayer().getId();

			case VIRTUAL_DRAW_PAINT_MODE -> (T)(getValueMode( getDrawPaint() ));
			case VIRTUAL_DRAW_WIDTH_MODE -> (T)(getValueMode( getDrawWidth() ));
			case VIRTUAL_DRAW_PATTERN_MODE -> (T)(getValueMode( getDashPattern() ));
			case VIRTUAL_DRAW_CAP_MODE -> (T)(getValueMode( getDrawCap() ));
			case VIRTUAL_FILL_PAINT_MODE -> (T)(getValueMode( getFillPaint() ));

			case VIRTUAL_TEXT_SIZE_MODE -> (T)(getValueMode( getTextSize() ));
			case VIRTUAL_FONT_NAME_MODE -> (T)(getValueMode( getFontName() ));
			case VIRTUAL_FONT_WEIGHT_MODE -> (T)(getValueMode( getFontWeight() ));
			case VIRTUAL_FONT_POSTURE_MODE -> (T)(getValueMode( getFontPosture() ));
			case VIRTUAL_FONT_UNDERLINE_MODE -> (T)(getValueMode( getFontUnderline() ));
			case VIRTUAL_FONT_STRIKETHROUGH_MODE -> (T)(getValueMode( getFontStrikethrough() ));

			default -> super.getValue( key );
		};
	}

	@Override
	public <T> T setValue( String key, T newValue ) {
		return switch( key ) {
			case VIRTUAL_FILL_PAINT_MODE -> changeFillPaintMode( newValue );
			case VIRTUAL_DRAW_PAINT_MODE -> changeDrawPaintMode( newValue );
			case VIRTUAL_DRAW_WIDTH_MODE -> changeDrawWidthMode( newValue );
			case VIRTUAL_DRAW_PATTERN_MODE -> changeDrawPatternMode( newValue );
			case VIRTUAL_DRAW_CAP_MODE -> changeDrawCapMode( newValue );

			case VIRTUAL_TEXT_SIZE_MODE -> changeTextSizeMode( newValue );
			case VIRTUAL_FONT_NAME_MODE -> changeFontNameMode( newValue );
			case VIRTUAL_FONT_WEIGHT_MODE -> changeFontWeightMode( newValue );
			case VIRTUAL_FONT_POSTURE_MODE -> changeFontPostureMode( newValue );
			case VIRTUAL_FONT_UNDERLINE_MODE -> changeFontUnderlineMode( newValue );
			case VIRTUAL_FONT_STRIKETHROUGH_MODE -> changeFontStrikethroughMode( newValue );

			default -> super.setValue( key, newValue );
		};
	}

	//	@Override
	//	protected Bounds computeVisualBounds() {
	//		Bounds textBounds = new FontMetrics( calcFont() ).computeStringBounds( getText() );
	//		double x = getOrigin().getX() + textBounds.getMinX();
	//		double y = getOrigin().getY() - (textBounds.getMinY() + textBounds.getHeight());
	//		double w = textBounds.getWidth();
	//		double h = textBounds.getHeight();
	//
	//		Bounds bounds = new BoundingBox( x, y, w, h );
	//		CadTransform rotate = CadTransform.rotation( getOrigin().getX(), getOrigin().getY(), calcRotate() );
	//		return rotate.apply( bounds );
	//	}

	/**
	 * Get the local (non-rotated) text bounds.
	 *
	 * @return The local text bounds.
	 */
	public Bounds getLocalTextBounds() {
		if( cachedTextBounds == null ) cachedTextBounds = new FontMetrics( calcFont() ).computeStringBounds( getText() );
		return cachedTextBounds;
	}

	@Override
	public List<Point3D> getReferencePoints() {
		Bounds bounds = getLocalTextBounds();
		Point3D origin = getOrigin();
		Point3D point = origin.add( bounds.getWidth(), 0, 0 );
		return CadGeometry.rotate360( origin, calcRotate(), List.of( origin, point ) );
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
		return Map.of(
			ORIGIN,
			getOrigin(),
			TEXT,
			getText(),
			ROTATE,
			getRotate(),
			TEXT_SIZE,
			getTextSize(),
			FONT_NAME,
			getFontName(),
			FONT_WEIGHT,
			getFontName(),
			FONT_POSTURE,
			getFontName(),
			FONT_UNDERLINE,
			getFontName(),
			FONT_STRIKETHROUGH,
			getFontName()
		);
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
			setRotate( String.valueOf( rotate ) );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to apply transform" );
		}
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, TEXT );
		map.putAll( asMap( TEXT, ROTATE, TEXT_SIZE, FONT_NAME, FONT_WEIGHT, FONT_POSTURE, FONT_UNDERLINE, FONT_STRIKETHROUGH ) );
		return map;
	}

	@Override
	public DesignText updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( TEXT ) ) setText( (String)map.get( TEXT ) );
		if( map.containsKey( ROTATE ) ) setRotate( (String)map.get( ROTATE ) );

		if( map.containsKey( TEXT_SIZE ) ) setTextSize( (String)map.get( TEXT_SIZE ) );
		if( map.containsKey( FONT_NAME ) ) setFontName( (String)map.get( FONT_NAME ) );
		if( map.containsKey( FONT_WEIGHT ) ) setTextSize( (String)map.get( FONT_WEIGHT ) );
		if( map.containsKey( FONT_POSTURE ) ) setTextSize( (String)map.get( FONT_POSTURE ) );
		if( map.containsKey( FONT_UNDERLINE ) ) setTextSize( (String)map.get( FONT_UNDERLINE ) );
		if( map.containsKey( FONT_STRIKETHROUGH ) ) setTextSize( (String)map.get( FONT_STRIKETHROUGH ) );

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
		return super.toString( ORIGIN, TEXT, ROTATE, TEXT_SIZE, FONT_NAME, FONT_WEIGHT, FONT_POSTURE, FONT_UNDERLINE, FONT_STRIKETHROUGH );
	}

	<T> T changeTextSizeMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_TEXT_SIZE_MODE );
		try( Txn ignored = Txn.create() ) {
			setTextSize( isCustom ? getTextSizeWithInheritance() : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_TEXT_SIZE_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error setting text size" );
		}
		return newValue;
	}

	<T> T changeFontNameMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_FONT_NAME_MODE );
		try( Txn ignored = Txn.create() ) {
			setFontName( isCustom ? getFontNameWithInheritance() : String.valueOf( newValue ).toLowerCase() );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_FONT_NAME_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error setting font name" );
		}
		return newValue;
	}

	<T> T changeFontWeightMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_FONT_WEIGHT_MODE );
		try( Txn ignored = Txn.create() ) {
			setFontWeight( isCustom ? getFontWeightWithInheritance() : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_FONT_WEIGHT_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error setting font weight" );
		}
		return newValue;
	}

	<T> T changeFontPostureMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_FONT_POSTURE_MODE );
		try( Txn ignored = Txn.create() ) {
			setFontPosture( isCustom ? getFontPostureWithInheritance() : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_FONT_POSTURE_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error setting font posture" );
		}
		return newValue;
	}

	<T> T changeFontUnderlineMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_FONT_UNDERLINE_MODE );
		try( Txn ignored = Txn.create() ) {
			setFontUnderline( isCustom ? getFontUnderlineWithInheritance() : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_FONT_UNDERLINE_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error setting font underline" );
		}
		return newValue;
	}

	<T> T changeFontStrikethroughMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_FONT_STRIKETHROUGH_MODE );
		try( Txn ignored = Txn.create() ) {
			setFontStrikethrough( isCustom ? getFontStrikethroughWithInheritance() : null );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_FONT_STRIKETHROUGH_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error setting font strikethrough" );
		}
		return newValue;
	}

}
