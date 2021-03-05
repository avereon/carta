package com.avereon.cartesia.settings;

import com.avereon.util.Log;
import com.avereon.zerra.color.Paints;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;

/**
 * Because ComboBoxBase requires a skin, which in turn requires a behavior,
 * which is in a restricted package, it is not possible to create a custom combo-box
 * control.
 */
public class PaintPickerCombo extends ComboBoxBase<String> {

	private static final System.Logger log = Log.get();

	private static final String DEFAULT_STYLE_CLASS = "color-picker";

	private final ObservableList<String> customPaints = FXCollections.observableArrayList();

	public PaintPickerCombo() {
		this( Paints.toString( Color.BLACK ) );
	}

	public PaintPickerCombo( String paint ) {
		getStyleClass().add( DEFAULT_STYLE_CLASS );
		setValue( paint );
	}

	@Override
	public void show() {
		log.log( Log.INFO, "PaintPicker.show()" );
		super.show();
	}

	@Override
	public void hide() {
		log.log( Log.INFO, "PaintPicker.hide()" );
		super.hide();
	}

	@Override
	public void arm() {
		log.log( Log.INFO, "PaintPicker.arm()" );
		super.arm();
	}

	@Override
	public void disarm() {
		log.log( Log.INFO, "PaintPicker.disarm()" );
		super.disarm();
	}

	//	public final ObservableList<String> getCustomPaints() {
//		return customPaints;
//	}
//
//	public final void setCustomPaints( List<String> paints ) {
//		customPaints.setAll( paints );
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Skin<?> createDefaultSkin() {
		PaintPickerSkin skin = new PaintPickerSkin(this);
		setOnMouseClicked( e -> skin.showOrHide() );
		return skin;
	}

}
