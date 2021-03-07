package com.avereon.cartesia.settings;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class PaintSwatch extends Pane {

	private final Pane background;

	private final Pane noPaintSwatch;

	private final Rectangle swatch;

	private ObjectProperty<Paint> paint;

	public PaintSwatch() {
		this( null );
	}

	public PaintSwatch( Paint paint ) {
		getStyleClass().add( "paint-swatch" );

		this.background = new Pane();
		this.noPaintSwatch = new Pane();
		this.swatch = new Rectangle();

		// The checkerboard background
		Rectangle a = new Rectangle();
		Rectangle b = new Rectangle();
		Rectangle c = new Rectangle();
		Rectangle d = new Rectangle();
		a.setFill( Color.DARKGRAY );
		a.widthProperty().bind( background.widthProperty().multiply( 0.5 ) );
		a.heightProperty().bind( background.heightProperty().multiply( 0.5 ) );
		b.setFill( Color.LIGHTGREY );
		b.xProperty().bind( background.widthProperty().multiply( 0.5 ) );
		b.widthProperty().bind( background.widthProperty().multiply( 0.5 ) );
		b.heightProperty().bind( background.heightProperty().multiply( 0.5 ) );
		c.setFill( Color.LIGHTGREY );
		c.yProperty().bind( background.heightProperty().multiply( 0.5 ) );
		c.widthProperty().bind( background.widthProperty().multiply( 0.5 ) );
		c.heightProperty().bind( background.heightProperty().multiply( 0.5 ) );
		d.setFill( Color.DARKGRAY );
		d.xProperty().bind( background.widthProperty().multiply( 0.5 ) );
		d.yProperty().bind( background.heightProperty().multiply( 0.5 ) );
		d.widthProperty().bind( background.widthProperty().multiply( 0.5 ) );
		d.heightProperty().bind( background.heightProperty().multiply( 0.5 ) );
		background.getChildren().addAll( a, b, c, d );
		background.layoutXProperty().bind( Bindings.createDoubleBinding( () -> getInsets().getLeft(), insetsProperty() ) );
		background.layoutYProperty().bind( Bindings.createDoubleBinding( () -> getInsets().getTop(), insetsProperty() ) );
		background.prefWidthProperty().bind( Bindings.createDoubleBinding( () -> getWidth() - getInsets().getLeft() - getInsets().getRight(), insetsProperty() ) );
		background.prefHeightProperty().bind( Bindings.createDoubleBinding( () -> getHeight() - getInsets().getTop() - getInsets().getBottom(), insetsProperty() ) );

		// The no paint swatch
		Line down = new Line();
		down.setStyle( "-fx-stroke: inherit" );
		Line up = new Line();
		up.setStyle( "-fx-stroke: inherit" );
		down.endXProperty().bind( noPaintSwatch.widthProperty() );
		down.endYProperty().bind( noPaintSwatch.heightProperty() );
		up.startYProperty().bind( noPaintSwatch.heightProperty() );
		up.endXProperty().bind( noPaintSwatch.widthProperty() );
		noPaintSwatch.setStyle( "-fx-stroke: inherit" );
		noPaintSwatch.layoutXProperty().bind( Bindings.createDoubleBinding( () -> getInsets().getLeft(), insetsProperty() ) );
		noPaintSwatch.layoutYProperty().bind( Bindings.createDoubleBinding( () -> getInsets().getTop(), insetsProperty() ) );
		noPaintSwatch.prefWidthProperty().bind( Bindings.createDoubleBinding( () -> getWidth() - getInsets().getLeft() - getInsets().getRight(), insetsProperty() ) );
		noPaintSwatch.prefHeightProperty().bind( Bindings.createDoubleBinding( () -> getHeight() - getInsets().getTop() - getInsets().getBottom(), insetsProperty() ) );
		noPaintSwatch.getChildren().addAll( up, down );

		// The paint swatch
		swatch.layoutXProperty().bind( Bindings.createDoubleBinding( () -> getInsets().getLeft(), insetsProperty() ) );
		swatch.layoutYProperty().bind( Bindings.createDoubleBinding( () -> getInsets().getTop(), insetsProperty() ) );
		swatch.widthProperty().bind( Bindings.createDoubleBinding( () -> getWidth() - getInsets().getLeft() - getInsets().getRight(), insetsProperty() ) );
		swatch.heightProperty().bind( Bindings.createDoubleBinding( () -> getHeight() - getInsets().getTop() - getInsets().getBottom(), insetsProperty() ) );
		swatch.setStroke( null );

		getChildren().addAll( background, noPaintSwatch, swatch );

		setPaint( paint );
	}

	public Paint getPaint() {
		return paint == null ? null : paint.get();
	}

	public ObjectProperty<Paint> paintProperty() {
		if( paint == null ) paint = new SimpleObjectProperty<>();
		return paint;
	}

	public void setPaint( Paint paint ) {
		paintProperty().set( paint );
		updateSwatch();
	}

	private void updateSwatch() {
		Paint paint = getPaint();
		background.setVisible( paint != null );
		noPaintSwatch.setVisible( paint == null );
		swatch.setVisible( paint != null );
		swatch.setFill( paint );
	}

}
