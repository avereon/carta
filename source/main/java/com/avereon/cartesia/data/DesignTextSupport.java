package com.avereon.cartesia.data;

import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public interface DesignTextSupport {

	String TEXT_SIZE = "text-size";

	String TEXT_FILL_PAINT = "text-fill-paint";

	String TEXT_DRAW_PAINT = "text-draw-paint";

	String TEXT_DRAW_WIDTH = "text-draw-width";

	String TEXT_DRAW_CAP = "text-draw-cap";

	String TEXT_DRAW_PATTERN = "text-draw-pattern";

	String FONT_NAME = "font-name";

	String FONT_WEIGHT="font-weight";

	String FONT_POSTURE = "font-posture";

	String FONT_UNDERLINE = "font-underline";

	String FONT_STRIKETHROUGH = "font-strikethrough";

	// Text size

	double calcTextSize();

	String getTextSize();

	<T extends DesignDrawable> T setTextSize( String value );

	// Font name

	String calcFontName();

	String getFontName();

	<T extends DesignDrawable> T setFontName( String value );

	// Font weight
	FontWeight calcFontWeight();

	String getFontWeight();

	<T extends DesignDrawable> T setFontWeight( String value );

	// Font posture
	FontPosture calcFontPosture();

	String getFontPosture();

	<T extends DesignDrawable> T setFontPosture( String value );

	// Font underline
	boolean calcFontUnderline();

	String getFontUnderline();

	<T extends DesignDrawable> T setFontUnderline( String value );

	// Font strikethrough
	boolean calcFontStrikethrough();

	String getFontStrikethrough();

	<T extends DesignDrawable> T setFontStrikethrough( String value );

}
