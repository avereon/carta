package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.curve.math.Geometry;
import com.avereon.curve.math.Point;
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
		M( "Move" ),
		A( "Arc" ),
		B( "Cubic" ),
		L( "Line" ),
		Q( "Quad" ),
		Z( "Close" );

		private final String titile;

		Command( String name ) {
			this.titile = name;
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
		if( step.command() == Command.M ) {
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
		double distance = Double.MAX_VALUE;
		double[] gPoint = CadPoints.asPoint( point );
		double[] start = Point.of();
		double[] prior = Point.of();

		for( Step step : steps ) {
			switch( step.command() ) {
				case M -> {
					distance = Math.min( distance, Geometry.distance( gPoint, Point.of( step.data()[ 0 ], step.data()[ 1 ] ) ) );
					start = Point.of( step.data()[ 0 ], step.data()[ 1 ] );
					prior = start;
				}
				case L -> {
					distance = Math.min( distance, Geometry.pointLineDistance( gPoint, prior, Point.of( step.data()[ 0 ], step.data()[ 1 ] ) ) );
					prior = Point.of( step.data()[ 0 ], step.data()[ 1 ] );
				}
				case A -> {
					distance = Math.min( distance,
						Geometry.pointArcDistance( gPoint, Point.of( step.data()[ 0 ], step.data()[ 1 ] ), Point.of( step.data()[ 2 ], step.data()[ 3 ] ), 0.0, step.data()[ 4 ], step.data()[ 5 ] )
					);
					prior = Geometry.arcEndPoints( Point.of( step.data()[ 0 ], step.data()[ 1 ] ), Point.of( step.data()[ 2 ], step.data()[ 3 ] ), 0.0, step.data()[ 4 ], step.data()[ 5 ] )[ 1 ];
				}
				case Q -> {
					// FIXME Calculate point quad distance
					prior = Point.of( step.data()[ 2 ], step.data()[ 3 ] );
				}
				case B -> {
					// FIXME Calculate point cubic distance
					prior = Point.of( step.data()[ 4 ], step.data()[ 5 ] );
				}
				case Z -> {
					distance = Math.min( distance, Geometry.pointLineDistance( gPoint, prior, start ) );
					prior = start;
				}
			}
		}

		return distance;
	}

	@Override
	public double pathLength() {
		double length = 0;
		double[] start = Point.of();
		double[] prior = Point.of();
		for( Step step : steps ) {
			switch( step.command() ) {
				case M -> {
					start = Point.of( step.data()[ 0 ], step.data()[ 1 ] );
					prior = start;
				}
				case L -> {
					length += Geometry.distance( prior, Point.of( step.data()[ 0 ], step.data()[ 1 ] ) );
					prior = Point.of( step.data()[ 0 ], step.data()[ 1 ] );
				}
				case A -> {
					length += Geometry.arcLength( Point.of( step.data()[ 0 ], step.data()[ 1 ] ), Point.of( step.data()[ 2 ], step.data()[ 3 ] ), 0.0, step.data()[ 4 ], step.data()[ 5 ] );
					prior = Geometry.arcEndPoints( Point.of( step.data()[ 0 ], step.data()[ 1 ] ), Point.of( step.data()[ 2 ], step.data()[ 3 ] ), 0.0, step.data()[ 4 ], step.data()[ 5 ] )[ 1 ];
				}
				case Q -> {
					length += Geometry.quadArcLength( prior, Point.of( step.data()[ 0 ], step.data()[ 1 ] ), Point.of( step.data()[ 2 ], step.data()[ 3 ] ) );
					prior = Point.of( step.data()[ 2 ], step.data()[ 3 ] );
				}
				case B -> {
					length += Geometry.cubicArcLength( prior, Point.of( step.data()[ 0 ], step.data()[ 1 ] ), Point.of( step.data()[ 2 ], step.data()[ 3 ] ), Point.of( step.data()[ 4 ], step.data()[ 5 ] ) );
					prior = Point.of( step.data()[ 4 ], step.data()[ 5 ] );
				}
				case Z -> {
					length += Geometry.distance( prior, start );
					prior = start;
				}
			}
		}

		return length;
	}

	@Override
	public DesignPath cloneShape() {
		return new DesignPath().copyFrom( this, true );
	}

	@Override
	public void apply( CadTransform transform ) {
		try( Txn ignored = Txn.create() ) {
			setOrigin( transform.apply( getOrigin() ) );
			steps.forEach( step -> step.apply( transform ) );
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
		steps.add( new Step( Command.L, x, y ) );
		return this;
	}

	/**
	 * Add an arc to the path.
	 *
	 * @param x The x coordinate of the end point
	 * @param y The y coordinate of the end point
	 * @param rx The x radius of the ellipse
	 * @param ry The y radius of the ellipse
	 * @param rotate The rotate angle of the ellipse
	 * @param largeArc The large arc flag - determines if the arc should be
	 * greater than or less than 180 degrees:
	 * 0 = less than or equal to 180 degrees, 1 = greater than 180 degrees
	 * @param sweep The sweep flag - determines if the arc should begin moving at
	 * positive angles or negative ones:
	 * 0 = positive, 1 = negative
	 * @return The path
	 */
	public DesignPath arc( double x, double y, double rx, double ry, double rotate, double largeArc, double sweep ) {
		steps.add( new Step( Command.A, x, y, rx, ry, rotate, largeArc, sweep ) );
		return this;
	}

	/**
	 * Add a circle to the path. This implementation uses a move and two arc
	 * commands to create the circle around the y-axis.
	 *
	 * @param x The x coordinate of the center of the circle
	 * @param y The y coordinate of the center of the circle
	 * @param r The radius of the circle
	 * @return The path
	 */
	public DesignPath circle( double x, double y, double r ) {
		move( x, y - r );
		arc( x, y + r, r, r, 0, 0, 0 );
		arc( x, y - r, r, r, 0, 0, 0 );
		return this;
	}

	public DesignPath quad( double bx, double by, double cx, double cy ) {
		steps.add( new Step( Command.Q, bx, by, cx, cy ) );
		return this;
	}

	public DesignPath cubic( double bx, double by, double cx, double cy, double dx, double dy ) {
		steps.add( new Step( Command.B, bx, by, cx, cy, dx, dy ) );
		return this;
	}

	public DesignPath close() {
		steps.add( new Step( Command.Z ) );
		return this;
	}

	public DesignPath move( double x, double y ) {
		if( steps.isEmpty() ) setOrigin( new Point3D( x, y, 0.0 ) );
		steps.add( new Step( Command.M, x, y ) );
		return this;
	}

	@Override
	protected Map<String, Object> asMap() {
		List<String> steps = this.steps.stream().map( Step::marshall ).toList();

		Map<String, Object> map = super.asMap();
		map.put( SHAPE, PATH );
		map.put( STEPS, steps );
		return map;
	}

	public record Step(DesignPath.Command command, double... data) {

		private static final String DELIMITER = " ";

		public void apply( CadTransform transform ) {
			// Transform points
			int count = switch( command ) {
				case M, L, A -> 2;
				case Q -> 4;
				case B -> 6;
				default -> 0;
			};
			for( int index = 0; index < count; index += 2 ) {
				Point3D point = transform.apply( new Point3D( data[ index ], data[ index + 1 ], 0 ) );
				data[ index ] = point.getX();
				data[ index + 1 ] = point.getY();
			}

			// TODO Transform distances (like radii)
			// TODO Transform angles (like rotate)
		}

		public String marshall() {
			return (command.name() + DELIMITER + String.join( DELIMITER, Arrays.stream( data ).mapToObj( String::valueOf ).toList() )).trim();
		}

		public static Step unmarshall( String string ) {
			String[] parts = string.split( DELIMITER );
			Command command = Command.valueOf( parts[ 0 ].toUpperCase() );
			String[] data = Arrays.copyOfRange( parts, 1, parts.length );
			return new Step( command, Arrays.stream( data ).mapToDouble( Double::parseDouble ).toArray() );
		}

	}

}
