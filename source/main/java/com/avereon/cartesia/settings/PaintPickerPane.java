package com.avereon.cartesia.settings;

import com.avereon.cartesia.BundleKey;
import com.avereon.product.Product;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class PaintPickerPane extends BorderPane {

	private static final String DEFAULT_PAINT_STRING = "";

	private final ComboBox<String> mode;

	private ObservableList<String> customOptions;

	private ObservableList<String> standardOptions;

	public PaintPickerPane( Product product ) {
		// Custom options
		//String none = product.rb().textOr( BundleKey.LABEL, "none", "None" );

		// Standard options
		String solid = product.rb().textOr( BundleKey.LABEL, "solid", "Solid Color" );
		String linear = product.rb().textOr( BundleKey.LABEL, "linear-gradient", "Linear Gradient" );
		String radial = product.rb().textOr( BundleKey.LABEL, "radial-gradient", "Radial Gradient" );

		// How about a combo for the mode: none, solid, linear[] and radial()
		// To the right of the combo a component to define gradient stops
		// Below that, the tabs for palette, RGB, HSB an WEB
		// Opacity can be a slider on the right or the bottom
		// Below that the OK and Cancel buttons

		PaintEntry custom = new PaintEntry( "custom", "Custom", "#ff0000ff" );
		PaintEntry layer = new PaintEntry( "layer", "Layer", "#00ff00ff" );
		PaintEntry none = new PaintEntry( "none", "None", "#0000ffff" );

		mode = new ComboBox<>();
		mode.getItems().addAll( solid, linear, radial );

		setTop( new HBox( mode ) );

		getCustomOptions().addListener( (ListChangeListener<String>)( e ) -> {
			//
		} );
	}

	@Override
	public void requestFocus() {
		mode.requestFocus();
	}

	public ObservableList<String> getCustomOptions() {
		if( customOptions == null ) customOptions = new SimpleListProperty<>();
		return customOptions;
	}

}
