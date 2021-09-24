package com.avereon.cartesia.data;

import com.avereon.cartesia.math.CadTransform;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.Point3D;
import lombok.CustomLog;

@CustomLog
public class DesignPath extends DesignShape {

	public static final String PATH = "path";

	public static final String CLOSED = "closed";

	public DesignPath() {
		super( null );
	}

	@Override
	public double distanceTo( Point3D point ) {
		return Double.NaN;
	}

	@Override
	public double pathLength() {
		return Double.NaN;
	}

	@Override
	public DesignPath cloneShape() {
		return new DesignPath().copyFrom( this, true );
	}

	@Override
	public void apply( CadTransform transform ) {
		try( Txn ignored = Txn.create() ) {
			setOrigin( transform.apply( getOrigin() ) );
			//setOriginControl( transform.apply( getOriginControl() ) );
			//setPointControl( transform.apply( getPointControl() ) );
			//setPoint( transform.apply( getPoint() ) );
		} catch( TxnException exception ) {
			log.atWarn().log( "Unable to apply transform" );
		}
	}

}
