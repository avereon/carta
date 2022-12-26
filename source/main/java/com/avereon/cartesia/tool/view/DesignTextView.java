package com.avereon.cartesia.tool.view;

import com.avereon.cartesia.data.*;
import com.avereon.cartesia.tool.ConstructionPoint;
import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.zarra.javafx.Fx;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.List;

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
		DesignText line = getDesignText();
		Text shape = new Text( line.getOrigin().getX(), line.getOrigin().getY(), line.getText() );
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
			((Line)getShape()).setStartX( getDesignText().getOrigin().getX() );
			((Line)getShape()).setStartY( getDesignText().getOrigin().getY() );
		} ) );
		getDesignShape().register( DesignText.TEXT, textHandler = e -> Fx.run( () -> {
			((Text)getShape()).setText( getDesignText().getText() );
		}) );
		getDesignShape().register( DesignText.FONT, fontHandler = e -> Fx.run( () -> {
			((Text)getShape()).setFont( getDesignText().getFont() );
		}) );
		getDesignShape().register( DesignText.ROTATE, rotateHandler = e -> Fx.run( () -> {
			updateRotate( getDesignText(), getShape() );
		} ) );
	}

	@Override
	void unregisterListeners() {
		getDesignShape().unregister( DesignText.ROTATE, rotateHandler );
		getDesignShape().unregister( DesignText.FONT, fontHandler );
		getDesignShape().unregister( DesignText.TEXT, textHandler );
		getDesignShape().unregister( DesignText.ORIGIN, originHandler );
		super.unregisterListeners();
	}

}
