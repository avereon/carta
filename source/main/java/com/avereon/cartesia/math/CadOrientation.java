package com.avereon.cartesia.math;

import com.avereon.curve.math.Orientation;
import com.avereon.curve.math.Transform;
import javafx.geometry.Point3D;

public class CadOrientation {

	private final Orientation orientation;

	public CadOrientation() {
		this.orientation = new Orientation();
	}

	public CadOrientation( Point3D origin ) {
		this.orientation = new Orientation( CadPoints.asPoint( origin ) );
	}

	public CadOrientation( Point3D origin, Point3D normal ) {
		this.orientation = new Orientation( CadPoints.asPoint( origin ), CadPoints.asPoint( normal ) );
	}

	public CadOrientation( Point3D origin, Point3D normal, Point3D rotate ) {
		this.orientation = new Orientation( CadPoints.asPoint( origin ), CadPoints.asPoint( normal ), CadPoints.asPoint( rotate ) );
	}

	private CadOrientation( Orientation orientation ) {
		this.orientation = orientation;
	}

	public Point3D getOrigin() {
		return CadPoints.toFxPoint( orientation.getOrigin() );
	}

	public void setOrigin( Point3D origin ) {
		orientation.setOrigin( CadPoints.asPoint( origin ) );
	}

	public Point3D getNormal() {
		return CadPoints.toFxPoint( orientation.getNormal() );
	}

	public void setNormal( Point3D normal ) {
		orientation.setNormal( CadPoints.asPoint( normal ) );
	}

	public void setNormalCorrectRotate( Point3D normal ) {
		orientation.setNormalCorrectRotate( CadPoints.asPoint( normal ) );
	}

	public Point3D getRotate() {
		return CadPoints.toFxPoint( orientation.getRotate() );
	}

	public void setRotate( Point3D rotate ) {
		orientation.setRotate( CadPoints.asPoint( rotate ) );
	}

	public void set( CadOrientation orientation ) {
		this.orientation.set( orientation.orientation );
	}

	public CadOrientation getPose() {
		return new CadOrientation( Point3D.ZERO, getNormal(), getRotate() );
	}

	public void setPose( Point3D normal, Point3D rotate ) {
		orientation.setPose( CadPoints.asPoint( normal ), CadPoints.asPoint( rotate ) );
	}

	public void set( Point3D origin, Point3D normal, Point3D rotate ) {
		orientation.set( CadPoints.asPoint( origin ), CadPoints.asPoint( normal ), CadPoints.asPoint( rotate ) );
	}

	public void setRotationAngles( double xrotation, double yrotation, double zrotation ) {
		orientation.setRotationAngles( xrotation, yrotation, zrotation );
	}

	public void transform( Transform transform ) {
		orientation.transform( transform );
	}

	public void transformOrigin( Transform transform ) {
		orientation.transformOrigin( transform );
	}

	public void transformAxes( Transform transform ) {
		orientation.transformAxes( transform );
	}

	public CadTransform getLocalToTargetTransform() {
		return new CadTransform( orientation.getLocalToTargetTransform() );
	}

	public CadTransform getTargetToLocalTransform() {
		return new CadTransform( orientation.getTargetToLocalTransform() );
	}

	@Override
	public int hashCode() {
		return orientation.hashCode();
	}

	@Override
	public boolean equals( Object object ) {
		if( !(object instanceof CadOrientation) ) return false;
		return this.orientation.equals( ((CadOrientation)object).orientation );
	}

	public String toJson() {return orientation.toJson();}

	public static CadOrientation fromThreePoints( Point3D origin, Point3D xaxis, Point3D point ) {
		return new CadOrientation( Orientation.fromThreePoints( CadPoints.asPoint( origin ), CadPoints.asPoint( xaxis ), CadPoints.asPoint( point ) ) );
	}

	public static boolean areGeometricallyEqual( CadOrientation a, CadOrientation b ) {return Orientation.areGeometricallyEqual( a.orientation, b.orientation );}

}
