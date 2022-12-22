package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import javafx.scene.text.Font;

public class DesignTextLine extends DesignText {

	public static final String TEXTLINE = "textline";

	public DesignTextLine() {
		this( null );
	}

	public DesignTextLine( Point3D origin) {
		this( origin, null );
	}

	public DesignTextLine( Point3D origin, String text ) {
		this( origin, text, null );
	}

	public DesignTextLine( Point3D origin, String text, Font font ) {
		this( origin, text, font, 0.0);
	}

	public DesignTextLine( Point3D origin, String text, Font font, Double rotate ) {
		super( origin, text, font, rotate);
	}

}
