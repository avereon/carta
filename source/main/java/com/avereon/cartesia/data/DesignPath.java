package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadTransform;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

// NOTE Can only add lines, arcs, and curves.

@CustomLog
public class DesignPath extends DesignShape {

	public enum ElementType {
		MOVE,
		ARC,
		LINE,
		CURVE,
		CLOSE
	}

	public static final String ELEMENTS = "elements";

	private static final String PATH_ORDER = "path-order";

	private final AtomicInteger counter;

	private final Comparator<DesignShape> pathOrderComparator;

	public DesignPath() {
		super( null );
		counter = new AtomicInteger();
		pathOrderComparator = new PathOrderComparator();
	}

	public void add( DesignArc arc ) {
		addPathShape( arc );
	}

	public void add( DesignCurve curve ) {
		addPathShape( curve );
	}

	public void add( DesignLine line ) {
		addPathShape( line );
	}

	public List<DesignShape> getPathElements() {
		List<DesignShape> shapes = new ArrayList<>( getValues( ELEMENTS ) );
		shapes.sort( pathOrderComparator );
		return shapes;
	}

	private void addPathShape( DesignShape shape ) {
		shape.setValue( PATH_ORDER, counter.getAndIncrement() );
		addToSet( ELEMENTS, shape );
	}

	@Override
	public double distanceTo( Point3D point ) {
		return Double.NaN;
	}

	@Override
	public double pathLength() {
		return Double.NaN;
	}

	@Override
	public DesignPath cloneShape() {
		return new DesignPath().copyFrom( this, true );
	}

	@Override
	public void apply( CadTransform transform ) {
		try( Txn ignored = Txn.create() ) {
			setOrigin( transform.apply( getOrigin() ) );
			//setOriginControl( transform.apply( getOriginControl() ) );
			//setPointControl( transform.apply( getPointControl() ) );
			//setPoint( transform.apply( getPoint() ) );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to apply transform" );
		}
	}

	private static final class PathOrderComparator implements Comparator<DesignShape> {

		@Override
		public int compare( DesignShape s1, DesignShape s2 ) {
			return ((int)s1.getValue( PATH_ORDER )) - ((int)s2.getValue( PATH_ORDER ));
		}

	}

	private static abstract class PathOperation {}

	private static final class MoveTo extends PathOperation {

		private final Point3D point;

		public MoveTo( Point3D point ) {
			this.point = point;
		}

		public Point3D getPoint() {
			return point;
		}

	}

	private static final class LineTo extends PathOperation {

		private final Point3D point;

		public LineTo( Point3D point ) {
			this.point = point;
		}

		public Point3D getPoint() {
			return point;
		}

	}

	private static final class ArcTo extends PathOperation {

		private final Point3D radius;

		private final double rotate;

		private final Point3D point;

		private final boolean largeArc;

		private final boolean sweep;

		/**
		 * Create a new instance of ArcTo.
		 *
		 * @param radius the arc radii
		 * @param rotate the arc rotation
		 * @param point the arc endpoint
		 * @param largeArc true means that the arc greater than 180 degrees will be drawn
		 * @param sweep true means that the arc will be drawn in the positive angle direction
		 */
		public ArcTo( Point3D radius, double rotate, Point3D point, boolean largeArc, boolean sweep ) {
			this.radius = radius;
			this.rotate = rotate;
			this.point = point;
			this.largeArc = largeArc;
			this.sweep = sweep;
		}

		public Point3D getRadius() {
			return radius;
		}

		public double getRotate() {
			return rotate;
		}

		public Point3D getPoint() {
			return point;
		}

		public boolean isLargeArc() {
			return largeArc;
		}

		public boolean isSweep() {
			return sweep;
		}
	}

	private static final class CurveTo extends PathOperation {

		private final Point3D originControl;

		private final Point3D pointControl;

		private final Point3D point;

		public CurveTo( Point3D originControl, Point3D pointControl, Point3D point ) {
			this.originControl = originControl;
			this.pointControl = pointControl;
			this.point = point;
		}

		public Point3D getOriginControl() {
			return originControl;
		}

		public Point3D getPointControl() {
			return pointControl;
		}

		public Point3D getPoint() {
			return point;
		}

	}

	private static final class Close extends PathOperation {}

}
