package com.avereon.cartesia.tool;

import com.avereon.cartesia.math.CadMath;
import com.avereon.data.Node;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;

@SuppressWarnings( "UnusedReturnValue" )
public class DesignWorkplane extends Node {

	public static final double DEFAULT_BOUNDARY_X = 10.0;

	public static final double DEFAULT_BOUNDARY_Y = 10.0;

	public static final String DEFAULT_ORIGIN = "0,0,0";

	public static final String DEFAULT_MAJOR_GRID_SIZE = "1.0";

	public static final String DEFAULT_MINOR_GRID_SIZE = "0.5";

	public static final String DEFAULT_SNAP_GRID_SIZE = "0.1";

	public static final Color DEFAULT_AXIS_COLOR = Color.web( "#80a0d060" );

	public static final Color DEFAULT_MAJOR_GRID_COLOR = Color.web( "#80a0d020" );

	public static final Color DEFAULT_MINOR_GRID_COLOR = Color.web( "#80a0d010" );

	public static final String ORIGIN = "origin";

	public static final String BOUNDARY_X1 = "boundary-x1";

	public static final String BOUNDARY_Y1 = "boundary-y1";

	public static final String BOUNDARY_X2 = "boundary-x2";

	public static final String BOUNDARY_Y2 = "boundary-y2";

	public static final String MAJOR_GRID_X = "major-grid-x";

	public static final String MAJOR_GRID_Y = "major-grid-y";

	public static final String MAJOR_GRID_Z = "major-grid-z";

	public static final String MINOR_GRID_X = "minor-grid-x";

	public static final String MINOR_GRID_Y = "minor-grid-y";

	public static final String MINOR_GRID_Z = "minor-grid-z";

	public static final String SNAP_GRID_X = "snap-grid-x";

	public static final String SNAP_GRID_Y = "snap-grid-y";

	public static final String SNAP_GRID_Z = "snap-grid-z";

	private double majorGridX;

	private double majorGridY;

	private double majorGridZ;

	private double minorGridX;

	private double minorGridY;

	private double minorGridZ;

	private double snapGridX;

	private double snapGridY;

	private double snapGridZ;

	public DesignWorkplane() {
		this( -DEFAULT_BOUNDARY_X,
			-DEFAULT_BOUNDARY_Y,
			DEFAULT_BOUNDARY_X,
			DEFAULT_BOUNDARY_Y,
			DEFAULT_MAJOR_GRID_SIZE,
			DEFAULT_MINOR_GRID_SIZE,
			DEFAULT_SNAP_GRID_SIZE
		);
	}

	public DesignWorkplane(
		double boundaryX1, double boundaryY1, double boundaryX2, double boundaryY2, String majorGrid, String minorGrid, String snapGrid
	) {
		this( DEFAULT_ORIGIN,
			boundaryX1,
			boundaryY1,
			boundaryX2,
			boundaryY2,
			majorGrid,
			majorGrid,
			majorGrid,
			minorGrid,
			minorGrid,
			minorGrid,
			snapGrid,
			snapGrid,
			snapGrid
		);
	}

	public DesignWorkplane(
		double boundaryX1,
		double boundaryY1,
		double boundaryX2,
		double boundaryY2,
		String majorGridX,
		String majorGridY,
		String minorGridX,
		String minorGridY,
		String snapGridX,
		String snapGridY
	) {
		this( DEFAULT_ORIGIN,
			boundaryX1,
			boundaryY1,
			boundaryX2,
			boundaryY2,
			majorGridX,
			majorGridY,
			DEFAULT_MAJOR_GRID_SIZE,
			minorGridX,
			minorGridY,
			DEFAULT_MINOR_GRID_SIZE,
			snapGridX,
			snapGridY,
			DEFAULT_SNAP_GRID_SIZE
		);
	}

	public DesignWorkplane(
		String origin,
		double boundaryX1,
		double boundaryY1,
		double boundaryX2,
		double boundaryY2,
		String majorGridX,
		String majorGridY,
		String majorGridZ,
		String minorGridX,
		String minorGridY,
		String minorGridZ,
		String snapGridX,
		String snapGridY,
		String snapGridZ
	) {
		setOrigin( origin );
		setBoundaryX1( boundaryX1 );
		setBoundaryY1( boundaryY1 );
		setBoundaryX2( boundaryX2 );
		setBoundaryY2( boundaryY2 );
		setMajorGridX( majorGridX );
		setMajorGridY( majorGridY );
		setMajorGridZ( majorGridZ );
		setMinorGridX( minorGridX );
		setMinorGridY( minorGridY );
		setMinorGridZ( minorGridZ );
		setSnapGridX( snapGridX );
		setSnapGridY( snapGridY );
		setSnapGridZ( snapGridZ );
	}

	public String getOrigin() {
		return getValue( ORIGIN );
	}

	public DesignWorkplane setOrigin( String origin ) {
		setValue( ORIGIN, origin );
		return this;
	}

	public double getBoundaryX1() {
		return getValue( BOUNDARY_X1 );
	}

	public DesignWorkplane setBoundaryX1( double boundaryX1 ) {
		setValue( BOUNDARY_X1, boundaryX1 );
		return this;
	}

