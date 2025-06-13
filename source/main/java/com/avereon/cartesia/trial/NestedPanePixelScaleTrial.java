package com.avereon.cartesia.trial;

import com.avereon.util.ThreadUtil;
import com.avereon.zerra.javafx.Fx;
import com.avereon.zerra.javafx.FxUtil;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

public class NestedPanePixelScaleTrial extends Application {

	private Stage stage;

	private Scene scene;

	private Pane tool;

	private Pane world;

	@Override
	public void start( Stage stage ) throws Exception {
		this.stage = stage;

		double scale = 80;
		double width = 500;
		double height = 500;

		// This should be as simple as:
		// 1. Move the origin from upper-left to center using pixels
		// 2. Scale according to the desired design scale
		// 3. Flip the Y-axis. In theory, this should be done in step 2 also.

		// But because JavaFX works the way it does, have to pre-scale the geometry

		// The pre-scaled world
		Line line1 = new Line( -2 * scale, -2 * scale, 2 * scale, 2 * scale );
		line1.setStroke( javafx.scene.paint.Color.RED.darker().darker() );
		line1.setStrokeWidth( 1 * scale );
		line1.setStrokeLineCap( StrokeLineCap.ROUND );
		Line line2 = new Line( -2 * scale, 2 * scale, 2 * scale, -2 * scale );
		line2.setStroke( javafx.scene.paint.Color.GREEN );
		line2.setStrokeWidth( 1 * scale );
		line2.setStrokeLineCap( StrokeLineCap.ROUND );

		this.world = new Pane();
		world.setScaleY( -1 );
		world.getChildren().addAll( line1, line2 );
		world.resizeRelocate( 0.5 * width, 0.5 * height - FxUtil.getContentBounds( world ).getHeight(), 0, 0 );

		this.tool = new Pane();
		tool.setStyle( "-fx-background-color: #222222;" );
		Rectangle border = new Rectangle( 0.5, 0.5, width, height );
		border.setFill( Color.TRANSPARENT );
		border.setStroke( Color.GRAY );
		tool.getChildren().addAll( border, world );

		this.scene = new Scene( tool, width, height );
		stage.setScene( scene );

		stage.setTitle( "Nested Pane Pixel Scale Trial" );
		stage.centerOnScreen();
		stage.show();

		// Apparently trying to set the stage size is inconsistent on Mint with my settings
		// Stop fighting with it and move on
		// Stage.sizeToScene can be inconsistent, so set the size directly
		//stage.sizeToScene();
		//stage.setWidth( 500 );
		// The extra 32 is to account for the title bar
		//stage.setHeight( 500 );

		//		//System.out.println( "World pos: " + world.getLayoutX() + ", " + world.getLayoutY() );
		//		System.out.println( "Stage size: " + stage.getWidth() + ", " + stage.getHeight() );
		//		System.out.println( "Scene size: " + scene.getWidth() + ", " + scene.getHeight() );
		//		System.out.println( "Tool size:  " + tool.getWidth() + ", " + tool.getHeight() );
		//

		//		tool.widthProperty().addListener( ( _, _, _ ) -> printBounds() );
		//		tool.heightProperty().addListener( ( _, _, _ ) -> printBounds() );

		new Thread( () -> {
			ThreadUtil.pause( 100 );
			printBounds();

			Fx.run( () -> {
				//				Bounds worldToTool = FxUtil.localToAncestor( scaledWorld, tool );
				//				Rectangle worldBounds = FxUtil.toRectangle( worldToTool );
				//				worldBounds.setFill( Color.TRANSPARENT );
				//				worldBounds.setStroke( Color.YELLOW );
				//				tool.getChildren().add( worldBounds );
				//				System.out.println( "World to tool: " + worldToTool );

				Bounds line1BoundsInTool = FxUtil.localToAncestor( line1, tool );
				Rectangle line1Bounds = FxUtil.toRectangle( line1BoundsInTool );
				line1Bounds.setFill( Color.TRANSPARENT );
				line1Bounds.setStroke( Color.YELLOW );
				tool.getChildren().add( line1Bounds );
				System.out.println( "Line 1 to tool: " + line1BoundsInTool );
			} );
		} ).start();
	}

	private void printBounds() {
		System.out.println( "Stage size: " + stage.getWidth() + ", " + stage.getHeight() );
		System.out.println( "Scene size: " + scene.getWidth() + ", " + scene.getHeight() );
		System.out.println( "Tool size:  " + tool.getWidth() + ", " + tool.getHeight() );
		System.out.println();
	}

}
