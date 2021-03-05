package com.avereon.cartesia.settings;

import com.avereon.util.Log;
import com.avereon.zerra.color.Paints;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ComboBoxPopupControl;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.StringConverter;

/*
NEXT Take a look at DatePickerSkin to get some ideas.
 */
public class PaintPickerSkin extends ComboBoxPopupControl<String> {

	private static final System.Logger log = Log.get();

	private final Label displayNode;

	private final StackPane paintPreview;

	private Node popupContent;

	public PaintPickerSkin( PaintPickerCombo control ) {
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

		log.log( Log.INFO, "PaintPickerSkin.constructor()" );
	}

	@Override
	public void show() {
		log.log( Log.INFO, "PaintPickerSkin.show()" );
		super.show();
	}

	@Override
	protected Node getPopupContent() {
		if( popupContent == null ) {
			log.log( Log.INFO, "PaintPickerSkin.getPopupContent()" );
			VBox content = new VBox();
			content.getChildren().add( new Label( "POPUP CONTENT" ) );
			//popupContent = new ColorPalette(colorPicker.getValue(), colorPicker);
			//popupContent = new ColorPalette((ColorPicker)getSkinnable());
			//popupContent.setPopupControl(getPopup());
			popupContent = content;
		}
		return popupContent;
	}

	/*
	 * This is just like ColorPickerSkin
	 */
	@Override
	protected TextField getEditor() {
		return null;
	}

	/*
	 * This is just like ColorPickerSkin
	 */
	@Override
	protected StringConverter<String> getConverter() {
		return null;
	}

	@Override
	public Node getDisplayNode() {
		return displayNode;
	}

	public void showOrHide() {
		if (getControl().isShowing()){
			getControl().hide();
		}else {
			getControl().show();
		}
	}

	private PaintPickerCombo getControl() {
		return (PaintPickerCombo)getNode();
	}

//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	protected double computePrefWidth( double height, double topInset, double rightInset, double bottomInset, double leftInset ) {
//		double width = 0;
//		String displayNodeText = displayNode.getText();
//		//		for (String name : colorNameMap.values()) {
//		//			displayNode.setText(name);
//		//			width = Math.max(width, super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset));
//		//		}
//		displayNode.setText( Paints.toString( Color.BLACK ) );
//		width = Math.max( width, super.computePrefWidth( height, topInset, rightInset, bottomInset, leftInset ) );
//		displayNode.setText( displayNodeText );
//		return width;
//	}

	private void updatePaint() {
		final PaintPickerCombo paintPickerCombo = (PaintPickerCombo)getSkinnable();
		String paintCode = paintPickerCombo.getValue();

		if( "layer".equals( paintCode )) paintCode = Paints.toString( Color.MAGENTA );
		if( "none".equals( paintCode )) paintCode = Paints.toString( Color.RED );

		Paint paint = Paints.parse( paintCode );

		displayNode.setText( paintCode );
		paintPreview.setBackground( new Background( new BackgroundFill( paint, CornerRadii.EMPTY, Insets.EMPTY ) ) );
	}


//	private class PaintPickerBehavior extends com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior {
//
//		/**
//		 *
//		 * @param comboBox
//		 */
//		public PaintPickerBehavior( ComboBoxBase comboBox ) {
//			super( comboBox );
//		}
//	}

}
