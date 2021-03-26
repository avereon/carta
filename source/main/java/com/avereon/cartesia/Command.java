package com.avereon.cartesia;

import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadMath;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignPane;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.cartesia.tool.view.DesignShapeView;
import com.avereon.product.Rb;
import com.avereon.util.Log;
import com.avereon.zerra.color.Paints;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Command {

	public static final Object INCOMPLETE = new Object();

	public static final Object COMPLETE = new Object();

	public static final Object INVALID = new Object();

	private static final System.Logger log = Log.get();

	private final List<DesignShape> reference;

	private final List<DesignShape> preview;

	private int step;

	protected Command() {
		this.reference = new CopyOnWriteArrayList<>();
		this.preview = new CopyOnWriteArrayList<>();
	}

	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		return COMPLETE;
	}

	public void cancel( CommandContext context ) throws Exception {
		if( context.getTool() != null ) {
			clearReferenceAndPreview( context );
			context.getTool().getDesign().clearSelected();
			context.getTool().setCursor( Cursor.DEFAULT );
		}
	}

	public int getStep() {
		return step;
	}

	public void incrementStep() {
		step++;
	}

	public CommandContext.Input getInputMode() {
		return CommandContext.Input.NONE;
	}

	public boolean clearSelectionWhenComplete() {
		return true;
	}

	public void handle( KeyEvent event ) {}

	public void handle( MouseEvent event ) {}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	protected double asDouble( Object value ) throws Exception {
		if( value instanceof Double ) return (Double)value;
		if( value instanceof Point3D ) return ((Point3D)value).distance( Point3D.ZERO );
		return CadMath.eval( String.valueOf( value ) );
	}

	protected double asDouble( Point3D anchor, Object value ) throws Exception {
		if( value instanceof Double ) return (Double)value;
		if( value instanceof Point3D ) return ((Point3D)value).distance( anchor );
		return CadMath.eval( String.valueOf( value ) );
	}

	protected Point3D asPoint( CommandContext context, Object value ) throws Exception {
		if( value instanceof Point3D ) return (Point3D)value;
		return CadShapes.parsePoint( String.valueOf( value ), context.getAnchor() );
	}

	protected Point3D asPoint( Point3D anchor, Object value ) throws Exception {
		if( value instanceof Point3D ) return (Point3D)value;
		return CadShapes.parsePoint( String.valueOf( value ), anchor );
	}

	protected void promptForNumber( CommandContext context, String key ) {
		context.getTool().setCursor( null );
		promptForValue( context, key, CommandContext.Input.NUMBER );
	}

	protected void promptForPoint( CommandContext context, String key ) {
		context.getTool().setCursor( context.getTool().getReticle() );
		promptForValue( context, key, CommandContext.Input.POINT );
	}

	protected void promptForShape( CommandContext context, String key ) {
		context.getTool().setCursor( Cursor.HAND );
		promptForValue( context, key, CommandContext.Input.NONE );
	}

	protected void promptForText( CommandContext context, String key ) {
		context.getTool().setCursor( Cursor.TEXT );
		promptForValue( context, key, CommandContext.Input.TEXT );
	}

	protected DesignShape findNearestShapeAtMouse( CommandContext context, Point3D mouse ) {
		List<Shape> shapes = context.getTool().screenPointFindAndWait( mouse );
		return shapes.isEmpty() ? DesignShape.NONE : DesignShapeView.getDesignData( shapes.get( 0 ) );
	}

	protected DesignShape findNearestShapeAtPoint( CommandContext context, Point3D point ) {
		return findNearestShapeAtMouse( context, context.getTool().worldToScreen( point ) );
	}

	protected DesignShape selectNearestShapeAtMouse( CommandContext context, Point3D mouse ) {
		List<Shape> shapes = context.getTool().screenPointSelectAndWait( mouse );
		return shapes.isEmpty() ? DesignShape.NONE : DesignShapeView.getDesignData( shapes.get( 0 ) );
	}

	protected DesignShape selectNearestShapeAtPoint( CommandContext context, Point3D point ) {
		return selectNearestShapeAtMouse( context, context.getTool().worldToScreen( point ) );
	}

	protected List<DesignShape> cloneShapes( Collection<DesignShape> shapes, boolean reference ) {
		return shapes.stream().map( s -> {
			DesignShape clone = s.clone();
			clone.setReference( reference );
			if( s.getLayer() == null ) {
				//context.getTool().getCurrentLayer().addShape( clone );
			} else {
				s.getLayer().addShape( clone );
			}
			return clone;
		} ).collect( Collectors.toList() );
	}

	protected void setCaptureUndoChanges( CommandContext context, boolean enabled ) {
		context.getTool().getAsset().setCaptureUndoChanges( enabled );
	}

	protected Collection<DesignShape> getReference() {
		return this.reference;
	}

	protected void addReference( CommandContext context, DesignShape... shapes ) {
		// TODO Should there be a specific reference layer?
		addReference( context, List.of( shapes ).stream().peek( s -> {
			s.setReference( true );
		} ).collect( Collectors.toList() ) );
	}

	protected void addReference( CommandContext context, Collection<DesignShape> shapes ) {
		this.reference.addAll( cloneShapes( shapes, true ) );
		String referencePaint = Paints.toString( DesignPane.DEFAULT_SELECT_DRAW_PAINT );
		this.reference.forEach( s -> {
			s.setDrawPaint( referencePaint );
			if( s.getLayer() == null ) context.getTool().getCurrentLayer().addShape( s );
		} );
	}

	protected void removeReference( CommandContext context, DesignShape... shapes ) {
		removeReference( context, List.of( shapes ) );
	}

	protected void removeReference( CommandContext context, Collection<DesignShape> shapeList ) {
		shapeList.forEach( s -> {
			s.getLayer().removeShape( s );
		} );
		reference.removeAll( shapeList );
	}

	protected void clearReference( CommandContext context ) {
		// The shapes have to be removed before capturing undo changes again
		removeReference( context, reference );
		reference.clear();
	}

	protected Collection<DesignShape> getPreview() {
		return this.preview;
	}

	protected void addPreview( CommandContext context, DesignShape... shapes ) {
		addPreview( context, List.of( shapes ).stream().peek( s -> {
			s.setReference( true );
			//tool.getCurrentLayer().addShape( s );
		} ).collect( Collectors.toList() ) );
	}

	protected void addPreview( CommandContext context, Collection<DesignShape> shapes ) {
		this.preview.addAll( cloneShapes( shapes, true ) );
	}

	protected void removePreview( CommandContext context, DesignShape... shapes ) {
		removePreview( context, List.of( shapes ) );
	}

	protected void removePreview( CommandContext context, Collection<DesignShape> shapeList ) {
		shapeList.forEach( s -> {
			s.getLayer().removeShape( s );
		} );
		preview.removeAll( shapeList );
	}

	protected void clearPreview( CommandContext context ) {
		// The shapes have to be removed before capturing undo changes again
		removePreview( context, preview );
		preview.clear();
	}

	protected void clearReferenceAndPreview( CommandContext context ) {
		clearReference( context );
		clearPreview( context );
	}

	private void promptForValue( CommandContext context, String key, CommandContext.Input mode ) {
		String text = Rb.text( BundleKey.PROMPT, key );
		context.submit( context.getTool(), new Prompt( text, mode ) );
	}

}
