package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.transaction.Txn;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.Map;

@CustomLog
public class DesignLine extends DesignShape {

	public static final String LINE = "line";

	public static final String POINT = "point";

	public DesignLine() {
		this( null, null );
	}

	public DesignLine( Point3D origin, Point3D point ) {
		super( origin );
		addModifyingKeys( POINT );
		setPoint( point );
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
		return CadGeometry.linePointDistance( getOrigin(), getPoint(), point );
	}

	@Override
	public DesignLine cloneShape() {
		return new DesignLine().copyFrom( this, true );
	}

	@Override
	public void apply( CadTransform transform ) {
		Txn.run( () -> {
			setOrigin( transform.apply( getOrigin() ) );
			setPoint( transform.apply( getPoint() ) );
		} );
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, LINE );
		map.putAll( asMap( POINT ) );
		return map;
	}

	public DesignLine updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		setPoint( ParseUtil.parsePoint3D( (String)map.get( POINT ) ) );
		return this;
	}

	public void moveEndpoint( Point3D source, Point3D target ) {
		if( CadGeometry.areSamePoint( getOrigin(), source ) ) {
			setOrigin( target );
		} else {
			setPoint( target );
		}
	}

	//	@Override
	//	public String toString() {
	//		return super.toString( ORIGIN, POINT );
	//	}

}
