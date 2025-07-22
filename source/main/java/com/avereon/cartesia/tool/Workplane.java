package com.avereon.cartesia.tool;

import com.avereon.cartesia.math.CadMath;
import com.avereon.cartesia.math.CadShapes;
import com.avereon.data.Node;
import com.avereon.transaction.Txn;
import com.avereon.zerra.color.Paints;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Paint;
import lombok.CustomLog;

// FIXME All properties must be observable
// So, I've got myself a mess here. Both FX Node and data Node are
// "technically" observable, but I think I was aiming for FX observable. So
// why is the old one a data Node? Probably to fit into the data model, and
// make it easier to store the values in settings, etc.

/**
 * The workplane represents the 2D workplane that the user interacts with in the
 * design tool. It contains the grid, coordinate system, and other properties
 * that define the workplane's behavior and appearance.
 */

@CustomLog
@SuppressWarnings( "UnusedReturnValue" )
public class Workplane extends Node {

	public static final GridStyle DEFAULT_GRID_STYLE = GridStyle.LINE;

	public static final double DEFAULT_BOUNDARY_X = 0.0;

	public static final double DEFAULT_BOUNDARY_Y = 0.0;

	public static final Grid DEFAULT_GRID_SYSTEM = Grid.ORTHO;

	public static final String DEFAULT_ORIGIN = "0,0";

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

	public static final String BOUNDARY_X1 = "boundary-x1";

	public static final String BOUNDARY_Y1 = "boundary-y1";

	public static final String BOUNDARY_X2 = "boundary-x2";

	public static final String BOUNDARY_Y2 = "boundary-y2";

	public static final String GRID_SYSTEM = "grid-system";

	public static final String GRID_STYLE = "grid-style";

	public static final String GRID_ORIGIN = "grid-origin";

	public static final String GRID_AXIS_VISIBLE = "grid-axis-visible";

	public static final String GRID_AXIS_PAINT = "grid-axis-paint";

	public static final String GRID_AXIS_WIDTH = "grid-axis-width";

	public static final String GRID_MAJOR_VISIBLE = "grid-major-visible";

	public static final String GRID_MAJOR_X = "grid-major-x";

	public static final String GRID_MAJOR_Y = "grid-major-y";

	public static final String GRID_MAJOR_PAINT = "grid-major-paint";

	public static final String GRID_MAJOR_WIDTH = "grid-major-width";

	public static final String GRID_MINOR_VISIBLE = "grid-minor-visible";

	public static final String GRID_MINOR_X = "grid-minor-x";

	public static final String GRID_MINOR_Y = "grid-minor-y";

	public static final String GRID_MINOR_PAINT = "grid-minor-paint";

	public static final String GRID_MINOR_WIDTH = "grid-minor-width";

	public static final String GRID_SNAP_X = "grid-snap-x";

	public static final String GRID_SNAP_Y = "grid-snap-y";

	private Point3D cachedGridOrigin;

	private Grid grid;

	/**
	 * The cached grid axis visible.
	 */
	private Boolean gridAxisVisible;

	/**
	 * The cached grid axis paint.
	 */
	private Paint gridAxisPaint;

	/**
	 * The cached grid axis width.
	 */
	private Double gridAxisWidth;

	/**
	 * The cached major grid X spacing.
	 */
	private Double majorGridX;

	/**
	 * The cached major grid Y spacing.
	 */
	private Double majorGridY;

	/**
	 * The cached major grid Z spacing.
	 */
	private Double majorGridZ;

	/**
	 * The cached major grid paint.
	 */
	private Paint majorGridPaint;

	/**
	 * The cached major grid width.
	 */
	private Double majorGridWidth;

	/**
	 * The cached minor grid X spacing.
	 */
	private Double minorGridX;

	/**
	 * The cached minor grid Y spacing.
	 */
	private Double minorGridY;

	/**
	 * The cached minor grid Z spacing.
	 */
	private Double minorGridZ;

	private Paint minorGridPaint;

	private Double minorGridWidth;

	/**
	 * The cached snap grid X spacing.
	 */
	private Double snapGridX;

	/**
	 * The cached snap grid Y spacing.
	 */
	private Double snapGridY;

	/**
	 * The cached snap grid Z spacing.
	 */
	private Double snapGridZ;

