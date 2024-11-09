package com.avereon.cartesia.tool;

import com.avereon.cartesia.math.CadMath;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.data.Node;
import com.avereon.transaction.Txn;
import com.avereon.zarra.color.Paints;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.paint.Paint;
import lombok.CustomLog;

@CustomLog
@SuppressWarnings( "UnusedReturnValue" )
public class DesignWorkplane extends Node {

	public static final GridStyle DEFAULT_GRID_STYLE = GridStyle.LINE;

	public static final double DEFAULT_BOUNDARY_X = 0.0;

	public static final double DEFAULT_BOUNDARY_Y = 0.0;

	public static final Grid DEFAULT_COORDINATE_SYSTEM = Grid.ORTHO;

	public static final String DEFAULT_GRID_ORIGIN = "0,0,0";

	public static final boolean DEFAULT_GRID_AXIS_VISIBLE = true;

	public static final String DEFAULT_GRID_AXIS_PAINT = "#c0c040ff";

	public static final String DEFAULT_GRID_AXIS_WIDTH = "0.05";

	public static final boolean DEFAULT_GRID_MAJOR_VISIBLE = true;

	public static final String DEFAULT_GRID_MAJOR_SIZE = "1.0";

	public static final String DEFAULT_GRID_MAJOR_PAINT = "#80a0d080";

	public static final String DEFAULT_GRID_MAJOR_WIDTH = "0.03";

	public static final boolean DEFAULT_GRID_MINOR_VISIBLE = true;

	public static final String DEFAULT_GRID_MINOR_SIZE = "0.5";

	public static final String DEFAULT_GRID_MINOR_PAINT = "#80a0d040";

	public static final String DEFAULT_GRID_MINOR_WIDTH = "0.02";

	public static final String DEFAULT_GRID_SNAP_SIZE = "0.1";

	public static final String WORKPANE_ORIGIN = "workpane-origin";

	public static final String GRID_STYLE = "grid-style";

	public static final String BOUNDARY_X1 = "boundary-x1";

	public static final String BOUNDARY_Y1 = "boundary-y1";

	public static final String BOUNDARY_X2 = "boundary-x2";

	public static final String BOUNDARY_Y2 = "boundary-y2";

	public static final String COORDINATE_SYSTEM = "coordinate-system";

	public static final String GRID_ORIGIN = "grid-origin";

	public static final String GRID_AXIS_VISIBLE = "grid-axis-visible";

	public static final String GRID_AXIS_PAINT = "grid-axis-paint";

	public static final String GRID_AXIS_WIDTH = "grid-axis-width";

	public static final String GRID_MAJOR_VISIBLE = "grid-major-visible";

	public static final String GRID_MAJOR_X = "grid-major-x";

	public static final String GRID_MAJOR_Y = "grid-major-y";

	public static final String GRID_MAJOR_Z = "grid-major-z";

	public static final String GRID_MAJOR_PAINT = "grid-major-paint";

	public static final String GRID_MAJOR_WIDTH = "grid-major-width";

	public static final String GRID_MINOR_VISIBLE = "grid-minor-visible";

	public static final String GRID_MINOR_X = "grid-minor-x";

	public static final String GRID_MINOR_Y = "grid-minor-y";

	public static final String GRID_MINOR_Z = "grid-minor-z";

	public static final String GRID_MINOR_PAINT = "grid-minor-paint";

	public static final String GRID_MINOR_WIDTH = "grid-minor-width";

	public static final String GRID_SNAP_X = "grid-snap-x";

	public static final String GRID_SNAP_Y = "grid-snap-y";

	public static final String GRID_SNAP_Z = "grid-snap-z";

	private Point3D cachedGridOrigin;

	private Grid grid;

	/**
	 * The cached grid axis paint.
	 */
	private Paint gridAxisPaint;

	/**
	 * The cached grid axis width.
	 */
	private double gridAxisWidth;

	/**
	 * The cached major grid X spacing.
	 */
	private double majorGridX;

	/**
	 * The cached major grid Y spacing.
	 */
	private double majorGridY;

	/**
	 * The cached major grid Z spacing.
	 */
	private double majorGridZ;

	/**
	 * The cached major grid paint.
	 */
	private Paint majorGridPaint;

