package com.avereon.cartesia.settings;

import com.avereon.settings.SettingsEvent;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.UiFactory;
import com.avereon.xenon.tool.settings.SettingData;
import com.avereon.xenon.tool.settings.SettingEditor;
import com.avereon.zerra.color.Colors;
import com.avereon.zerra.color.Paints;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.util.Callback;

import java.util.List;
import java.util.stream.Collectors;

public class PaintSettingEditor extends SettingEditor {

	private static final Paint DEFAULT_PAINT = Color.BLACK;

	private final Label label;

	private final Button button;

	private final ComboBox<String> comboBox;

	private final ColorPicker colorPicker;

	//private final PaintPicker paintPicker;

	private List<Node> nodes;

	public PaintSettingEditor( ProgramProduct product, String bundleKey, SettingData setting ) {
		super( product, bundleKey, setting );
		label = new Label();
		button = new Button();
		comboBox = new ComboBox<>();
		colorPicker = new ColorPicker();
		//paintPicker = new PaintPicker();
	}

	@Override
	public void addComponents( GridPane pane, int row ) {
		String rbKey = setting.getBundleKey();
		String value = setting.getSettings().get( getKey(), Paints.toString( DEFAULT_PAINT ) );

		label.setText( product.rb().text( getBundleKey(), rbKey ) );
		label.setMinWidth( Region.USE_PREF_SIZE );

		button.setText( value );
		button.setMaxWidth( Double.MAX_VALUE );
		HBox.setHgrow( button, Priority.ALWAYS );

		comboBox.getItems().addAll( "None", "Solid", "Linear", "Radial" );
		comboBox.setMaxWidth( Double.MAX_VALUE );
		HBox.setHgrow( comboBox, Priority.SOMETIMES );

		colorPicker.setId( rbKey );
		colorPicker.setMaxWidth( Double.MAX_VALUE );
		HBox.setHgrow( colorPicker, Priority.ALWAYS );

		PaintEntry custom = new PaintEntry( "custom", "Custom", "#ff0000ff" );
		PaintEntry layer = new PaintEntry( "layer", "Layer", "#00ff00ff" );
		PaintEntry none = new PaintEntry( "none", "None", "#0000ffff" );

		//paintPicker.setId( rbKey );
		//paintPicker.setMaxWidth( Double.MAX_VALUE );
		//paintPicker.getItems().setAll( none, layer, custom );
		//paintPicker.setCellFactory( new PaintEntryCellFactory() );
		//paintPicker.setButtonCell( new PaintEntryButtonCell() );
		//paintPicker.setValue( layer );

		nodes = List.of( label, button );
//		nodes = List.of( label, comboBox, colorPicker );

		// Add the event handlers
		colorPicker.setOnAction( this::doPickerValueChanged );

		// Set component state
		setDisable( setting.isDisable() );
		setVisible( setting.isVisible() );

		HBox box = new HBox( button );
//		HBox box = new HBox( comboBox, colorPicker );
		GridPane.setHgrow( box, Priority.ALWAYS );

		// Add the components
		pane.addRow( row, label, box );

		button.setOnAction( e -> doShowPaintDialog() );
	}

	@Override
	public List<Node> getComponents() {
		return nodes;
	}

	@Override
	protected void doSettingValueChanged( SettingsEvent event ) {
		Object value = event.getNewValue();
		Paint paint;
		try {
			paint = Paints.parse( String.valueOf( value ) );
		} catch( Exception exception ) {
			paint = DEFAULT_PAINT;
		}
		if( event.getEventType() == SettingsEvent.CHANGED && getKey().equals( event.getKey() ) ) button.setText( Paints.toString( paint ) );
	}

	private void doShowPaintDialog() {
//		Dialog<String> dialog = new Dialog<>();
//		dialog.initOwner( button.getScene().getWindow() );
//		dialog.getDialogPane().setContent( new PaintPickerPane( getProduct() ) );
//		dialog.getDialogPane().getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );
//		dialog.show();

		DialogPane pane = new DialogPane();
		pane.setContent( new PaintPickerPane( getProduct() ) );
		pane.getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );

		Point2D anchor = button.localToScreen( new Point2D( 0, button.getHeight() ) );

		// ...or maybe a popup
		Popup popup = new Popup();
		popup.ownerNodeProperty();
		popup.getContent().add( pane );
		popup.show( button, anchor.getX(), anchor.getY() );
	}

	private void doPickerValueChanged( ActionEvent event ) {
		//setting.getSettings().set( setting.getKey(), Paints.parse( paintPicker.getValue() ) );
	}

	private static class PaintEntry {

		private final String key;

		private final String label;

		private String paint;

		public PaintEntry( String key, String label, String paint ) {
			this.key = key;
			this.label = label;
			this.paint = paint;
		}

		public String getKey() {
			return key;
		}

		public String getLabel() {
			return label;
		}

		public String getPaint() {
			return paint;
		}

		public void setPaint( String paint ) {
			this.paint = paint;
		}

	}

	private static class PaintEntryButtonCell extends ListCell<PaintEntry> {

		@Override
		protected void updateItem( PaintEntry item, boolean empty ) {
			super.updateItem( item, empty );
			if( item == null ) {
				setGraphic( null );
				setText( null );
			} else {
				System.err.println( "paint=" + item.getPaint() );
				setText( item.getPaint() );
			}
		}

	}

	private static class PaintEntryCellFactory implements Callback<ListView<PaintEntry>, ListCell<PaintEntry>> {

		@Override
		public ListCell<PaintEntry> call( ListView<PaintEntry> param ) {
			final ListCell<PaintEntry> cell = new ListCell<>() {

				@Override
				public void updateItem( PaintEntry item, boolean empty ) {
					super.updateItem( item, empty );

					if( item != null ) {
						if( "custom".equals( item.getKey() ) ) {
							setGraphic( new PaintPalette( item ) );
						} else {
							setGraphic( new Label( item.getLabel(), new Circle( 8, Paints.parse( item.getPaint() ) ) ) );
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

		private final PaintEntry entry;

		private List<Color> bases;

		public PaintPalette( PaintEntry entry ) {
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
			button.onActionProperty().set( e -> entry.setPaint( Paints.toString( paint ) ) );

			return button;
		}

	}

}
