package com.avereon.cartesia.tool;

import com.avereon.cartesia.error.UnknownCommand;
import com.avereon.product.Rb;
import com.avereon.util.TextUtil;
import com.avereon.zerra.javafx.Fx;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import lombok.CustomLog;

@CustomLog
public class CommandPrompt extends BorderPane implements EventHandler<KeyEvent> {

	private final CommandContext context;

	private final Label prompt;

	private final TextField command;

	public CommandPrompt( CommandContext context ) {
		this.context = context;

		getStyleClass().add( "cartesia-command" );
		setLeft( prompt = new Label() );
		setCenter( command = new TextField() );
		setPrompt( TextUtil.EMPTY );

		// This listener handles processing the text in the command prompt
		command.textProperty().addListener( ( p, o, n ) -> this.textChanged( n ) );

		// This listener handles key pressed events for some special cases
		command.addEventHandler( KeyEvent.KEY_PRESSED, context::doProcessKeyPress );
	}

	public void setPrompt( String prompt ) {
		final String effectivePrompt = !TextUtil.isEmpty( prompt ) ? prompt : Rb.text( "prompt", "command" );
		final DesignTool tool = context.getTool();
		if( tool != null ) Fx.run( tool::showCommandPrompt );
		Fx.run( () -> this.prompt.setText( effectivePrompt ) );
	}

	public String getText() {
		return command.getText().trim();
	}

	@Override
	public void handle( KeyEvent event ) {
		// This method is part of a delicate balance between an event handler on the
		// workpane, this method and the command text field.
		try {
			command.fireEvent( event );

			// Consume the original event after firing the event to the command
			event.consume();
		} catch( UnknownCommand exception ) {
			log.atWarn().withCause( exception );
		}
	}

	@Override
	public void requestFocus() {
		// Intentionally do nothing
		// The design tool should request focus instead
	}

	public void clear() {
		Fx.run( () -> command.setText( TextUtil.EMPTY ) );
		setPrompt( TextUtil.EMPTY );
	}

	private void textChanged( String text ) {
		try {
			context.processText( text );
		} catch( UnknownCommand exception ) {
			log.atError().withCause( exception );
		}
	}

}
