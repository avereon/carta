package com.avereon.cartesia;

import com.avereon.util.Log;
import com.avereon.xenon.ProgramProduct;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class CommandPrompt extends BorderPane {

	private static final System.Logger log = Log.get();

	private final TextField command;

	public CommandPrompt( ProgramProduct product ) {
		getStyleClass().add( "cartesia-command");
		setLeft( new Label( product.rb().text( "prompt", "command" ) ) );
		setCenter( command = new TextField() );
	}

	public void clear() {
		// Just clear the current text
		command.setText( "" );
	}

	public void update( DesignTool tool, KeyEvent event ) {
		command.fireEvent( event );

		if( event.getEventType() == KeyEvent.KEY_PRESSED ) {
			switch( event.getCode() ) {
				case ESCAPE: {
					// Cancel the command stack
					clear();
					break;
				}
				case ENTER: {
					process( tool, command.getText() );
					clear();
					break;
				}
			}
		} else if( event.getEventType() == KeyEvent.KEY_TYPED ) {
			Class<Command<?>> commandClass = CommandMap.get( command.getText() );
			if( commandClass != null ) {
				log.log( Log.WARN, "Auto command=" + commandClass );
				clear();
			}
		}
	}

	private void process( DesignTool tool, String command ) {
		Design design = tool.getAssetModel();
		// TODO This needs to get the command stack/processor for the asset and push the command
		//design.getCommandProcessor().evaluate( command );
	}

}
