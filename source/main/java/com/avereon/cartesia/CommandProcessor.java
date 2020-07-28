package com.avereon.cartesia;

import com.avereon.cartesia.el.CasExpressionParser;
import com.avereon.util.Log;
import com.avereon.util.TextUtil;
import javafx.geometry.Point3D;

import java.util.Objects;
import java.util.Stack;

/**
 * The command processor handles processing commands for a design. It is common
 * for multiple tools on the same asset to work with the command processor
 * through the course of a command. The command processor holds the state for
 * commands "in progress".
 * <p>
 * The way this should work is a command pushes param commands onto the stack
 * for anything that is not available. Those commands are evaluated until there
 * are enough parameters for the original command.
 */
public class CommandProcessor {

	private static final System.Logger log = Log.get();

	private Stack<Command<?>> commandStack;

	private Stack<Object> valueStack;

	private Point3D anchor;

	public CommandProcessor() {
		anchor = new Point3D( 0, 0, 0 );
	}

	public void cancel() {
		commandStack.clear();
		valueStack.clear();
	}

	public void evaluate( String input ) {
		if( TextUtil.isEmpty( input ) ) return;

		Class<Command<?>> commandClass = CommandMap.get( input );
		Point3D point = parsePoint( input );
		// Can/should a number just be a point?
		Double number = parseValue( input );
		String text = input.trim();

		// TODO Handle the input

		if( commandClass != null ) {
			try {
				log.log( Log.WARN, "Command found {0}", commandClass.getName() );
				Command<?> commandInstance = commandClass.getConstructor().newInstance();
			} catch( Exception exception ) {
				exception.printStackTrace();
			}
		}

		// The text needs to be turned into a command
		// The text can be a lettered command to start one
		// ...or it can be a value expression for a point
		// ...or it can be a text value (but maybe text should be wysiwyg)
	}

	void setAnchor( Point3D anchor ) {
		this.anchor = anchor;
	}

	Point3D parsePoint( String input ) {
		input = Objects.requireNonNull( input ).trim();

		boolean relative = false;
		boolean polar = false;

		if( input.charAt( 0 ) == '@' ) {
			input = input.substring( 1 ).trim();
			relative = true;
		}
		if( input.charAt( 0 ) == '>' || input.charAt( 0 ) == '<' ) {
			// Modifier > is for polar coords
			input = input.substring( 1 ).trim();
			polar = true;
		}

		Point3D point = null;
		String[] coords = input.split( "," );
		switch( coords.length ) {
			case 1: {
				point = new Point3D( parseValue( coords[ 0 ] ), 0, 0 );
				break;
			}
			case 2: {
				point = new Point3D( parseValue( coords[ 0 ] ), parseValue( coords[ 1 ] ), 0 );
				break;
			}
			case 3: {
				point = new Point3D( parseValue( coords[ 0 ] ), parseValue( coords[ 1 ] ), parseValue( coords[ 2 ] ) );
				break;
			}
		}

		if( point == null ) return null;
		if( polar ) point = fromPolar( point );
		if( relative ) point = anchor.add( point );

		return point;
	}

	Point3D fromPolar( Point3D point ) {
		double angle = point.getX();
		double radius = point.getY();
		return new Point3D( radius * Math.cos( angle ), radius * Math.sin( angle ), 0 );
	}

	Double parseValue( String text ) {
		CasExpressionParser jep = new CasExpressionParser();
		jep.addStandardConstants();
		jep.addStandardFunctions();
		jep.parseExpression( text );
		return jep.getValue();
	}

	public void pushValue( Object value ) {
		valueStack.push( value );

		// NEXT Check the command on the stack if there are enough parameters to process
		// The command can ask for another value...
		// or cancel
		// or evaluate and return a value
	}

}
