package com.avereon.cartesia;

import com.avereon.cartesia.command.PromptCommand;
import com.avereon.cartesia.math.Geometry;
import com.avereon.cartesia.math.Maths;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignTool;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;

public class Command {

	public static final Object INCOMPLETE = new Object();

	private static final Object COMPLETE = new Object();

	public Object execute( CommandContext context, DesignTool tool, Object... parameters ) throws Exception {
		return null;
	}

	public boolean isInputCommand() {
		return false;
	}

	public void handle( KeyEvent event ) {}

	public void handle( MouseEvent event ) {}

	public void handle( MouseDragEvent event ) {}

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

	protected Point3D asPoint( Object value, Point3D anchor ) throws Exception {
		if( value instanceof Point3D ) return (Point3D)value;
		return Geometry.parsePoint( String.valueOf( value ), anchor );
	}

	protected void promptForValue( CommandContext context, DesignTool tool, String bundleKey, String key ) {
		String prompt = context.getProduct().rb().text( bundleKey, key );
		context.submit( tool, new PromptCommand( prompt ) );
	}

}
