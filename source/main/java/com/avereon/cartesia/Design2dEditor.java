package com.avereon.cartesia;

import com.avereon.util.Log;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class Design2dEditor extends DesignTool {

	private static final System.Logger log = Log.get();

	public Design2dEditor( ProgramProduct product, Asset asset ) {
		super( product, asset );

	}

	@Override
	protected Point3D mouseToWorld( MouseEvent event ) {
		// TODO Convert to design coordinates
		return new Point3D( event.getX(), event.getY(), 0.0 );
	}

	//public void add( Node node ) {
	//	geometry.getChildren().add( node );
	//}

}
