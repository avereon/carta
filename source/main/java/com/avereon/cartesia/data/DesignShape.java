package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.math.*;
import com.avereon.data.Node;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import lombok.CustomLog;

import java.util.Comparator;
import java.util.Map;

import static com.avereon.data.NodeEvent.MODIFIED;

@CustomLog
public abstract class DesignShape extends DesignDrawable {

	// The shape types in order of simplicity:
	// BOX, LINE, ELLIPSE, ARC, QUAD, CUBIC, PATH, MARKER, TEXT

	public enum Type {
		ARC,
		BOX,
		CUBIC,
		ELLIPSE,
		LINE,
		MARKER,
		PATH,
		QUAD,
		TEXT
	}

	public static final DesignShape NONE = new DesignShape() {}.setId( "NONE" );

	public static final String SHAPE = "shape";

	public static final String ORIGIN = "origin";

	public static final String ROTATE = "rotate";

	public static final String SELECTED = "selected";

	public static final String REFERENCE = "reference";

	private Shape fxShapeCache;

	private Bounds boundsCache;

	private Bounds visualBoundsCache;

	public DesignShape() {
		this( null );
	}

	public DesignShape( Point3D origin ) {
		addModifyingKeys( ORIGIN, ROTATE );
		setOrigin( origin );

		// Register a listener to clear caches
		register( MODIFIED, e -> {
			if( e.getNewValue() == Boolean.TRUE ) {
				boundsCache = null;
				visualBoundsCache = null;
				fxShapeCache = null;
			}
		} );
	}

	public Type getType() {
		throw new UnsupportedOperationException();
	}

	public Point3D getOrigin() {
		return getValue( ORIGIN );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignShape> T setOrigin( Point3D origin ) {
		setValue( ORIGIN, origin );
		return (T)this;
	}

	public double calcRotate() {
		String rotate = getRotate();
		return rotate == null ? 0.0 : CadMath.evalNoException( rotate );
	}

	public String getRotate() {
		return getValue( ROTATE );
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignShape> T setRotate( String value ) {
		setValue( ROTATE, value );
		return (T)this;
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DesignShape> T setRotate( double value ) {
		setRotate( String.valueOf( value ) );
		return (T)this;
	}

	public boolean isReference() {
		return getValue( REFERENCE, false );
	}

	public DesignShape setReference( boolean preview ) {
		setValue( REFERENCE, preview ? true : null );
		return this;
	}

	public boolean isSelected() {
		return getValue( SELECTED, false );
	}

	public DesignShape setSelected( boolean selected ) {
		setValue( SELECTED, selected ? true : null );
		return this;
	}

	public Shape getFxShape() {
		if( fxShapeCache == null ) fxShapeCache = CadGeometry.toFxShape( this );
		return fxShapeCache;
	}

	/**
	 * Get the geometric bounds of this shape. The geometric bounds include the
	 * geometry with the transforms applied, but not the stroke or effects.
	 *
	 * @return The geometric bounds of the shape
	 */
	public Bounds getBounds() {
		if( boundsCache == null ) boundsCache = computeGeometricBounds();
		return boundsCache;
	}

	protected Bounds computeGeometricBounds() {
		Shape shape = getFxShape();
		shape.setStroke( null );
		return shape.getBoundsInParent();
	}

	/**
	 * This is a special implementation using FX to compute visual selection boundaries.
	 *
	 * @return The visual bounds of the shape
	 */
	public Bounds getVisualBounds() {
		if( visualBoundsCache == null ) visualBoundsCache = computeVisualBounds();
		return visualBoundsCache;
	}

	protected Bounds computeVisualBounds() {
		Paint drawPaint = calcDrawPaint();
		boolean hasDraw = drawPaint != null && drawPaint != Color.TRANSPARENT;

		Shape shape = getFxShape();
		shape.setStroke( hasDraw ? Color.YELLOW : null );
		return shape.getBoundsInParent();
	}

	public double distanceTo( Point3D point ) {
		return Double.NaN;
	}

	public double pathLength() {
		return Double.NaN;
	}

	public CadTransform getRotateTransform() {
		double rotate = calcRotate();
		if( rotate == 0.0 ) return CadTransform.identity();
		return CadTransform.rotation( getOrigin(), CadPoints.UNIT_Z, rotate );
	}

	public CadOrientation getOrientation() {
		return calcOrientation( getOrigin(), calcRotate() );
	}

	public static CadOrientation calcOrientation( Point3D origin, double rotate ) {
		return new CadOrientation( origin, CadPoints.UNIT_Z, CadGeometry.rotate360( CadPoints.UNIT_Y, rotate ) );
	}

	/**
	 * Get a map of the shape information keyed by RB label.
	 *
	 * @return the map of the shape information
	 */
	public Map<String, Object> getInformation() {
		return Map.of();
	}

	@SuppressWarnings( "MethodDoesntCallSuperMethod" )
	public DesignShape clone() {
		// NOTE Immediately adding the cloned shape to the layer will trigger the modified flag
		return cloneShape();
	}

	protected DesignShape cloneShape() {
		throw new UnsupportedOperationException();
	}

	public void apply( CadTransform transform ) {
		throw new UnsupportedOperationException();
	}

	protected Map<String, Object> asMap() {
		Map<String, Object> map = super.asMap();
		map.putAll( asMap( ORIGIN ) );
		return map;
	}

	public DesignShape updateFrom( Map<String, Object> map ) {
		super.updateFrom( map );
		setOrigin( ParseUtil.parsePoint3D( (String)map.get( ORIGIN ) ) );
		if( map.containsKey( ROTATE ) ) setRotate( (String)map.get( ROTATE ) );
		return this;
	}

	public DesignShape updateFrom( DesignShape shape ) {
		try( Txn ignore = Txn.create() ) {
			this.setOrigin( shape.getOrigin() );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to update curve" );
		}

		return this;
	}

	@Override
	public <T extends Node> Comparator<T> getComparator() {
		return ( a, b ) -> {
			if( a instanceof DesignShape && b instanceof DesignShape ) {
				return ((DesignShape)b).getOrder() - ((DesignShape)a).getOrder();
			} else {
				return 0;
			}
		};
	}

}

