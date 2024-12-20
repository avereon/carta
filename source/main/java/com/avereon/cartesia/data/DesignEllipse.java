package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadOrientation;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.curve.math.Arithmetic;
import com.avereon.curve.math.Constants;
import com.avereon.curve.math.Geometry;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@CustomLog
public class DesignEllipse extends DesignShape {

	public static final String CIRCLE = "circle";

	public static final String ELLIPSE = "ellipse";

	public static final String RADII = "radii";

	/**
	 * @deprecated Maintained for backward compatibility only
	 */
	@Deprecated
	@SuppressWarnings( "DeprecatedIsStillUsed" )
	private static final String X_RADIUS = "x-radius";

	/**
	 * @deprecated Maintained for backward compatibility only
	 */
	@Deprecated
	@SuppressWarnings( "DeprecatedIsStillUsed" )
	private static final String Y_RADIUS = "y-radius";

	private static final String PERIMETER = "perimeter";

	@Deprecated
	// This is not to be used publicly
	static final String RADIUS = "radius";

	public DesignEllipse() {
		this( null, 0.0 );
	}

	public DesignEllipse( Point3D origin, Double radius ) {
		this( origin, radius, radius );
	}

	public DesignEllipse( Point3D origin, Point3D radii ) {
		this( origin, radii, null );
	}

	public DesignEllipse( Point3D origin, Double xRadius, Double yRadius ) {
		this( origin, xRadius, yRadius, null );
	}

	public DesignEllipse( Point3D origin, Double xRadius, Double yRadius, Double rotate ) {
		this( origin, new Point3D( xRadius, yRadius, 0.0 ), rotate );
	}

	public DesignEllipse( Point3D origin, Point3D radii, Double rotate ) {
		super( origin );
		addModifyingKeys( RADII );
		setRadii( radii );
		setRotate( rotate == null || rotate == 0.0 ? null : rotate.toString() );
	}

	@Override
	public DesignShape.Type getType() {
		return DesignShape.Type.ELLIPSE;
	}

