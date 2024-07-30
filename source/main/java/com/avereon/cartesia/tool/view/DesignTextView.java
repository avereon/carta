package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.data.DesignText;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zarra.font.FontMetrics;
import com.avereon.zarra.javafx.Fx;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import lombok.CustomLog;

import java.util.List;

@CustomLog
public class DesignTextView extends DesignShapeView {

	/**
	 * This constant is "points per inch". This is the size of a font point:
	 * <a href="https://fonts.google.com/knowledge/glossary/point_size">...</a>
	 */
	private static final double PPI = 1.0 / 72.0;

	private EventHandler<NodeEvent> originHandler;

	private EventHandler<NodeEvent> textHandler;

	private EventHandler<NodeEvent> textSizeHandler;

	private EventHandler<NodeEvent> fontNameHandler;

	private EventHandler<NodeEvent> fontWeightHandler;

	private EventHandler<NodeEvent> fontPostureHandler;

	private EventHandler<NodeEvent> fontUnderlineHandler;

	private EventHandler<NodeEvent> fontStrikethroughHandler;

	private EventHandler<NodeEvent> rotateHandler;

	public DesignTextView( DesignPane pane, DesignShape designShape ) {
		super( pane, designShape );
	}

	public DesignText getDesignText() {
		return (DesignText)getDesignNode();
	}

	@Override
	protected List<Shape> generateGeometry() {
		DesignText designText = getDesignText();
		Text text = new Text( designText.getOrigin().getX(), designText.getOrigin().getY(), designText.getText() );
		configureShape( text );
		return List.of( text );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Text text = (Text)shapes.get( 0 );
		DesignText designText = getDesignText();
		ConstructionPoint oo = cp( pane, Bindings.createDoubleBinding( text::getX, text.layoutBoundsProperty() ), Bindings.createDoubleBinding( text::getY, text.layoutBoundsProperty() ) );
		ConstructionPoint ow = cp(
			pane,
			Bindings.createDoubleBinding( () -> CadGeometry
				.rotate360( new Point3D( text.getX(), text.getY(), 0 ), new Point3D( text.getX() + text.layoutBoundsProperty().get().getWidth(), text.getY(), 0 ), designText.calcRotate() )
				.getX(), text.layoutBoundsProperty(), text.getTransforms() ),
			Bindings.createDoubleBinding( () -> CadGeometry
				.rotate360( new Point3D( text.getX(), text.getY(), 0 ), new Point3D( text.getX() + text.layoutBoundsProperty().get().getWidth(), text.getY(), 0 ), designText.calcRotate() )
				.getY(), text.layoutBoundsProperty(), text.getTransforms() )
		);
		ConstructionPoint oh = cp(
			pane,
			Bindings.createDoubleBinding( () -> CadGeometry
				.rotate360( new Point3D( text.getX(), text.getY(), 0 ), new Point3D( text.getX(), text.getY() + text.layoutBoundsProperty().get().getHeight(), 0 ), designText.calcRotate() )
				.getX(), text.layoutBoundsProperty(), text.getTransforms() ),
			Bindings.createDoubleBinding( () -> CadGeometry
				.rotate360( new Point3D( text.getX(), text.getY(), 0 ), new Point3D( text.getX(), text.getY() + text.layoutBoundsProperty().get().getHeight(), 0 ), designText.calcRotate() )
				.getY(), text.layoutBoundsProperty(), text.getTransforms() )
		);
		ConstructionPoint wh = cp( pane, Bindings.createDoubleBinding( () -> CadGeometry.rotate360(
			new Point3D( text.getX(), text.getY(), 0 ),
			new Point3D( text.getX() + text.layoutBoundsProperty().get().getWidth(), text.getY() + text.layoutBoundsProperty().get().getHeight(), 0 ),
			designText.calcRotate()
		).getX(), text.layoutBoundsProperty(), text.getTransforms() ), Bindings.createDoubleBinding( () -> CadGeometry.rotate360(
			new Point3D( text.getX(), text.getY(), 0 ),
			new Point3D( text.getX() + text.layoutBoundsProperty().get().getWidth(), text.getY() + text.layoutBoundsProperty().get().getHeight(), 0 ),
			designText.calcRotate()
		).getY(), text.layoutBoundsProperty(), text.getTransforms() ) );

		return setConstructionPoints( text, List.of( oo, ow, oh, wh ) );
	}

