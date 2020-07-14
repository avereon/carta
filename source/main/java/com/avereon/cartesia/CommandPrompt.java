package com.avereon.cartesia;

import com.avereon.xenon.ProgramProduct;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class CommandPrompt extends BorderPane {

	private TextField command;

	public CommandPrompt( ProgramProduct product ) {
		setLeft( new Label( product.rb().text( "prompt", "command" ) ) );
	}

}
