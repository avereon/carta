package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.List;
import java.util.Map;

@CustomLog
public class DesignQuad extends DesignShape {

	public static final String QUAD = "quad";

	public static final String CONTROL = "control";

	public static final String POINT = "point";

	private static final String LENGTH = "length";

	public DesignQuad() {
		this( null, null, null );
	}

	public DesignQuad( Point3D origin, Point3D originControl, Point3D point ) {
		super( origin );
		addModifyingKeys( CONTROL, POINT );
		setControl( originControl );
		setPoint( point );
	}

	@Override
	public Type getType() {
		return Type.QUAD;
	}

	public Point3D getControl() {
		return getValue( CONTROL );
	}

	public DesignShape setControl( Point3D value ) {
		setValue( CONTROL, value );
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
	public List<Point3D> getReferencePoints() {
		return List.of( getOrigin(), getControl(), getPoint() );
	}

	@Override
	public double distanceTo( Point3D point ) {
		// TODO Improve DesignQuad.distanceTo()
		// This implementation is a simple estimate based on the origin and point
		return CadGeometry.linePointDistance( getOrigin(), getPoint(), point );
	}

	@Override
	public double pathLength() {
		return CadGeometry.quadArcLength( this );
	}

	@Override
	public Map<String, Object> getInformation() {
		return Map.of( ORIGIN, getOrigin(), CONTROL, getControl(), POINT, getPoint(), LENGTH, pathLength() );
	}

	@Override
	public DesignQuad cloneShape() {
		return new DesignQuad().copyFrom( this, true );
	}

	@Override
	public void apply( CadTransform transform ) {
		try( Txn ignored = Txn.create() ) {
			setOrigin( transform.apply( getOrigin() ) );
			setControl( transform.apply( getControl() ) );
			setPoint( transform.apply( getPoint() ) );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to apply transform" );
		}
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, QUAD );
		map.putAll( asMap( CONTROL, POINT ) );
		return map;
	}

	@Override
	public DesignQuad updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		setControl( ParseUtil.parsePoint3D( (String)map.get( CONTROL ) ) );
		setPoint( ParseUtil.parsePoint3D( (String)map.get( POINT ) ) );
		return this;
	}

	@Override
	public DesignShape updateFrom( DesignShape shape ) {
		super.updateFrom( shape );
		if( !(shape instanceof DesignQuad curve) ) return this;

		try( Txn ignore = Txn.create() ) {
			this.setControl( curve.getControl() );
			this.setPoint( curve.getPoint() );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to update curve" );
		}

		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, CONTROL, POINT );
	}

}
