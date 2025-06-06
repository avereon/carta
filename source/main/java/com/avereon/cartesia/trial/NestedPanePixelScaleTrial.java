package com.avereon.cartesia.trial;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

public class NestedPanePixelScaleTrial extends Application {

	@Override
	public void start( Stage stage ) throws Exception {
		Line line1 = new Line( -2, -2, 2, 2 );
		line1.setStroke( javafx.scene.paint.Color.RED );
		line1.setStrokeWidth( 1 );
		line1.setStrokeLineCap( StrokeLineCap.ROUND );
		Line line2 = new Line( -2, 2, 2, -2 );
		line2.setStroke( javafx.scene.paint.Color.GREEN );
		line2.setStrokeWidth( 1 );
		line2.setStrokeLineCap( StrokeLineCap.ROUND );

		Pane world = new Pane();
		world.setBorder( Border.EMPTY );
		world.relocate( 0, 0 );
		world.getChildren().addAll( line1, line2 );
		world.resizeRelocate( 0, 0, 2000, 2000 );

		double scale = 1;
		Pane glass = new Pane();
		glass.setBorder( Border.EMPTY );
		glass.relocate( 0, 0 );
		//		glass.setTranslateX( 250 );
		//		glass.setTranslateY( 250 );
		glass.setScaleX( scale );
		glass.setScaleY( -scale );
		glass.getChildren().add( world );

		Pane tool = new Pane();
		tool.setStyle( "-fx-background-color: #222222;" );
		Rectangle border = new Rectangle( 0, 0, 500, 500 );
		border.setStroke( javafx.scene.paint.Color.GRAY );
		tool.getChildren().addAll( border, glass );

		Scene scene = new Scene( tool, 500, 500 );
		stage.setScene( scene );

		stage.setTitle( "Nested Pane Pixel Scale Trial" );
		stage.show();
		stage.sizeToScene();
		stage.centerOnScreen();

		// NEXT Figure out why the world pane is not at 0,0
		System.out.println( "World pos: " + world.getLayoutX() + ", " + world.getLayoutY() );
	}

}
