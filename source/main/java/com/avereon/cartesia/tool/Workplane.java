package com.avereon.cartesia.tool;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

public class Workplane {

	public static final double DEFAULT_BOUNDARY_X = 10.0;

	public static final double DEFAULT_BOUNDARY_Y = 10.0;

	public static final double DEFAULT_GRID_SIZE = 0.1;

	public static final double DEFAULT_MINOR_GRID_SIZE = 0.5;

	public static final double DEFAULT_MAJOR_GRID_SIZE = 1.0;

	public static final boolean DEFAULT_GRID_VISIBLE = false;

	public static final boolean DEFAULT_GRID_SNAP_ENABLED = false;

	public static final Color DEFAULT_AXIS_COLOR = Color.web( "#20408060" );

	public static final Color DEFAULT_MAJOR_GRID_COLOR = Color.web( "#20408020" );

	public static final Color DEFAULT_MINOR_GRID_COLOR = Color.web( "#20408010" );

	private Point3D origin = Point3D.ZERO;

	private double boundaryX1 = -DEFAULT_BOUNDARY_X;

	private double boundaryY1 = -DEFAULT_BOUNDARY_Y;

	private double boundaryX2 = DEFAULT_BOUNDARY_X;

	private double boundaryY2 = DEFAULT_BOUNDARY_Y;

	private double snapSpacingX = DEFAULT_GRID_SIZE;

	private double snapSpacingY = DEFAULT_GRID_SIZE;

	private double snapSpacingZ = DEFAULT_GRID_SIZE;

	private double minorIntervalX = DEFAULT_MINOR_GRID_SIZE;

	private double minorIntervalY = DEFAULT_MINOR_GRID_SIZE;

	private double minorIntervalZ = DEFAULT_MINOR_GRID_SIZE;

	private double majorIntervalX = DEFAULT_MAJOR_GRID_SIZE;

	private double majorIntervalY = DEFAULT_MAJOR_GRID_SIZE;

	private double majorIntervalZ = DEFAULT_MAJOR_GRID_SIZE;

	public Workplane() {}

	public Workplane(
		double boundaryX1, double boundaryY1, double boundaryX2, double boundaryY2, double majorInterval, double minorInterval, double snapSpacing
	) {
		this( Point3D.ZERO, boundaryX1, boundaryY1, boundaryX2, boundaryY2, majorInterval, majorInterval, minorInterval, minorInterval, snapSpacing, snapSpacing );
	}

	public Workplane(
		double boundaryX1,
		double boundaryY1,
		double boundaryX2,
		double boundaryY2,
		double majorIntervalX,
		double majorIntervalY,
		double minorIntervalX,
		double minorIntervalY,
		double snapSpacingX,
		double snapSpacingY
	) {
		this(
			Point3D.ZERO,
			boundaryX1,
			boundaryY1,
			boundaryX2,
			boundaryY2,
			majorIntervalX,
			majorIntervalY,
			minorIntervalX,
			minorIntervalY,
			snapSpacingX,
			snapSpacingY
		);
	}

	public Workplane(
		Point3D origin,
		double boundaryX1,
		double boundaryY1,
		double boundaryX2,
		double boundaryY2,
		double majorIntervalX,
		double majorIntervalY,
		double minorIntervalX,
		double minorIntervalY,
		double snapSpacingX,
		double snapSpacingY
	) {
		this.origin = origin;
		this.boundaryX1 = boundaryX1;
		this.boundaryY1 = boundaryY1;
		this.boundaryX2 = boundaryX2;
		this.boundaryY2 = boundaryY2;
		this.snapSpacingX = snapSpacingX;
		this.snapSpacingY = snapSpacingY;
		this.minorIntervalX = minorIntervalX;
		this.minorIntervalY = minorIntervalY;
		this.majorIntervalX = majorIntervalX;
		this.majorIntervalY = majorIntervalY;
	}

	public Point3D getOrigin() {
		return origin;
	}

	public Workplane setOrigin( Point3D origin ) {
		this.origin = origin;
		return this;
	}

	public double getBoundaryX1() {
		return boundaryX1;
	}

	public Workplane setBoundaryX1( double boundaryX1 ) {
		this.boundaryX1 = boundaryX1;
		return this;
	}

	public double getBoundaryY1() {
		return boundaryY1;
	}

	public Workplane setBoundaryY1( double boundaryY1 ) {
		this.boundaryY1 = boundaryY1;
		return this;
	}

	public double getBoundaryX2() {
		return boundaryX2;
	}

	public Workplane setBoundaryX2( double boundaryX2 ) {
		this.boundaryX2 = boundaryX2;
		return this;
	}

	public double getBoundaryY2() {
		return boundaryY2;
	}

	public Workplane setBoundaryY2( double boundaryY2 ) {
		this.boundaryY2 = boundaryY2;
		return this;
	}

	public double getSnapSpacingX() {
		return snapSpacingX;
	}

	public Workplane setSnapSpacingX( double snapSpacingX ) {
		this.snapSpacingX = snapSpacingX;
		return this;
	}

	public double getSnapSpacingY() {
		return snapSpacingY;
	}

	public Workplane setSnapSpacingY( double snapSpacingY ) {
		this.snapSpacingY = snapSpacingY;
		return this;
	}

	public double getSnapSpacingZ() {
		return snapSpacingZ;
	}

	public Workplane setSnapSpacingZ( double snapSpacingZ ) {
		this.snapSpacingZ = snapSpacingZ;
		return this;
	}

	public double getMinorIntervalX() {
		return minorIntervalX;
	}

	public Workplane setMinorIntervalX( double minorIntervalX ) {
		this.minorIntervalX = minorIntervalX;
		return this;
	}

	public double getMinorIntervalY() {
		return minorIntervalY;
	}

	public Workplane setMinorIntervalY( double minorIntervalY ) {
		this.minorIntervalY = minorIntervalY;
		return this;
	}

	public double getMinorIntervalZ() {
		return minorIntervalZ;
	}

	public Workplane setMinorIntervalZ( double minorIntervalZ ) {
		this.minorIntervalZ = minorIntervalZ;
		return this;
	}

	public double getMajorIntervalX() {
		return majorIntervalX;
	}

	public Workplane setMajorIntervalX( double majorIntervalX ) {
		this.majorIntervalX = majorIntervalX;
		return this;
	}

	public double getMajorIntervalY() {
		return majorIntervalY;
	}

	public Workplane setMajorIntervalY( double majorIntervalY ) {
		this.majorIntervalY = majorIntervalY;
		return this;
	}

	public double getMajorIntervalZ() {
		return majorIntervalZ;
	}

	public Workplane setMajorIntervalZ( double majorIntervalZ ) {
		this.majorIntervalZ = majorIntervalZ;
		return this;
	}

}
