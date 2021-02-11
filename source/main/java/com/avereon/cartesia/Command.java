package com.avereon.cartesia;

import com.avereon.cartesia.command.PromptCommand;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.Maths;
import com.avereon.cartesia.math.Shapes;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.util.Log;
import com.avereon.zerra.javafx.Fx;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.function.Supplier;

public class Command {

	public static final Object INCOMPLETE = new Object();

	public static final Object COMPLETE = new Object();

	public static final Object INVALID = new Object();

	private static final System.Logger log = Log.get();

	private DesignShape preview;

	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		return null;
	}

	public void cancel( DesignTool tool ) throws Exception {
		if( tool != null ) {
			tool.getCurrentLayer().removeShape( preview );
			tool.setCursor( Cursor.DEFAULT );
			tool.getDesign().clearSelected();
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
		return Maths.eval( String.valueOf( value ) );
	}

	protected double asDouble( Point3D anchor, Object value ) throws Exception {
		if( value instanceof Double ) return (Double)value;
		if( value instanceof Point3D ) return ((Point3D)value).distance( anchor );
		return Maths.eval( String.valueOf( value ) );
	}

	protected Point3D asPoint( DesignTool tool, Object value, Point3D anchor ) throws Exception {
		if( value instanceof Point3D ) return (Point3D)value;
		return Shapes.parsePoint( String.valueOf( value ), anchor );
	}

	protected void promptForNumber( CommandContext context, DesignTool tool, String bundleKey, String key ) {
		tool.setCursor( tool.getReticle() );
		promptForValue( context, tool, bundleKey, key );
	}

	protected void promptForPoint( CommandContext context, DesignTool tool, String bundleKey, String key ) {
		tool.setCursor( tool.getReticle() );
		promptForValue( context, tool, bundleKey, key );
	}

	protected void promptForText( CommandContext context, DesignTool tool, String bundleKey, String key ) {
		tool.setCursor( Cursor.TEXT );
		promptForValue( context, tool, bundleKey, key );
	}

	protected DesignShape selectNearestShapeAtMouse( CommandContext context, DesignTool tool ) {
		return selectNearestShapeAtPoint( tool, context.getMouse() );
	}

	protected DesignShape selectNearestShapeAtPoint( DesignTool tool, Point3D point ) {
		try {
			List<DesignShape> shapes = FxProducer.get( () -> tool.selectShapes( point ) );
			return Shapes.findNearestShapeToPoint( shapes, point );
		} catch( InterruptedException exception ) {
			log.log( Log.ERROR, exception );
		}

		return DesignShape.NONE;
	}

	private static class FxProducer<T> {

		private final Supplier<T> supplier;

		private T result;

		private boolean flag;

		public FxProducer( Supplier<T> supplier ) {
			this.supplier = supplier;
		}

		public static <T> T get( Supplier<T> supplier ) throws InterruptedException {
			return new FxProducer<>( supplier ).get();
		}

		public synchronized T get() throws InterruptedException {
			if( Fx.isFxThread() ) {
				result = supplier.get();
			} else {
				Fx.run( () -> this.set( supplier.get() ) );
				while( !flag ) {
					this.wait( 1000 );
				}
			}
			return result;
		}

		private synchronized void set( T result ) {
			this.result = result;
			this.flag = true;
			this.notifyAll();
		}

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

	private void promptForValue( CommandContext context, DesignTool tool, String bundleKey, String key ) {
		String prompt = context.getProduct().rb().text( bundleKey, key );
		context.submit( tool, new PromptCommand( prompt ) );
	}

}
