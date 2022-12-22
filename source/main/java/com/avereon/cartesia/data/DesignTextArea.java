package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import javafx.scene.text.Font;

public class DesignTextArea extends DesignTextLine {

	public static final String TEXTAREA = "textarea";

	public DesignTextArea() {
		this( null );
	}

	public DesignTextArea( Point3D origin) {
		this( origin, null );
	}

	public DesignTextArea( Point3D origin, String text ) {
		this( origin, text, null );
	}

	public DesignTextArea( Point3D origin, String text, Font font ) {
		this( origin, text, font, 0.0);
	}

	public DesignTextArea( Point3D origin, String text, Font font, Double rotate ) {
		super( origin, text, font, rotate);
	}

}
