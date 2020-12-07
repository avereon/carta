package com.avereon.cartesia.settings;

import com.avereon.settings.SettingsEvent;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.settings.Setting;
import com.avereon.xenon.tool.settings.SettingEditor;
import com.avereon.zerra.color.Colors;
import com.avereon.zerra.color.Paints;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
		paintPicker.setValue( new PaintEntry( "layer", "Layer", "#000000ff" ) );
		paintPicker.setMaxWidth( Double.MAX_VALUE );

		PaintEntry layer = new PaintEntry( "layer", "Layer", "#0000ffff" );
		PaintEntry custom = new PaintEntry( "custom", "Custom", "#ff0000ff" );

		paintPicker.getItems().setAll( layer, custom );
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
				setText( item.getPaint() );
			}
		}

	}

	private static class PaintEntryCellFactory implements Callback<ListView<PaintEntry>, ListCell<PaintEntry>> {

		@Override
		public ListCell<PaintEntry> call( ListView<PaintEntry> param ) {
			final ListCell<PaintEntry> cell = new ListCell<>() {

				{
					super.setPrefWidth( 100 );
				}

				@Override
				public void updateItem( PaintEntry item, boolean empty ) {
					super.updateItem( item, empty );

					if( item != null ) {
						if( "custom".equals( item.getKey() ) ) {
							setGraphic( new PaintPallette( item ) );
						} else {
							setGraphic( new Label( item.getLabel(), new Circle( 10, Paints.parse( item.getPaint() ) ) ) );
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

	private static class PaintPallette extends VBox {

		private PaintEntry entry;

		private List<Color> bases;

		public PaintPallette( PaintEntry entry ) {
			this.entry = entry;

			bases = List.of( Color.BLACK, Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE, Color.WHITE );

			for( double factor = 0.75; factor > 0.0; factor -= 0.25 ) {
				HBox shades = new HBox();
				for( Color base : bases ) {
					Color color = Colors.getShade( base, factor );
					shades.getChildren().add( new Rectangle(16,16,color) );
				}
				getChildren().add( shades );
			}

			HBox hue = new HBox();
			for( Color base : bases ) {
				hue.getChildren().add( new Rectangle(16,16,base) );
			}
			getChildren().add( hue );

			for( double factor = 0.25; factor < 1.0; factor += 0.25 ) {
				HBox tints = new HBox();
				for( Color base : bases ) {
					Color color = Colors.getTint( base, factor );
					tints.getChildren().add( new Rectangle(16,16,color) );
				}
				getChildren().add( tints );
			}

		}

		// TODO When the paint is selected set the paint in the paint entry

	}

}
