package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadMath;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.curve.math.Constants;
import com.avereon.curve.math.Geometry;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.Point3D;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import lombok.CustomLog;

import java.util.Map;

@CustomLog
public class DesignMarker extends DesignShape {

	public enum Type {

		CG {
			public Path getPath() {
				double r = HALF_SIZE;
				double s = 0.5 * HALF_WIDTH * r;
				double t = r - 2 * s;

				Path path = new Path();
				path.getElements().add( new MoveTo( 0, -r ) );
				path.getElements().add( new ArcTo( r, r, 0, 0, r, false, false ) );
				path.getElements().add( new ArcTo( r, r, 0, 0, -r, false, false ) );
				path.getElements().add( new ClosePath() );

				path.getElements().add( new MoveTo( 0, 0 ) );
				path.getElements().add( new LineTo( 0, -r + 2 * s ) );
				path.getElements().add( new ArcTo( t, t, 0, r - 2 * s, 0, false, true ) );
				path.getElements().add( new ClosePath() );

				path.getElements().add( new MoveTo( 0, 0 ) );
				path.getElements().add( new LineTo( 0, r - 2 * s ) );
				path.getElements().add( new ArcTo( t, t, 0, -r + 2 * s, 0, false, true ) );
				path.getElements().add( new ClosePath() );

				return path;
			}
		},
		CIRCLE {
			public Path getPath() {
				double r = HALF_SIZE;
				Path path = new Path();
				path.getElements().add( new MoveTo( 0, -r ) );
				path.getElements().add( new ArcTo( r, r, 0, 0, r, false, false ) );
				path.getElements().add( new ArcTo( r, r, 0, 0, -r, false, false ) );
				path.getElements().add( new ClosePath() );
				return path;
			}
		},
		CROSS {
			public Path getPath() {
				double r = HALF_SIZE;
				double s = HALF_WIDTH * r;
				Path path = new Path( new MoveTo( -s, -r ) );
				path.getElements().add( new LineTo( s, -r ) );
				path.getElements().add( new LineTo( s, -s ) );

				path.getElements().add( new LineTo( r, -s ) );
				path.getElements().add( new LineTo( r, s ) );
				path.getElements().add( new LineTo( s, s ) );

				path.getElements().add( new LineTo( s, r ) );
				path.getElements().add( new LineTo( -s, r ) );
				path.getElements().add( new LineTo( -s, s ) );

				path.getElements().add( new LineTo( -r, s ) );
				path.getElements().add( new LineTo( -r, -s ) );
				path.getElements().add( new LineTo( -s, -s ) );

				path.getElements().add( new ClosePath() );
				return path;
			}
		},
		DIAMOND {
			public Path getPath() {
				Path path = new Path();
				path.getElements().add( new MoveTo( -HALF_SIZE, 0 ) );
				path.getElements().add( new LineTo( 0, HALF_SIZE ) );
				path.getElements().add( new LineTo( HALF_SIZE, 0 ) );
				path.getElements().add( new LineTo( 0, -HALF_SIZE ) );
				path.getElements().add( new ClosePath() );
				return path;
			}
		},
		REFERENCE {
			public Path getPath() {
				return STAR.getPath();
			}
		},
		SQUARE {
			public Path getPath() {
				double z = HALF_SIZE * Constants.SQRT_ONE_HALF;
				Path path = new Path();
				path.getElements().add( new MoveTo( -z, -z ) );
				path.getElements().add( new LineTo( -z, z ) );
				path.getElements().add( new LineTo( z, z ) );
				path.getElements().add( new LineTo( z, -z ) );
				path.getElements().add( new ClosePath() );
				return path;
			}
		},
		STAR {
			public Path getPath() {
				double r = HALF_SIZE;
				double s = r * 0.5 * (3 - Math.sqrt( 5 ));

				Path path = new Path();
				for( int index = 0; index < 10; index++ ) {
					boolean point = index % 2 == 0;
					double alpha = 2 * Math.PI * (index / 10.0) + 0.5 * Math.PI;
					double z = point ? r : s;
					double a = z * Math.cos( alpha );
					double b = z * Math.sin( alpha );
					path.getElements().add( index == 0 ? new MoveTo( a, b ) : new LineTo( a, b ) );
				}
				path.getElements().add( new ClosePath() );

				return path;
			}
		},
		X {
			public Path getPath() {
				double r = HALF_SIZE;
				double s = Constants.SQRT_ONE_HALF * r;
				double t = HALF_WIDTH * s;

				Path path = new Path( new MoveTo( 0, -2 * t ) );
				path.getElements().add( new LineTo( s - t, -s - t ) );
				path.getElements().add( new LineTo( s + t, -s + t ) );

				path.getElements().add( new LineTo( 2 * t, 0 ) );
				path.getElements().add( new LineTo( s + t, s - t ) );
				path.getElements().add( new LineTo( s - t, s + t ) );

				path.getElements().add( new LineTo( 0, 2 * t ) );
				path.getElements().add( new LineTo( -s + t, s + t ) );
				path.getElements().add( new LineTo( -s - t, s - t ) );

				path.getElements().add( new LineTo( -2 * t, 0 ) );
				path.getElements().add( new LineTo( -s - t, -s + t ) );
				path.getElements().add( new LineTo( -s + t, -s - t ) );
				path.getElements().add( new ClosePath() );

				return path;
			}
		};