	/**
	 * The cached major grid width.
	 */
	private double majorGridWidth;

	/**
	 * The cached minor grid X spacing.
	 */
	private double minorGridX;

	/**
	 * The cached minor grid Y spacing.
	 */
	private double minorGridY;

	/**
	 * The cached minor grid Z spacing.
	 */
	private double minorGridZ;

	private Paint minorGridPaint;

	private double minorGridWidth;

	/**
	 * The cached snap grid X spacing.
	 */
	private double snapGridX;

	/**
	 * The cached snap grid Y spacing.
	 */
	private double snapGridY;

	/**
	 * The cached snap grid Z spacing.
	 */
	private double snapGridZ;

	private boolean majorGridShowing = DEFAULT_GRID_MAJOR_VISIBLE;

	private boolean minorGridShowing = DEFAULT_GRID_MINOR_VISIBLE;

	public DesignWorkplane() {
		this( -DEFAULT_BOUNDARY_X, -DEFAULT_BOUNDARY_Y, DEFAULT_BOUNDARY_X, DEFAULT_BOUNDARY_Y, DEFAULT_GRID_MAJOR_SIZE, DEFAULT_GRID_MINOR_SIZE, DEFAULT_GRID_SNAP_SIZE );
	}

	public DesignWorkplane(
		double boundaryX1, double boundaryY1, double boundaryX2, double boundaryY2, String majorGrid, String minorGrid, String snapGrid
	) {
		this(
			DEFAULT_COORDINATE_SYSTEM,
			DEFAULT_GRID_ORIGIN,
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
			snapGrid,
			DEFAULT_GRID_STYLE
		);
	}

	public DesignWorkplane(
		double boundaryX1, double boundaryY1, double boundaryX2, double boundaryY2, String majorGridX, String majorGridY, String minorGridX, String minorGridY, String snapGridX, String snapGridY
	) {
		this(
			DEFAULT_COORDINATE_SYSTEM,
			DEFAULT_GRID_ORIGIN,
			boundaryX1,
			boundaryY1,
			boundaryX2,
			boundaryY2,
			majorGridX,
			majorGridY,
			DEFAULT_GRID_MAJOR_SIZE,
			minorGridX,
			minorGridY,
			DEFAULT_GRID_MINOR_SIZE,
			snapGridX,
			snapGridY,
			DEFAULT_GRID_SNAP_SIZE,
			DEFAULT_GRID_STYLE
		);
	}

	public DesignWorkplane(
		Grid grid,
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
		String snapGridZ,
		GridStyle style
	) {
		Txn.run( () -> {
			setCoordinateSystem( grid );
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
			setGridStyle( style );

			setGridAxisPaint( Paints.parse( DEFAULT_GRID_AXIS_PAINT ) );
			setGridAxisWidth( DEFAULT_GRID_AXIS_WIDTH );
			setMajorGridPaint( Paints.parse( DEFAULT_GRID_MAJOR_PAINT ) );
			setMajorGridWidth( DEFAULT_GRID_MAJOR_WIDTH );
			setMinorGridPaint( Paints.parse( DEFAULT_GRID_MINOR_PAINT ) );
			setMinorGridWidth( DEFAULT_GRID_MINOR_WIDTH );
		} );
	}

	public Grid getCoordinateSystem() {
		return getValue( COORDINATE_SYSTEM );
	}

	public void setCoordinateSystem( Grid grid ) {
		setValue( COORDINATE_SYSTEM, grid == null ? Grid.ORTHO : grid );
	}

	public Point3D calcOrigin() {
		if( cachedGridOrigin == null ) cachedGridOrigin = CadShapes.parsePoint( getOrigin() );
		return cachedGridOrigin;
	}

	public String getOrigin() {
		return getValue( GRID_ORIGIN, DEFAULT_GRID_ORIGIN );
	}

