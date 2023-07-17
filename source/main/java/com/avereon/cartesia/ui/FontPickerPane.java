package com.avereon.cartesia.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

public class FontPickerPane extends VBox {

	private final TextField fontField;

	private final ComboBox<String> fontFamily;

	private ObservableList<String> fontFamilyOptions;

	private final ComboBox<FontWeight> fontWeight;

	private ObservableList<FontWeight> fontWeightOptions;

	private StringProperty font;

	private String prior;

	public FontPickerPane() {
		getStyleClass().add( "cartesia-font-picker-pane" );

		// The font text field for manual entry
		fontField = new TextField( "This is the font field" );

		fontFamily = new ComboBox<>( getFontFamilyOptions() );
		fontFamily.setCellFactory( new FontFamilyCellFactory() );

		fontWeight = new ComboBox<>( getFontWeightOptions() );
		fontWeight.setCellFactory( new FontWeightCellFactory( null ) );

		// Add the children
		getChildren().addAll( fontField, fontFamily, fontWeight );

		// The text field change handler
		fontField.textProperty().addListener( ( p, o, n ) -> doSetFont( n ) );

		// The font family change handler
		fontFamily.getSelectionModel().selectedItemProperty().addListener( ( p, o, n ) -> {
			fontWeight.setCellFactory( new FontWeightCellFactory( n ) );
		} );
	}

	public ObservableList<String> getFontFamilyOptions() {
		if( fontFamilyOptions == null ) {
			fontFamilyOptions = FXCollections.observableArrayList( Font.getFamilies() );
		}
		return fontFamilyOptions;
	}

	public ObservableList<FontWeight> getFontWeightOptions() {
		if( fontWeightOptions == null ) {
			fontWeightOptions = FXCollections.observableArrayList( FontWeight.values() );
		}
		return fontWeightOptions;
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

	private void doSetFont( String font ) {
		fontProperty().set( font );
	}

	private static class FontFamilyCellFactory implements Callback<ListView<String>, ListCell<String>> {

		@Override
		public ListCell<String> call( ListView<String> stringListView ) {
			return new ListCell<>() {

				@Override
				protected void updateItem( String item, boolean empty ) {
					super.updateItem( item, empty );
					setFont( Font.font( item ) );
					setText( item );
				}
			};
		}

	}

	private static class FontWeightCellFactory implements Callback<ListView<FontWeight>, ListCell<FontWeight>> {

		private final String fontFamily;

		public FontWeightCellFactory( String fontFamily ) {
			this.fontFamily = fontFamily;
		}

		@Override
		public ListCell<FontWeight> call( ListView<FontWeight> stringListView ) {
			return new ListCell<>() {

				@Override
				protected void updateItem( FontWeight item, boolean empty ) {
					super.updateItem( item, empty );
					setFont( Font.font( fontFamily, item, getFont().getSize() ) );
					setText( item == null ? null : item.toString() );
				}
			};
		}

	}

}
