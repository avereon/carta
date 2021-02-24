package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadOrientation;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.curve.math.Geometry;
import com.avereon.curve.math.Vector;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

import java.util.Map;
import java.util.Objects;

public class DesignEllipse extends DesignShape {

	public static final String CIRCLE = "circle";

	public static final String ELLIPSE = "ellipse";

	public static final String RADIUS = "radius";

	public static final String X_RADIUS = "x-radius";

	public static final String Y_RADIUS = "y-radius";

	public static final String ROTATE = "rotate";

	private static final System.Logger log = Log.get();

	public DesignEllipse() {
		this( null, null );
	}

	public DesignEllipse( Point3D origin, Double radius ) {
		this( origin, radius, radius );
	}

	public DesignEllipse( Point3D origin, Double xRadius, Double yRadius ) {
		this( origin, xRadius, yRadius, null );
	}

	public DesignEllipse( Point3D origin, Double xRadius, Double yRadius, Double rotate ) {
		super( origin );
		addModifyingKeys( RADIUS, X_RADIUS, Y_RADIUS, ROTATE );

		if( Objects.equals( xRadius, yRadius ) ) setRadius( xRadius );
		setXRadius( xRadius );
		setYRadius( yRadius );
		setRotate( rotate );
	}

	public Double getRadius() {
		return getValue( RADIUS );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignEllipse> T setRadius( Double value ) {
		try( Txn ignore = Txn.create() ) {
			setValue( RADIUS, value );
			setValue( X_RADIUS, value );
			setValue( Y_RADIUS, value );
		} catch( TxnException e ) {
			log.log( Log.ERROR, e );
		}
		return (T)this;
	}

	public Double getXRadius() {
		return getValue( X_RADIUS, (Double)getValue( RADIUS ) );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignEllipse> T setXRadius( Double value ) {
		setValue( X_RADIUS, value );
		return (T)this;
	}

	public Double getYRadius() {
		return getValue( Y_RADIUS, (Double)getValue( RADIUS ) );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignEllipse> T setYRadius( Double value ) {
		setValue( Y_RADIUS, value );
		return (T)this;
	}

	public Double getRotate() {
		return getValue( ROTATE );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignEllipse> T setRotate( Double value ) {
		setValue( ROTATE, value );
		return (T)this;
	}

	/**
	 * Test if a given point is on the ellipse.
	 *
	 * @param point
	 * @return
	 */
	public boolean isCoincident( Point3D point ) {
		Point3D local = getOrientation().getTargetToLocalTransform().apply( point );
		//Point3D test = getConstructionPoint( getXRadius(), getYRadius(), Math.atan2( local.y, local.x ) );
		Point3D test = CadPoints.toFxPoint( Geometry.polarToCartesian( new double[]{ getXRadius(), getYRadius(), Math.atan2( local.getY(), local.getX() ) } ) );
		return CadGeometry.areSamePoint( new Point3D( local.getX(), local.getY(), 0 ), new Point3D( test.getX(), test.getY(), 0 ) );
	}

	public CadOrientation getOrientation() {
		Double rotate = getRotate();
		double rotateValue = rotate == null ? 0 : rotate;
		Point3D rotateVector = CadPoints.toFxPoint( Vector.rotate( CadPoints.asPoint( CadPoints.UNIT_Y ), rotateValue ) );
		return new CadOrientation( getOrigin(), CadPoints.UNIT_Z, CadPoints.UNIT_Y );
	}

	@Override
	public double distanceTo( Point3D point ) {
		// TODO Improve DesignEllipse.distanceTo()
		// This implementation is a simple estimate based on the origin and radius
		double[] o = CadPoints.asPoint( getOrigin() );
		double[] p = CadPoints.asPoint( point );
		return Math.abs( Geometry.distance( o, p ) - getRadius() );
	}

	@Override
	public void apply( CadTransform transform ) {
		setOrigin( transform.apply( getOrigin()) );
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, Objects.equals( getXRadius(), getYRadius() ) ? CIRCLE : ELLIPSE );
		map.putAll( asMap( RADIUS, X_RADIUS, Y_RADIUS, ROTATE ) );
		return map;
	}

	public DesignEllipse updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( RADIUS ) ) setRadius( (Double)map.get( RADIUS ) );
		if( map.containsKey( X_RADIUS ) ) setXRadius( (Double)map.get( X_RADIUS ) );
		if( map.containsKey( Y_RADIUS ) ) setYRadius( (Double)map.get( Y_RADIUS ) );
		if( map.containsKey( ROTATE ) ) setRotate( (Double)map.get( ROTATE ) );
		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, RADIUS, X_RADIUS, Y_RADIUS, ROTATE );
	}

}
