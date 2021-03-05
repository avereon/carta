package com.avereon.cartesia.settings;

import com.avereon.product.Product;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.paint.Paint;
import javafx.stage.Popup;

public class PaintPicker extends Button {

	private final PaintPickerPane paintPicker;

	private final Popup popup;

	// TODO Can this handle custom options like 'layer'?
	private ObjectProperty<Paint> paint;

	public PaintPicker( Product product ) {
		paintPicker = new PaintPickerPane( product );

		DialogPane pane = new DialogPane() {
			protected Node createButton( ButtonType buttonType) {
				return doCreateButton( buttonType );
			}
		};
		pane.setContent( paintPicker );
		pane.getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );

		popup = new Popup();
		popup.setAutoHide( true );
		popup.setHideOnEscape( true );
		popup.getContent().add( pane );

		setOnAction( e -> doTogglePaintDialog() );
	}

	public Paint getPaint() {
		return paint == null ? null : paint.get();
	}

	public ObjectProperty<Paint> paintProperty() {
		if( paint == null ) paint = new SimpleObjectProperty<>();
		return paint;
	}

	public void setPaint( Paint paint ) {
		paintProperty().set( paint );
	}

	private Node doCreateButton( ButtonType buttonType) {
		final Button button = new Button(buttonType.getText());
		final ButtonBar.ButtonData buttonData = buttonType.getButtonData();
		ButtonBar.setButtonData(button, buttonData);
		button.setDefaultButton(buttonData.isDefaultButton());
		button.setCancelButton(buttonData.isCancelButton());
		button.addEventHandler( ActionEvent.ACTION, ae -> {
			if (ae.isConsumed()) return;
			setResultAndClose(buttonType);
		});

		return button;
	}

	private void doTogglePaintDialog() {
		if( !popup.isShowing() ) {
			Point2D anchor = localToScreen( new Point2D( 0, getHeight() ) );
			popup.show( this, anchor.getX(), anchor.getY() );
			paintPicker.requestFocus();
		} else {
			popup.hide();
		}
	}

	private void setResultAndClose(ButtonType type ) {
		// Get value from PaintPickerPane
		if( type == ButtonType.APPLY ) setPaint( null );
		popup.hide();
	}

}
