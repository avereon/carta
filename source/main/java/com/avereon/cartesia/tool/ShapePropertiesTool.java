package com.avereon.cartesia.tool;

import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.tool.PropertiesTool;
import com.avereon.xenon.workpane.Workpane;

public class ShapePropertiesTool extends PropertiesTool {

	public ShapePropertiesTool( ProgramProduct product, Asset asset ) {
		super( product, asset );
	}

	@Override
	public Workpane.Placement getPlacement() {
		return Workpane.Placement.DOCK_RIGHT;
	}

}
