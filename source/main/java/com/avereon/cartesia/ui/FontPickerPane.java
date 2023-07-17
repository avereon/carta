package com.avereon.cartesia.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class FontPickerPane extends VBox {

	private final TextField fontField;

	private StringProperty font;

	private String prior;

	public FontPickerPane() {
		getStyleClass().add( "cartesia-font-picker-pane" );

		// The font text field for manual entry
		fontField = new TextField("This is the font field");

		// Add the children
		getChildren().addAll( fontField );

		// The text field change handler
		fontField.textProperty().addListener( ( p, o, n ) -> doSetFont( n ) );
	}

	public String getFont() {
		return font == null ? null : font.get();
	}

	public StringProperty fontProperty() {
		if( font == null ) font = new SimpleStringProperty();
		return font;
	}

	public void setFont( String font ) {
		// Do not call doSetFont() here, changing the fontField will do that if needed
		fontField.setText( font );
	}

	private void doSetFont( String paint ) {
		fontProperty().set( paint );
	}

}