	public DesignWorkplane setOrigin( String origin ) {
		setValue( GRID_ORIGIN, origin );
		cachedGridOrigin = null;
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

	// ******************************
	// Grid Axis
	// ******************************
	public boolean isGridAxisVisible() {
		return getValue( GRID_AXIS_VISIBLE, DEFAULT_GRID_AXIS_VISIBLE );
	}

	public DesignWorkplane setGridAxisVisible( boolean visible ) {
		setValue( GRID_AXIS_VISIBLE, visible );
		return this;
	}

	public Paint calcGridAxisPaint() {
		return gridAxisPaint;
	}

	public String getGridAxisPaint() {
		return getValue( GRID_AXIS_PAINT );
	}

	public DesignWorkplane setGridAxisPaint( Paint paint ) {
		this.gridAxisPaint = paint;
		setValue( GRID_AXIS_PAINT, Paints.toString( paint ) );
		return this;
	}

	public double calcGridAxisWidth() {
		return gridAxisWidth;
	}

	public String getGridAxisWidth() {
		return getValue( GRID_AXIS_WIDTH );
	}

	public DesignWorkplane setGridAxisWidth( String width ) {
		this.gridAxisWidth = CadMath.evalNoException( width );
		setValue( GRID_AXIS_WIDTH, width );
		return this;
	}

	// ******************************
	// Grid Major Spacing
	// ******************************
	public boolean isMajorGridVisible() {
		return getValue( GRID_MAJOR_VISIBLE, DEFAULT_GRID_MAJOR_VISIBLE );
	}

	public DesignWorkplane setMajorGridVisible( boolean visible ) {
		setValue( GRID_MAJOR_VISIBLE, visible );
		return this;
	}

	public boolean isMajorGridShowing() {
		return majorGridShowing;
	}

	public DesignWorkplane setMajorGridShowing( boolean showing ) {
		majorGridShowing = showing;
		return this;
	}

	public double calcMajorGridX() {
		return majorGridX;
	}

	public String getMajorGridX() {
		return getValue( GRID_MAJOR_X );
	}

	public DesignWorkplane setMajorGridX( String majorGridX ) {
		this.majorGridX = CadMath.evalNoException( majorGridX );
		setValue( GRID_MAJOR_X, majorGridX );
		return this;
	}

	public double calcMajorGridY() {
		return majorGridY;
	}

	public String getMajorGridY() {
		return getValue( GRID_MAJOR_Y );
	}

	public DesignWorkplane setMajorGridY( String majorGridY ) {
		this.majorGridY = CadMath.evalNoException( majorGridY );
		setValue( GRID_MAJOR_Y, majorGridY );
		return this;
	}

	public double calcMajorGridZ() {
		return majorGridZ;
	}

	public String getMajorGridZ() {
		return getValue( GRID_MAJOR_Z );
	}

	public DesignWorkplane setMajorGridZ( String majorGridZ ) {
		this.majorGridZ = CadMath.evalNoException( majorGridZ );
		setValue( GRID_MAJOR_Z, majorGridZ );
		return this;
	}

	public Paint calcMajorGridPaint() {
		return majorGridPaint;
	}

	public String getMajorGridPaint() {
		return getValue( GRID_MAJOR_PAINT );
	}

	public DesignWorkplane setMajorGridPaint( Paint paint ) {
		this.majorGridPaint = paint;
		setValue( GRID_MAJOR_PAINT, Paints.toString( paint ) );
		return this;
	}

	public double calcMajorGridWidth() {
		return majorGridWidth;
	}

	public String getMajorGridWidth() {
		return getValue( GRID_MAJOR_WIDTH );
	}

	public DesignWorkplane setMajorGridWidth( String width ) {
		this.majorGridWidth = CadMath.evalNoException( width );
		setValue( GRID_MAJOR_WIDTH, width );
		return this;
	}

	// ******************************
	// Grid Minor Spacing
	// ******************************
	public boolean isMinorGridVisible() {
		return getValue( GRID_MINOR_VISIBLE, DEFAULT_GRID_MINOR_VISIBLE );
	}

	public DesignWorkplane setMinorGridVisible( boolean visible ) {
		setValue( GRID_MINOR_VISIBLE, visible );
		return this;
	}

	public boolean isMinorGridShowing() {
		return minorGridShowing;
	}

	public DesignWorkplane setMinorGridShowing( boolean showing ) {
		minorGridShowing = showing;
		return this;
	}

	public double calcMinorGridX() {
		return minorGridX;
	}

	public String getMinorGridX() {
		return getValue( GRID_MINOR_X );
	}

	public DesignWorkplane setMinorGridX( String minorGridX ) {
		this.minorGridX = CadMath.evalNoException( minorGridX );
		setValue( GRID_MINOR_X, minorGridX );
		return this;
	}

	public double calcMinorGridY() {
		return minorGridY;
	}

	public String getMinorGridY() {
		return getValue( GRID_MINOR_Y );
	}

	public DesignWorkplane setMinorGridY( String minorGridY ) {
		this.minorGridY = CadMath.evalNoException( minorGridY );
		setValue( GRID_MINOR_Y, minorGridY );
		return this;
	}

	public double calcMinorGridZ() {
		return minorGridZ;
	}

	public String getMinorGridZ() {
		return getValue( GRID_MINOR_Z );
	}

	public DesignWorkplane setMinorGridZ( String minorGridZ ) {
		this.minorGridZ = CadMath.evalNoException( minorGridZ );
		setValue( GRID_MINOR_Z, minorGridZ );
		return this;
	}

	public Paint calcMinorGridPaint() {
		return minorGridPaint;
	}

	public String getMinorGridPaint() {
		return getValue( GRID_MINOR_PAINT );
	}

	public DesignWorkplane setMinorGridPaint( Paint paint ) {
		this.minorGridPaint = paint;
		setValue( GRID_MINOR_PAINT, Paints.toString( paint ) );
		return this;
	}

	public double calcMinorGridWidth() {
		return minorGridWidth;
	}

	public String getMinorGridWidth() {
		return getValue( GRID_MINOR_WIDTH );
	}

	public DesignWorkplane setMinorGridWidth( String width ) {
		this.minorGridWidth = CadMath.evalNoException( width );
		setValue( GRID_MINOR_WIDTH, width );
		return this;
	}

	// ******************************
	// Grid Snap Spacing
	// ******************************
	public double calcSnapGridX() {
		return snapGridX;
	}

	public String getSnapGridX() {
		return getValue( GRID_SNAP_X );
	}

	public DesignWorkplane setSnapGridX( String snapGridX ) {
		this.snapGridX = CadMath.evalNoException( snapGridX );
		setValue( GRID_SNAP_X, snapGridX );
		return this;
	}

	public double calcSnapGridY() {
		return snapGridY;
	}

	public String getSnapGridY() {
		return getValue( GRID_SNAP_Y );
	}

	public DesignWorkplane setSnapGridY( String snapGridY ) {
		this.snapGridY = CadMath.evalNoException( snapGridY );
		setValue( GRID_SNAP_Y, snapGridY );
		return this;
	}

	public double calcSnapGridZ() {
		return snapGridZ;
	}

	public String getSnapGridZ() {
		return getValue( GRID_SNAP_Z );
	}

	public DesignWorkplane setSnapGridZ( String snapGridZ ) {
		this.snapGridZ = CadMath.evalNoException( snapGridZ );
		setValue( GRID_SNAP_Z, snapGridZ );
		return this;
	}

	public GridStyle getGridStyle() {
		return getValue( GRID_STYLE );
	}

	public DesignWorkplane setGridStyle( GridStyle style ) {
		setValue( GRID_STYLE, style );
		return this;
	}

	public Bounds getBounds() {
		double boundaryXmin = Math.min( getBoundaryX1(), getBoundaryX2() );
		double boundaryXmax = Math.max( getBoundaryX1(), getBoundaryX2() );
		double boundaryYmin = Math.min( getBoundaryY1(), getBoundaryY2() );
		double boundaryYmax = Math.max( getBoundaryY1(), getBoundaryY2() );
		return new BoundingBox( boundaryXmin, boundaryYmin, boundaryXmax - boundaryXmin, boundaryYmax - boundaryYmin );
	}

	public DesignWorkplane setBounds( Point2D min, Point2D max ) {
		Txn.run( () -> {
			setBoundaryX1( min.getX() );
			setBoundaryY1( min.getY() );
			setBoundaryX2( max.getX() );
			setBoundaryY2( max.getY() );
		} );
		return this;
	}

	public DesignWorkplane setBounds( Bounds bounds ) {
		if( bounds == null ) return this;
		Txn.run( () -> {
			setBoundaryX1( bounds.getMinX() );
			setBoundaryY1( bounds.getMinY() );
			setBoundaryX2( bounds.getMaxX() );
			setBoundaryY2( bounds.getMaxY() );
		} );
		return this;
	}

}
