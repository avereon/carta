package com.avereon.cartesia;

import com.avereon.cartesia.cursor.StandardCursor;
import com.avereon.util.Log;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.asset.Asset;
import javafx.scene.layout.Pane;

public class Design2dEditor extends ProgramTool {

	private static final System.Logger log = Log.get();

	private final Pane geometry;

	public Design2dEditor( ProgramProduct product, Asset asset ) {
		super( product, asset );

		getChildren().add( geometry = new Pane() );

		setCursor( StandardCursor.DUPLEX );

		//onMousePressedProperty().set( e -> add( new Circle(100,100,100, Color.RED) ) );

		// Add a key listener (actions can start a command immediately)
		// that sends the keys to a command processor
		// that handles the processing of commands
		// and their eventual outcome
	}

	//public void add( Node node ) {
	//	geometry.getChildren().add( node );
	//}

	public void setCursor( StandardCursor cursor ) {
		super.setCursor( cursor.get() );
	}

}
