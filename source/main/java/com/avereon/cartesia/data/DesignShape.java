package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.data.util.DesignShapeOrderComparator;
import com.avereon.cartesia.math.*;
import com.avereon.data.Node;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.shape.Shape;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CustomLog
@Accessors( chain = true )
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

	//public static final String SELECTED = "selected";

	//public static final String REFERENCE = "reference";

	private final Map<String, Object> cache;

	private static final String CACHE_BOUNDS = "bounds";

	private static final String CACHE_FX_SHAPE = "fx-shape";

	private static final String CACHE_SELECT_BOUNDS = "select-bounds";

	// Convenience method for rendering

	/**
	 * The preview flag is a special flag that indicates the shape is a preview
	 * shape. This flag is used to optimize the rendering process.
	 */
	@Getter
	@Setter
	@Deprecated
	private boolean preview;

	// Convenience method for rendering

	/**
	 * The selected flag is a special flag that indicates the shape is a selected
	 * shape. This flag is used to optimize the rendering process.
	 */
	@Getter
	@Setter
	private boolean selected;

	public DesignShape() {
		this( null );
	}

	public DesignShape( Point3D origin ) {
		addModifyingKeys( ORIGIN, ROTATE );
		setOrigin( origin );

		cache = new ConcurrentHashMap<>();
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

	/**
	 * Evaluate the rotation of the shape in degrees.
	 *
	 * @return The rotation in degrees
	 */
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

	@Override
	public <T> T setValue( String key, T newValue ) {
		if( getModifyingKeys().contains( key ) ) cache.clear();
		return super.setValue( key, newValue );
	}

	// Convenience method for selecting
	public Shape getFxShape() {
		return (Shape)cache.computeIfAbsent( CACHE_FX_SHAPE, k -> CadGeometry.toFxShape( this ) );
	}

	/**
	 * Get the geometric bounds of this shape. The geometric bounds include the
	 * geometry with the transforms applied, but not the stroke or effects.
	 *
	 * @return The geometric bounds of the shape
	 */
	public Bounds getBounds() {
		return (Bounds)cache.computeIfAbsent( CACHE_BOUNDS, k -> computeGeometricBounds() );
	}

	protected Bounds computeGeometricBounds() {
		Shape shape = getFxShape();
		shape.setStrokeWidth( 0 );
		return shape.getBoundsInParent();
	}

	/**
	 * This is a special implementation using FX to compute visual selection boundaries.
	 *
	 * @return The visual bounds of the shape
	 */
	public Bounds getSelectBounds() {
		return (Bounds)cache.computeIfAbsent( CACHE_SELECT_BOUNDS, k -> computeSelectBounds() );
	}

	protected Bounds computeSelectBounds() {
		Shape shape = getFxShape();
		shape.setStrokeWidth( calcDrawWidth() );
		return shape.getBoundsInParent();
	}

	public List<Point3D> getReferencePoints() {
		return List.of();
	}

	public List<Point3D> getConstructionPoints() {
		// A list of all the little points that make up the shape
		return List.of();
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
	public <T extends Node> Comparator<T> getNaturalComparator() {
		return ( a, b ) -> {
			if( a instanceof DesignShape && b instanceof DesignShape ) {
				return ((DesignShape)b).getOrder() - ((DesignShape)a).getOrder();
			} else {
				return 0;
			}
		};
	}

	public static Comparator<DesignShape> getComparator() {
		return new DesignShapeOrderComparator();
	}

	protected Map<String, Object> getCache() {
		return cache;
	}

}

