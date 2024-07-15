package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.curve.math.Geometry;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.Map;

@CustomLog
public class DesignCubic extends DesignShape {

	public static final String CURVE = "curve";

	public static final String CUBIC = "cubic";

	public static final String ORIGIN_CONTROL = "origin-control";

	public static final String POINT_CONTROL = "point-control";

	public static final String POINT = "point";

	private static final String LENGTH = "length";

	public DesignCubic() {
		this( null, null, null, null );
	}

	public DesignCubic( Point3D origin, Point3D originControl, Point3D pointControl, Point3D point ) {
		super( origin );
		addModifyingKeys( ORIGIN_CONTROL, POINT_CONTROL, POINT );
		setOriginControl( originControl );
		setPointControl( pointControl );
		setPoint( point );
	}

	@Override
	public DesignShape.Type getType() {
		return DesignShape.Type.CUBIC;
	}

	public Point3D getOriginControl() {
		return getValue( ORIGIN_CONTROL );
	}

	public DesignShape setOriginControl( Point3D value ) {
		setValue( ORIGIN_CONTROL, value );
		return this;
	}

	public Point3D getPointControl() {
		return getValue( POINT_CONTROL );
	}

	public DesignShape setPointControl( Point3D value ) {
		setValue( POINT_CONTROL, value );
		return this;
	}

	public Point3D getPoint() {
		return getValue( POINT );
	}

	public DesignShape setPoint( Point3D point ) {
		setValue( POINT, point );
		return this;
	}

	@Override
	public double distanceTo( Point3D point ) {
		// TODO Improve DesignCurve.distanceTo()
		// This implementation is a simple estimate based on the origin and point
		double[] a = CadPoints.asPoint( getOrigin() );
		double[] b = CadPoints.asPoint( getPoint() );
		double[] p = CadPoints.asPoint( point );
		return Geometry.linePointDistance( a, b, p );
	}

	@Override
	public double pathLength() {
		return CadGeometry.cubicArcLength( this );
	}

	@Override
	public Map<String, Object> getInformation() {
		return Map.of( ORIGIN, getOrigin(),ORIGIN_CONTROL, getOriginControl(), POINT_CONTROL, getPointControl(), POINT, getPoint(), LENGTH, pathLength() );
	}

	@Override
	public DesignCubic cloneShape() {
		return new DesignCubic().copyFrom( this, true );
	}

	@Override
	public void apply( CadTransform transform ) {
		try( Txn ignored = Txn.create() ) {
			setOrigin( transform.apply( getOrigin() ) );
			setOriginControl( transform.apply( getOriginControl() ) );
			setPointControl( transform.apply( getPointControl() ) );
			setPoint( transform.apply( getPoint() ) );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to apply transform" );
		}
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, CURVE );
		map.putAll( asMap( ORIGIN_CONTROL, POINT_CONTROL, POINT ) );
		return map;
	}

	@Override
	public DesignCubic updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		setOriginControl( ParseUtil.parsePoint3D( (String)map.get( ORIGIN_CONTROL ) ) );
		setPointControl( ParseUtil.parsePoint3D( (String)map.get( POINT_CONTROL ) ) );
		setPoint( ParseUtil.parsePoint3D( (String)map.get( POINT ) ) );
		return this;
	}

	@Override
	public DesignShape updateFrom( DesignShape shape ) {
		super.updateFrom( shape );
		if( !(shape instanceof DesignCubic curve) ) return this;

		try( Txn ignore = Txn.create() ) {
			this.setOriginControl( curve.getOriginControl() );
			this.setPointControl( curve.getPointControl() );
			this.setPoint( curve.getPoint() );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to update curve" );
		}

		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, ORIGIN_CONTROL, POINT_CONTROL, POINT );
	}

}
