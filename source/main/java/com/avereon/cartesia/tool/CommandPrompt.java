package com.avereon.cartesia.tool;

import com.avereon.util.Log;
import com.avereon.util.TextUtil;
import com.avereon.xenon.ProgramProduct;
import com.avereon.zerra.javafx.Fx;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class CommandPrompt extends BorderPane implements EventHandler<KeyEvent> {

	private static final System.Logger log = Log.get();

	private final ProgramProduct product;

	private final Label prompt;

	private final TextField command;

	public CommandPrompt( ProgramProduct product, CommandContext context ) {
		this.product = product;

		getStyleClass().add( "cartesia-command" );
		setLeft( prompt = new Label() );
		setCenter( command = new TextField() );
		setPrompt( null );

		// FIXME I have two slightly competing handlers here
		// one listening to just keys
		// the other listening to the text
		command.addEventHandler( KeyEvent.ANY, context::handle );
		command.textProperty().addListener( ( p, o, n ) -> context.text( n ) );
	}

	private ProgramProduct getProduct() {
		return product;
	}

	public void setPrompt( String prompt ) {
		final String effectivePrompt = !TextUtil.isEmpty( prompt ) ? prompt : getProduct().rb().text( "prompt", "command" );
		Platform.runLater( () -> this.prompt.setText( effectivePrompt ) );
	}

	public String getText() {
		return command.getText().trim();
	}

	@Override
	public void handle( KeyEvent event ) {
		command.fireEvent( event );
	}

	public void clear() {
		Fx.run( () -> command.setText( TextUtil.EMPTY ) );
		setPrompt( TextUtil.EMPTY );
	}

}
