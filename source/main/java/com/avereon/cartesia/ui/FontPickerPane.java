package com.avereon.cartesia.ui;

import com.avereon.zerra.font.FontUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

public class FontPickerPane extends VBox {

	private StringProperty font;

	private final ComboBox<String> fontFamily;

	private ObservableList<String> fontFamilyOptions;

	private final ComboBox<FontWeight> fontWeight;

	private ObservableList<FontWeight> fontWeightOptions;

	private final Label sample;

	private String prior;

	public FontPickerPane() {
		getStyleClass().add( "cartesia-string-picker-pane" );

		// The string sample field
		sample = new Label( "Sample" );

		// Font Family
		fontFamily = new ComboBox<>( getFontFamilyOptions() );
		fontFamily.setCellFactory( new FontFamilyCellFactory() );
		fontFamily.setMaxWidth( Double.MAX_VALUE );

		// Font weight
		fontWeight = new ComboBox<>( getFontWeightOptions() );
		fontWeight.setCellFactory( new FontWeightCellFactory( null ) );
		fontWeight.setMaxWidth( Double.MAX_VALUE );

		// Add the children
		getChildren().addAll( fontFamily, fontWeight, sample );

		// The string family change handler
		fontFamily.getSelectionModel().selectedItemProperty().addListener( ( p, o, n ) -> {
			doSetFontProperty( n, fontWeight.getSelectionModel().getSelectedItem() );
			fontWeight.setCellFactory( new FontWeightCellFactory( n ) );
		} );
		fontWeight.getSelectionModel().selectedItemProperty().addListener( ( p, o, n ) -> {
			doSetFontProperty( fontFamily.getSelectionModel().getSelectedItem(), n );
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

	public void setFont( String string ) {
		// Do not call doSetFontProperty() here, changing the fontField will do that if needed
		//sample.setText( string );
		Font font = FontUtil.decode( string );
		fontFamily.getSelectionModel().select( font.getFamily() );
		fontWeight.getSelectionModel().select( FontUtil.getFontWeight( font.getStyle() ) );
	}

	private void doSetFontProperty( String family, FontWeight weight ) {
		doSetFontProperty( FontUtil.encode( Font.font( family, weight, -1 ) ) );
	}

	private void doSetFontProperty( String font ) {
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
