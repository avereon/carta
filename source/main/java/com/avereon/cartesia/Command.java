package com.avereon.cartesia;

import com.avereon.cartesia.command.PromptCommand;
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

import java.util.List;

public class Command {

	public static final Object INCOMPLETE = new Object();

	public static final Object COMPLETE = new Object();

	public static final Object INVALID = new Object();

	private static final System.Logger log = Log.get();

	private int step;

	private DesignShape preview;

	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		return null;
	}

	public void cancel( DesignTool tool ) throws Exception {
		if( tool != null ) {
			tool.getCurrentLayer().removeShape( preview );
			tool.getDesign().clearSelected();
			tool.setCursor( Cursor.DEFAULT );
		}
	}

	public boolean isInputCommand() {
		return false;
	}

	public void handle( KeyEvent event ) {}

	public void handle( MouseEvent event ) {}

	protected Object invalid() {
		return INVALID;
	}

	protected Object incomplete() {
		return INCOMPLETE;
	}

	protected Object complete() {
		return COMPLETE;
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
		return shapes.isEmpty() ? DesignShape.NONE : DesignShapeView.getDesignData( shapes.get(0) );
	}

	protected DesignShape findNearestShapeAtPoint( DesignTool tool, Point3D point ) {
		return findNearestShapeAtMouse( tool, tool.worldToScreen( point ) );
	}

	protected DesignShape selectNearestShapeAtMouse( DesignTool tool, Point3D mouse ) {
		List<Shape> shapes = tool.screenPointSelectAndWait( mouse );
		return shapes.isEmpty() ? DesignShape.NONE : DesignShapeView.getDesignData( shapes.get(0) );
	}

	protected DesignShape selectNearestShapeAtPoint( DesignTool tool, Point3D point ) {
		return selectNearestShapeAtMouse( tool, tool.worldToScreen( point ) );
	}

	protected int getStep() {
		return step;
	}

	public void incrementStep() {
		step++;
	}

	protected void setPreview( DesignTool tool, DesignShape preview ) {
		this.preview = preview;
		tool.getAsset().setCaptureUndoChanges( false );
		tool.getCurrentLayer().addShape( preview );
		preview.setPreview( true );
	}

	@SuppressWarnings( "unchecked" )
	protected <T extends DesignShape> T getPreview() {
		return (T)preview;
	}

	protected Object commitPreview( DesignTool tool ) {
		removePreview( tool );
		tool.getCurrentLayer().addShape( preview );
		return complete();
	}

	protected void removePreview( DesignTool tool ) {
		tool.getCurrentLayer().removeShape( preview );
		preview.setPreview( false );
		tool.getAsset().setCaptureUndoChanges( true );
	}

	private void promptForValue( CommandContext context, DesignTool tool, String key, boolean isText ) {
		String prompt = tool.getProduct().rb().text( BundleKey.PROMPT, key );
		context.submit( tool, new PromptCommand( prompt, isText ) );
	}

}
