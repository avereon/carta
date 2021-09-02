package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadOrientation;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.curve.math.Geometry;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.Map;
import java.util.Objects;

@CustomLog
public class DesignEllipse extends DesignShape {

	public static final String CIRCLE = "circle";

	public static final String ELLIPSE = "ellipse";

	public static final String X_RADIUS = "x-radius";

	public static final String Y_RADIUS = "y-radius";

	public static final String ROTATE = "rotate";

	// This is not to be used publicly
	static final String RADIUS = "radius";

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
		addModifyingKeys( X_RADIUS, Y_RADIUS, ROTATE );

		if( Objects.equals( xRadius, yRadius ) ) setRadius( xRadius );
		setXRadius( xRadius );
		setYRadius( yRadius );
		setRotate( rotate );
	}

	public Double getRadius() {
		return getValue( X_RADIUS );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignEllipse> T setRadius( Double value ) {
		try( Txn ignore = Txn.create() ) {
			setValue( X_RADIUS, value );
			setValue( Y_RADIUS, value );
		} catch( TxnException e ) {
			log.atError().withCause( e );
		}
		return (T)this;
	}

	public Double getXRadius() {
		return getValue( X_RADIUS );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignEllipse> T setXRadius( Double value ) {
		setValue( X_RADIUS, value );
		return (T)this;
	}

	public Double getYRadius() {
		return getValue( Y_RADIUS );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignEllipse> T setYRadius( Double value ) {
		setValue( Y_RADIUS, value );
		return (T)this;
	}

	public double calcRotate() {
		return hasKey( ROTATE ) ? getRotate() : 0.0;
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
		Point3D test = CadPoints.toFxPoint( Geometry.polarToCartesian( new double[]{ getXRadius(), getYRadius(), Math.atan2( local.getY(), local.getX() ) } ) );
		return CadGeometry.areSamePoint( new Point3D( local.getX(), local.getY(), 0 ), new Point3D( test.getX(), test.getY(), 0 ) );
	}

	public CadTransform getLocalTransform() {
		return calcLocalTransform( getOrigin(), getXRadius(), getYRadius(), calcRotate() );
	}

	public static CadTransform calcLocalTransform( Point3D center, double xRadius, double yRadius, double rotate ) {
		return CadTransform.scale( 1, xRadius / yRadius, 0 ).combine( calcOrientation( center, rotate ).getTargetToLocalTransform() );
	}

	public CadOrientation getOrientation() {
		return calcOrientation( getOrigin(), calcRotate() );
	}

	public static CadOrientation calcOrientation( Point3D center, double rotate ) {
		return new CadOrientation( center, CadPoints.UNIT_Z, CadGeometry.rotate360( CadPoints.UNIT_Y, rotate ) );
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
	public DesignEllipse cloneShape() {
		return new DesignEllipse().copyFrom( this, true );
	}

	@Override
	public void apply( CadTransform transform ) {
		CadTransform original = getOrientation().getLocalToTargetTransform();
		double oldRotate = CadGeometry.angle360( getOrientation().getRotate() );
		CadOrientation newPose = getOrientation().clone().transform( transform );
		double newRotate = CadGeometry.angle360( newPose.getRotate() );
		CadTransform combined = newPose.getTargetToLocalTransform().combine( transform.combine( original ) );
		double dRotate = newRotate - oldRotate;

		Point3D origin = transform.apply( getOrigin() );
		double xRadius = Math.abs( combined.apply( new Point3D( getXRadius(), 0, 0 ) ).getX() );
		double yRadius = Math.abs( combined.apply( new Point3D( 0, getYRadius(), 0 ) ).getY() );
		double rotate = calcRotate() + dRotate;

		try( Txn ignored = Txn.create() ) {
			setOrigin( origin );
			setXRadius( xRadius );
			setYRadius( yRadius );
			setRotate( rotate );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to apply transform" );
		}
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, Objects.equals( getXRadius(), getYRadius() ) ? CIRCLE : ELLIPSE );
		if( CadGeometry.areSameSize( getXRadius(), getYRadius() ) ) {
			map.put( RADIUS, getValue( X_RADIUS ) );
		} else {
			//map.putAll( asMap( X_RADIUS, Y_RADIUS ) );
			map.put( X_RADIUS, getValue( X_RADIUS ) );
			map.put( Y_RADIUS, getValue( Y_RADIUS ) );
		}
		map.putAll( asMap( ROTATE ) );
		return map;
	}

	public DesignEllipse updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( RADIUS ) ) setXRadius( (Double)map.get( RADIUS ) );
		if( map.containsKey( RADIUS ) ) setYRadius( (Double)map.get( RADIUS ) );
		if( map.containsKey( X_RADIUS ) ) setXRadius( (Double)map.get( X_RADIUS ) );
		if( map.containsKey( Y_RADIUS ) ) setYRadius( (Double)map.get( Y_RADIUS ) );
		if( map.containsKey( ROTATE ) ) setRotate( (Double)map.get( ROTATE ) );
		return this;
	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, X_RADIUS, Y_RADIUS, ROTATE );
	}

}
