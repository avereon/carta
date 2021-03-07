package com.avereon.cartesia.settings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class PaintPickerPane extends VBox {

	private static final String DEFAULT_PAINT_STRING = "";

	private final ComboBox<PaintMode> mode;

	private ObservableList<PaintMode> options;

	private StringProperty paint;

	public PaintPickerPane() {
		// How about a combo for the mode: none, solid, linear[] and radial()
		// To the right of the combo a component to define gradient stops
		// Below that, the tabs for palette, RGB, HSB an WEB
		// Opacity can be a slider on the right or the bottom
		// Below that the OK and Cancel buttons

		mode = new ComboBox<>();
		mode.setMaxWidth( Double.MAX_VALUE );

		getChildren().add( mode );

		getOptions().addListener( (ListChangeListener<PaintMode>)( e ) -> {
			mode.getItems().clear();
			mode.getItems().addAll( options );
			updateMode();
		} );
	}

	@Override
	public void requestFocus() {
		mode.requestFocus();
	}

	public ObservableList<PaintMode> getOptions() {
		if( options == null ) options = FXCollections.observableArrayList();
		return options;
	}

	public String getPaint() {
		return paint == null ? null : paint.get();
	}

	public StringProperty paintProperty() {
		if( paint == null ) paint = new SimpleStringProperty();
		return paint;
	}

	public void setPaint( String paint ) {
		paintProperty().set( paint );
		updateMode();
	}

	private void updateMode() {
		if( getPaint() == null ) {
			mode.getSelectionModel().select( PaintMode.NONE );
		} else if( getPaint().startsWith( "#" ) ) {
			mode.getSelectionModel().select( PaintMode.SOLID );
		} else if( getPaint().startsWith( "[" ) ) {
			mode.getSelectionModel().select( PaintMode.LINEAR );
		} else if( getPaint().startsWith( "(" ) ) {
			mode.getSelectionModel().select( PaintMode.RADIAL );
		} else {
			mode.getItems().stream().filter( m -> Objects.equals( getPaint(), m.getKey() ) ).findAny().ifPresent( m -> mode.getSelectionModel().select( m ) );
		}
	}

}