	public double getBoundaryY1() {
		return getValue( BOUNDARY_Y1 );
	}

	public DesignWorkplane setBoundaryY1( double boundaryY1 ) {
		setValue( BOUNDARY_Y1, boundaryY1 );
		return this;
	}

	public double getBoundaryX2() {
		return getValue( BOUNDARY_X2 );
	}

	public DesignWorkplane setBoundaryX2( double boundaryX2 ) {
		setValue( BOUNDARY_X2, boundaryX2 );
		return this;
	}

	public double getBoundaryY2() {
		return getValue( BOUNDARY_Y2 );
	}

	public DesignWorkplane setBoundaryY2( double boundaryY2 ) {
		setValue( BOUNDARY_Y2, boundaryY2 );
		return this;
	}

	public double calcMajorGridX() {
		return majorGridX;
	}

	public String getMajorGridX() {
		return getValue( MAJOR_GRID_X );
	}

	public DesignWorkplane setMajorGridX( String majorGridX ) {
		this.majorGridX = CadMath.evalNoException( majorGridX );
		setValue( MAJOR_GRID_X, majorGridX );
		return this;
	}

	public double calcMajorGridY() {
		return majorGridY;
	}

	public String getMajorGridY() {
		return getValue( MAJOR_GRID_Y );
	}

	public DesignWorkplane setMajorGridY( String majorGridY ) {
		this.majorGridY = CadMath.evalNoException( majorGridY );
		setValue( MAJOR_GRID_Y, majorGridY );
		return this;
	}

	public double calcMajorGridZ() {
		return majorGridZ;
	}

	public String getMajorGridZ() {
		return getValue( MAJOR_GRID_Z );
	}

	public DesignWorkplane setMajorGridZ( String majorGridZ ) {
		this.majorGridZ = CadMath.evalNoException( majorGridZ );
		setValue( MAJOR_GRID_Z, majorGridZ );
		return this;
	}

	public double calcMinorGridX() {
		return minorGridX;
	}

	public String getMinorGridX() {
		return getValue( MINOR_GRID_X );
	}

	public DesignWorkplane setMinorGridX( String minorGridX ) {
		this.minorGridX = CadMath.evalNoException( minorGridX );
		setValue( MINOR_GRID_X, minorGridX );
		return this;
	}

	public double calcMinorGridY() {
		return minorGridY;
	}

	public String getMinorGridY() {
		return getValue( MINOR_GRID_Y );
	}

	public DesignWorkplane setMinorGridY( String minorGridY ) {
		this.minorGridY = CadMath.evalNoException( minorGridY );
		setValue( MINOR_GRID_Y, minorGridY );
		return this;
	}

	public double calcMinorGridZ() {
		return minorGridZ;
	}

	public String getMinorGridZ() {
		return getValue( MINOR_GRID_Z );
	}

	public DesignWorkplane setMinorGridZ( String minorGridZ ) {
		this.minorGridZ = CadMath.evalNoException( minorGridZ );
		setValue( MINOR_GRID_Z, minorGridZ );
		return this;
	}

	public double calcSnapGridX() {
		return snapGridX;
	}

	public String getSnapGridX() {
		return getValue( SNAP_GRID_X );
	}

	public DesignWorkplane setSnapGridX( String snapGridX ) {
		this.snapGridX = CadMath.evalNoException( snapGridX );
		setValue( SNAP_GRID_X, snapGridX );
		return this;
	}

	public double calcSnapGridY() {
		return snapGridY;
	}

	public String getSnapGridY() {
		return getValue( SNAP_GRID_Y );
	}

	public DesignWorkplane setSnapGridY( String snapGridY ) {
		this.snapGridY = CadMath.evalNoException( snapGridY );
		setValue( SNAP_GRID_Y, snapGridY );
		return this;
	}

	public double calcSnapGridZ() {
		return snapGridZ;
	}

	public String getSnapGridZ() {
		return getValue( SNAP_GRID_Z );
	}

	public DesignWorkplane setSnapGridZ( String snapGridZ ) {
		this.snapGridZ = CadMath.evalNoException( snapGridZ );
		setValue( SNAP_GRID_Z, snapGridZ );
		return this;
	}

	public Bounds getBounds() {
		double boundaryXmin = Math.min( getBoundaryX1(), getBoundaryX2() );
		double boundaryXmax = Math.max( getBoundaryX1(), getBoundaryX2() );
		double boundaryYmin = Math.min( getBoundaryY1(), getBoundaryY2() );
		double boundaryYmax = Math.max( getBoundaryY1(), getBoundaryY2() );
		return new BoundingBox( boundaryXmin, boundaryYmin, boundaryXmax - boundaryXmin, boundaryYmax - boundaryYmin );
	}

	public DesignWorkplane setBounds( Bounds bounds ) {
		if( bounds == null ) return this;
		setBoundaryX1( bounds.getMinX() );
		setBoundaryY1( bounds.getMinY() );
		setBoundaryX2( bounds.getMaxX() );
		setBoundaryY2( bounds.getMaxY() );
		return this;
	}

}
