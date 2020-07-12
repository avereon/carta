package com.avereon.cartesia;

import com.avereon.util.Log;
import com.avereon.venza.image.VectorImage;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.asset.Asset;
import javafx.geometry.Dimension2D;
import javafx.scene.ImageCursor;
import javafx.scene.layout.Pane;

public class Design2dEditor extends ProgramTool {

	private static final System.Logger log = Log.get();

	private final Pane geometry;

	public Design2dEditor( ProgramProduct product, Asset asset ) {
		super( product, asset );

		getChildren().add( geometry = new Pane() );

		// NEXT Create an icon for the crosshair cursor
		VectorImage icon = (VectorImage)getProgram().getIconLibrary().getIcon( "document" );

		Dimension2D size = ImageCursor.getBestSize( 48, 48 );
		setCursor( new ImageCursor( icon.resize( size.getWidth() ).getImage() ) );

		//onMousePressedProperty().set( e -> add( new Circle(100,100,100, Color.RED) ) );

		// Add a key listener (actions can start a command immediately)
		// that sends the keys to a command processor
		// that handles the processing of commands
		// and their eventual outcome
	}

	//public void add( Node node ) {
	//	geometry.getChildren().add( node );
	//}

}