	@Override
	protected void configureShape( Shape shape ) {
		super.configureShape( shape );
		Text text = (Text)shape;
		text.setBoundsType( TextBoundsType.VISUAL );

		updateFont( getDesignText(), shape );
		updateScale( getDesignText(), shape );
		updateTranslate( getDesignText(), shape );
		updateRotate( getDesignText(), shape );
	}

	@Override
	void registerListeners() {
		super.registerListeners();
		DesignText designText = getDesignText();
		getDesignShape().register( DesignText.ORIGIN, originHandler = e -> Fx.run( () -> {
			((Text)getShape()).setX( designText.getOrigin().getX() );
			((Text)getShape()).setY( designText.getOrigin().getY() );
		} ) );
		getDesignShape().register( DesignText.TEXT, textHandler = e -> Fx.run( () -> {
			((Text)getShape()).setText( designText.getText() );
		} ) );
		getDesignShape().register( DesignText.ROTATE, rotateHandler = e -> Fx.run( () -> {
			updateRotate( designText, getShape() );
		} ) );

		getDesignShape().register( DesignText.TEXT_SIZE, textSizeHandler = e -> Fx.run( () -> {
			updateFont( designText, getShape() );
		} ) );
		getDesignShape().register( DesignText.FONT_NAME, fontNameHandler = e -> Fx.run( () -> {
			updateFont( designText, getShape() );
		} ) );
		getDesignShape().register( DesignText.FONT_WEIGHT, fontWeightHandler = e -> Fx.run( () -> {
			updateFont( designText, getShape() );
		} ) );
		getDesignShape().register( DesignText.FONT_POSTURE, fontPostureHandler = e -> Fx.run( () -> {
			updateFont( designText, getShape() );
		} ) );
		getDesignShape().register( DesignText.FONT_UNDERLINE, fontUnderlineHandler = e -> Fx.run( () -> {
			updateFont( designText, getShape() );
		} ) );
		getDesignShape().register( DesignText.FONT_STRIKETHROUGH, fontStrikethroughHandler = e -> Fx.run( () -> {
			updateFont( designText, getShape() );
		} ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignText.FONT_STRIKETHROUGH, fontStrikethroughHandler );
		getDesignShape().unregister( DesignText.FONT_UNDERLINE, fontUnderlineHandler );
		getDesignShape().unregister( DesignText.FONT_POSTURE, fontPostureHandler );
		getDesignShape().unregister( DesignText.FONT_WEIGHT, fontWeightHandler );
		getDesignShape().unregister( DesignText.FONT_NAME, fontNameHandler );
		getDesignShape().unregister( DesignText.TEXT_SIZE, textSizeHandler );

		getDesignShape().unregister( DesignText.ROTATE, rotateHandler );
		getDesignShape().unregister( DesignText.TEXT, textHandler );
		getDesignShape().unregister( DesignText.ORIGIN, originHandler );
		super.unregisterListeners();
	}

	void updateFont( DesignText designText, Shape shape ) {
		Text text = (Text)shape;
		text.setFont( designText.calcFont() );
		text.setUnderline( designText.calcFontUnderline() );
		text.setStrikethrough( designText.calcFontStrikethrough() );

		// Font settings can affect the translation
		updateTranslate( designText, shape );
	}

	void updateTranslate( DesignText designText, Shape shape ) {
		FontMetrics metrics = new FontMetrics( designText.calcFont() );
		shape.setTranslateX( metrics.getLead() );
		shape.setTranslateY( metrics.getAscent() + metrics.getDescent() );
	}

}
