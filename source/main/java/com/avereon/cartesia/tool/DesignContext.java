package com.avereon.cartesia.tool;

import com.avereon.xenon.ProgramProduct;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DesignContext {

//	private final Design design;

	private final CommandContext commandContext;

	private final CoordinateStatus coordinates;

	public DesignContext( ProgramProduct product ) {
		this.commandContext = new CommandContext( product );
		this.coordinates = new CoordinateStatus( product );
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
