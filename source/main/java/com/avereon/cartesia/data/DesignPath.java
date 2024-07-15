package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadTransform;
import com.avereon.curve.math.Point;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CustomLog
public class DesignPath extends DesignShape {

	public enum Command {
		MOVE,
		ARC,
		CUBIC,
		LINE,
		QUAD,
		CLOSE
	}

	public static final String PATH = "path";

	public static final String CLOSED = "closed";

	private final List<Element> elements = new ArrayList<>();

	public DesignPath() {
		super( null );
		addModifyingKeys( PATH, CLOSED );
	}

	public DesignPath( Point3D origin ) {
		this();
		if( origin != null ) move( origin.getX(), origin.getY() );
	}

	public DesignPath( DesignPath path ) {
		this();

		List<Element> source = path.getElements();
		Element element = source.getFirst();
		if( element.command() == Command.MOVE ) {
			setOrigin( new Point3D( element.data()[ 0 ], element.data()[ 1 ], 0 ) );
			elements.addAll( path.getElements() );
		} else {
			throw new IllegalArgumentException( "DesignPath does not start with a move command" );
		}
	}

	@Override
	public DesignShape.Type getType() {
		return DesignShape.Type.PATH;
	}

	public List<Element> getElements() {
		return new ArrayList<>( elements );
	}

	@Override
	public double distanceTo( Point3D point ) {
		return Double.NaN;
	}

	@Override
	public double pathLength() {
		return Double.NaN;
	}

	@Override
	public DesignPath cloneShape() {
		return new DesignPath().copyFrom( this, true );
	}

	@Override
	public void apply( CadTransform transform ) {
		try( Txn ignored = Txn.create() ) {
			setOrigin( transform.apply( getOrigin() ) );
			elements.forEach( element -> element.apply( transform ) );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to apply transform" );
		}
	}

	public DesignPath add( DesignPath path ) {
		elements.addAll( path.getElements() );
		return this;
	}

	public DesignPath line( double x, double y ) {
		return line( Point.of( x, y ) );
	}

	public DesignPath line( double[] point ) {
		elements.add( new Element( Command.LINE, point ) );
		return this;
	}

	public DesignPath arc( double x, double y, double rx, double ry, double start, double extent ) {
		elements.add( new Element( Command.ARC, new double[]{ x, y, rx, ry, start, extent } ) );
		return this;
	}

	public DesignPath circle( double x, double y, double r ) {
		elements.add( new Element( Command.ARC, new double[]{ x, y, r, r, -90, 180 } ) );
		elements.add( new Element( Command.ARC, new double[]{ x, y, r, r, 90, 180 } ) );
		return this;
	}

	public DesignPath quad( double bx, double by, double cx, double cy ) {
		elements.add( new Element( Command.QUAD, new double[]{ bx, by, cx, cy } ) );
		return this;
	}

	public DesignPath cubic( double bx, double by, double cx, double cy, double dx, double dy ) {
		elements.add( new Element( Command.CUBIC, new double[]{ bx, by, cx, cy, dx, dy } ) );
		return this;
	}

	public DesignPath close() {
		elements.add( new Element( Command.CLOSE, new double[]{} ) );
		return this;
	}

	public DesignPath move( double x, double y ) {
		if( elements.isEmpty() ) setOrigin( new Point3D( x, y, 0.0 ) );
		elements.add( new Element( Command.MOVE, new double[]{ x, y } ) );
		return this;
	}

	public record Element(DesignPath.Command command, double[] data) {

		public void apply( CadTransform transform ) {
			// Limit the values changed depending on the command
			int count = switch( command ) {
				case MOVE, LINE -> 2;
				case ARC, QUAD -> 4;
				case CUBIC -> 6;
				default -> 0;
			};

			for( int index = 0; index < count; index += 2 ) {
				Point3D point = transform.apply( new Point3D( data[ index ], data[ index + 1 ], 0 ) );
				data[ index ] = point.getX();
				data[ index + 1 ] = point.getY();
			}
		}

	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, PATH );
		// TODO Add elements
		return map;
	}

}
