package com.avereon.cartesia.ui;

import com.avereon.zarra.font.FontUtil;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import lombok.CustomLog;

@CustomLog
public class FontPicker extends Button {

	private final FontPickerPane pickerPane;

	private final Popup popup;

	private String prior;

	public FontPicker() {
		getStyleClass().add( "cartesia-font-picker" );

		DialogPane pane = new DialogPane() {

			protected Node createButton( ButtonType buttonType ) {
				return doCreateButton( buttonType );
			}
		};
		pane.setContent( pickerPane = new FontPickerPane() );
		pane.getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );
		pane.minWidthProperty().bind( this.widthProperty() );

		popup = new Popup();
		popup.setAutoHide( true );
		popup.setHideOnEscape( true );
		popup.getContent().add( pane );

		setOnAction( e -> doToggleFontDialog() );

		doUpdateText( null );
		pickerPane.fontProperty().addListener( ( p, o, n ) -> {
			doUpdateText( n );
		} );
	}

	public String getFontAsString() {
		return pickerPane.getFont();
	}

	public StringProperty fontAsStringProperty() {
		return pickerPane.fontProperty();
	}

	public void setFontAsString( String font ) {
		pickerPane.setFont( font );
	}

	public void setPrior( String paint ) {
		this.prior = paint;
	}

	private Node doCreateButton( ButtonType buttonType ) {
		final Button button = new Button( buttonType.getText() );
		final ButtonBar.ButtonData buttonData = buttonType.getButtonData();
		ButtonBar.setButtonData( button, buttonData );
		button.setDefaultButton( buttonData.isDefaultButton() );
		button.setCancelButton( buttonData.isCancelButton() );
		button.addEventHandler( ActionEvent.ACTION, e -> {
			if( e.isConsumed() ) return;
			setResultAndClose( buttonType );
		} );

		return button;
	}

	private void doUpdateText( String text ) {
		Font font = FontUtil.decode( text );
		setText( font == null ? null : font.getFamily() );
		setFont( font == null ? null : Font.font( font.getFamily(), FontUtil.getFontWeight( font.getStyle() ), FontUtil.getFontPosture( font.getStyle() ), -1 ) );
	}

	private void doToggleFontDialog() {
		if( !popup.isShowing() ) {
			log.atConfig().log( "Show font picker pane..." );
			setPrior( getFontAsString() );
			Point2D anchor = localToScreen( new Point2D( 0, getHeight() ) );
			popup.show( this, anchor.getX(), anchor.getY() );
			pickerPane.requestFocus();
		} else {
			popup.hide();
		}
	}

	private void setResultAndClose( ButtonType type ) {
		if( type == ButtonType.CANCEL ) setFontAsString( prior );
		popup.hide();
	}

}
