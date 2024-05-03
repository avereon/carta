package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
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

	public DesignLine( double x1, double y1, double x2, double y2 ) {
		this( new Point3D( x1, y1, 0 ), new Point3D( x2, y2, 0 ) );
	}

	public DesignLine( double x1, double y1, double z1, double x2, double y2, double z2 ) {
		this( new Point3D( x1, y1, z1 ), new Point3D( x2, y2, z2 ) );
	}

	public DesignLine( Point3D origin, Point3D point ) {
		super( origin );
		addModifyingKeys( POINT );
		setPoint( point );
	}

	@Override
	public DesignShape.Type getType() {
		return DesignShape.Type.LINE;
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

	@Override
	protected Bounds computeBounds() {
		Point3D origin = getOrigin();
		Point3D point = getPoint();
		Bounds bounds = new BoundingBox( origin.getX(), origin.getY(), origin.getZ(), point.getX() - origin.getX(), point.getY() - origin.getY(), point.getZ() - origin.getZ() );
		return getRotateTransform().apply( bounds );
	}

	@Override
	protected Bounds computeVisualBounds() {
		Point3D origin = getOrigin();
		Point3D point = getPoint();
		double length = origin.distance( point );

		// Start with the line on the x-axis, drawn at length
		double x1 = 0;
		double y1 = 0;
		double x2 = length;

		double drawWidth = calcDrawWidth();
		double halfWidth = 0.5 * drawWidth;
		StrokeType drawAlign = calcDrawAlign();
		if( drawAlign == StrokeType.CENTERED ) {
			y1 = halfWidth;
		} else if( drawAlign == StrokeType.INSIDE ) {
			y1 = -drawWidth;
		} else if( drawAlign == StrokeType.OUTSIDE ) {
			y1 = drawWidth;
		}

		if( calcDrawCap() == StrokeLineCap.SQUARE) {
			x1 -= halfWidth;
			x2 += halfWidth;
		}

		// Create the bounding box
		Bounds bounds = new BoundingBox( x1, y1, x2, drawWidth );

		// Create the transform
		double angle = CadGeometry.angle360( point.subtract( origin ) );
		CadTransform transform = CadTransform.rotation( angle );
		transform.combine( CadTransform.translation( origin ) );

		return transform.apply( bounds );
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
