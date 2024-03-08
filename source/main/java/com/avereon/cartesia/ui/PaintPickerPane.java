package com.avereon.cartesia.ui;

import com.avereon.xenon.UiFactory;
import com.avereon.zarra.color.PaintSwatch;
import com.avereon.zarra.color.Paints;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

import java.util.Map;

public class PaintPickerPane extends VBox {

	private final ComboBox<PaintMode> mode;

	private final TextField paintField;

	private final Map<PaintMode, PaintPaletteBox> paletteBoxes;

	private ObservableList<PaintMode> options;

	private StringProperty paint;

	private String prior;

	private PaintPaletteBox paletteBox;

	public PaintPickerPane() {
		getStyleClass().add( "cartesia-paint-picker-pane" );
		// How about a combo for the mode: none, solid, linear[] and radial()
		// To the right of the combo a component to define gradient stops
		// Below that, the tabs for palette, RGB, HSB and WEB
		// Opacity can be a slider on the right or the bottom
		// Below that the OK and Cancel buttons

		this.paletteBoxes = Map.of( PaintMode.PALETTE_BASIC, new PaintPaletteBox( new BasicPaintPalette() ), PaintMode.PALETTE_MATERIAL, new PaintPaletteBox( new MaterialPaintPalette() ) );

		// The paint mode chooser
		mode = new ComboBox<>();
		mode.setMaxWidth( Double.MAX_VALUE );
		mode.getItems().addAll( PaintMode.PALETTE_MATERIAL, PaintMode.PALETTE_BASIC, PaintMode.NONE );

		// The paint stop editor
		//RangeSlider paintStopEditor = new RangeSlider();

		// The color palette
		paletteBox = paletteBoxes.get( PaintMode.PALETTE_BASIC );

		// The color selection tabs
		// Apparently tab pane does not do well in a popup
		TabPane colorTabs = new TabPane();
		colorTabs.getTabs().add( new Tab( "Palette", new Label( "DONT JITTER" ) ) );

		// The paint text field for manual entry
		paintField = new TextField();

		// Add the children
		getChildren().addAll( mode, paletteBox, paintField );

		// Not sure that we should pick up the options from a setting
		//		getOptions().addListener( (ListChangeListener<PaintMode>)( e ) -> {
		//			mode.getItems().clear();
		//			mode.getItems().addAll( options );
		//		} );

		// The mode change handler
		mode.valueProperty().addListener( this::doModeChanged );

		// The text field change handler
		paintField.textProperty().addListener( ( p, o, n ) -> doSetPaint( n ) );
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
		// Do not call doSetPaint() here, changing the paintField will do that if needed
		paintField.setText( paint );
		updateMode( paint );
	}

	private void doSetPaint( String paint ) {
		paintProperty().set( paint );
	}

	private void doModeChanged( ObservableValue<? extends PaintMode> p, PaintMode o, PaintMode n ) {
		if( n == PaintMode.NONE ) {
			prior = getPaint();
			doSetPaint( null );
		} else if( prior != null ) {
			doSetPaint( prior );

			// If n is a palette mode, set the paint to the first color in the palette
			if( n.isPalette() ) {
				// Change the palette box to the new palette
				paletteBox = paletteBoxes.get( n );
				getChildren().set( 1, paletteBox );
			}
		}
	}

	private void updateMode( String paint ) {
		mode.getSelectionModel().select( PaintMode.getPaintMode( paint ) );
	}

	private class PaintPaletteBox extends VBox {

		public PaintPaletteBox( PaintPalette palette ) {
			super( UiFactory.PAD );
			for( int row = 0; row < palette.rowCount(); row++ ) {
				HBox rowBox = new HBox( UiFactory.PAD );
				rowBox.setAlignment( Pos.CENTER );
				for( int column = 0; column < palette.columnCount(); column++ ) {
					rowBox.getChildren().add( getSwatch( palette.getPaint( row, column ) ) );
				}
				getChildren().add( rowBox );
			}
		}

		private PaintSwatch getSwatch( Paint paint ) {
			PaintSwatch swatch = new PaintSwatch( paint );
			swatch.onMouseClickedProperty().set( e -> doSetPaint( Paints.toString( paint ) ) );
			return swatch;
		}

	}

}
