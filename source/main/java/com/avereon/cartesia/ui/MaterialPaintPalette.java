package com.avereon.cartesia.ui;

import com.avereon.zarra.color.MaterialColor;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MaterialPaintPalette extends BasePaintPalette {

	private static final List<Color> BASE_COLORS;

	static {
		BASE_COLORS = new ArrayList<>( MaterialColor.getColors() );
		BASE_COLORS.sort( Comparator.comparingDouble( Color::getHue ) );
	}

	public MaterialPaintPalette() {
		super( BASE_COLORS );
	}

}
