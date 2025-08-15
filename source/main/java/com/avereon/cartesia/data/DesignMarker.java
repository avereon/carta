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

import java.util.List;
import java.util.Map;

@CustomLog
public class DesignMarker extends DesignShape {

	public enum Type {

		DEFAULT {
			public Path getFxPath() {
				return CROSS.getFxPath();
			}

			public DesignPath getDesignPath() {
				return CROSS.getDesignPath();
			}
		},
		CG {
			public Path getFxPath() {
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

			public DesignPath getDesignPath() {
				double r = HALF_SIZE;
				double s = 0.5 * HALF_WIDTH * r;
				double t = r - 2 * s;

				DesignPath path = new DesignPath( new Point3D( 0, 0, 0 ) );
				path.circle( 0, 0, r );

				path.move( 0, 0 );
				path.line( t, 0 );
				path.arc( 0, -t, t, t, 0, 0, 0 );
				path.close();

				path.move( 0, 0 );
				path.line( 0, t );
				path.arc( -t, 0, t, t, 0, 0, 1 );
				path.close();

				return path;
			}

		},
		CIRCLE {
			public Path getFxPath() {
				double r = HALF_SIZE;
				Path path = new Path();
				path.getElements().add( new MoveTo( 0, -r ) );
				path.getElements().add( new ArcTo( r, r, 0, 0, r, false, false ) );
				path.getElements().add( new ArcTo( r, r, 0, 0, -r, false, false ) );
				path.getElements().add( new ClosePath() );
				return path;
			}

			public DesignPath getDesignPath() {
				DesignPath path = new DesignPath();
				path.circle( 0, 0, HALF_SIZE );
				path.close();
				return path;
			}
		},
		CROSS {
			public Path getFxPath() {
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

			public DesignPath getDesignPath() {
				double r = HALF_SIZE;
				double s = HALF_WIDTH * r;
				DesignPath path = new DesignPath( new Point3D( -s, -r, 0 ) );

				path.line( s, -r );
				path.line( s, -s );

				path.line( r, -s );
				path.line( r, s );
				path.line( s, s );

				path.line( s, r );
				path.line( -s, r );
				path.line( -s, s );

				path.line( -r, s );
				path.line( -r, -s );
				path.line( -s, -s );

				path.close();

				return path;
			}
		},
		DIAMOND {
			public Path getFxPath() {
				double r = HALF_SIZE;
				Path path = new Path();
				path.getElements().add( new MoveTo( -r, 0 ) );
				path.getElements().add( new LineTo( 0, r ) );
				path.getElements().add( new LineTo( r, 0 ) );
				path.getElements().add( new LineTo( 0, -r ) );
				path.getElements().add( new ClosePath() );
				return path;
			}

			public DesignPath getDesignPath() {
				double r = HALF_SIZE;
				DesignPath path = new DesignPath( new Point3D( -r, 0, 0 ) );
				path.line( 0, r );
				path.line( r, 0 );
				path.line( 0, -r );
				path.close();
				return path;
			}
		},
		REFERENCE {
			public Path getFxPath() {
				return STAR.getFxPath();
			}

			public DesignPath getDesignPath() {
				return STAR.getDesignPath();
			}
		},
		RETICLE {
			public Path getFxPath() {
				double s = 0.1 * LINE_WIDTH;
				double r = HALF_SIZE;
				double r1 = 0.5 * r + s;
				double r2 = 0.5 * r - s;

				Path path = new Path();

				path.getElements().add( new MoveTo( -s, -r ) );
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

				path.getElements().add( new MoveTo( 0, -r1 ) );
				path.getElements().add( new ArcTo( r1, r1, 0, 0, r1, false, false ) );
				path.getElements().add( new ArcTo( r1, r1, 0, 0, -r1, false, false ) );

				path.getElements().add( new MoveTo( 0, -r2 ) );
				path.getElements().add( new ArcTo( r2, r2, 0, 0, r2, false, true ) );
				path.getElements().add( new ArcTo( r2, r2, 0, 0, -r2, false, true ) );

				path.getElements().add( new ClosePath() );

				return path;
			}

			public DesignPath getDesignPath() {
				double s = 0.1 * LINE_WIDTH;
				double r = HALF_SIZE;
				double r1 = 0.5 * r + s;
				double r2 = 0.5 * r - s;

				DesignPath path = new DesignPath( new Point3D( -s, -r, 0 ) );

				path.line( s, -r );
				path.line( s, -s );

				path.line( r, -s );
				path.line( r, s );
				path.line( s, s );

				path.line( s, r );
				path.line( -s, r );
				path.line( -s, s );

				path.line( -r, s );
				path.line( -r, -s );
				path.line( -s, -s );

				path.move( 0, -r1 );
				path.circle( 0, 0, r1 );
				path.move( 0, -r2 );
				path.circle( 0, 0, r2 );

				path.close();

				return path;
			}
		},
		RING {
			public Path getFxPath() {
				double r = HALF_SIZE * 0.8;
				Path path = new Path();
				path.getElements().addAll( CIRCLE.getFxPath().getElements() );
				path.getElements().add( new MoveTo( 0, -r ) );
				path.getElements().add( new ArcTo( r, r, 0, 0, r, false, true ) );
				path.getElements().add( new ArcTo( r, r, 0, 0, -r, false, true ) );
				path.getElements().add( new ClosePath() );
				return path;
			}

			public DesignPath getDesignPath() {
				double r = HALF_SIZE * 0.8;
				DesignPath path = new DesignPath( CIRCLE.getDesignPath() );
				path.move( 0, -r );
				path.circle( 0, 0, r );
				path.close();
				return path;
			}
		},
		SQUARE {
			public Path getFxPath() {
				double z = HALF_SIZE * Constants.SQRT_ONE_HALF;
				Path path = new Path();
				path.getElements().add( new MoveTo( -z, -z ) );
				path.getElements().add( new LineTo( -z, z ) );
				path.getElements().add( new LineTo( z, z ) );
				path.getElements().add( new LineTo( z, -z ) );
				path.getElements().add( new ClosePath() );
				return path;
			}

			public DesignPath getDesignPath() {
				double z = HALF_SIZE * Constants.SQRT_ONE_HALF;
				DesignPath path = new DesignPath( new Point3D( -z, -z, 0 ) );
				path.line( -z, z );
				path.line( z, z );
				path.line( z, -z );
				path.close();
				return path;
			}
		},
		STAR {
			public Path getFxPath() {
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

			public DesignPath getDesignPath() {
				double r = HALF_SIZE;
				double s = r * 0.5 * (3 - Math.sqrt( 5 ));

				DesignPath path = new DesignPath( (Point3D)null );
				for( int index = 0; index < 10; index++ ) {
					boolean point = index % 2 == 0;
					double alpha = 2 * Math.PI * (index / 10.0) + 0.5 * Math.PI;
					double z = point ? r : s;
					double a = z * Math.cos( alpha );
					double b = z * Math.sin( alpha );
					if( index == 0 ) {
						path.move( a, b );
					} else {
						path.line( a, b );
					}
				}
				path.close();

				return path;
			}
		},
		X {
			public Path getFxPath() {
				double s = Constants.SQRT_ONE_HALF * HALF_SIZE;
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

			public DesignPath getDesignPath() {
				double s = Constants.SQRT_ONE_HALF * HALF_SIZE;
				double t = HALF_WIDTH * s;

				DesignPath path = new DesignPath();
				path.move( 0, -2 * t );
				path.line( s - t, -s - t );
				path.line( s + t, -s + t );

				path.line( 2 * t, 0 );
				path.line( s + t, s - t );
				path.line( s - t, s + t );

				path.line( 0, 2 * t );
				path.line( -s + t, s + t );
				path.line( -s - t, s - t );

				path.line( -2 * t, 0 );
				path.line( -s - t, -s + t );
				path.line( -s + t, -s - t );
				path.close();

				return path;
			}
		};

		private static final double SIZE = 1.0;

		private static final double HALF_SIZE = 0.5 * SIZE;

		private static final double LINE_WIDTH = 0.2;

		private static final double HALF_WIDTH = 0.5 * LINE_WIDTH;

		/**
		 * @return The JavaFX path of the marker
		 * @deprecated In favor of {@link #getDesignPath}
		 */
		@Deprecated
		public abstract Path getFxPath();

		/**
		 * Get the path of the marker.
		 *
		 * @return The {@link DesignPath} of the marker
		 */
		public abstract DesignPath getDesignPath();
	}

	public static final String MARKER = "marker";

	public static final String SIZE = "size";

	public static final String TYPE = "type";

	public static final Type DEFAULT_TYPE = Type.CROSS;

	public static final double DEFAULT_SIZE = 1.0;

	private static final double ZERO_DRAW_WIDTH = 0.0;

	public DesignMarker() {
		this( null );
	}

	public DesignMarker( Point3D origin ) {
		this( origin, "1", DEFAULT_TYPE );
	}

	public DesignMarker( Point3D origin, String size ) {
		this( origin, size, DEFAULT_TYPE );
	}

	public DesignMarker( Point3D origin, Type type ) {
		this( origin, String.valueOf( DEFAULT_SIZE ), type );
	}

	public DesignMarker( Point3D origin, double size, Type type ) {
		this( origin, String.valueOf( size ), type );
	}

	public DesignMarker( Point3D origin, String size, Type type ) {
		super( origin );
		setSize( size );
		setType( type.name() );
		addModifyingKeys( ORIGIN, SIZE, TYPE );
	}

	@Override
	public DesignShape.Type getType() {
		return DesignShape.Type.MARKER;
	}

	public DesignPath getPath() {
		Type type = calcType();
		DesignPath path = type.getDesignPath();

		path.apply( CadTransform.translation( getOrigin() ) );

		return path;
	}

	public List<DesignPath.Step> getSteps() {
		DesignPath path = getPath();
		return path == null ? List.of() : path.getSteps();
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
		String type = getMarkerType();
		if( type == null ) type = DesignMarker.DEFAULT_TYPE.name();
		return DesignMarker.Type.valueOf( type.toUpperCase() );
	}

	/**
	 * Get the marker type. This string should correspond to a known marker type.
	 *
	 * @return The lower case string value of the marker type
	 */
	public String getMarkerType() {
		return getValue( TYPE );
	}

	/**
	 * Set the marker type. This string must correspond to a known marker type.
	 * The value is converted to lower case.
	 *
	 * @param type The marker type
	 * @return The marker
	 */
	public DesignMarker setType( String type ) {
		if( type != null ) type = type.toLowerCase();
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
	public List<Point3D> getReferencePoints() {
		return List.of( getOrigin() );
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

	//	@Override
	//	public Bounds getVisualBounds() {
	//		// Special handling of markers because they are not shapes
	//		Point3D origin = getOrigin();
	//		double size = calcSize();
	//		double halfSize = 0.5 * size;
	//		return new BoundingBox( origin.getX() - halfSize, origin.getY() - halfSize, size, size );
	//	}

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
	public DesignMarker updateFrom( DesignShape shape ) {
		super.updateFrom( shape );
		if( !(shape instanceof DesignMarker marker) ) return this;

		try( Txn ignore = Txn.create() ) {
			this.setSize( marker.getSize() );
			this.setType( marker.getMarkerType() );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to update curve" );
		}

		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN );
	}

}
