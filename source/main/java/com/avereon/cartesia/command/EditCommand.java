package com.avereon.cartesia.command;

import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadGeometry;
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

	protected void reflipShapes( Collection<DesignShape> shapes, Point3D origin, Point3D lastPoint, Point3D point ) {
		transformShapes( shapes, CadTransform.mirror( origin, point ).combine( CadTransform.mirror( origin, lastPoint ) ) );
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

	protected void scaleShapes( Collection<DesignShape> shapes, Point3D center, Point3D anchor, Point3D target ) {
		double zX = (target.getX() - center.getX()) / (anchor.getX() - center.getX());
		double zY = (target.getY() - center.getY()) / (anchor.getY() - center.getY());
		double zZ = (target.getZ() - center.getZ()) / (anchor.getZ() - center.getZ());
		transformShapes( shapes, CadTransform.scale( center, zX, zY, 1 ) );
	}

	protected void rescaleShapes( Collection<DesignShape> shapes, Point3D center, Point3D anchor, Point3D prior, Point3D target ) {
		double zX = (target.getX() - center.getX()) / (anchor.getX() - center.getX());
		double zY = (target.getY() - center.getY()) / (anchor.getY() - center.getY());
		double zZ = (target.getZ() - center.getZ()) / (anchor.getZ() - center.getZ());
		double izX = 1 / (prior.getX() - center.getX()) / (anchor.getX() - center.getX());
		double izY = 1 / (prior.getY() - center.getY()) / (anchor.getY() - center.getY());
		double izZ = 1 / (prior.getZ() - center.getZ()) / (anchor.getZ() - center.getZ());
		transformShapes( shapes, CadTransform.scale( center, zX, zY, 1 ).combine( CadTransform.scale( center, izX, izY, 1 ) ) );
	}

	protected void scaleShapes( DesignTool tool, Point3D center, Point3D anchor, Point3D target ) {
		double zX = (target.getX() - center.getX()) / (anchor.getX() - center.getX());
		double zY = (target.getY() - center.getY()) / (anchor.getY() - center.getY());
		double zZ = (target.getZ() - center.getZ()) / (anchor.getZ() - center.getZ());
		apply( tool, CadTransform.scale( center, zX, zY, 1 ) );
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
