package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.Map;

@CustomLog
public class DesignBox extends DesignShape {

	public static final String BOX = "box";

	public static final String SIZE = "size";

	public static final String PERIMETER = "perimeter";

	public DesignBox() {
		this( null, null );
	}

	public DesignBox( double x, double y, double w, double h ) {
		this( new Point3D( x, y, 0 ), new Point3D( w, h, 0 ) );
	}

	public DesignBox( Point3D origin, Point3D size ) {
		super( origin );
		addModifyingKeys( SIZE );
		setSize( size );
	}

	@Override
	public Type getType() {
		return Type.BOX;
	}

	public Point3D getSize() {
		return getValue( SIZE );
	}

	public DesignShape setSize( Point3D point ) {
		setValue( SIZE, point );
		return this;
	}

	@Override
	protected Bounds computeBounds() {
		Point3D origin = getOrigin();
		Point3D size = getSize();
		Bounds bounds = new BoundingBox( origin.getX(), origin.getY(), origin.getZ(), size.getX(), size.getY(), size.getZ() );

		return getLocalTransform().apply( bounds );
	}

	@Override
	public double distanceTo( Point3D point ) {
		Point3D origin = getOrigin();
		Point3D size = getSize();

		double x1 = origin.getX();
		double y1 = origin.getY();
		double x2 = origin.getX() + size.getX();
		double y2 = origin.getY() + size.getY();

		double distance = Double.MAX_VALUE;
		distance = Math.min( distance, CadGeometry.linePointDistance( new Point3D( x1, y1, 0 ), new Point3D( x2, y1, 0 ), point ) );
		distance = Math.min( distance, CadGeometry.linePointDistance( new Point3D( x1, y2, 0 ), new Point3D( x2, y2, 0 ), point ) );
		distance = Math.min( distance, CadGeometry.linePointDistance( new Point3D( x1, y1, 0 ), new Point3D( x1, y2, 0 ), point ) );
		distance = Math.min( distance, CadGeometry.linePointDistance( new Point3D( x2, y1, 0 ), new Point3D( x2, y2, 0 ), point ) );

		return distance;
	}

	@Override
	public double pathLength() {
		return getSize().getX() * 2 + getSize().getY() * 2;
	}

	@Override
	public Map<String, Object> getInformation() {
		return Map.of( ORIGIN, getOrigin(), SIZE, getSize(), PERIMETER, pathLength() );
	}

	@Override
	public DesignBox cloneShape() {
		return new DesignBox().copyFrom( this, true );
	}

	@Override
	public void apply( CadTransform transform ) {
		Txn.run( () -> {
			setOrigin( transform.apply( getOrigin() ) );
			setSize( transform.apply( getSize() ) );
		} );
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.put( SHAPE, BOX );
		map.putAll( asMap( SIZE ) );
		return map;
	}

	public DesignBox updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		setSize( ParseUtil.parsePoint3D( (String)map.get( SIZE ) ) );
		return this;
	}

	public DesignShape updateFrom( DesignShape shape ) {
		super.updateFrom( shape );
		if( !(shape instanceof DesignBox box) ) return this;

		try( Txn ignore = Txn.create() ) {
			this.setSize( box.getSize() );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to update curve" );
		}

		return this;
	}

}
