package com.avereon.cartesia.data;

import com.avereon.cartesia.ParseUtil;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.data.Node;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import com.avereon.zarra.javafx.Fx;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.Comparator;
import java.util.Map;

@CustomLog
public abstract class DesignShape extends DesignDrawable {

	public enum Type {
		ARC,
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

	public static final String SELECTED = "selected";

	public static final String REFERENCE = "reference";

	public DesignShape() {
		this( null );
	}

	public DesignShape( Point3D origin ) {
		addModifyingKeys( ORIGIN );
		setOrigin( origin );
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

	public Bounds getBounds() {
		return Fx.EMPTY_BOUNDS;
	}

	public double distanceTo( Point3D point ) {
		return Double.NaN;
	}

	public double pathLength() {
		return Double.NaN;
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