		private static final double SIZE = 1.0;

		private static final double HALF_SIZE = 0.5 * SIZE;

		private static final double LINE_WIDTH = 0.2;

		private static final double HALF_WIDTH = 0.5 * LINE_WIDTH;

		public abstract Path getPath();
	}

	public static final String MARKER = "marker";

	public static final String SIZE = "size";

	public static final String TYPE = "type";

	public static final double DEFAULT_SIZE = 1.0;

	public static final Type DEFAULT_TYPE = Type.CROSS;

	private static final double ZERO_DRAW_WIDTH = 0.0;

	public DesignMarker() {
		this( null );
	}

	public DesignMarker( Point3D origin ) {
		super( origin );
		addModifyingKeys( ORIGIN, SIZE, TYPE );
	}

	public double calcSize() {
		String size = getSize();
		if( size != null ) return CadMath.evalNoException( size );
		return DEFAULT_SIZE;
	}

	public String getSize() {
		return getValue( SIZE );
	}

	public DesignMarker setSize( String size ) {
		setValue( SIZE, size );
		return this;
	}

	public Type calcType() {
		String type = getType();
		if( type == null ) type = DesignMarker.DEFAULT_TYPE.name();
		return DesignMarker.Type.valueOf( type.toUpperCase() );
	}

	public String getType() {
		return getValue( TYPE );
	}

	public DesignMarker setType( String type ) {
		setValue( TYPE, type );
		return this;
	}

	@Override
	public double calcDrawWidth() {
		return ZERO_DRAW_WIDTH;
	}

	@Override
	public Paint calcFillPaint() {
		return calcDrawPaint();
	}

	@Override
	public double distanceTo( Point3D point ) {
		double[] o = CadPoints.asPoint( getOrigin() );
		double[] p = CadPoints.asPoint( point );
		return Geometry.distance( o, p );
	}

	@Override
	public double pathLength() {
		return 0.0;
	}

	@Override
	public Map<String, Object> getInformation() {
		return Map.of( ORIGIN, getOrigin() );
	}

	@Override
	public DesignMarker cloneShape() {
		return new DesignMarker().copyFrom( this, true );
	}

	@Override
	public void apply( CadTransform transform ) {
		try( Txn ignored = Txn.create() ) {
			setOrigin( transform.apply( getOrigin() ) );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to apply transform" );
		}
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, MARKER );
		map.putAll( asMap( SIZE, TYPE ) );
		return map;
	}

	public DesignMarker updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		setSize( (String)map.get( SIZE ) );
		setType( (String)map.get( TYPE ) );
		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN );
	}

}
