package com.avereon.cartesia;

import com.avereon.cartesia.data.Design;
import com.avereon.util.Log;
import com.avereon.util.TextUtil;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class CommandPrompt extends BorderPane {

	private static final System.Logger log = Log.get();

	private final DesignTool tool;

	private final Label prompt;

	private final TextField command;

	private static final boolean DEFAULT_AUTO_COMMAND = true;

	public CommandPrompt( DesignTool tool ) {
		this.tool = tool;
		getStyleClass().add( "cartesia-command" );
		setLeft( prompt = new Label() );
		setCenter( command = new TextField() );
		command.addEventHandler( KeyEvent.ANY, this::key );

		setPrompt( null );
	}

	public void setPrompt( String prompt ) {
		if( TextUtil.isEmpty( prompt ) ) prompt = tool.getProduct().rb().text( "prompt", "command" );
		final String effectivePrompt = prompt;
		Platform.runLater( () -> this.prompt.setText( effectivePrompt ) );
	}

	public void relay( Point3D point ) {
		getDesign().getCommandProcessor().evaluate( tool, point );
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
					getDesign().getCommandProcessor().cancel( tool );
					clear();
					break;
				}
				case ENTER: {
					if( TextUtil.isEmpty( command.getText() )) {
						getDesign().getCommandProcessor().evaluate( tool, tool.getMousePoint() );
					} else {
						process( command.getText() );
					}
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
		try {
			getDesign().getCommandProcessor().evaluate( tool, command );
		} catch( CommandException exception ) {
			log.log( Log.ERROR, exception );
		}
	}

	private void clear() {
		command.setText( "" );
	}

}
