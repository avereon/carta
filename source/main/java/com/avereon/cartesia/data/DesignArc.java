package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadOrientation;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.curve.math.Geometry;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.Point3D;
import javafx.scene.shape.ArcType;
import lombok.CustomLog;

import java.util.HashMap;
import java.util.Map;

@CustomLog
public class DesignArc extends DesignEllipse {

	public enum Type {
		CHORD( ArcType.CHORD ),
		OPEN( ArcType.OPEN ),
		PIE( ArcType.ROUND );

		private final ArcType fxArcType;

		Type( ArcType fxArcType ) {
			this.fxArcType = fxArcType;
		}

		public ArcType arcType() {
			return fxArcType;
		}
	}

	public static final String ARC = "arc";

	public static final String START = "start";

	public static final String EXTENT = "extent";

	public static final String TYPE = "type";

	private static final String END = "end";

	private static final String LENGTH = "length";

	private static final String START_POINT = "start-point";

	private static final String MID_POINT = "mid-point";

	private static final String END_POINT = "end-point";

	public DesignArc() {
		this( null, null, null, null, null );
	}

	public DesignArc( Point3D origin, Double radius, Double start, Double extent, Type type ) {
		this( origin, radius, radius, start, extent, type );
	}

	public DesignArc( Point3D origin, Double xRadius, Double yRadius, Double start, Double extent, Type type ) {
		this( origin, xRadius, yRadius, null, start, extent, type );
	}

	public DesignArc( Point3D origin, Double xRadius, Double yRadius, Double rotate, Double start, Double extent, Type type ) {
		super( origin, xRadius, yRadius, rotate );
		addModifyingKeys( START, EXTENT, TYPE );
		setStart( start );
		setExtent( extent );
		setType( type );
		if( type == Type.OPEN ) setFillPaint( null );
	}

	public Double getStart() {
		return getValue( START );
	}

	public DesignArc setStart( Double value ) {
		setValue( START, value );
		return this;
	}

	public Double getExtent() {
		return getValue( EXTENT );
	}

	public DesignArc setExtent( Double value ) {
		setValue( EXTENT, value );
		return this;
	}

	public Double calcMid() {
		return getStart() + 0.5 * getExtent();
	}

	public Double calcEnd() {
		return getStart() + getExtent();
	}

	public Point3D calcStartPoint() {
		return CadGeometry.ellipsePoint360( this, getStart() );
	}

	public Point3D calcMidPoint() {
		return CadGeometry.ellipsePoint360( this, calcMid() );
	}

	public Point3D calcEndPoint() {
		return CadGeometry.ellipsePoint360( this, calcEnd() );
	}

	public DesignArc.Type getType() {
		return getValue( TYPE );
	}

	public DesignArc setType( DesignArc.Type value ) {
		setValue( TYPE, value );
		return this;
	}

	@Override
	public double distanceTo( Point3D point ) {
		// TODO Improve DesignArc.distanceTo()
		// This implementation is a simple estimate based on the origin and radius
		double[] o = CadPoints.asPoint( getOrigin() );
		double[] p = CadPoints.asPoint( point );
		return Math.abs( Geometry.distance( o, p ) - getRadius() );
	}

	@Override
	public double pathLength() {
		// If the arc is circular then use the circle formula
		if( isCircle() ) return getRadius() * Math.abs( Math.toRadians( getExtent() ) );

		// TODO Improve DesignArc.pathLength()
		// TODO Calc the t of the start
		// TODO Calc the t of the start+extent
		// TODO Calc the parametric path length

		return Double.NaN;
	}

	private double t( double angle ) {
		return Math.atan( getXRadius() / getYRadius() * Math.tan( angle ) );
	}

	private double t360( double angle ) {
		return Math.toDegrees( t( Math.toRadians( angle ) ) );
	}

	@Override
	public Map<String, Object> getInformation() {
		Map<String, Object> info = new HashMap<>();
		info.put( ORIGIN, getOrigin() );
		if( isCircle() ) {
			info.put( RADIUS, getRadius() );
		} else {
			info.put( X_RADIUS, getXRadius() );
			info.put( Y_RADIUS, getYRadius() );
		}
		if( getRotate() != null ) info.put( ROTATE, getRotate() );
		info.put( START, getStart() );
		info.put( EXTENT, getExtent() );
		info.put( END, calcEnd() );
		info.put( LENGTH, pathLength() );

		info.put( START_POINT, calcStartPoint() );
		info.put( MID_POINT, calcMidPoint() );
		info.put( END_POINT, calcEndPoint() );
		return info;
	}

	@Override
	public DesignArc cloneShape() {
		return new DesignArc().copyFrom( this, true );
	}

	@Override
	public void apply( CadTransform transform ) {
		CadOrientation newPose = getOrientation().clone().transform( transform );
		double rotate = CadGeometry.angle360( newPose.getRotate() ) - 90;
		double extent = getExtent();
		if( transform.isMirror() ) extent = -extent;

		double oldStart = getStart() + calcRotate();
		Point3D startPoint = transform.apply( getOrigin().add( CadGeometry.polarToCartesian360( new Point3D( 1, oldStart, 0 ) ) ) );
		double newStart = CadGeometry.cartesianToPolar360( startPoint.subtract( transform.apply( getOrigin() ) ) ).getY();
		double rotatedStart = CadGeometry.normalizeAngle360( newStart - rotate );

		try( Txn ignored = Txn.create() ) {
			super.apply( transform );
			setStart( rotatedStart );
			setExtent( extent );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to apply transform" );
		}
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, ARC );
		map.putAll( asMap( START, EXTENT, TYPE ) );
		return map;
	}

	public DesignArc updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( START ) ) setStart( (Double)map.get( START ) );
		if( map.containsKey( EXTENT ) ) setExtent( (Double)map.get( EXTENT ) );
		if( map.containsKey( TYPE ) ) setType( Type.valueOf( ((String)map.get( TYPE )).toUpperCase() ) );
		return this;
	}

	@Override
	public DesignShape updateFrom( DesignShape shape ) {
		super.updateFrom( shape );
		if( !(shape instanceof DesignArc arc) ) return this;

		try( Txn ignore = Txn.create() ) {
			this.setXRadius( arc.getXRadius() );
			this.setYRadius( arc.getYRadius() );
			this.setRotate( arc.getRotate() );
			this.setStart( arc.getStart() );
			this.setExtent( arc.getExtent() );
			this.setType( arc.getType() );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to update curve" );
		}

		return this;
	}

	public void moveEndpoint( Point3D source, Point3D target ) {
		if( source == null ) return;

		// Determine the start point
		Point3D startPoint = CadGeometry.ellipsePoint360( this, getStart() );

		// Determine the target angle
		double theta = CadGeometry.ellipseAngle360( this, target );

		try( Txn ignore = Txn.create() ) {
			double extent;
			if( CadGeometry.areSamePoint( startPoint, source ) ) {
				setStart( theta );
				extent = getExtent() + getStart() - theta;
			} else {
				extent = theta - getStart();
			}
			setExtent( CadGeometry.clampAngle360( extent ) );
		} catch( TxnException exception ) {
			log.atSevere().log( "Unable to trim arc" );
		}
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, X_RADIUS, Y_RADIUS, ROTATE, START, EXTENT, TYPE );
	}

}
