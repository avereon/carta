package com.avereon.cartesia;

import com.avereon.cartesia.command.Prompt;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadMath;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignShapeView;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Command {

	public static final Object INCOMPLETE = new Object();

	public static final Object COMPLETE = new Object();

	public static final Object INVALID = new Object();

	private static final System.Logger log = Log.get();

	private final List<DesignShape> preview;

	private int step;

	protected Command() {
		this.preview = new CopyOnWriteArrayList<>();
	}

	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		return null;
	}

	public void cancel( DesignTool tool ) throws Exception {
		if( tool != null ) {
			preview.forEach( s -> tool.getCurrentLayer().removeShape( s ) );
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

	public boolean isInputCommand() {
		return false;
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

	protected Point3D asPoint( DesignTool tool, Object value, Point3D anchor ) throws Exception {
		if( value instanceof Point3D ) return (Point3D)value;
		return CadShapes.parsePoint( String.valueOf( value ), anchor );
	}

	protected void promptForNumber( CommandContext context, DesignTool tool, String key ) {
		tool.setCursor( null );
		promptForValue( context, tool, key, false );
	}

	protected void promptForPoint( CommandContext context, DesignTool tool, String key ) {
		tool.setCursor( tool.getReticle() );
		promptForValue( context, tool, key, false );
	}

	protected void promptForShape( CommandContext context, DesignTool tool, String key ) {
		tool.setCursor( Cursor.HAND );
		promptForValue( context, tool, key, false );
	}

	protected void promptForText( CommandContext context, DesignTool tool, String key ) {
		tool.setCursor( Cursor.TEXT );
		promptForValue( context, tool, key, true );
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

	protected void addPreview( DesignTool tool, DesignShape... shapes ) {
		List<DesignShape> shapeList = Arrays.asList( shapes );
		this.preview.addAll( shapeList );
		tool.getAsset().setCaptureUndoChanges( false );
		shapeList.forEach( s -> tool.getCurrentLayer().addShape( s ) );
		shapeList.forEach( s -> s.setPreview( true ) );
	}

	protected void removePreview( DesignTool tool, DesignShape... shapes ) {
		removePreview( tool, Arrays.asList( shapes ) );
	}

	protected void removePreview( DesignTool tool, List<DesignShape> shapeList ) {
		shapeList.forEach( s -> tool.getCurrentLayer().removeShape( s ) );
		shapeList.forEach( s -> s.setPreview( false ) );
		preview.removeAll( shapeList );
	}

	protected Object commitPreview( DesignTool tool ) {
		List<DesignShape> shapes = new ArrayList<>(preview);

		// Clear preview enables capturing undo changes again
		resetPreview( tool );

		// Add the shapes to the layer like normal
		shapes.forEach( s -> tool.getCurrentLayer().addShape( s ) );
		return COMPLETE;
	}

	protected void resetPreview( DesignTool tool ) {
		// The shapes have to be removed before capturing undo changes again
		removePreview( tool, preview );
		tool.getAsset().setCaptureUndoChanges( true );
		preview.clear();
	}

	private void promptForValue( CommandContext context, DesignTool tool, String key, boolean isText ) {
		String text = tool.getProduct().rb().text( BundleKey.PROMPT, key );
		context.submit( tool, new Prompt( text, isText ) );
	}

}
