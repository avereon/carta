package com.avereon.cartesia;

import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;

public interface CommandProcessor {

	String getPriorCommand();

	void cancel( BaseDesignTool tool );

	void mouse( Point3D point );

	void evaluate( BaseDesignTool tool, Point3D point );

	void evaluate( BaseDesignTool tool, String input ) throws CommandException;

	void pushValue( BaseDesignTool tool, Object object );

	Object pullValue();

	boolean isSelecting();

	boolean isAutoCommandSafe();
}
