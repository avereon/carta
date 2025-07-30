package com.avereon.cartesia.tool.design;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.transform.Transform;

public interface DesignRenderer {

	/**
	 * Retrieves the transformation matrix that converts world coordinates to
	 * screen coordinates.
	 *
	 * @return a Transform object representing the world-to-screen coordinate transformation
	 */
	Transform getWorldToScreenTransform();

	/**
	 * Transforms coordinates from world coordinates to screen coordinates.
	 *
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @return The converted coordinates in screen coordinates
	 */
	Point2D worldToScreen( double x, double y );

	/**
	 * Converts a point from world coordinates to screen coordinates.
	 *
	 * @param point The point to convert
	 * @return The converted point in screen coordinates
	 */
	Point2D worldToScreen( Point2D point );

	/**
	 * Transforms coordinates from world coordinates to screen coordinates.
	 *
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param z The z coordinate
	 * @return The converted coordinates in screen coordinates
	 */
	Point3D worldToScreen( double x, double y, double z );

	/**
	 * Converts a point from world coordinates to screen coordinates.
	 *
	 * @param point The point to convert
	 * @return The converted point in screen coordinates
	 */
	Point3D worldToScreen( Point3D point );

	/**
	 * Converts bounds from world coordinates to screen coordinates.
	 *
	 * @param bounds The bounds in world coordinates to convert
	 * @return The converted bounds in screen coordinates
	 */
	Bounds worldToScreen( Bounds bounds );

	/**
	 * Retrieves the transformation matrix that converts screen coordinates to
	 * world coordinates.
	 *
	 * @return a Transform object representing the screen-to-world coordinate transformation
	 */
	Transform getScreenToWorldTransform();

	/**
	 * Converts a point from screen coordinates to world coordinates.
	 *
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @return The converted point in world coordinates
	 */
	Point2D screenToWorld( double x, double y );

	/**
	 * Converts a point from screen coordinates to world coordinates.
	 *
	 * @param point The point in screen coordinates to convert
	 * @return The converted point in world coordinates
	 */
	Point2D screenToWorld( Point2D point );

	/**
	 * Convert screen coordinates to world coordinates.
	 *
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param z The z coordinate
	 * @return The converted coordinates in world coordinates
	 */
	Point3D screenToWorld( double x, double y, double z );

	/**
	 * Convert a point from screen coordinates to world coordinates.
	 *
	 * @param point The point in screen coordinates to convert
	 * @return The converted point in world coordinates
	 */
	Point3D screenToWorld( Point3D point );

	/**
	 * Converts bounds from screen coordinates to world coordinates.
	 *
	 * @param bounds The bounds in screen coordinates to convert
	 * @return The converted bounds in world coordinates
	 */
	Bounds screenToWorld( Bounds bounds );

	/**
	 * Change the current zoom by the zoom factor. The zoom is centered on the
	 * provided anchor point in world coordinates. The current zoom is changed by
	 * multiplying the current zoom by the factor, and that becomes the new zoom.
	 *
	 * @param anchor The anchor point in world coordinates
	 * @param factor The zoom factor
	 */
	void zoom( Point3D anchor, double factor );
}
