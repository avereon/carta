package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.transaction.Txn;
import com.avereon.util.Log;
import javafx.geometry.Point3D;

import java.util.Collection;

public abstract class EditCommand extends Command {

	private static final System.Logger log = Log.get();

	private boolean copy;

	protected void setCloneShapeOnExecute() {
		this.copy = true;
	}

	protected Collection<DesignShape> getExecuteShapes( DesignTool tool ) {
		return copy ? cloneShapes( tool.getSelectedGeometry() ) : tool.getSelectedGeometry();
	}

	protected void moveShapes( Collection<DesignShape> shapes, Point3D anchor, Point3D target ) {
		transformShapes( shapes, CadTransform.translation( target.subtract( anchor ) ) );
	}

	protected void copyShapes( Collection<DesignShape> shapes, Point3D anchor, Point3D target ) {
		moveShapes( shapes, anchor, target );
	}

	protected void rotateShapes( Collection<DesignShape> shapes, Point3D center, Point3D anchor, Point3D target ) {
		rotateShapes( shapes, center, CadGeometry.pointAngle360( anchor, center, target ) );
	}

	protected void rotateShapes( Collection<DesignShape> shapes, Point3D center, double angle ) {
		transformShapes( shapes, CadTransform.rotation( center, CadPoints.UNIT_Z, angle ) );
	}

	protected void radialCopyShapes( Collection<DesignShape> shapes, Point3D center, Point3D anchor, Point3D target ) {
		rotateShapes( shapes, center, anchor, target );
	}

	protected void scaleShapes( Collection<DesignShape> shapes, Point3D center, Point3D anchor, Point3D target ) {
		transformShapes( shapes, CadTransform.scale( center, target.distance( center ) / anchor.distance( center ) ) );
	}

	protected void rescaleShapes( Collection<DesignShape> shapes, Point3D center, Point3D anchor, Point3D lastPoint, Point3D target ) {
		transformShapes(
			shapes,
			CadTransform.scale( center, target.distance( center ) / anchor.distance( center ) ).combine( CadTransform.scale( center, 1 / (lastPoint.distance( center ) / anchor.distance( center )) ) )
		);
	}

	protected void flipShapes( Collection<DesignShape> shapes, Point3D origin, Point3D point ) {
		if( CadGeometry.areSamePoint( origin, point ) ) return;
		transformShapes( shapes, CadTransform.mirror( origin, point ) );
	}

	protected void reflipShapes( Collection<DesignShape> shapes, Point3D origin, Point3D lastPoint, Point3D point ) {
		transformShapes( shapes, CadTransform.mirror( origin, point ).combine( CadTransform.mirror( origin, lastPoint ) ) );
	}

	protected void deleteShapes( Collection<DesignShape> shapes ) {
		Txn.run( () -> shapes.stream().filter( s -> s.getLayer() != null ).forEach( s -> s.getLayer().removeShape( s ) ) );
	}

	private void transformShapes( Collection<DesignShape> shapes, CadTransform transform ) {
		Txn.run( () -> shapes.forEach( s -> s.apply( transform ) ) );
	}

}