	private Boolean majorGridShowing = DEFAULT_GRID_MAJOR_VISIBLE;

	private Boolean minorGridShowing = DEFAULT_GRID_MINOR_VISIBLE;

	public Workplane() {
		this( -DEFAULT_BOUNDARY_X, -DEFAULT_BOUNDARY_Y, DEFAULT_BOUNDARY_X, DEFAULT_BOUNDARY_Y, DEFAULT_GRID_MAJOR_SIZE, DEFAULT_GRID_MINOR_SIZE, DEFAULT_GRID_SNAP_SIZE );
	}

	public Workplane( double boundaryX1, double boundaryY1, double boundaryX2, double boundaryY2 ) {
		this( boundaryX1, boundaryY1, boundaryX2, boundaryY2, DEFAULT_GRID_MAJOR_SIZE, DEFAULT_GRID_MINOR_SIZE, DEFAULT_GRID_SNAP_SIZE );
	}

	public Workplane( double boundaryX1, double boundaryY1, double boundaryX2, double boundaryY2, String majorGrid, String minorGrid, String snapGrid ) {
		this( DEFAULT_GRID_SYSTEM, DEFAULT_ORIGIN, boundaryX1, boundaryY1, boundaryX2, boundaryY2, majorGrid, majorGrid, minorGrid, minorGrid, snapGrid, snapGrid, DEFAULT_GRID_STYLE );
	}

	public Workplane(
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
		this( DEFAULT_GRID_SYSTEM, DEFAULT_ORIGIN, boundaryX1, boundaryY1, boundaryX2, boundaryY2, majorGridX, majorGridY, minorGridX, minorGridY, snapGridX, snapGridY, DEFAULT_GRID_STYLE );
	}

	public Workplane(
		Grid grid,
		String origin,
		double boundaryX1,
		double boundaryY1,
		double boundaryX2,
		double boundaryY2,
		String majorGridX,
		String majorGridY,
		String minorGridX,
		String minorGridY,
		String snapGridX,
		String snapGridY,
		GridStyle style
	) {
		Txn.run( () -> {
			setGridSystem( grid );
			setOrigin( origin );
			setBoundaryX1( boundaryX1 );
			setBoundaryY1( boundaryY1 );
			setBoundaryX2( boundaryX2 );
			setBoundaryY2( boundaryY2 );
			setMajorGridX( majorGridX );
			setMajorGridY( majorGridY );
			setMinorGridX( minorGridX );
			setMinorGridY( minorGridY );
			setSnapGridX( snapGridX );
			setSnapGridY( snapGridY );
			setGridStyle( style );

			setGridAxisPaint( DEFAULT_GRID_AXIS_PAINT );
			setGridAxisWidth( DEFAULT_GRID_AXIS_WIDTH );
			setMajorGridPaint( DEFAULT_GRID_MAJOR_PAINT );
			setMajorGridWidth( DEFAULT_GRID_MAJOR_WIDTH );
			setMinorGridPaint( DEFAULT_GRID_MINOR_PAINT );
			setMinorGridWidth( DEFAULT_GRID_MINOR_WIDTH );
		} );
	}

	public Grid getGridSystem() {
		return getValue( GRID_SYSTEM );
	}

	public void setGridSystem( Grid grid ) {
		setValue( GRID_SYSTEM, grid == null ? Grid.ORTHO : grid );
	}

	public Point3D calcOrigin() {
		if( cachedGridOrigin == null ) cachedGridOrigin = CadShapes.parsePoint( getOrigin() );
		return cachedGridOrigin;
	}

	public String getOrigin() {
		return getValue( GRID_ORIGIN, DEFAULT_ORIGIN );
	}

	public Workplane setOrigin( String origin ) {
		setValue( GRID_ORIGIN, origin );
		cachedGridOrigin = null;
		return this;
	}

	public double getBoundaryX1() {
		return getValue( BOUNDARY_X1 );
	}

	public Workplane setBoundaryX1( double boundaryX1 ) {
		setValue( BOUNDARY_X1, boundaryX1 );
		return this;
	}

	public double getBoundaryY1() {
		return getValue( BOUNDARY_Y1 );
	}

	public Workplane setBoundaryY1( double boundaryY1 ) {
		setValue( BOUNDARY_Y1, boundaryY1 );
		return this;
	}

