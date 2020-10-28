package com.avereon.cartesia.snap;

import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;

public interface Snap {

	String getPromptKey();

	Point3D snap( DesignTool tool, Point3D mouse );

}
