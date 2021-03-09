package com.avereon.cartesia.settings;

import com.avereon.xenon.UiFactory;
import com.avereon.zerra.color.Colors;
import com.avereon.zerra.color.Paints;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PaintPickerPane extends VBox {

	private static final String DEFAULT_PAINT_STRING = "";

	private final ComboBox<PaintMode> mode;

	private ObservableList<PaintMode> options;

	private StringProperty paint;

	private String prior;

	public PaintPickerPane() {
		getStyleClass().add( "paint-picker-pane" );
		// How about a combo for the mode: none, solid, linear[] and radial()
		// To the right of the combo a component to define gradient stops
		// Below that, the tabs for palette, RGB, HSB an WEB
		// Opacity can be a slider on the right or the bottom
		// Below that the OK and Cancel buttons

		// The paint mode chooser
		mode = new ComboBox<>();
		mode.setMaxWidth( Double.MAX_VALUE );

		// The paint stop editor
		//RangeSlider paintStopEditor = new RangeSlider();

		// The color selection tabs
		// Apparently tab pane does not do well in a popup
		TabPane colorTabs = new TabPane();
		colorTabs.getTabs().add( new Tab( "Palette", new Label( "DONT JITTER" ) ) );

		// Add the children
		getChildren().addAll( mode, new PaintPalette( new PaintMode( "", "", "#ff0000" ) ) );

		getOptions().addListener( (ListChangeListener<PaintMode>)( e ) -> {
			mode.getItems().clear();
			mode.getItems().addAll( options );
			updateMode();
		} );

		// Add the change handlers
		mode.valueProperty().addListener( this::doModeChanged );
	}

	private void doModeChanged( ObservableValue<? extends PaintMode> observable, PaintMode oldValue, PaintMode newValue ) {
		if( newValue == PaintMode.NONE ) {
			doSetPaint( null );
		} else {
			doSetPaint( prior );
		}
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
		doSetPaint( paint );
		this.prior = paint;
		updateMode();
	}

	private void doSetPaint( String paint ) {
		paintProperty().set( paint );
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

	private class PaintPalette extends VBox {

		private final PaintMode entry;

		private final List<Color> bases;

		public PaintPalette( PaintMode entry ) {
			this.entry = entry;

			setSpacing( UiFactory.PAD );
			bases = List.of( Color.GRAY, Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE );

			for( double factor = 0.75; factor > 0.0; factor -= 0.25 ) {
				final double shadeFactor = factor;
				HBox shades = new HBox( UiFactory.PAD );
				shades.getChildren().addAll( bases.stream().map( base -> getSwatch( Colors.getShade( base, shadeFactor ) ) ).collect( Collectors.toList() ) );
				getChildren().add( shades );
			}

			HBox hue = new HBox( UiFactory.PAD );
			hue.getChildren().addAll( bases.stream().map( this::getSwatch ).collect( Collectors.toList() ) );
			getChildren().add( hue );

			for( double factor = 0.25; factor < 1.0; factor += 0.25 ) {
				final double tintFactor = factor;
				HBox tints = new HBox( UiFactory.PAD );
				tints.getChildren().addAll( bases.stream().map( base -> getSwatch( Colors.getTint( base, tintFactor ) ) ).collect( Collectors.toList() ) );
				getChildren().add( tints );
			}
		}

		private Node getSwatch( Paint paint ) {
			PaintSwatch swatch = new PaintSwatch( paint );
			swatch.onMouseClickedProperty().set( e -> doSetPaint( Paints.toString( paint ) ) );
			return swatch;
		}

	}

}
