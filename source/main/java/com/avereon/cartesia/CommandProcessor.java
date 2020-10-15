package com.avereon.cartesia;

import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;

public interface CommandProcessor {

	String getPriorCommand();

	void cancel( DesignTool tool );

	void mouse( Point3D point );

	void evaluate( DesignTool tool, Point3D point );

	void evaluate( DesignTool tool, String input ) throws CommandException;

	void pushValue( DesignTool tool, Object object );

	Object pullValue();

	boolean isSelecting();

	boolean isAutoCommandSafe();
}
