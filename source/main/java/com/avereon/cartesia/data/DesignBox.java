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

	//	@Override
	//	protected Bounds computeGeometricBounds() {
	//		// Computing the bounds for a box is pretty simple
	//		// because the box fills its bounds exactly
	//
	//		Point3D origin = getOrigin();
	//		Point3D size = getSize();
	//		Bounds bounds = new BoundingBox( origin.getX(), origin.getY(), origin.getZ(), size.getX(), size.getY(), size.getZ() );
	//		return getRotateTransform().apply( bounds );
	//	}

	//	@Override
	//	protected Bounds computeVisualBounds() {
	//		Point3D origin = getOrigin();
	//		Point3D size = getSize();
	//		StrokeLineCap cap = calcDrawCap();
	//		double drawWidth = calcDrawWidth();
	//		double halfWidth = 0.5 * drawWidth;
	//		boolean hasStroke = drawWidth > 0.0;
	//
	//		// Start with the box at the origin on the x-axis
	//		double x1 = 0;
	//		double y1 = 0;
	//		double x2 = size.getX();
	//		double y2 = size.getY();
	//
	//		if( hasStroke && cap == StrokeLineCap.SQUARE ) {
	//			// Adjust the line for the stroke width
	//			StrokeType drawAlign = calcDrawAlign();
	//			if( drawAlign == StrokeType.CENTERED ) {
	//				x1 -= halfWidth;
	//				y1 -= halfWidth;
	//				x2 += halfWidth;
	//				y2 += halfWidth;
	//			} else if( drawAlign == StrokeType.INSIDE ) {
	//				x1 += drawWidth;
	//				y1 += drawWidth;
	//				x2 -= drawWidth;
	//				y2 -= drawWidth;
	//			} else if( drawAlign == StrokeType.OUTSIDE ) {
	//				x1 = -drawWidth;
	//				y1 = -drawWidth;
	//				x2 += drawWidth;
	//				y2 += drawWidth;
	//			}
	//		}
	//
	//		// Calculate the width and height
	//		double width = Math.abs( x2 - x1 );
	//		double height = Math.abs( y2 - y1 );
	//
	//		// Create the initial bounding box
	//		Bounds bounds = new BoundingBox( x1, y1, width, height );
	//
	//		// Create the transform
	//		CadTransform transform = CadTransform.translation( origin ).combine( getRotateTransform() );
	//
	//		// Apply the transform to the bounding box
	//		bounds = transform.apply( bounds );
	//
	//		// If the line cap is round, calculate the bounding box with a different strategy
	//		if( hasStroke && cap == StrokeLineCap.ROUND ) {
	//			//Bounds baseBounds = new BoundingBox( origin.getX(), origin.getY(), size.getX(), size.getY() );
	//			x1 = Math.min( bounds.getMinX(), bounds.getMinX() - halfWidth );
	//			y1 = Math.min( bounds.getMinY(), bounds.getMinY() - halfWidth );
	//			width = Math.max( bounds.getWidth(), bounds.getWidth() + drawWidth );
	//			height = Math.max( bounds.getHeight(), bounds.getHeight() + drawWidth );
	//			bounds = new BoundingBox( x1, y1, width, height );
	//		}
	//
	//		return bounds;
	//	}

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
