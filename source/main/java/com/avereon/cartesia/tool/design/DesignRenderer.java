package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.tool.RenderConstants;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.layout.Pane;

import java.util.Collection;

public abstract class DesignRenderer extends Pane implements RenderConstants {

	private final DoubleProperty dpiX;

	private final DoubleProperty dpiY;

	private final DoubleProperty viewCenterX;

	private final DoubleProperty viewCenterY;

	private final DoubleProperty viewCenterZ;

	private final DoubleProperty viewRotate;

	private final DoubleProperty viewZoomX;

	private final DoubleProperty viewZoomY;

	public DesignRenderer() {
		/*
	  The renderer is configured to render at 96 DPI by default, but it can be
		configured to render for different media just as easily by changing the
		DPI setting:

		screen: setDpi( Screen.getPrimary().getDpi() );
		printer: setDpi( PrintResolution.getCrossFeedResolution(), PrintResolution.getFeedResolution() );
		*/
		dpiX = new SimpleDoubleProperty( DEFAULT_DPI );
		dpiY = new SimpleDoubleProperty( DEFAULT_DPI );

		viewCenterX = new SimpleDoubleProperty( DEFAULT_CENTER.getX() );
		viewCenterY = new SimpleDoubleProperty( DEFAULT_CENTER.getY() );
		viewCenterZ = new SimpleDoubleProperty( DEFAULT_CENTER.getZ() );
		viewRotate = new SimpleDoubleProperty( DEFAULT_ROTATE );
		viewZoomX = new SimpleDoubleProperty( DEFAULT_ZOOM.getX() );
		viewZoomY = new SimpleDoubleProperty( DEFAULT_ZOOM.getY() );
	}

	public abstract void setDesign( Design design );

	public abstract void setVisibleLayers( Collection<DesignLayer> layers );

	/**
	 * Called to request the design be rendered.
	 */
	public abstract void render();

	/**
	 * Called to request the design be printed.
	 *
	 * @param factor The scale factor to apply to the design when printing.
	 */
	@Deprecated
	public abstract void print( double factor );

	/**
	 * Set the DPI for the renderer. This method sets the DPI for both the X and Y
	 * axes to the same value.
	 *
	 * @param dpi The DPI to set
	 */
	public void setDpi( double dpi ) {
		setDpi( dpi, dpi );
	}

	/**
	 * Set the DPI for the renderer. This method sets the DPI for both the X and Y
	 * axes.
	 *
	 * @param dpiX The DPI to set for the X axis
	 * @param dpiY The DPI to set for the Y axis
	 */
	public void setDpi( double dpiX, double dpiY ) {
		setDpiX( dpiX );
		setDpiY( dpiY );
	}

	public void setDpi( Point2D dpi ) {
		setDpiX( dpi.getX() );
		setDpiY( dpi.getY() );
	}

	public void setDpiX( double dpi ) {
		dpiX.set( dpi );
	}

	public double getDpiX() {
		return dpiX.get();
	}

	public DoubleProperty dpiXProperty() {
		return dpiX;
	}

	public double getDpiY() {
		return dpiY.get();
	}

	public void setDpiY( double dpi ) {
		dpiY.set( dpi );
	}

	public DoubleProperty dpiYProperty() {
		return dpiY;
	}

	public Point3D getViewCenter() {
		return new Point3D( viewCenterX.get(), viewCenterY.get(), viewCenterZ.get() );
	}

	public void setViewCenter( double x, double y, double z ) {
		viewCenterX.set( x );
		viewCenterY.set( y );
		viewCenterZ.set( z );
	}

	public void setViewCenter( Point3D center ) {
		setViewCenter( center.getX(), center.getY(), center.getZ() );
	}

	public double getViewCenterX() {
		return viewCenterX.get();
	}

	public void setViewCenterX( double x ) {
		viewCenterX.set( x );
	}

	public DoubleProperty viewCenterXProperty() {
		return viewCenterX;
	}

	public double getViewCenterY() {
		return viewCenterY.get();
	}

	public void setViewCenterY( double y ) {
		viewCenterY.set( y );
	}

	public DoubleProperty viewCenterYProperty() {
		return viewCenterY;
	}

	public double getViewCenterZ() {
		return viewCenterZ.get();
	}

	public void setViewCenterZ( double z ) {
		viewCenterZ.set( z );
	}

	public DoubleProperty viewCenterZProperty() {
		return viewCenterZ;
	}

	public double getViewRotate() {
		return viewRotate.get();
	}

	public void setViewRotate( double rotate ) {
		viewRotate.set( rotate );
	}

	public DoubleProperty viewRotateProperty() {
		return viewRotate;
	}

	public Point2D getViewZoom() {
		return new Point2D( viewZoomX.get(), viewZoomY.get() );
	}

	public void setViewZoom( double zoom ) {
		setViewZoom( zoom, zoom );
	}

	public void setViewZoom( double zoomX, double zoomY ) {
		setViewZoomX( zoomX );
		setViewZoomY( zoomY );
	}

	public void setViewZoom( Point2D zoom ) {
		setViewZoomX( zoom.getX() );
		setViewZoomY( zoom.getY() );
	}

	public double getViewZoomX() {
		return viewZoomX.get();
	}

	public void setViewZoomX( double zoom ) {
		viewZoomX.set( zoom );
	}

	public DoubleProperty viewZoomXProperty() {
		return viewZoomX;
	}

	public double getViewZoomY() {
		return viewZoomY.get();
	}

	public void setViewZoomY( double zoom ) {
		viewZoomY.set( zoom );
	}

	public DoubleProperty viewZoomYProperty() {
		return viewZoomY;
	}

	/**
	 * Change the zoom by the zoom factor. The zoom is centered on the provided
	 * anchor point in world coordinates. The current zoom is multiplied by the
	 * zoom factor.
	 *
	 * @param anchor The anchor point in world coordinates
	 * @param factor The zoom factor
	 */
	public void zoom( Point3D anchor, double factor ) {
		Point3D offset = getViewCenter().subtract( anchor );

		// The zoom has to be set before the viewpoint
		setViewZoom( getViewZoom().multiply( factor ) );
		setViewCenter( anchor.add( offset.multiply( 1 / factor ) ) );
	}

}
