package com.avereon.cartesia.data;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.xenon.ProgramProduct;

public class DesignContext {

	private final ProgramProduct product;

	private final Design design;

	private final CommandContext commandContext;

	public DesignContext( ProgramProduct product, Design design ) {
		this.product = product;
		this.design = design;

		this.commandContext = new CommandContext( product, this );
	}

	public ProgramProduct getProduct() {
		return product;
	}

	public Design getDesign() {
		return design;
	}

	public CommandContext getCommandContext() {
		return commandContext;
	}
}
