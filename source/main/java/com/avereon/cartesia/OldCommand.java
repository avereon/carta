package com.avereon.cartesia;

import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;

import java.util.List;

public abstract class OldCommand {

	public boolean isAutoCommandSafe() {
		return true;
	}

	public void mouse( Point3D point ) {}

	public List<OldCommand> getPreSteps( DesignTool tool ) {
		return List.of();
	}

	public void evaluate( CommandProcessor processor, DesignTool tool ) throws CommandException {}

}
