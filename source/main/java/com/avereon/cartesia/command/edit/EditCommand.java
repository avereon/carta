package com.avereon.cartesia.command.edit;

import com.avereon.cartesia.command.Command;
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
import java.util.List;

@CustomLog
public abstract class EditCommand extends Command {

	private static final String CLONE_LAYER_KEY = "clone-layer";

	private boolean copy;

	protected void setCloneShapeOnExecute() {
		this.copy = true;
	}

	protected void flipShapes( Collection<DesignShape> shapes, Point3D origin, Point3D point ) {
		if( CadGeometry.areSamePoint( origin, point ) ) return;
		transformShapes( shapes, CadTransform.mirror( origin, point ) );
	}

	protected void flipShapes( DesignTool tool, Point3D origin, Point3D point ) {
		if( CadGeometry.areSamePoint( origin, point ) ) return;
		applyToSelected( tool, CadTransform.mirror( origin, point ) );
	}

	protected void moveShapes( Collection<DesignShape> shapes, Point3D anchor, Point3D target ) {
		transformShapes( shapes, CadTransform.translation( target.subtract( anchor ) ) );
	}

	protected void moveShapes( DesignTool tool, Point3D anchor, Point3D target ) {
		applyToSelected( tool, CadTransform.translation( target.subtract( anchor ) ) );
	}

	protected void rotateShapes( Collection<DesignShape> shapes, Point3D center, double angle ) {
		transformShapes( shapes, CadTransform.rotation( center, CadPoints.UNIT_Z, angle ) );
	}

	protected void rotateShapes( DesignTool tool, Point3D center, Point3D anchor, Point3D target ) {
		rotateShapes( tool, center, CadGeometry.pointAngle360( anchor, center, target ) );
	}

	protected void rotateShapes( DesignTool tool, Point3D center, double angle ) {
		applyToSelected( tool, CadTransform.rotation( center, CadPoints.UNIT_Z, angle ) );
	}

	protected void scaleShapes( Collection<DesignShape> shapes, Point3D anchor, Point3D source, Point3D target ) {
		transformShapes( shapes, CadTransform.scale( anchor, source, target ) );
	}

	protected void scaleShapes( DesignTool tool, Point3D anchor, Point3D source, Point3D target ) {
		applyToSelected( tool, CadTransform.scale( anchor, source, target ) );
	}

	protected void scaleShapes( DesignTool tool, Point3D anchor, double scale ) {
		applyToSelected( tool, CadTransform.scale( anchor, scale ) );
	}

	protected void stretchShapes( Collection<DesignShape> shapes, Point3D anchor, Point3D source, Point3D target ) {
		transformShapes( shapes, CadTransform.stretch( anchor, source, target ) );
	}

	protected void stretchShapes( DesignTool tool, Point3D anchor, Point3D source, Point3D target ) {
		applyToSelected( tool, CadTransform.stretch( anchor, source, target ) );
	}

	protected void deleteShapes( DesignTool tool ) {
		Txn.run( () -> tool.getSelectedShapes().stream().filter( s -> s.getLayer() != null ).forEach( s -> s.getLayer().removeShape( s ) ) );
	}

	private void transformShapes( Collection<DesignShape> shapes, CadTransform transform ) {
		Txn.run( () -> shapes.forEach( s -> s.apply( transform ) ) );
	}

	protected void applyToSelected( DesignTool tool, CadTransform transform ) {
		apply( tool.getSelectedShapes(), transform );
	}

	protected void apply( List<DesignShape> shapes, CadTransform transform ) {
		// Determine what shapes to transform
		List<DesignShape> transformingShapes = copy ? clone( shapes ) : shapes;

		// Transform the shapes
		Txn.run( () -> transformingShapes.forEach( s -> s.apply( transform ) ) );

		// Add the shapes to their respective layers if needed
		if( copy ) store( transformingShapes );
	}

	private List<DesignShape> clone( List<DesignShape> shapes ) {
		return shapes.stream().map( shape -> {
			DesignShape clone = shape.clone();
			clone.setValue( CLONE_LAYER_KEY, NodeLink.of( shape.getLayer() ) );
			return clone;
		} ).toList();
	}

	private void store( List<DesignShape> shapes ) {
		Txn.run( () -> shapes.forEach( shape -> {
			NodeLink<DesignLayer> link = shape.getValue( CLONE_LAYER_KEY );
			link.getNode().addShape( shape );
			shape.setValue( CLONE_LAYER_KEY, null );
		} ) );
	}

}
