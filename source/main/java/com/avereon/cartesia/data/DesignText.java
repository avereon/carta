package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import javafx.scene.text.Font;

public abstract class DesignText extends DesignShape {

	public static final String TEXT = "text";

	public static final String FONT = "font";

	public static final String ROTATE = "rotate";

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
		addModifyingKeys( FONT, ROTATE );

		setText( text );
		setFont( font );
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
		return hasKey( FONT ) ? getFont() : Font.getDefault();
	}

	public Font getFont() {
		return getValue( FONT );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignText> T setFont( Font value ) {
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

}
