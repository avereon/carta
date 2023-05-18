package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.Design;
import com.avereon.xenon.XenonProgramProduct;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DesignContext {

	private final XenonProgramProduct product;

	private final Design design;

	private final CommandContext commandContext;

	private final CoordinateStatus coordinates;

	public DesignContext( XenonProgramProduct product, Design design ) {
		this.product = product;
		this.design = design;
		this.commandContext = new CommandContext( product );
		this.coordinates = new CoordinateStatus();
	}

	public final XenonProgramProduct getProduct() {
		return product;
	}

	public final Design getDesign() {
		return design;
	}

	public final CommandContext getCommandContext() {
		return commandContext;
	}

	public final CommandPrompt getCommandPrompt() {
		return getCommandContext().getCommandPrompt();
	}

	public final CoordinateStatus getCoordinateStatus() {
		return coordinates;
	}

	public final void setMouse( MouseEvent event ) {
		DesignTool tool = (DesignTool)event.getSource();
		Point3D screenMouse = new Point3D( event.getX(), event.getY(), event.getZ() );
		getCommandContext().setScreenMouse( screenMouse );

		Point3D worldMouse = tool.mouseToWorkplane( screenMouse );
		getCommandContext().setWorldMouse( worldMouse );
		getCoordinateStatus().updatePosition( worldMouse );
		getCoordinateStatus().updateZoom( tool.getZoom() );
	}

}
