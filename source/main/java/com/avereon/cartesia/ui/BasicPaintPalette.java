package com.avereon.cartesia.ui;

import com.avereon.zarra.color.Colors;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;

public class BasicPaintPalette implements PaintPalette {

	private static final List<Color> BASE_COLORS = List.of( Color.GRAY, Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE, Color.MAGENTA, Color.CYAN );

	private static final int COLUMN_COUNT = BASE_COLORS.size();

	private static final int ROW_COUNT = 9;

	private static final Paint[][] paints;

	static {
		int row = 0;
		int column = 0;
		paints = new Paint[ ROW_COUNT ][ COLUMN_COUNT ];

		for( Color base : BASE_COLORS ) {
			// Shades
			for( double factor = 1.0; factor > 0.0; factor -= 0.25 ) {
				paints[ row ][ column ] = Colors.getShade( base, factor );
				row++;
			}

			// Hue
			paints[ row ][ column ] = base;
			row++;

			// Tints
			for( double factor = 0.25; factor <= 1.0; factor += 0.25 ) {
				paints[ row ][ column ] = Colors.getTint( base, factor );
				row++;
			}
			column++;
			row = 0;
		}
	}

	@Override
	public int columnCount() {
		return COLUMN_COUNT;
	}

	@Override
	public int rowCount() {
		return ROW_COUNT;
	}

	@Override
	public Paint getPaint( int row, int column ) {
		return paints[ row ][ column ];
	}

}
