package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.data.DesignText;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zarra.font.FontMetrics;
import com.avereon.zarra.font.FontUtil;
import com.avereon.zarra.javafx.Fx;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.CustomLog;

import java.util.List;

@CustomLog
public class DesignTextView extends DesignShapeView {

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
		shape.setFont( FontUtil.decode( text.getTextFont() ) );
		shape.setScaleY( -1 );

		FontMetrics metrics = new FontMetrics( shape.getFont() );
		shape.setLayoutY( metrics.getAscent() + metrics.getDescent() );

		return List.of( shape );
	}

	@Override
	protected List<ConstructionPoint> generateConstructionPoints( DesignPane pane, List<Shape> shapes ) {
		Text text = (Text)shapes.get( 0 );
		ConstructionPoint o = cp( pane, text.xProperty(), text.yProperty() );
		return setConstructionPoints( text, List.of( o ) );
	}

	@Override
	void registerListeners() {
		super.registerListeners();
		getDesignShape().register( DesignText.ORIGIN, originHandler = e -> Fx.run( () -> {
			((Text)getShape()).setX( getDesignText().getOrigin().getX() );
			((Text)getShape()).setY( getDesignText().getOrigin().getY() );
		} ) );
		getDesignShape().register( DesignText.TEXT, textHandler = e -> Fx.run( () -> {
			((Text)getShape()).setText( getDesignText().getText() );
		} ) );
		getDesignShape().register( DesignText.TEXT_FONT, fontHandler = e -> Fx.run( () -> {
			Font font = getDesignText().calcTextFont();
			((Text)getShape()).setFont( font );
			FontMetrics metrics = new FontMetrics( font );
			getShape().setLayoutY( metrics.getAscent() + metrics.getDescent() );
		} ) );
		getDesignShape().register( DesignText.ROTATE, rotateHandler = e -> Fx.run( () -> {
			updateRotate( getDesignText(), getShape() );
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

}
