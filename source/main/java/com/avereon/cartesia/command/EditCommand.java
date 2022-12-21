package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadOrientation;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.cartesia.tool.DesignTool;
import com.avereon.data.NodeLink;
import com.avereon.transaction.Txn;
import javafx.geometry.Point3D;
import lombok.CustomLog;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@CustomLog
public abstract class EditCommand extends Command {

	private boolean copy;

	protected void setCloneShapeOnExecute() {
		this.copy = true;
	}

	protected Collection<DesignShape> getCommandShapes( DesignTool tool ) {
		return copy ? cloneAndAddShapes( tool.getSelectedGeometry() ) : tool.getSelectedGeometry();
	}

	protected void flipShapes( Collection<DesignShape> shapes, Point3D origin, Point3D point ) {
		if( CadGeometry.areSamePoint( origin, point ) ) return;
		transformShapes( shapes, CadTransform.mirror( origin, point ) );
	}

	protected void flipShapes( DesignTool tool, Point3D origin, Point3D point ) {
		if( CadGeometry.areSamePoint( origin, point ) ) return;
		apply( tool, CadTransform.mirror( origin, point ) );
	}

	protected void moveShapes( Collection<DesignShape> shapes, Point3D anchor, Point3D target ) {
		transformShapes( shapes, CadTransform.translation( target.subtract( anchor ) ) );
	}

	protected void moveShapes( DesignTool tool, Point3D anchor, Point3D target ) {
		apply( tool, CadTransform.translation( target.subtract( anchor ) ) );
	}

	protected void rotateShapes( Collection<DesignShape> shapes, Point3D center, double angle ) {
		transformShapes( shapes, CadTransform.rotation( center, CadPoints.UNIT_Z, angle ) );
	}

	protected void rotateShapes( DesignTool tool, Point3D center, Point3D anchor, Point3D target ) {
		apply( tool, CadTransform.rotation( center, CadPoints.UNIT_Z, CadGeometry.pointAngle360( anchor, center, target ) ) );
	}

	protected void scaleShapes( Collection<DesignShape> shapes, Point3D anchor, Point3D source, Point3D target ) {
		transformShapes( shapes, getScaleTransform( anchor, source, target ) );
	}

	protected void scaleShapes( DesignTool tool, Point3D anchor, Point3D source, Point3D target ) {
		apply( tool, getScaleTransform( anchor, source, target ) );
	}

	private CadTransform getScaleTransform( Point3D anchor, Point3D source, Point3D target ) {
		// This implementation uses a rotate/scale/-rotate transform
		Point3D base = source.subtract( anchor );
		Point3D stretch = target.subtract( anchor );

		// Create an orientation such that the z-axis is aligned with the base
		CadOrientation orientation = new CadOrientation( anchor, base );

		double scale = stretch.dotProduct( base ) / (base.magnitude() * base.magnitude());

		return orientation.getLocalToTargetTransform().combine( CadTransform.scale( 1, 1, scale ) ).combine( orientation.getTargetToLocalTransform() );
	}

	protected void deleteShapes( DesignTool tool ) {
		Txn.run( () -> tool.getSelectedGeometry().stream().filter( s -> s.getLayer() != null ).forEach( s -> s.getLayer().removeShape( s ) ) );
	}

	private void transformShapes( Collection<DesignShape> shapes, CadTransform transform ) {
		Txn.run( () -> shapes.forEach( s -> s.apply( transform ) ) );
	}

	protected void apply( DesignTool tool, CadTransform transform ) {
		Collection<DesignShape> originalShapes = tool.getSelectedGeometry();

		// Clone
		Collection<DesignShape> cloneShapes = Set.of();
		if( copy ) {
			cloneShapes = originalShapes.stream().map( s -> {
				DesignShape clone = s.clone().setSelected( false ).setReference( false );
				clone.setValue( "clone-layer", NodeLink.of( s.getLayer() ) );
				// NOTE Reference flag should be set before adding shape to layer, otherwise reference shapes will trigger the modified flag
				//if( s.getLayer() != null ) s.getLayer().addShape( clone );
				return clone;
			} ).collect( Collectors.toList() );
		}

		// Transform
		Collection<DesignShape> shapes = copy ? cloneShapes : originalShapes;
		Txn.run( () -> shapes.forEach( s -> s.apply( transform ) ) );

		// Add
		if( copy ) {
			Txn.run( () -> {
				shapes.forEach( s -> {
					((NodeLink<DesignLayer>)s.getValue( "clone-layer" )).getNode().addShape( s );
					s.setValue( "clone-layer", null );
				} );
			} );
		}
	}

}
