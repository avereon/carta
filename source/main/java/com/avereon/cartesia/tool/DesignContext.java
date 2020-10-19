package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.Design;
import com.avereon.xenon.ProgramProduct;

public class DesignContext {

	private final ProgramProduct product;

	private final Design design;

	private final CommandContext commandContext;

	private final CoordinateStatus coordinates;

	private final CommandPrompt commandPrompt;

	public DesignContext( ProgramProduct product, Design design ) {
		this.product = product;
		this.design = design;
		this.commandPrompt = new CommandPrompt( product, design );
		this.coordinates = new CoordinateStatus( product );
		this.commandContext = new CommandContext( product, this );
	}

	public final ProgramProduct getProduct() {
		return product;
	}

	public final Design getDesign() {
		return design;
	}

	public final CommandPrompt getCommandPrompt() {
		return commandPrompt;
	}

	public final CoordinateStatus getCoordinateStatus() {
		return coordinates;
	}

	public final CommandContext getCommandContext() {
		return commandContext;
	}

}
