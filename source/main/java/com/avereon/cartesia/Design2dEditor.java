package com.avereon.cartesia;

import com.avereon.util.Log;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import javafx.scene.layout.Pane;

public class Design2dEditor extends DesignTool {

	private static final System.Logger log = Log.get();

	private final Pane geometry;

	public Design2dEditor( ProgramProduct product, Asset asset ) {
		super( product, asset );

		getChildren().add( geometry = new Pane() );

		// Add a key listener (actions can start a command immediately)
		// that sends the keys to a command processor
		// that handles the processing of commands
		// and their eventual outcome
	}

	//public void add( Node node ) {
	//	geometry.getChildren().add( node );
	//}

}
