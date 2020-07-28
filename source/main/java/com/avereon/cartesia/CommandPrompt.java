package com.avereon.cartesia;

import com.avereon.util.Log;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class CommandPrompt extends BorderPane {

	private static final System.Logger log = Log.get();

	private final DesignTool tool;

	private final TextField command;

	private static final boolean DEFAULT_AUTO_COMMAND = true;

	public CommandPrompt( DesignTool tool ) {
		this.tool = tool;
		getStyleClass().add( "cartesia-command" );
		setLeft( new Label( tool.getProduct().rb().text( "prompt", "command" ) ) );
		setCenter( command = new TextField() );
		command.addEventHandler( KeyEvent.ANY, this::key );
	}

	public void relay( KeyEvent event ) {
		command.fireEvent( event );
	}

	private Design getDesign() {
		return tool.getAssetModel();
	}

	private void key( KeyEvent event ) {
		// On each key event the situation needs to be evaluated...
		// If ESC was pressed, then the whole command stack should be cancelled
		// If ENTER was pressed, then an attempt to process the text should be forced
		// If a key was typed, and auto commands are enabled, and the text matched a command then it should be run

		if( event.getEventType() == KeyEvent.KEY_PRESSED ) {
			switch( event.getCode() ) {
				case ESCAPE: {
					// Cancel the command stack
					getDesign().getCommandProcessor().cancel();
					clear();
					break;
				}
				case ENTER: {
					process( command.getText() );
					clear();
					break;
				}
			}
		} else if( event.getEventType() == KeyEvent.KEY_TYPED ) {
			String id = command.getText();
			boolean autoCommand = tool.getProduct().getSettings().get( "command-auto-start", Boolean.class, DEFAULT_AUTO_COMMAND );
			if( autoCommand && CommandMap.hasCommand( id ) ) {
				process( id );
				clear();
			}
		}
	}

	private void process( String command ) {
		getDesign().getCommandProcessor().evaluate( command );
	}

	private void clear() {
		command.setText( "" );
	}

}
