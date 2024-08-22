package com.avereon.cartesia.math;

import com.avereon.curve.math.Point;
import com.avereon.curve.math.Transform;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;

import java.nio.DoubleBuffer;

import static com.avereon.curve.math.Vector.UNIT_Z;

public class CadTransform {

	private final Transform transform;

	/**
	 * See {@link Transform}
	 */
	public CadTransform( double[][] matrix ) {
		this( new Transform( matrix ) );
	}

	/**
	 * See {@link Transform}
	 */
	public CadTransform(
		double m00, double m01, double m02, double m03, double m10, double m11, double m12, double m13, double m20, double m21, double m22, double m23, double m30, double m31, double m32, double m33
	) {
		this( new Transform( m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33 ) );
	}

	CadTransform( Transform transform ) {
		this.transform = transform;
	}

	public boolean isMirror() {
		return transform.isMirror();
	}

	public final Point3D apply( Point3D vector ) {
		return CadPoints.toFxPoint( transform.apply( CadPoints.asPoint( vector ) ) );
	}

	public final Bounds apply( Bounds bounds ) {
		Point3D a = apply( new Point3D( bounds.getMinX(), bounds.getMinY(), 0 ) );
		Point3D b = apply( new Point3D( bounds.getMaxX(), bounds.getMinY(), 0 ) );
		Point3D c = apply( new Point3D( bounds.getMinX(), bounds.getMaxY(), 0 ) );
		Point3D d = apply( new Point3D( bounds.getMaxX(), bounds.getMaxY(), 0 ) );

		double x = min4( a.getX(), b.getX(), c.getX(), d.getX() );
		double y = min4( a.getY(), b.getY(), c.getY(), d.getY() );
		double w = max4( a.getX(), b.getX(), c.getX(), d.getX() ) - x;
		double h = max4( a.getY(), b.getY(), c.getY(), d.getY() ) - y;

		return new BoundingBox( x, y, w, h );
	}

	private double min4( double a, double b, double c, double d ) {
		return Math.min( Math.min( Math.min( a, b ), c ), d );
	}

	private double max4( double a, double b, double c, double d ) {
		return Math.max( Math.max( Math.max( a, b ), c ), d );
	}

	public Point3D applyDirection( Point3D vector ) {
		return CadPoints.toFxPoint( transform.applyDirection( CadPoints.asPoint( vector ) ) );
	}

	public Point3D applyXY( Point3D vector ) {
		return CadPoints.toFxPoint( transform.applyXY( CadPoints.asPoint( vector ) ) );
	}

	public double applyZ( Point3D vector ) {
		return transform.applyZ( CadPoints.asPoint( vector ) );
	}

	/**
	 * Combines this transform with another transform.
	 *
	 * @param transform The transform to combine with this transform
	 * @return A new transform that is the combination of this transform and the given transform
	 */
	public CadTransform combine( CadTransform transform ) {
		return new CadTransform( this.transform.combine( transform.transform ) );
	}

	public CadTransform inverse() {
		return new CadTransform( transform.inverse() );
	}

	public DoubleBuffer getMatrix() {
		return transform.getMatrix();
	}

	public static CadTransform identity() {
		return new CadTransform( Transform.identity() );
	}

	public static CadTransform scale( double scale ) {
		return scale( scale, scale, scale );
	}

	public static CadTransform scale( double scaleX, double scaleY, double scaleZ ) {
		return new CadTransform( Transform.scale( scaleX, scaleY, scaleZ ) );
	}

	public static CadTransform scale( Point3D origin, double scale ) {
		return scale( origin, scale, scale, scale );
	}

	public static CadTransform scale( Point3D origin, double scaleX, double scaleY, double scaleZ ) {
		return new CadTransform( Transform.scale( CadPoints.asPoint( origin ), scaleX, scaleY, scaleZ ) );
	}

	/**
	 * Create a transform that scales geometry in both the x and y directions.
	 *
	 * @param anchor The anchor point
	 * @param source The source point
	 * @param target The target point
	 * @return The scale transform
	 */
	public static CadTransform scale( Point3D anchor, Point3D source, Point3D target ) {
		Point3D base = source.subtract( anchor );
		Point3D stretch = target.subtract( anchor );

		// Create an orientation such that the x-axis is aligned with the base
		CadOrientation orientation = new CadOrientation( anchor, CadPoints.UNIT_Z, base );

		double scale = stretch.dotProduct( base ) / (base.magnitude() * base.magnitude());

		return orientation.getLocalToWorldTransform().combine( scale( scale, scale, 1 ) ).combine( orientation.getWorldToLocalTransform() );
	}

