package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.Map;

@CustomLog
public class DesignLine extends DesignShape {

	public static final String LINE = "line";

	public static final String POINT = "point";

	public static final String LENGTH = "length";

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
	public double pathLength() {
		return getPoint().distance( getOrigin() );
	}

	@Override
	public Map<String, Object> getInformation() {
		return Map.of( ORIGIN, getOrigin(), POINT, getPoint(), LENGTH, pathLength() );
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

	public DesignShape updateFrom( DesignShape shape ) {
		super.updateFrom( shape );
		if( !(shape instanceof DesignLine line) ) return this;

		try( Txn ignore = Txn.create() ) {
			this.setPoint( line.getPoint() );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to update curve" );
		}

		return this;
	}

	public void moveEndpoint( Point3D source, Point3D target ) {
		if( CadGeometry.areSamePoint( getOrigin(), source ) ) {
			setOrigin( target );
		} else if( CadGeometry.areSamePoint( getPoint(), source ) ) {
			setPoint( target );
		}
	}

	//	@Override
	//	public String toString() {
	//		return super.toString( ORIGIN, POINT );
	//	}

}
