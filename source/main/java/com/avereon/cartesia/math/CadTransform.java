package com.avereon.cartesia.math;

import com.avereon.curve.math.Transform;
import javafx.geometry.Point3D;

import java.nio.DoubleBuffer;

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
		double m00,
		double m01,
		double m02,
		double m03,
		double m10,
		double m11,
		double m12,
		double m13,
		double m20,
		double m21,
		double m22,
		double m23,
		double m30,
		double m31,
		double m32,
		double m33
	) {
		this( new Transform( m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33 ) );
	}

	CadTransform( Transform transform ) {
		this.transform = transform;
	}

	public boolean isMirror() {
		return transform.isMirror();
	}

	public Point3D apply( Point3D vector ) {
		return CadPoints.toFxPoint( transform.apply( CadPoints.asPoint( vector ) ) );
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

	public static CadTransform scale( double scaleX, double scaleY, double scaleZ ) {
		return new CadTransform( Transform.scale( scaleX, scaleY, scaleZ ) );
	}

	public static CadTransform scale( Point3D origin, double scaleX, double scaleY, double scaleZ ) {
		return new CadTransform( Transform.scale( CadPoints.asPoint( origin ), scaleX, scaleY, scaleZ ) );
	}

	public static CadTransform translation( Point3D offset ) {
		return new CadTransform( Transform.translation( CadPoints.asPoint( offset ) ) );
	}

	public static CadTransform translation( double deltaX, double deltaY, double deltaZ ) {
		return new CadTransform( Transform.translation( deltaX, deltaY, deltaZ ) );
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

	public static CadTransform mirror( Point3D origin, Point3D normal, Point3D rotate ) {
		return new CadTransform( Transform.mirror( CadPoints.asPoint( origin ), CadPoints.asPoint( normal ), CadPoints.asPoint( rotate ) ) );
	}

	public static CadTransform localTransform( Point3D origin, Point3D normal, Point3D rotate ) {
		return new CadTransform( Transform.localTransform( CadPoints.asPoint( origin ), CadPoints.asPoint( normal ), CadPoints.asPoint( rotate ) ) );
	}

	public static CadTransform targetTransform( Point3D origin, Point3D normal, Point3D rotate ) {
		return new CadTransform( Transform.targetTransform( CadPoints.asPoint( origin ), CadPoints.asPoint( normal ), CadPoints.asPoint( rotate ) ) );
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
