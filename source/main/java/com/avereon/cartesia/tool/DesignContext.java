package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.Design;
import com.avereon.xenon.ProgramProduct;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class DesignContext {

	private final ProgramProduct product;

	private final Design design;

	private final CommandContext commandContext;

	private final CoordinateStatus coordinates;

	private final DesignWorkplane workplane;

	private CoordinateSystem coordinateSystem;

	public DesignContext( ProgramProduct product, Design design ) {
		this.product = product;
		this.design = design;
		this.commandContext = new CommandContext( product );
		this.coordinates = new CoordinateStatus( product );
		this.coordinateSystem = CoordinateSystem.ORTHO;
		this.workplane = new DesignWorkplane();
	}

	public final ProgramProduct getProduct() {
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

	@Deprecated
	public final DesignWorkplane getWorkplane() {
		return workplane;
	}

	public final CoordinateSystem getCoordinateSystem() {
		return coordinateSystem;
	}

	public final void setCoordinateSystem( CoordinateSystem coordinateSystem ) {
		this.coordinateSystem = coordinateSystem == null ? CoordinateSystem.ORTHO : coordinateSystem;
	}

	public final void setMouse( MouseEvent event ) {
		DesignTool tool = (DesignTool)event.getSource();
		Point3D screenMouse = new Point3D( event.getX(), event.getY(), event.getZ() );
		getCommandContext().setScreenMouse( screenMouse );

		Point3D worldMouse = tool.mouseToWorkplane( screenMouse );
		getCommandContext().setWorldMouse( worldMouse );
		getCoordinateStatus().updatePosition( worldMouse );
	}

}
