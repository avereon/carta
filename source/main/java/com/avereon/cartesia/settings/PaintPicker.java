package com.avereon.cartesia.settings;

import com.avereon.zerra.color.Paints;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
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

	private final PaintSwatch swatch;

	private final PaintPickerPane pickerPane;

	private final Popup popup;

	public PaintPicker() {
		getStyleClass().add( "paint-picker" );

		setGraphic( swatch = new PaintSwatch() );

		DialogPane pane = new DialogPane() {
			protected Node createButton( ButtonType buttonType) {
				return doCreateButton( buttonType );
			}
		};
		pane.setContent( pickerPane = new PaintPickerPane() );
		pane.getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );

		popup = new Popup();
		popup.setAutoHide( true );
		popup.setHideOnEscape( true );
		popup.getContent().add( pane );

		setOnAction( e -> doTogglePaintDialog() );
	}

	/**
	 * Convenience method to get the paint.
	 * @return the paint
	 */
	public Paint getPaint() {
		return calcPaint();
	}

	/**
	 * Convenience method to set the paint.
	 * @param paint the paint
	 */
	public void setPaint( Paint paint ) {
		setPaintAsString( Paints.toString( paint ) );
	}

	public String getPaintAsString() {
		return pickerPane.getPaint();
	}

	public StringProperty paintAsStringProperty() {
		return pickerPane.paintProperty();
	}

	public void setPaintAsString( String paint ) {
		pickerPane.setPaint( paint );
		swatch.setPaint( calcPaint() );
		setText( paint );
	}

	public ObservableList<PaintMode> getOptions() {
		return pickerPane.getOptions();
	}

	private Paint calcPaint() {
		// TODO Use the paint converters to convert from string to paint
		String paint = pickerPane.getPaint();
		return paint == null ? null : Paints.parse( paint );
	}

	private Node doCreateButton( ButtonType buttonType) {
		final Button button = new Button(buttonType.getText());
		final ButtonBar.ButtonData buttonData = buttonType.getButtonData();
		ButtonBar.setButtonData(button, buttonData);
		button.setDefaultButton(buttonData.isDefaultButton());
		button.setCancelButton(buttonData.isCancelButton());
		button.addEventHandler( ActionEvent.ACTION, e -> {
			if ( e.isConsumed()) return;
			setResultAndClose(buttonType);
		});

		return button;
	}

	private void doTogglePaintDialog() {
		if( !popup.isShowing() ) {
			Point2D anchor = localToScreen( new Point2D( 0, getHeight() ) );
			popup.show( this, anchor.getX(), anchor.getY() );
			pickerPane.requestFocus();
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
