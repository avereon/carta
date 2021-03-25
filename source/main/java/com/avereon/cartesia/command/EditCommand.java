package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

import java.util.Collection;

public abstract class EditCommand extends Command {

	private static final System.Logger log = Log.get();

	protected void moveShapes( Collection<DesignShape> shapes, Point3D anchor, Point3D target ) {
		transformShapes( shapes, CadTransform.translation( target.subtract( anchor ) ) );
	}

	protected void copyShapes( Collection<DesignShape> shapes, Point3D anchor, Point3D target ) {
		moveShapes( cloneShapes( shapes ), anchor, target );
	}

	protected void rotateShapes( Collection<DesignShape> shapes, Point3D center, Point3D anchor, Point3D target ) {
		rotateShapes( shapes, center, CadGeometry.pointAngle360( anchor, center, target ) );
	}

	protected void rotateShapes( Collection<DesignShape> shapes, Point3D center, double angle ) {
		transformShapes( shapes, CadTransform.rotation( center, CadPoints.UNIT_Z, angle ) );
	}

	protected void radialCopyShapes( Collection<DesignShape> shapes, Point3D center, Point3D anchor, Point3D target ) {
		rotateShapes( cloneShapes( shapes ), center, anchor, target );
	}

	protected void flipShapes( Collection<DesignShape> shapes, Point3D origin, Point3D point ) {
		transformShapes( shapes, CadTransform.mirror( origin, point ) );
	}

	protected void mirrorShapes( Collection<DesignShape> shapes, Point3D anchor, Point3D target ) {
		flipShapes( cloneShapes( shapes ), anchor, target );
	}

	private void transformShapes( Collection<DesignShape> shapes, CadTransform transform ) {
		try( Txn ignore = Txn.create() ) {
			shapes.forEach( s -> s.apply( transform ) );
		} catch( TxnException exception ) {
			log.log( Log.ERROR, "Error transforming shapes", exception );
		}
	}

}
