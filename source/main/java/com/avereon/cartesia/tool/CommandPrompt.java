package com.avereon.cartesia.tool;

import com.avereon.cartesia.error.UnknownCommand;
import com.avereon.product.Rb;
import com.avereon.util.TextUtil;
import com.avereon.zerra.javafx.Fx;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import lombok.CustomLog;

@CustomLog
public class CommandPrompt extends BorderPane {

	private final CommandContext context;

	private final Label prompt;

	private final TextField command;

	public CommandPrompt( CommandContext context ) {
		this.context = context;

		getStyleClass().add( "cartesia-command" );
		setLeft( prompt = new Label() );
		setCenter( command = new TextField() );
		setPrompt( TextUtil.EMPTY );

		// This listener handles key events for special cases
		command.addEventHandler( KeyEvent.ANY, this::handleKeyEvent );

		// This listener handles processing the text in the command prompt
		command.textProperty().addListener( this::handleTextChange );
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
	public void requestFocus() {
		// Intentionally do nothing, the design tool should request focus instead
	}

	public void clear() {
		Fx.run( () -> command.setText( TextUtil.EMPTY ) );
		setPrompt( TextUtil.EMPTY );
	}

	void fireEvent( KeyEvent event ) {
		command.fireEvent( event );
		event.consume();
	}

	private void handleKeyEvent( KeyEvent event ) {
		// NOTE This method was originally implemented to capture the SPACE key event
		// This is no longer desired behavior on the command line and therefore this code has been removed
	}

	private void handleTextChange( ObservableValue<? extends String> property, String oldValue, String newValue ) {
		// Forward the new text value to the command context which will determine what to do
		try {
			context.processText( newValue, false );
		} catch( UnknownCommand exception ) {
			log.atError().withCause( exception );
		}
	}

}