	public double getBoundaryX2() {
		return getValue( BOUNDARY_X2 );
	}

	public Workplane setBoundaryX2( double boundaryX2 ) {
		setValue( BOUNDARY_X2, boundaryX2 );
		return this;
	}

	public double getBoundaryY2() {
		return getValue( BOUNDARY_Y2 );
	}

	public Workplane setBoundaryY2( double boundaryY2 ) {
		setValue( BOUNDARY_Y2, boundaryY2 );
		return this;
	}

	// ******************************
	// Grid Axis
	// ******************************
	public boolean isGridAxisVisible() {
		if( gridAxisVisible == null ) gridAxisVisible = getValue( GRID_AXIS_VISIBLE, DEFAULT_GRID_AXIS_VISIBLE );
		return gridAxisVisible;
	}

	public Workplane setGridAxisVisible( Boolean visible ) {
		setValue( GRID_AXIS_VISIBLE, visible );
		this.gridAxisVisible = null;
		return this;
	}

	public Paint calcGridAxisPaint() {
		if( gridAxisPaint == null ) gridAxisPaint = Paints.parse( getGridAxisPaint() );
		return gridAxisPaint;
	}

	public String getGridAxisPaint() {
		return getValue( GRID_AXIS_PAINT, DEFAULT_GRID_AXIS_PAINT );
	}

	public Workplane setGridAxisPaint( String paint ) {
		setValue( GRID_AXIS_PAINT, paint );
		this.gridAxisPaint = null;
		return this;
	}

	public double calcGridAxisWidth() {
		if( gridAxisWidth == null ) gridAxisWidth = CadMath.evalNoException( getGridAxisWidth() );
		return gridAxisWidth;
	}

	public String getGridAxisWidth() {
		return getValue( GRID_AXIS_WIDTH, DEFAULT_GRID_AXIS_WIDTH );
	}

	public Workplane setGridAxisWidth( String width ) {
		setValue( GRID_AXIS_WIDTH, width );
		this.gridAxisWidth = null;
		return this;
	}

	// ******************************
	// Grid Major Spacing
	// ******************************
	public boolean isMajorGridVisible() {
		return getValue( GRID_MAJOR_VISIBLE, DEFAULT_GRID_MAJOR_VISIBLE );
	}

	public Workplane setMajorGridVisible( Boolean visible ) {
		setValue( GRID_MAJOR_VISIBLE, visible );
		return this;
	}

	public boolean isMajorGridShowing() {
		if( majorGridShowing == null ) majorGridShowing = DEFAULT_GRID_MAJOR_VISIBLE;
		return majorGridShowing;
	}

	public Workplane setMajorGridShowing( Boolean showing ) {
		majorGridShowing = showing;
		return this;
	}

	public double calcMajorGridX() {
		if( majorGridX == null ) majorGridX = CadMath.evalNoException( getMajorGridX() );
		return majorGridX;
	}

	public String getMajorGridX() {
		return getValue( GRID_MAJOR_X, DEFAULT_GRID_MAJOR_SIZE );
	}

	public Workplane setMajorGridX( String majorGridX ) {
		setValue( GRID_MAJOR_X, majorGridX );
		this.majorGridX = null;
		return this;
	}

	public Double calcMajorGridY() {
		if( majorGridY == null ) majorGridY = CadMath.evalNoException( getMajorGridY() );
		return majorGridY;
	}

	public String getMajorGridY() {
		return getValue( GRID_MAJOR_Y, DEFAULT_GRID_MAJOR_SIZE );
	}

	public Workplane setMajorGridY( String majorGridY ) {
		setValue( GRID_MAJOR_Y, majorGridY );
		this.majorGridY = null;
		return this;
	}

	public Paint calcMajorGridPaint() {
		if( majorGridPaint == null ) majorGridPaint = Paints.parse( getMajorGridPaint() );
		return majorGridPaint;
	}

	public String getMajorGridPaint() {
		return getValue( GRID_MAJOR_PAINT, DEFAULT_GRID_MAJOR_PAINT );
	}

	public Workplane setMajorGridPaint( String paint ) {
		setValue( GRID_MAJOR_PAINT, paint );
		this.majorGridPaint = null;
		return this;
	}

	public double calcMajorGridWidth() {
		if( majorGridWidth == null ) majorGridWidth = CadMath.evalNoException( getMajorGridWidth() );
		return majorGridWidth;
	}

