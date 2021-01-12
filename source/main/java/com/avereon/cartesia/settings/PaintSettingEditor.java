package com.avereon.cartesia.settings;

import com.avereon.settings.SettingsEvent;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.UiFactory;
import com.avereon.xenon.tool.settings.Setting;
import com.avereon.xenon.tool.settings.SettingEditor;
import com.avereon.zerra.color.Colors;
import com.avereon.zerra.color.Paints;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

import java.util.List;
import java.util.stream.Collectors;

public class PaintSettingEditor extends SettingEditor implements EventHandler<ActionEvent> {

	private static final Paint DEFAULT_PAINT = Color.BLACK;

	private final Label label;

	private final ComboBox<PaintEntry> paintPicker;

	private List<Node> nodes;

	public PaintSettingEditor( ProgramProduct product, String bundleKey, Setting setting ) {
		super( product, bundleKey, setting );
		label = new Label();
		paintPicker = new ComboBox<>();
	}

	@Override
	public void addComponents( GridPane pane, int row ) {
		String rbKey = setting.getBundleKey();
		String value = setting.getSettings().get( key, Paints.toString( DEFAULT_PAINT ) );

		label.setText( product.rb().text( getBundleKey(), rbKey ) );
		label.setMinWidth( Region.USE_PREF_SIZE );

		paintPicker.setId( rbKey );
		paintPicker.setMaxWidth( Double.MAX_VALUE );

		PaintEntry custom = new PaintEntry( "custom", "Custom", "#ff0000ff" );
		PaintEntry layer = new PaintEntry( "layer", "Layer", "#00ff00ff" );
		PaintEntry none = new PaintEntry( "none", "None", "#0000ffff" );

		paintPicker.getItems().setAll( none, layer, custom );
		paintPicker.setCellFactory( new PaintEntryCellFactory() );
		paintPicker.setButtonCell( new PaintEntryButtonCell() );
		paintPicker.setValue( layer );

		nodes = List.of( label, paintPicker );

		// Add the event handlers
		paintPicker.setOnAction( this );

		// Set component state
		setDisable( setting.isDisable() );
		setVisible( setting.isVisible() );

		// Add the components
		pane.addRow( row, label, paintPicker );
	}

	@Override
	public List<Node> getComponents() {
		return nodes;
	}

	@Override
	public void handle( ActionEvent event ) {
		//setting.getSettings().set( setting.getKey(), Paints.parse( paintPicker.getValue() ) );
	}

	@Override
	public void handle( SettingsEvent event ) {
		Object value = event.getNewValue();
		Paint paint;
		try {
			paint = Paints.parse( String.valueOf( value ) );
		} catch( Exception exception ) {
			paint = DEFAULT_PAINT;
		}
		//if( event.getEventType() == SettingsEvent.CHANGED && key.equals( event.getKey() ) ) paintPicker.setValue( Paints.toString( paint ) );
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
