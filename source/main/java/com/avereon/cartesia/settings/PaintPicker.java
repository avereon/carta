package com.avereon.cartesia.settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;

public class PaintPicker extends ComboBoxBase<Paint> {

	private static final String DEFAULT_STYLE_CLASS = "color-picker";

	private final ObservableList<Paint> customPaints = FXCollections.observableArrayList();

	public PaintPicker() {
		this( Color.BLACK );
	}

	public PaintPicker( Paint paint ) {
		setValue( paint );
		getStyleClass().add( DEFAULT_STYLE_CLASS );
	}

	public final ObservableList<Paint> getCustomPaints() {
		return customPaints;
	}

	public final void setCustomPaints( List<Paint> paints ) {
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
