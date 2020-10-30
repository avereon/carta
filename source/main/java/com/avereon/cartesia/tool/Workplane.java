package com.avereon.cartesia.tool;

import javafx.geometry.Point3D;

public class Workplane {

	private Point3D origin = Point3D.ZERO;

	private double snapSpacingX = 1.0;

	private double snapSpacingY = 1.0;

	private double snapSpacingZ = 1.0;

	private double minorIntervalX = 1.0;

	private double minorIntervalY = 1.0;

	private double minorIntervalZ = 1.0;

	private double majorIntervalX = 10.0;

	private double majorIntervalY = 10.0;

	private double majorIntervalZ = 10.0;

	public Point3D getOrigin() {
		return origin;
	}

	public Workplane setOrigin( Point3D origin ) {
		this.origin = origin;
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
