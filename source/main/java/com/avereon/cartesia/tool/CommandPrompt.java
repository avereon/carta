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
	}

	private void handleKeyEvent( KeyEvent event ) {
		// FIXME This breaks the normal action handling
		// Because this captures all key events and sends them to the command prompt
		// text field which consumes some important events like delete, undo and
		// redo. It, however, does not capture escape or enter which has been the
		// cause of some confusion.

		// This method is part of a delicate balance between an event handler on the
		// workpane, this method and the command text field.

		//		try {
		//			command.fireEvent( event );
		//
		//			// Consume the original event after firing the event to the command
		//			event.consume();
		//		} catch( UnknownCommand exception ) {
		//			log.atWarn().withCause( exception );
		//		}
	}

	private void handleTextChange( ObservableValue<? extends String> property, String oldValue, String newValue ) {
		try {
			// Process text calls doCommand
			context.processText( newValue, false );
		} catch( UnknownCommand exception ) {
			log.atError().withCause( exception );
		}
	}

}
