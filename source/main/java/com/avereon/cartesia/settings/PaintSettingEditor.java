package com.avereon.cartesia.settings;

import com.avereon.product.Rb;
import com.avereon.settings.SettingsEvent;
import com.avereon.util.Log;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.UiFactory;
import com.avereon.xenon.tool.settings.SettingData;
import com.avereon.xenon.tool.settings.SettingEditor;
import com.avereon.zerra.color.Colors;
import com.avereon.zerra.color.Paints;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

import java.util.List;
import java.util.stream.Collectors;

public class PaintSettingEditor extends SettingEditor {

	private static final Paint DEFAULT_PAINT = Color.BLACK;

	private static final System.Logger log = Log.get();

	private final Label label;

	private final PaintPicker paintPicker;

	private List<Node> nodes;

	public PaintSettingEditor( ProgramProduct product, String bundleKey, SettingData setting ) {
		super( product, bundleKey, setting );
		label = new Label();
		paintPicker = new PaintPicker();
	}

	@Override
	public void addComponents( GridPane pane, int row ) {
		String rbKey = setting.getBundleKey();
		String value = setting.getSettings().get( getKey() );

		label.setText( Rb.text( getBundleKey(), rbKey ) );
		label.setMinWidth( Region.USE_PREF_SIZE );

		paintPicker.setId( rbKey );
		paintPicker.setPaintAsString( value );
		paintPicker.getOptions().addAll( setting.getOptions().stream().map( o -> switch( o.getKey() ) {
			case "none" -> PaintMode.NONE;
			case "solid" -> PaintMode.SOLID;
			case "linear" -> PaintMode.LINEAR;
			case "radial" -> PaintMode.RADIAL;
			default -> new PaintMode( o.getKey(), o.getName(), o.getOptionValue() );
		} ).collect( Collectors.toList() ) );
		paintPicker.setMaxWidth( Double.MAX_VALUE );
		HBox.setHgrow( paintPicker, Priority.ALWAYS );

		nodes = List.of( label, paintPicker );

		// Add the event handlers
		paintPicker.paintAsStringProperty().addListener( this::doPickerValueChanged );

		// Set component state
		setDisable( setting.isDisable() );
		setVisible( setting.isVisible() );

		// Add the components
		pane.addRow( row, label, new HBox( paintPicker ) );
	}

	@Override
	public List<Node> getComponents() {
		return nodes;
	}

	@Override
	protected void doSettingValueChanged( SettingsEvent event ) {
		if( event.getEventType() != SettingsEvent.CHANGED || !getKey().equals( event.getKey() ) ) return;

		Object value = event.getNewValue();
		String paint = value == null ? null : String.valueOf( value );
		paintPicker.setPaintAsString( paint );
	}

	private void doPickerValueChanged( ObservableValue<? extends String> property, String oldValue, String newValue ) {
		setting.getSettings().set( setting.getKey(), newValue );
	}

	private static class PaintEntryButtonCell extends ListCell<PaintMode> {

		@Override
		protected void updateItem( PaintMode item, boolean empty ) {
			super.updateItem( item, empty );
			if( item == null ) {
				setGraphic( null );
				setText( null );
			} else {
				System.err.println( "paint=" + item.getValue() );
				setText( item.getValue() );
			}
		}

	}

	private static class PaintEntryCellFactory implements Callback<ListView<PaintMode>, ListCell<PaintMode>> {

		@Override
		public ListCell<PaintMode> call( ListView<PaintMode> param ) {
			final ListCell<PaintMode> cell = new ListCell<>() {

				@Override
				public void updateItem( PaintMode item, boolean empty ) {
					super.updateItem( item, empty );

					if( item != null ) {
						if( "custom".equals( item.getKey() ) ) {
							setGraphic( new PaintPalette( item ) );
						} else {
							setGraphic( new Label( item.getLabel(), new Circle( 8, Paints.parse( item.getValue() ) ) ) );
						}
					} else {
						setGraphic( null );
						setText( null );
					}
				}

			};
			return cell;
		}

	}

	private static class PaintPalette extends VBox {

		private final PaintMode entry;

		private List<Color> bases;

		public PaintPalette( PaintMode entry ) {
			this.entry = entry;

			setSpacing( UiFactory.PAD );
			bases = List.of( Color.GRAY, Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE );

			for( double factor = 0.75; factor > 0.0; factor -= 0.25 ) {
				final double shadeFactor = factor;
				HBox shades = new HBox( UiFactory.PAD );
				shades.getChildren().addAll( bases.stream().map( base -> getButton( Colors.getShade( base, shadeFactor ) ) ).collect( Collectors.toList() ) );
				getChildren().add( shades );
			}

			HBox hue = new HBox( UiFactory.PAD );
			hue.getChildren().addAll( bases.stream().map( this::getButton ).collect( Collectors.toList() ) );
			getChildren().add( hue );

			for( double factor = 0.25; factor < 1.0; factor += 0.25 ) {
				final double tintFactor = factor;
				HBox tints = new HBox( UiFactory.PAD );
				tints.getChildren().addAll( bases.stream().map( base -> getButton( Colors.getTint( base, tintFactor ) ) ).collect( Collectors.toList() ) );
				getChildren().add( tints );
			}
		}

		private Button getButton( Paint paint ) {
			Button button = new Button( "", new Rectangle( 16, 16, paint ) );

			// This needs to be moved to xenon for the properties tool to pick it up
			button.getStyleClass().setAll( "paint-picker-swatch" );
			button.onActionProperty().set( e -> entry.setValue( Paints.toString( paint ) ) );

			return button;
		}

	}

}
