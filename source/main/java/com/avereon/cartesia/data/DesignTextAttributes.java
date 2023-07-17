package com.avereon.cartesia.data;

import javafx.scene.shape.StrokeLineCap;

public interface DesignTextAttributes {

	/**
	 * @deprecated in favor of FONT_NAME
	 */
	@Deprecated
	String TEXT_FONT = "text-font";

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

	@Deprecated
	static final String DEFAULT_TEXT_FONT = "System|Regular|1.0";

	static final String DEFAULT_TEXT_SIZE = "1";

	static final String DEFAULT_TEXT_FILL_PAINT = "#000000ff";

	static final String DEFAULT_TEXT_DRAW_PAINT = null;

	static final String DEFAULT_TEXT_DRAW_WIDTH = "0.05";

	static final String DEFAULT_TEXT_DRAW_CAP = StrokeLineCap.ROUND.name().toLowerCase();

	static final String DEFAULT_TEXT_DRAW_PATTERN = null;

	static final String DEFAULT_FONT_NAME = null;

	static final String DEFUALT_FONT_WEIGHT = null;

	static final String DEFAULT_FONT_POSTURE = null;

	static final String DEFAULT_FONT_UNDERLINE = null;

	static final String DEFAULT_FONT_STRIKETHROUGH = null;

}
