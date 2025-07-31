package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.tool.RenderConstants;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.layout.StackPane;
import lombok.CustomLog;

@CustomLog
public abstract class BaseDesignRenderer extends StackPane implements DesignRenderer, RenderConstants {

	private final DoubleProperty dpiX;

	private final DoubleProperty dpiY;

	private final DoubleProperty outputScaleX;

	private final DoubleProperty outputScaleY;

	private final DoubleProperty viewCenterX;

	private final DoubleProperty viewCenterY;

	private final DoubleProperty viewCenterZ;

	private final DoubleProperty viewRotate;

	private final DoubleProperty viewZoomX;

	private final DoubleProperty viewZoomY;

	public BaseDesignRenderer() {
		getStyleClass().add( "tool-renderer" );

		/*
	  The renderer is configured to render at 96 DPI by default, but it can be
		configured to render for different media just as easily by changing the
		DPI setting:

		screen: setDpi( Screen.getPrimary().getDpi() );
		printer: setDpi( PrintResolution.getCrossFeedResolution(), PrintResolution.getFeedResolution() );
		*/
		dpiX = new SimpleDoubleProperty( DEFAULT_DPI );
		dpiY = new SimpleDoubleProperty( DEFAULT_DPI );

		/*
		The output scale is used when working with high resolution (HiDPI) monitors.
		This allows for fractional scaling as well and needs to be taken into account
		for detailed rendering of the model when the application may not require it.
		 */
		outputScaleX = new SimpleDoubleProperty( DEFAULT_OUTPUT_SCALE );
		outputScaleY = new SimpleDoubleProperty( DEFAULT_OUTPUT_SCALE );

		/*
		The world view settings. These are the view settings from the user's
		perspective.
		 */
		viewCenterX = new SimpleDoubleProperty( DEFAULT_CENTER.getX() );
		viewCenterY = new SimpleDoubleProperty( DEFAULT_CENTER.getY() );
		viewCenterZ = new SimpleDoubleProperty( DEFAULT_CENTER.getZ() );
		viewRotate = new SimpleDoubleProperty( DEFAULT_ROTATE );
		viewZoomX = new SimpleDoubleProperty( DEFAULT_ZOOM );
		viewZoomY = new SimpleDoubleProperty( DEFAULT_ZOOM );
	}

	/**
	 * Set the DPI for the renderer. This method sets the DPI for both the X and Y
	 * axes.
	 *
	 * @param dpiX The DPI to set for the X axis
	 * @param dpiY The DPI to set for the Y axis
	 */
	@Override
	public void setDpi( double dpiX, double dpiY ) {
		setDpiX( dpiX );
		setDpiY( dpiY );
	}

	@Override
	public void setDpiX( double dpi ) {
		dpiX.set( dpi );
	}

	@Override
	public double getDpiX() {
		return dpiX.get();
	}

	@Override
	public DoubleProperty dpiXProperty() {
		return dpiX;
	}

	@Override
	public double getDpiY() {
		return dpiY.get();
	}

	@Override
	public void setDpiY( double dpi ) {
		dpiY.set( dpi );
	}

	@Override
	public DoubleProperty dpiYProperty() {
		return dpiY;
	}

	@Override
	public void setOutputScale( double scaleX, double scaleY ) {
		setOutputScaleX( scaleX );
		setOutputScaleY( scaleY );
	}

	@Override
	public double getOutputScaleX() {
		return outputScaleX.get();
	}

	@Override
	public void setOutputScaleX( double scale ) {
		outputScaleX.set( scale );
	}

	@Override
	public DoubleProperty outputScaleXProperty() {
		return outputScaleX;
	}

	@Override
	public double getOutputScaleY() {
		return outputScaleY.get();
	}

	@Override
	public void setOutputScaleY( double scale ) {
		outputScaleY.set( scale );
	}

	@Override
	public DoubleProperty outputScaleYProperty() {
		return outputScaleY;
	}

	@Override
	public Point3D getViewCenter() {
		return new Point3D( viewCenterX.get(), viewCenterY.get(), viewCenterZ.get() );
	}

	@Override
	public void setViewCenter( double x, double y, double z ) {
		viewCenterX.set( x );
		viewCenterY.set( y );
		viewCenterZ.set( z );
	}

	@Override
	public void setViewCenter( Point3D center ) {
		setViewCenter( center.getX(), center.getY(), center.getZ() );
	}

	@Override
	public double getViewCenterX() {
		return viewCenterX.get();
	}

	@Override
	public void setViewCenterX( double x ) {
		viewCenterX.set( x );
	}

	@Override
	public DoubleProperty viewCenterXProperty() {
		return viewCenterX;
	}

	@Override
	public double getViewCenterY() {
		return viewCenterY.get();
	}

	@Override
	public void setViewCenterY( double y ) {
		viewCenterY.set( y );
	}

	@Override
	public DoubleProperty viewCenterYProperty() {
		return viewCenterY;
	}

	@Override
	public double getViewCenterZ() {
		return viewCenterZ.get();
	}

	@Override
	public void setViewCenterZ( double z ) {
		viewCenterZ.set( z );
	}

	@Override
	public DoubleProperty viewCenterZProperty() {
		return viewCenterZ;
	}

	@Override
	public double getViewRotate() {
		return viewRotate.get();
	}

	@Override
	public void setViewRotate( double rotate ) {
		viewRotate.set( rotate );
	}

	@Override
	public DoubleProperty viewRotateProperty() {
		return viewRotate;
	}

	@Override
	public double getViewZoom() {
		return getViewZoomX();
	}

	@Override
	public void setViewZoom( double viewZoom ) {
		setViewZoom( viewZoom, viewZoom );
	}

	@Override
	public void setViewZoom( double zoomX, double zoomY ) {
		log.atConfig().log( "set view zoom..." );
		setViewZoomX( zoomX );
		setViewZoomY( zoomY );
	}

	@Override
	public void setViewZoom( Point2D zoom ) {
		setViewZoomX( zoom.getX() );
		setViewZoomY( zoom.getY() );
	}

	@Override
	public double getViewZoomX() {
		return viewZoomX.get();
	}

	@Override
	public void setViewZoomX( double zoom ) {
		viewZoomX.set( zoom );
	}

	@Override
	public DoubleProperty viewZoomXProperty() {
		return viewZoomX;
	}

	@Override
	public double getViewZoomY() {
		return viewZoomY.get();
	}

	@Override
	public void setViewZoomY( double zoom ) {
		viewZoomY.set( zoom );
	}

	@Override
	public DoubleProperty viewZoomYProperty() {
		return viewZoomY;
	}

	/**
	 * Change the current zoom by the zoom factor. The zoom is centered on the
	 * provided anchor point in world coordinates. The current zoom is changed by
	 * multiplying the current zoom by the factor, and that becomes the new zoom.
	 *
	 * @param anchor The anchor point in world coordinates
	 * @param factor The zoom factor
	 */
	public void zoom( Point3D anchor, double factor ) {
		Point3D offset = getViewCenter().subtract( anchor );

		// The new view zoom has to be set before the new view center
		setViewZoom( new Point2D( viewZoomX.get(), viewZoomY.get() ).multiply( factor ) );

		// The new view center has to be set after the new view zoom
		setViewCenter( anchor.add( offset.multiply( 1.0 / factor ) ) );
	}

}
