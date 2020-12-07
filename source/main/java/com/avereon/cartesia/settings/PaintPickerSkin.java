package com.avereon.cartesia.settings;

import com.avereon.zerra.color.Paints;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ComboBoxPopupControl;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.StringConverter;

public class PaintPickerSkin extends ComboBoxPopupControl<Paint> {

	private final Label displayNode;

	private final StackPane paintPreview;

	private Node popupContent;

	public PaintPickerSkin( PaintPicker control ) {
		super( control );

		paintPreview = new StackPane();

		displayNode = new Label();
		displayNode.setManaged( false );
		displayNode.setGraphic( paintPreview );
		displayNode.getStyleClass().add( "color-picker-label" );

		paintPreview.prefWidthProperty().bind( displayNode.heightProperty().multiply( 0.6 ) );
		paintPreview.prefHeightProperty().bind( displayNode.heightProperty().multiply( 0.6 ) );
		paintPreview.setBackground( new Background( new BackgroundFill( Color.RED, CornerRadii.EMPTY, Insets.EMPTY ) ) );

		registerChangeListener( control.valueProperty(), e -> updatePaint() );
		updatePaint();

		if( control.isShowing() ) show();
	}

	@Override
	protected Node getPopupContent() {
		if( popupContent == null ) {
			popupContent = new Label( "POPUP CONTENT" );
			//            popupContent = new ColorPalette(colorPicker.getValue(), colorPicker);
			//popupContent = new ColorPalette((ColorPicker)getSkinnable());
			//popupContent.setPopupControl(getPopup());
		}
		return popupContent;
	}

	/**
	 * PaintPicker does not use a text field for editing.
	 */
	@Override
	protected TextField getEditor() {
		return null;
	}

	@Override
	protected StringConverter<Paint> getConverter() {
		return null;
	}

	@Override
	public Node getDisplayNode() {
		return displayNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double computePrefWidth( double height, double topInset, double rightInset, double bottomInset, double leftInset ) {
		double width = 0;
		String displayNodeText = displayNode.getText();
		//		for (String name : colorNameMap.values()) {
		//			displayNode.setText(name);
		//			width = Math.max(width, super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset));
		//		}
		displayNode.setText( Paints.toString( Color.BLACK ) );
		width = Math.max( width, super.computePrefWidth( height, topInset, rightInset, bottomInset, leftInset ) );
		displayNode.setText( displayNodeText );
		return width;
	}

	private void updatePaint() {
		final PaintPicker paintPicker = (PaintPicker)getSkinnable();
		Paint paint = paintPicker.getValue();
		paintPreview.setBackground( new Background( new BackgroundFill( paint, CornerRadii.EMPTY, Insets.EMPTY ) ) );
		displayNode.setText( Paints.toString( paint ) );
	}

}
