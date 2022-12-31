package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.data.DesignText;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zarra.font.FontMetrics;
import com.avereon.zarra.javafx.Fx;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import lombok.CustomLog;

import java.util.List;

@CustomLog
public class DesignTextView extends DesignShapeView {

	private static final double PPI = 1.0 / 72.0;

	private EventHandler<NodeEvent> originHandler;

	private EventHandler<NodeEvent> textHandler;

	private EventHandler<NodeEvent> fontHandler;

	private EventHandler<NodeEvent> rotateHandler;

	public DesignTextView( DesignPane pane, DesignShape designShape ) {
		super( pane, designShape );
	}

	public DesignText getDesignText() {
		return (DesignText)getDesignNode();
	}

	@Override
	protected List<Shape> generateGeometry() {
		DesignText text = getDesignText();
		Text shape = new Text( text.getOrigin().getX(), text.getOrigin().getY(), text.getText() );
		configureShape( shape );
		return List.of( shape );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Text text = (Text)shapes.get( 0 );
		ConstructionPoint o = cp( pane, text.xProperty(), text.yProperty() );
		ConstructionPoint ow = cp( pane, text.xProperty().add( text.layoutBoundsProperty().get().getWidth() ), text.yProperty() );
		ConstructionPoint oh = cp( pane, text.xProperty(), text.yProperty().add( text.layoutBoundsProperty().get().getHeight() ) );
		ConstructionPoint wh = cp( pane, text.xProperty().add( text.layoutBoundsProperty().get().getWidth() ), text.yProperty().add( text.layoutBoundsProperty().get().getHeight() ) );
		return setConstructionPoints( text, List.of( o, ow, oh, wh ) );
	}

	@Override
	protected void configureShape( Shape shape ) {
		super.configureShape( shape );

		shape.setScaleX( 0.25 );
		shape.setScaleY( -0.25 );

		//updateScale( getDesignText(), shape );
		updateFont( getDesignText(), shape );
		updateRotate( getDesignText(), shape );

		//log.atConfig().log( "text position=%s", shape.localToParent( new Point2D( 0, 0 ) ) );
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
		getDesignShape().register( DesignText.TEXT_FONT, fontHandler = e -> Fx.run( () -> {
			((Text)getShape()).setFont( getDesignText().calcTextFont() );
			updateFont( designText, getShape() );
		} ) );
		getDesignShape().register( DesignText.ROTATE, rotateHandler = e -> Fx.run( () -> {
			updateRotate( designText, getShape() );
		} ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignText.ROTATE, rotateHandler );
		getDesignShape().unregister( DesignText.TEXT_FONT, fontHandler );
		getDesignShape().unregister( DesignText.TEXT, textHandler );
		getDesignShape().unregister( DesignText.ORIGIN, originHandler );
		super.unregisterListeners();
	}

	void updateFont( DesignText text, Shape shape ) {
		FontMetrics metrics = new FontMetrics( ((Text)shape).getFont() );
		shape.setTranslateY( metrics.getAscent() + metrics.getDescent() );
	}

}
