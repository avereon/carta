package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadOrientation;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.curve.math.Geometry;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import com.avereon.util.Log;
import javafx.geometry.Point3D;
import javafx.scene.shape.ArcType;

import java.util.Map;

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

	private static final System.Logger log = Log.get();

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
	public DesignArc clone() {
		log.log( Log.WARN, "this.fill=" + getFillPaint() );
		return new DesignArc().copyFrom( this );
	}

	@Override
	public void apply( CadTransform transform ) {
		CadTransform original = getOrientation().getLocalToTargetTransform();
		CadOrientation newPose = getOrientation().clone().transform( transform );
		CadTransform combined = newPose.getTargetToLocalTransform().combine( transform.combine( original ) );

		Point3D origin = transform.apply( getOrigin() );
		double xRadius = Math.abs( combined.apply( new Point3D( getXRadius(), 0, 0 ) ).getX() );
		double yRadius = Math.abs( combined.apply( new Point3D( 0, getYRadius(), 0 ) ).getY() );
		double rotate = CadGeometry.angle360( newPose.getRotate() ) - 90;
		//double start = getStart();

		try( Txn ignored = Txn.create() ) {
			setOrigin( origin );
			setXRadius( xRadius );
			setYRadius( yRadius );
			//setStart( getStart() + rotate );
			setRotate( rotate );
			if( transform.isMirror() ) setExtent( -getExtent() );
		} catch( TxnException exception ) {
			log.log( Log.WARN, "Unable to apply transform" );
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
	public String toString() {
		return super.toString( ORIGIN, X_RADIUS, Y_RADIUS, ROTATE, START, EXTENT, TYPE );
	}

}
