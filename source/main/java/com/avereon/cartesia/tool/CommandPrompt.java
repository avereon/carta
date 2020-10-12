package com.avereon.cartesia.tool;

import com.avereon.cartesia.CommandException;
import com.avereon.cartesia.CommandMap;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.data.Design;
import com.avereon.settings.Settings;
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

	private static final boolean DEFAULT_AUTO_COMMAND = true;

	private static final String SPACE = " ";

	private final DesignTool tool;

	private final Label prompt;

	private final TextField command;

	private boolean autoCommandEnabled;

	public CommandPrompt( DesignTool tool ) {
		this.tool = tool;
		getStyleClass().add( "cartesia-command" );
		setLeft( prompt = new Label() );
		setCenter( command = new TextField() );

		Settings productSettings = tool.getProduct().getSettings();
		autoCommandEnabled = productSettings.get( "command-auto-start", Boolean.class, DEFAULT_AUTO_COMMAND );
		productSettings.register( "command-auto-start", e -> setAutoCommandEnabled( Boolean.parseBoolean( String.valueOf( e.getNewValue() ) ) ) );

		command.addEventHandler( KeyEvent.ANY, this::key );

		setPrompt( null );
	}

	public void setPrompt( String prompt ) {
		if( TextUtil.isEmpty( prompt ) ) prompt = tool.getProduct().rb().text( "prompt", "command" );
		final String effectivePrompt = prompt;
		Platform.runLater( () -> this.prompt.setText( effectivePrompt ) );
	}

	public boolean isAutoCommandEnabled() {
		return autoCommandEnabled;
	}

	public void setAutoCommandEnabled( boolean autoCommandEnabled ) {
		this.autoCommandEnabled = autoCommandEnabled;
	}

	public void mouse( Point3D point ) {
		getDesign().getCommandProcessor().mouse( point );
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
		// This prevents double events
		event.consume();

		// On each key event the situation needs to be evaluated...
		// If ESC was pressed, then the whole command stack should be cancelled
		// If ENTER was pressed, then an attempt to process the text should be forced
		// If a key was typed, and auto commands are enabled, and the text matched a command then it should be run

		CommandProcessor processor = getDesign().getCommandProcessor();
		if( event.getEventType() == KeyEvent.KEY_PRESSED ) {
			switch( event.getCode() ) {
				case ESCAPE -> {
					// Cancel the command stack
					processor.cancel( tool );
					getDesign().clearSelected();
					clear();
				}
				case ENTER -> {
					if( TextUtil.isEmpty( command.getText() ) ) {
						processor.evaluate( tool, tool.getWorldPointAtMouse() );
					} else {
						process( command.getText() );
					}
					clear();
				}
			}
		} else if( event.getEventType() == KeyEvent.KEY_TYPED ) {
			String id = command.getText();
			boolean autoCommand = processor.isAutoCommandSafe() && autoCommandEnabled;
			if( SPACE.equals( event.getCharacter() ) && TextUtil.isEmpty( command.getText() ) ) {
				process( processor.getPriorCommand() );
				clear();
			} else if( autoCommand && CommandMap.hasCommand( id ) ) {
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
