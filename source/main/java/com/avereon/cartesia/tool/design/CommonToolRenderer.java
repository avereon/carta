package com.avereon.cartesia.tool.design;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.transform.Transform;

/**
 * Methods that are common to design tools and design renderers alike. It is
 * common that design tools delegate to their respective design renderer
 * implementations for many of these methods.
 */
public interface CommonToolRenderer {

	/**
	 * Retrieves the current center point of the view in world coordinates.
	 *
	 * @return A Point3D object representing the view center in world coordinates.
	 */
	Point3D getViewCenter();

	/**
	 * Sets the center point of the view in world coordinates.
	 *
	 * @param point The new center point of the view, specified as a Point3D object in world coordinates.
	 */
	void setViewCenter( Point3D point );

	/**
	 * Retrieves the current zoom level of the view.
	 *
	 * @return A double representing the current zoom factor. Higher values indicate zoom-in, and lower values indicate zoom-out.
	 */
	double getViewZoom();

	/**
	 * Sets the zoom level of the view. The zoom level determines how magnified or scaled
	 * the view appears, with higher values representing a closer zoom and lower values
	 * representing a zoomed-out view.
	 *
	 * @param viewZoom The new zoom level to set, specified as a double. Higher values indicate zoom-in, and lower values indicate zoom-out.
	 */
	void setViewZoom( double viewZoom );

	/**
	 * Retrieves the current rotation angle of the view.
	 *
	 * @return A double representing the current rotation angle of the view, in degrees.
	 */
	double getViewRotate();

	/**
	 * Sets the rotation angle of the view. This determines the current rotational
	 * orientation of the view, with the specified angle applied in degrees.
	 *
	 * @param angle The new rotation angle to set, specified as a double in degrees.
	 * Positive values rotate the view clockwise, and negative values
	 * rotate the view counterclockwise.
	 */
	void setViewRotate( double angle );

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
