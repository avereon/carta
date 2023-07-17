package com.avereon.cartesia.settings;

import com.avereon.cartesia.ui.FontPicker;
import com.avereon.product.Rb;
import com.avereon.settings.SettingsEvent;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.tool.settings.SettingData;
import com.avereon.xenon.tool.settings.SettingEditor;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import lombok.CustomLog;

import java.util.Collection;
import java.util.List;

@CustomLog
public class FontSettingEditor extends SettingEditor {

	private Label label;

	private final FontPicker fontPicker;

	private List<Node> nodes;

	public FontSettingEditor( XenonProgramProduct product, String rbKey, SettingData setting ) {
		super( product, rbKey, setting );
		label = new Label();
		fontPicker = new FontPicker();
	}

	@Override
	public void addComponents( GridPane pane, int row ) {
		String rbKey = setting.getRbKey();
		String value = setting.getSettings().get( getKey(), "SansSerif|1" );

		label = new Label( Rb.text( getProduct(), getRbKey(), rbKey ) );
		label.setMinWidth( Region.USE_PREF_SIZE );

		fontPicker.setId( rbKey );
		fontPicker.setFontAsString( value );
		fontPicker.setMaxWidth( Double.MAX_VALUE );
		HBox.setHgrow( fontPicker, Priority.ALWAYS );

		nodes = List.of( label, fontPicker );

		// Add the event handlers
		fontPicker.fontAsStringProperty().addListener( this::doPickerValueChanged );

		// Set component state
		setDisable( setting.isDisable() );
		setVisible( setting.isVisible() );

		// Add the components
		pane.addRow( row, label, fontPicker );
	}

	@Override
	protected Collection<Node> getComponents() {
		return nodes;
	}

	@Override
	protected void doSettingValueChanged( SettingsEvent event ) {
		if( event.getEventType() != SettingsEvent.CHANGED || !getKey().equals( event.getKey() ) ) return;

		Object value = event.getNewValue();
		String paint = value == null ? null : String.valueOf( value );
		fontPicker.setFontAsString( paint );
	}

	private void doPickerValueChanged( ObservableValue<? extends String> property, String oldValue, String newValue ) {
		setting.getSettings().set( setting.getKey(), newValue );
	}

}
