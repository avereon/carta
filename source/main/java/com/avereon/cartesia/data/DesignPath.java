package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadTransform;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

public class DesignPath extends DesignShape {

	public static final String PATH = "path";

	public static final String CLOSED = "closed";

	private static final System.Logger log = Log.get();

	public DesignPath() {
		super( null );
	}

	@Override
	public double distanceTo( Point3D point ) {
		return Double.NaN;
	}

	@Override
	public DesignPath cloneShape() {
		return new DesignPath().copyFrom( this );
	}

	@Override
	public void apply( CadTransform transform ) {
		try( Txn ignored = Txn.create() ) {
			setOrigin( transform.apply( getOrigin() ) );
			//setOriginControl( transform.apply( getOriginControl() ) );
			//setPointControl( transform.apply( getPointControl() ) );
			//setPoint( transform.apply( getPoint() ) );
		} catch( TxnException exception ) {
			log.log( Log.WARN, "Unable to apply transform" );
		}
	}

}
