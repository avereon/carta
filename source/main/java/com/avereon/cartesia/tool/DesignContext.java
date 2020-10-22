package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.Design;
import com.avereon.xenon.ProgramProduct;

public class DesignContext {

	private final ProgramProduct product;

	private final Design design;

	private final CommandContext commandContext;

	private final CoordinateStatus coordinates;

	public DesignContext( ProgramProduct product, Design design ) {
		this.product = product;
		this.design = design;
		this.coordinates = new CoordinateStatus( product );
		this.commandContext = new CommandContext( product );
	}

	public final ProgramProduct getProduct() {
		return product;
	}

	public final Design getDesign() {
		return design;
	}

	public final CoordinateStatus getCoordinateStatus() {
		return coordinates;
	}

	public final CommandContext getCommandContext() {
		return commandContext;
	}

	public final CommandPrompt getCommandPrompt() {
		return getCommandContext().getCommandPrompt();
	}

}