	public Point3D getRadii() {
		return getValue( RADII );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignEllipse> T setRadii( Point3D radii ) {
		setValue( RADII, radii );
		return (T)this;
	}

	public Double getRadius() {
		return getRadii().getX();
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignEllipse> T setRadius( Double value ) {
		setRadii( new Point3D( value, value, 0 ) );
		return (T)this;
	}

	public Double getXRadius() {
		return getRadii().getX();
	}

	public Double getYRadius() {
		return getRadii().getY();
	}

	//	@SuppressWarnings( "unchecked" )
	//	public double calcRotate() {
	//		return hasKey( ROTATE ) ? getRotate() : 0.0;
	//	}
	//
	//	public Double getRotate() {
	//		return getValue( ROTATE );
	//	}
	//
	//	@SuppressWarnings( "unchecked" )
	//	public <T extends DesignEllipse> T setRotate( Double value ) {
	//		if( value != null && CadGeometry.areSameAngle360( 0.0, value ) ) value = null;
	//		setValue( ROTATE, value );
	//		return (T)this;
	//	}

	/**
	 * Test if a given point is on the ellipse.
	 *
	 * @param point
	 * @return
	 */
	public boolean isCoincident( Point3D point ) {
		Point3D local = getOrientation().getWorldToLocalTransform().apply( point );
		Point3D test = CadPoints.toFxPoint( Geometry.polarToCartesian( new double[]{ getXRadius(), getYRadius(), Math.atan2( local.getY(), local.getX() ) } ) );
		return CadGeometry.areSamePoint( new Point3D( local.getX(), local.getY(), 0 ), new Point3D( test.getX(), test.getY(), 0 ) );
	}

	public CadTransform getRotateTransform() {
		return calcLocalTransform( getOrigin(), getXRadius(), getYRadius(), calcRotate() );
	}

	public static CadTransform calcLocalTransform( Point3D center, double xRadius, double yRadius, double rotate ) {
		return CadTransform.scale( 1, xRadius / yRadius, 0 ).combine( calcOrientation( center, rotate ).getWorldToLocalTransform() );
	}

	@Override
	protected Bounds computeGeometricBounds() {
		return CadGeometry.ellipseBounds( this );
	}

	@Override
	public List<Point3D> getReferencePoints() {
		Point3D radii = getRadii();
		Point3D p1 = getOrigin();
		Point3D p2 = p1.add( radii.getX(), 0, 0 );
		Point3D p3 = p1.add( 0, radii.getY(), 0 );
		Point3D p4 = p1.add( -radii.getX(), 0, 0 );
		Point3D p5 = p1.add( 0, -radii.getY(), 0 );

		return CadGeometry.rotate360( p1, calcRotate(), p1, p2, p3, p4, p5 );
	}

	@Override
	public double distanceTo( Point3D point ) {
		// TODO Improve DesignEllipse.distanceTo()
		// This implementation is a simple estimate based on the origin and radius
		return isCircular() ? Math.abs( CadGeometry.distance( getOrigin(), point ) - getRadius() ) : Double.NaN;
	}

	public boolean isCircular() {
		return Geometry.areSameSize( getXRadius(), getYRadius() );
	}

	@Override
	public double pathLength() {
		// If the ellipse is circular then use the circle formula
		if( isCircular() ) return Constants.FULL_CIRCLE * getRadius();

		double a = getXRadius();
		double b = getYRadius();
		double h = ((a - b) * (a - b)) / ((a + b) * (a + b));

		if( Geometry.areSameSize( a, 0.0 ) ) return 4 * b;
		if( Geometry.areSameSize( b, 0.0 ) ) return 4 * a;

		double factor = 0.0;
		for( int index = 0; index < 12; index++ ) {
			factor += pathTerm( index, h );
		}

		return Math.PI * (a + b) * factor;
	}

	private double pathTerm( int iteration, double h ) {
		double b = Arithmetic.bchi( iteration );
		return b * b * Math.pow( h, iteration );
	}

	@Override
	public Map<String, Object> getInformation() {
		Map<String, Object> info = new HashMap<>();
		info.put( ORIGIN, getOrigin() );
		if( isCircular() ) {
			info.put( RADIUS, getRadius() );
		} else {
			info.put( RADII, getRadii() );
		}
		if( getRotate() != null ) info.put( ROTATE, getRotate() );
		info.put( PERIMETER, pathLength() );
		return info;
	}

	@Override
	public DesignEllipse cloneShape() {
		return new DesignEllipse().copyFrom( this, true );
	}

	@Override
	public void apply( CadTransform transform ) {
		CadTransform original = getOrientation().getLocalToWorldTransform();
		CadOrientation newPose = getOrientation().clone().transform( transform );

		// Radii
		CadTransform combined = newPose.getWorldToLocalTransform().combine( transform.combine( original ) );
		double xRadius = Math.abs( combined.apply( new Point3D( getXRadius(), 0, 0 ) ).getX() );
		double yRadius = Math.abs( combined.apply( new Point3D( 0, getYRadius(), 0 ) ).getY() );

		// Rotate
		double oldRotate = CadGeometry.angle360( getOrientation().getRotate() );
		double newRotate = CadGeometry.angle360( newPose.getRotate() );
		double dRotate = newRotate - oldRotate;

		Point3D origin = transform.apply( getOrigin() );
		Point3D radii = new Point3D( xRadius, yRadius, 0 );
		double rotate = calcRotate() + dRotate;

		try( Txn ignored = Txn.create() ) {
			setOrigin( origin );
			setRadii( radii );
			setRotate( String.valueOf( rotate ) );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to apply transform" );
		}
	}

	protected Map<String, Object> asMap() {
		Double xRadius = getXRadius();
		Double yRadius = getYRadius();

		Map<String, Object> map = super.asMap();
		map.put( SHAPE, Objects.equals( xRadius, yRadius ) ? CIRCLE : ELLIPSE );
		map.putAll( asMap( RADII ) );
		map.putAll( asMap( ROTATE ) );

		return map;
	}

	public DesignEllipse updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		if( map.containsKey( RADII ) ) {
			setRadii( ParseUtil.parsePoint3D( (String)map.get( RADII ) ) );
		} else if( map.containsKey( RADIUS ) ) {
			// For backward compatibility
			double radius = (Double)map.get( RADIUS );
			setRadii( new Point3D( radius, radius, 0.0 ) );
		} else if( map.containsKey( X_RADIUS ) && map.containsKey( Y_RADIUS ) ) {
			// For backward compatibility
			setRadii( new Point3D( (Double)map.get( X_RADIUS ), (Double)map.get( Y_RADIUS ), 0 ) );
		}

		return this;
	}

	@Override
	public DesignShape updateFrom( DesignShape shape ) {
		super.updateFrom( shape );
		if( !(shape instanceof DesignEllipse ellipse) ) return this;

		try( Txn ignore = Txn.create() ) {
			this.setRadii( ellipse.getRadii() );
			this.setRotate( ellipse.getRotate() );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to update curve" );
		}

		return this;
	}

	//	@Override
	//	protected Bounds computeGeometricBounds() {
	//		// The bounds of a non-rotated ellipse
	//		Point3D origin = getOrigin();
	//		Point3D radii = getRadii();
	//		double x = origin.getX() - radii.getX();
	//		double y = origin.getY() - radii.getY();
	//		double w = 2 * radii.getX();
	//		double h = 2 * radii.getY();
	//		return new BoundingBox( x, y, w, h );
	//	}

	@Override
	public String toString() {
		return super.toString( ORIGIN, RADII, ROTATE );
	}

}
