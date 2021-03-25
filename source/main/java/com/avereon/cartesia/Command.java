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

import java.util.ArrayList;
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

	public void cancel( DesignTool tool ) throws Exception {
		if( tool != null ) {
			clearReferenceAndPreview( tool );
			tool.getDesign().clearSelected();
			tool.setCursor( Cursor.DEFAULT );
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

	protected void promptForNumber( CommandContext context, DesignTool tool, String key ) {
		tool.setCursor( null );
		promptForValue( context, tool, key, CommandContext.Input.NUMBER );
	}

	protected void promptForPoint( CommandContext context, DesignTool tool, String key ) {
		tool.setCursor( tool.getReticle() );
		promptForValue( context, tool, key, CommandContext.Input.POINT );
	}

	protected void promptForShape( CommandContext context, DesignTool tool, String key ) {
		tool.setCursor( Cursor.HAND );
		promptForValue( context, tool, key, CommandContext.Input.NONE );
	}

	protected void promptForText( CommandContext context, DesignTool tool, String key ) {
		tool.setCursor( Cursor.TEXT );
		promptForValue( context, tool, key, CommandContext.Input.TEXT );
	}

	protected DesignShape findNearestShapeAtMouse( DesignTool tool, Point3D mouse ) {
		List<Shape> shapes = tool.screenPointFindAndWait( mouse );
		return shapes.isEmpty() ? DesignShape.NONE : DesignShapeView.getDesignData( shapes.get( 0 ) );
	}

	protected DesignShape findNearestShapeAtPoint( DesignTool tool, Point3D point ) {
		return findNearestShapeAtMouse( tool, tool.worldToScreen( point ) );
	}

	protected DesignShape selectNearestShapeAtMouse( DesignTool tool, Point3D mouse ) {
		List<Shape> shapes = tool.screenPointSelectAndWait( mouse );
		return shapes.isEmpty() ? DesignShape.NONE : DesignShapeView.getDesignData( shapes.get( 0 ) );
	}

	protected DesignShape selectNearestShapeAtPoint( DesignTool tool, Point3D point ) {
		return selectNearestShapeAtMouse( tool, tool.worldToScreen( point ) );
	}

	protected List<DesignShape> cloneShapes( Collection<DesignShape> shapes ) {
		return shapes.stream().map( DesignShape::clone ).collect( Collectors.toList() );
	}

	protected void setCaptureUndoChanges( DesignTool tool, boolean enabled ) {
		tool.getAsset().setCaptureUndoChanges( enabled );
	}

	protected Collection<DesignShape> getReference() {
		return this.reference;
	}

	protected void addReference( DesignTool tool, DesignShape... shapes ) {
		addReference( tool, List.of( shapes ) );
	}

	protected void addReference( DesignTool tool, Collection<DesignShape> shapeList ) {
		this.reference.addAll( shapeList );
		String referencePaint = Paints.toString( DesignPane.DEFAULT_SELECT_DRAW_PAINT );
		shapeList.forEach( s -> {
			s.setReference( true );
			s.setDrawPaint( referencePaint );
			// TODO Should there be a specific reference layer?
			tool.getCurrentLayer().addShape( s );
		} );
	}

	protected void removeReference( DesignTool tool, DesignShape... shapes ) {
		removeReference( tool, List.of( shapes ) );
	}

	protected void removeReference( DesignTool tool, Collection<DesignShape> shapeList ) {
		shapeList.forEach( s -> {
			s.getLayer().removeShape( s );
			s.setReference( false );
		} );
		reference.removeAll( shapeList );
	}

	protected void clearReference( DesignTool tool ) {
		// The shapes have to be removed before capturing undo changes again
		removeReference( tool, reference );
		reference.clear();
	}

	protected Collection<DesignShape> getPreview() {
		return this.preview;
	}

	protected void addPreview( DesignTool tool, DesignShape... shapes ) {
		addPreview( tool, List.of( shapes ) );
	}

	protected void addPreview( DesignTool tool, Collection<DesignShape> shapeList ) {
		this.preview.addAll( shapeList );
		shapeList.forEach( s -> {
			s.setReference( true );
			if( s.getLayer() == null ) {
				tool.getCurrentLayer().addShape( s );
			} else {
				s.getLayer().addShape( s );
			}
		} );
	}

	protected void removePreview( DesignTool tool, DesignShape... shapes ) {
		removePreview( tool, List.of( shapes ) );
	}

	protected void removePreview( DesignTool tool, Collection<DesignShape> shapeList ) {
		shapeList.forEach( s -> {
			s.getLayer().removeShape( s );
			s.setReference( false );
		} );
		preview.removeAll( shapeList );
	}

	protected void clearPreview( DesignTool tool ) {
		// The shapes have to be removed before capturing undo changes again
		removePreview( tool, preview );
		preview.clear();
	}

	protected void clearReferenceAndPreview( DesignTool tool ) {
		clearReference( tool );
		clearPreview( tool );
	}

	@Deprecated
	protected Object commitPreview( DesignTool tool ) {
		List<DesignShape> shapes = new ArrayList<>( preview );

		// Clear preview enables capturing undo changes again
		clearPreview( tool );

		// Add the shapes to the layer like normal
		shapes.forEach( s -> tool.getCurrentLayer().addShape( s ) );
		return COMPLETE;
	}

	private void promptForValue( CommandContext context, DesignTool tool, String key, CommandContext.Input mode ) {
		String text = Rb.text( BundleKey.PROMPT, key );
		context.submit( tool, new Prompt( text, mode ) );
	}

}