	public String getMajorGridWidth() {
		return getValue( GRID_MAJOR_WIDTH, DEFAULT_GRID_MAJOR_WIDTH );
	}

	public Workplane setMajorGridWidth( String width ) {
		setValue( GRID_MAJOR_WIDTH, width );
		this.majorGridWidth = null;
		return this;
	}

	// ******************************
	// Grid Minor Spacing
	// ******************************
	public boolean isMinorGridVisible() {
		return getValue( GRID_MINOR_VISIBLE, DEFAULT_GRID_MINOR_VISIBLE );
	}

	public Workplane setMinorGridVisible( Boolean visible ) {
		setValue( GRID_MINOR_VISIBLE, visible );
		return this;
	}

	public boolean isMinorGridShowing() {
		if( minorGridShowing == null ) minorGridShowing = DEFAULT_GRID_MINOR_VISIBLE;
		return minorGridShowing;
	}

	public Workplane setMinorGridShowing( Boolean showing ) {
		minorGridShowing = showing;
		return this;
	}

	public double calcMinorGridX() {
		if( minorGridX == null ) minorGridX = CadMath.evalNoException( getMinorGridX() );
		return minorGridX;
	}

	public String getMinorGridX() {
		return getValue( GRID_MINOR_X, DEFAULT_GRID_MINOR_SIZE );
	}

	public Workplane setMinorGridX( String minorGridX ) {
		setValue( GRID_MINOR_X, minorGridX );
		this.minorGridX = null;
		return this;
	}

	public double calcMinorGridY() {
		if( minorGridY == null ) minorGridY = CadMath.evalNoException( getMinorGridY() );
		return minorGridY;
	}

	public String getMinorGridY() {
		return getValue( GRID_MINOR_Y, DEFAULT_GRID_MINOR_SIZE );
	}

	public Workplane setMinorGridY( String minorGridY ) {
		setValue( GRID_MINOR_Y, minorGridY );
		this.minorGridY = null;
		return this;
	}

	public Paint calcMinorGridPaint() {
		if( minorGridPaint == null ) minorGridPaint = Paints.parse( getMinorGridPaint() );
		return minorGridPaint;
	}

	public String getMinorGridPaint() {
		return getValue( GRID_MINOR_PAINT, DEFAULT_GRID_MINOR_PAINT );
	}

	public Workplane setMinorGridPaint( String paint ) {
		setValue( GRID_MINOR_PAINT, paint );
		this.minorGridPaint = null;
		return this;
	}

	public double calcMinorGridWidth() {
		if( minorGridWidth == null ) minorGridWidth = CadMath.evalNoException( getMinorGridWidth() );
		return minorGridWidth;
	}

	public String getMinorGridWidth() {
		return getValue( GRID_MINOR_WIDTH, DEFAULT_GRID_MINOR_WIDTH );
	}

	public Workplane setMinorGridWidth( String width ) {
		setValue( GRID_MINOR_WIDTH, width );
		this.minorGridWidth = null;
		return this;
	}

	// ******************************
	// Grid Snap Spacing
	// ******************************
	public double calcSnapGridX() {
		if( snapGridX == null ) snapGridX = CadMath.evalNoException( getSnapGridX() );
		return snapGridX;
	}

	public String getSnapGridX() {
		return getValue( GRID_SNAP_X, DEFAULT_GRID_SNAP_SIZE );
	}

	public Workplane setSnapGridX( String snapGridX ) {
		setValue( GRID_SNAP_X, snapGridX );
		this.snapGridX = null;
		return this;
	}

	public double calcSnapGridY() {
		if( snapGridY == null ) snapGridY = CadMath.evalNoException( getSnapGridY() );
		return snapGridY;
	}

	public String getSnapGridY() {
		return getValue( GRID_SNAP_Y, DEFAULT_GRID_SNAP_SIZE );
	}

	public Workplane setSnapGridY( String snapGridY ) {
		setValue( GRID_SNAP_Y, snapGridY );
		this.snapGridY = null;
		return this;
	}

	public GridStyle getGridStyle() {
		return getValue( GRID_STYLE );
	}

	public Workplane setGridStyle( GridStyle style ) {
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

	public Workplane setBounds( Bounds bounds ) {
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
