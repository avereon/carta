package com.avereon.cartesia.snap;

import com.avereon.cartesia.tool.BaseDesignTool;
import javafx.geometry.Point3D;

public interface Snap {

	String getPromptKey();

	Point3D snap( BaseDesignTool tool, Point3D point );

}
