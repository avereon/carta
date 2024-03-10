package com.avereon.cartesia.ui;

import javafx.scene.paint.Paint;

public class EmptyPaintPalette implements PaintPalette{

	@Override
	public int columnCount() {
		return 0;
	}

	@Override
	public int rowCount() {
		return 0;
	}

	@Override
	public Paint getPaint( int row, int column ) {
		return null;
	}

}
