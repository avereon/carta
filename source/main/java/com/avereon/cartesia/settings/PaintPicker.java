package com.avereon.cartesia.settings;

import com.avereon.zerra.color.Paints;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;

import java.util.List;

public class PaintPicker extends ComboBoxBase<String> {

	private static final String DEFAULT_STYLE_CLASS = "color-picker";

	private final ObservableList<String> customPaints = FXCollections.observableArrayList();

	public PaintPicker() {
		this( Paints.toString( Color.BLACK ) );
	}

	public PaintPicker( String paint ) {
		setValue( paint );
		getStyleClass().add( DEFAULT_STYLE_CLASS );
	}

	public final ObservableList<String> getCustomPaints() {
		return customPaints;
	}

	public final void setCustomPaints( List<String> paints ) {
		customPaints.setAll( paints );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Skin<?> createDefaultSkin() {
		return new PaintPickerSkin( this );
	}

}
