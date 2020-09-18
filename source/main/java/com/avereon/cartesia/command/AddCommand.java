package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandProcessor;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.cartesia.data.DesignShape;

public class AddCommand extends Command {

	@Override
	public void evaluate( CommandProcessor processor, DesignTool tool ) {
		Object shape = processor.pullValue();
		if( shape instanceof DesignShape ) tool.getDesign().getCurrentLayer().addShape( (DesignShape)shape );
	}

}
