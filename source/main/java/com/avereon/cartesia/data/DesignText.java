package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadOrientation;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.data.NodeEvent;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import com.avereon.zarra.font.FontUtil;
import javafx.geometry.Point3D;
import javafx.scene.text.Font;
import lombok.CustomLog;

import java.util.Map;

@CustomLog
public class DesignText extends DesignShape {

	public static final String TEXT = "text";

	public static final String FONT = "font";

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

	public DesignText( Point3D origin, String text, Font font ) {
		this( origin, text, font, 0.0 );
	}

	public DesignText( Point3D origin, String text, Font font, Double rotate ) {
		super( origin );
		addModifyingKeys( TEXT, FONT, ROTATE );

		setText( text );
		setFont( font == null ? null : FontUtil.encode( font ) );
		setRotate( rotate );
	}

	public String getText() {
		return getValue( TEXT );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignText> T setText( String value ) {
		setValue( TEXT, value );
		return (T)this;
	}

	public Font calcFont() {
		return FontUtil.decode( getFontWithInheritance() );
	}

	public String getFontWithInheritance() {
		String font = getFont();
		if( isCustomValue( font ) ) return font;

		DesignLayer layer = getLayer();
		return layer == null ? DesignLayer.DEFAULT_TEXT_FONT : FontUtil.encode(Font.getDefault());
	}

	public String getFont() {
		return getValue( FONT );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignText> T setFont( String value ) {
		log.atConfig().log( "Setting text font=" + value );
		setValue( FONT, value );
		return (T)this;
	}

	public double calcRotate() {
		return hasKey( ROTATE ) ? getRotate() : 0.0;
	}

	public Double getRotate() {
		return getValue( ROTATE );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignText> T setRotate( Double value ) {
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
		return Map.of( ORIGIN, getOrigin(), TEXT, getText(), FONT, getFont(), ROTATE, getRotate() );
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
		map.putAll( asMap( TEXT, FONT, ROTATE ) );
		return map;
	}

	@Override
	public DesignText updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( TEXT ) ) setText( (String)map.get( TEXT ) );
		if( map.containsKey( FONT ) ) setFont( (String)map.get( FONT ) );
		if( map.containsKey( ROTATE ) ) setRotate( (Double)map.get( ROTATE ) );
		return this;
	}

	@Override
	public DesignShape updateFrom( DesignShape shape ) {
		super.updateFrom( shape );
		if( !(shape instanceof DesignText text) ) return this;

		try( Txn ignore = Txn.create() ) {
			this.setText( text.getText() );
			this.setFont( text.getFont() );
			this.setRotate( text.getRotate() );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to update curve" );
		}

		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, TEXT, FONT, ROTATE );
	}

	// NEXT If font is a per element attribute then I don't need the following methods:

	@Override
	public <T> T setValue( String key, T newValue ) {
		if( key.equals( VIRTUAL_TEXT_FONT_MODE ) ) changeTextFontMode( newValue );
		return super.setValue( key, newValue );
	}

	<T> T changeTextFontMode( T newValue ) {
		boolean isCustom = MODE_CUSTOM.equals( newValue );

		String oldValue = getValue( VIRTUAL_TEXT_FONT_MODE );
		try( Txn ignored = Txn.create() ) {
			setFont( String.valueOf( newValue ) );
			setFont( isCustom ? getFontWithInheritance() : String.valueOf( newValue ) );
			Txn.submit( this, t -> getEventHub().dispatch( new NodeEvent( this, NodeEvent.VALUE_CHANGED, VIRTUAL_TEXT_FONT_MODE, oldValue, newValue ) ) );
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error setting text font" );
		}
		return newValue;
	}

}
