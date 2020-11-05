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

	private final Workplane workplane;

	private CoordinateSystem coordinateSystem;

	public DesignContext( ProgramProduct product, Design design ) {
		this.product = product;
		this.design = design;
		this.commandContext = new CommandContext( product );
		this.coordinates = new CoordinateStatus( product );
		this.coordinateSystem = CoordinateSystem.ORTHO;
		this.workplane = new Workplane();
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

	public final Workplane getWorkplane() {
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
		Point3D mouseOnWorkplane = tool.mouseToWorkplane( event.getX(), event.getY(), event.getZ() );

		getCommandContext().setMouse( mouseOnWorkplane );
		getCoordinateStatus().updatePosition( mouseOnWorkplane );
	}

}
