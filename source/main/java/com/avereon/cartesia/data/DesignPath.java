package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadTransform;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.Point3D;
import lombok.CustomLog;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CustomLog
public class DesignPath extends DesignShape {

	@Getter
	public enum Command {
		MOVE( "M" ),
		ARC( "A" ),
		CUBIC( "C" ),
		LINE( "L" ),
		QUAD( "Q" ),
		CLOSE( "Z" );

		private final String abbreviation;

		Command( String abbreviation ) {
			this.abbreviation = abbreviation;
		}
	}

	public static final String PATH = "path";

	public static final String STEPS = "steps";

	public static final String CLOSED = "closed";

	private final List<Step> steps = new ArrayList<>();

	public DesignPath() {
		super( null );
		addModifyingKeys( CLOSED );
	}

	public DesignPath( Point3D origin ) {
		this();
		if( origin != null ) move( origin.getX(), origin.getY() );
	}

	public DesignPath( DesignPath path ) {
		this();

		List<Step> source = path.getSteps();
		Step step = source.getFirst();
		if( step.command() == Command.MOVE ) {
			setOrigin( new Point3D( step.data()[ 0 ], step.data()[ 1 ], 0 ) );
			steps.addAll( path.getSteps() );
		} else {
			throw new IllegalArgumentException( "DesignPath does not start with a move command" );
		}
	}

	@Override
	public DesignShape.Type getType() {
		return DesignShape.Type.PATH;
	}

	public List<Step> getSteps() {
		return new ArrayList<>( steps );
	}

	public void setSteps( List<Step> steps ) {
		this.steps.clear();
		this.steps.addAll( steps );
		setModified( true );
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
			steps.forEach( element -> element.apply( transform ) );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to apply transform" );
		}
	}

	public DesignPath add( DesignPath path ) {
		steps.addAll( path.getSteps() );
		return this;
	}

	public DesignPath line( double[] point ) {
		return line( point[ 0 ], point[ 1 ] );
	}

	public DesignPath line( double x, double y ) {
		steps.add( new Step( Command.LINE, x, y ) );
		return this;
	}

	public DesignPath arc( double x, double y, double rx, double ry, double start, double extent ) {
		steps.add( new Step( Command.ARC, x, y, rx, ry, start, extent ) );
		return this;
	}

	public DesignPath circle( double x, double y, double r ) {
		arc( x, y, r, r, -90, 180 );
		arc( x, y, r, r, 90, 180 );
		return this;
	}

	public DesignPath quad( double bx, double by, double cx, double cy ) {
		steps.add( new Step( Command.QUAD, bx, by, cx, cy ) );
		return this;
	}

	public DesignPath cubic( double bx, double by, double cx, double cy, double dx, double dy ) {
		steps.add( new Step( Command.CUBIC, bx, by, cx, cy, dx, dy ) );
		return this;
	}

	public DesignPath close() {
		steps.add( new Step( Command.CLOSE ) );
		return this;
	}

	public DesignPath move( double x, double y ) {
		if( steps.isEmpty() ) setOrigin( new Point3D( x, y, 0.0 ) );
		steps.add( new Step( Command.MOVE, x, y ) );
		return this;
	}

	@Override
	protected Map<String, Object> asMap() {
		List<String> steps = this.steps.stream().map( Step::asString ).toList();

		Map<String, Object> map = super.asMap();
		map.put( SHAPE, PATH );
		map.put( STEPS, steps );
		return map;
	}

	public record Step(DesignPath.Command command, double... data) {

		public void apply( CadTransform transform ) {
			// Limit the values changed depending on the command
			int count = switch( command ) {
				case MOVE, LINE -> 2;
				case QUAD -> 4;
				case ARC, CUBIC -> 6;
				default -> 0;
			};

			for( int index = 0; index < count; index += 2 ) {
				Point3D point = transform.apply( new Point3D( data[ index ], data[ index + 1 ], 0 ) );
				data[ index ] = point.getX();
				data[ index + 1 ] = point.getY();
			}
		}

		public String asString() {
			return command.getAbbreviation() + " " + String.join( " ", Arrays.stream( data ).mapToObj( String::valueOf ).toList() );
		}

	}

}
