package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.DesignTool;
import com.avereon.cartesia.geometry.CsaShape;

public class AddCommand extends Command {

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		Object shape = processor.pullValue();
		if( shape instanceof CsaShape ) tool.getDesign().getCurrentLayer().addShape( (CsaShape)shape );
	}

}
