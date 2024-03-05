package com.avereon.cartesia.ui;

import javafx.scene.paint.Paint;

public interface PaintPalette {

	int columnCount();

	int rowCount();

	Paint getPaint( int row, int column );

}
