package com.avereon.cartesia.tool;

import com.avereon.cartesia.error.UnknownCommand;
import com.avereon.product.Rb;
import com.avereon.util.TextUtil;
import com.avereon.zarra.javafx.Fx;
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
		final BaseDesignTool tool = context.getTool();
		Fx.run( () -> {
			if( tool != null ) tool.showCommandPrompt();
			this.prompt.setText( effectivePrompt );
		} );
	}

	public String getPrompt() {
		return prompt.getText().trim();
	}

	public String getCommand() {
		return command.getText().trim();
	}

	@Override
	public void requestFocus() {
		// Intentionally do nothing, the design tool should request focus instead
	}

	/**
	 * Clear the command prompt and text
	 */
	public void clear() {
		Fx.run( () -> command.setText( TextUtil.EMPTY ) );
		setPrompt( TextUtil.EMPTY );
	}

	/**
	 * This method is for captured key events to be propagated to the command line
	 *
	 * @param event The event to propagate
	 */
	void fireEvent( KeyEvent event ) {
		command.fireEvent( event );
	}

	/**
	 * Capture special keys, except for the SPACE key. Also consume the events
	 * so thy are not propagated to the key capture logic in {@link BaseDesignTool}.
	 *
	 * @param event The key event
	 */
	private void handleKeyEvent( KeyEvent event ) {
		// Capture special keys, except do not capture the SPACE key here
		if( command.isFocused() && event.getEventType() == KeyEvent.KEY_PRESSED ) {
			switch( event.getCode() ) {
				case ESCAPE -> context.cancel( event );
				case ENTER -> context.enter( event );
			}
		}

		// Do not allow the event to bubble back up to the scene
		event.consume();
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
