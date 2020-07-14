package com.avereon.cartesia;

import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class Design3dEditor extends DesignTool {

	public Design3dEditor( ProgramProduct product, Asset asset ) {
		super( product, asset );
	}

	@Override
	protected Point3D mouseToWorld( MouseEvent event ) {
		// TODO Convert to design coordinates
		return new Point3D( event.getX(), event.getY(), event.getZ() );
	}

}