	/**
	 * Create a transform that scales geometry in only one direction, aligned with
	 * the vector defined from the anchor point to the source point.
	 *
	 * @param anchor The anchor point
	 * @param source The source point
	 * @param target The target point
	 * @return The squish transform
	 */
	public static CadTransform squish( Point3D anchor, Point3D source, Point3D target ) {
		// This implementation uses a rotate/scale/-rotate transform
		Point3D base = source.subtract( anchor );
		Point3D stretch = target.subtract( anchor );

		// Create an orientation such that the z-axis is aligned with the base
		CadOrientation orientation = new CadOrientation( anchor, base );

		double scale = stretch.dotProduct( base ) / (base.magnitude() * base.magnitude());

		return orientation.getLocalToWorldTransform().combine( CadTransform.scale( 1, 1, scale ) ).combine( orientation.getWorldToLocalTransform() );
	}

	public static CadTransform translation( Point3D offset ) {
		return new CadTransform( Transform.translation( CadPoints.asPoint( offset ) ) );
	}

	public static CadTransform translation( double deltaX, double deltaY, double deltaZ ) {
		return new CadTransform( Transform.translation( deltaX, deltaY, deltaZ ) );
	}

	public static CadTransform rotation( double angle ) {
		return new CadTransform( Transform.rotation( UNIT_Z, Math.toRadians( angle ) ) );
	}

	public static CadTransform rotation( double x, double y, double angle ) {
		return new CadTransform( Transform.rotation( Point.of( x, y, 0 ), UNIT_Z, Math.toRadians( angle ) ) );
	}

	public static CadTransform rotation( Point3D axis, double angle ) {
		return new CadTransform( Transform.rotation( CadPoints.asPoint( axis ), Math.toRadians( angle ) ) );
	}

	public static CadTransform rotation( Point3D origin, Point3D axis, double angle ) {
		return new CadTransform( Transform.rotation( CadPoints.asPoint( origin ), CadPoints.asPoint( axis ), Math.toRadians( angle ) ) );
	}

	public static CadTransform xrotation( double angle ) {
		return new CadTransform( Transform.xrotation( Math.toRadians( angle ) ) );
	}

	public static CadTransform yrotation( double angle ) {
		return new CadTransform( Transform.yrotation( Math.toRadians( angle ) ) );
	}

	public static CadTransform zrotation( double angle ) {
		return new CadTransform( Transform.zrotation( Math.toRadians( angle ) ) );
	}

	public static CadTransform mirror( Point3D origin, Point3D point ) {
		return new CadTransform( Transform.mirror( CadPoints.asPoint( origin ), CadPoints.asPoint( point ), UNIT_Z ) );
	}

	public static CadTransform mirror( Point3D origin, Point3D point, Point3D normal ) {
		return new CadTransform( Transform.mirror( CadPoints.asPoint( origin ), CadPoints.asPoint( point ), CadPoints.asPoint( normal ) ) );
	}

	public static CadTransform localTransform( Point3D origin, Point3D normal, Point3D rotate ) {
		return new CadTransform( Transform.localTransform( CadPoints.asPoint( origin ), CadPoints.asPoint( normal ), CadPoints.asPoint( rotate ) ) );
	}

	public static CadTransform targetTransform( Point3D origin, Point3D normal, Point3D rotate ) {
		return new CadTransform( Transform.worldTransform( CadPoints.asPoint( origin ), CadPoints.asPoint( normal ), CadPoints.asPoint( rotate ) ) );
	}

	public static CadTransform ortho( double left, double right, double bottom, double top, double near, double far ) {
		return new CadTransform( Transform.ortho( left, right, bottom, top, near, far ) );
	}

	public static CadTransform frustrum( double left, double right, double bottom, double top, double near, double far ) {
		return new CadTransform( Transform.frustrum( left, right, bottom, top, near, far ) );
	}

	public static CadTransform perspective( double distance ) {
		return new CadTransform( Transform.perspective( distance ) );
	}

	@Override
	public boolean equals( Object object ) {
		if( !(object instanceof CadTransform) ) return false;
		CadTransform that = (CadTransform)object;
		return transform.equals( that.transform );
	}

	@Override
	public int hashCode() {
		return transform.hashCode();
	}

	@Override
	public String toString() {
		return transform.toString();
	}

	public final double[][] getMatrixArray() {
		return transform.getMatrixArray();
	}

	Transform getTransform() {
		return transform;
	}

}
