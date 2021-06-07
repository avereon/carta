package com.avereon.cartesia.tool;

import com.avereon.cartesia.error.UnknownCommand;
import com.avereon.product.Rb;
import com.avereon.util.Log;
import com.avereon.util.TextUtil;
import com.avereon.xenon.ProgramProduct;
import com.avereon.zerra.javafx.Fx;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class CommandPrompt extends BorderPane implements EventHandler<KeyEvent> {

	private static final System.Logger log = Log.get();

	private final ProgramProduct product;

	private final CommandContext context;

	private final Label prompt;

	private final TextField command;

	public CommandPrompt( ProgramProduct product, CommandContext context ) {
		this.product = product;
		this.context = context;

		getStyleClass().add( "cartesia-command" );
		setLeft( prompt = new Label() );
		setCenter( command = new TextField() );
		setPrompt( TextUtil.EMPTY );

		// FIXME I have two slightly competing handlers here
		// one listening to just keys
		// the other listening to the text
		command.addEventHandler( KeyEvent.ANY, context::handle );
		command.textProperty().addListener( ( p, o, n ) -> this.textChanged( n ) );
	}

	public void setPrompt( String prompt ) {
		final String effectivePrompt = !TextUtil.isEmpty( prompt ) ? prompt : Rb.text( "prompt", "command" );
		Fx.run( () -> this.prompt.setText( effectivePrompt ) );
		if( context.getTool() != null ) Fx.run( () -> context.getTool().showCommandPrompt() );
	}

	public String getText() {
		return command.getText().trim();
	}

	private void textChanged( String text ) {
		try {
			context.text( text );
		} catch( UnknownCommand exception ) {
			log.log( Log.WARN, exception );
		}
	}

	@Override
	public void handle( KeyEvent event ) {
		try {
			command.fireEvent( event );
		} catch( UnknownCommand exception ) {
			log.log( Log.WARN, exception );
		}
	}

	public void clear() {
		Fx.run( () -> command.setText( TextUtil.EMPTY ) );
		setPrompt( TextUtil.EMPTY );
	}

	public void requestFocus() {
		Fx.run( command::requestFocus );
	}

}
