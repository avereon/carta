package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandException;
import com.avereon.cartesia.data.DesignDrawable;
import com.avereon.cartesia.data.DesignLayer;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadGeometry;
import com.avereon.cartesia.math.CadPoints;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.Point3D;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class EditCommand extends Command {

	protected void moveShapes( Collection<DesignShape> shapes, Point3D anchor, Point3D target ) throws CommandException {
		CadTransform transform = CadTransform.translation( target.subtract( anchor ) );
		try( Txn ignore = Txn.create() ) {
			shapes.forEach( s -> s.apply( transform ) );
		} catch( TxnException exception ) {
			throw new CommandException( "Error moving shapes", exception );
		}
	}

	protected void copyShapes( Collection<DesignShape> shapes, Point3D anchor, Point3D target ) throws CommandException {
		Map<DesignShape, DesignLayer> cloneLayers = shapes.stream().collect( Collectors.toMap( DesignShape::clone, DesignDrawable::getLayer ) );
		Set<DesignShape> clones = cloneLayers.keySet();
		moveShapes( clones, anchor, target );
		clones.forEach( c -> cloneLayers.get( c ).addShape( c ) );
	}

	protected void rotateShapes( Collection<DesignShape> shapes, Point3D center, Point3D anchor, Point3D target ) {
		rotateShapes( shapes, center, CadGeometry.pointAngle360( anchor, center, target ) );
	}

	protected void rotateShapes( Collection<DesignShape> shapes, Point3D center, double angle ) {
		CadTransform transform = CadTransform.rotation( center, CadPoints.UNIT_Z, angle );
		shapes.forEach( s -> s.apply( transform ) );
	}

	protected void radialCopyShapes( Collection<DesignShape> shapes, Point3D center, Point3D anchor, Point3D target ) {
//		Map<DesignShape, DesignLayer> cloneLayers = shapes.stream().collect( Collectors.toMap( DesignShape::clone, DesignDrawable::getParentLayer ) );
//		Set<DesignShape> clones = cloneLayers.keySet();
//		rotateShapes( clones, center, anchor, target );
//		clones.forEach( c -> cloneLayers.get( c ).addShape( c ) );
	}

}
