package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandException;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.CadTransform;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnException;
import javafx.geometry.Point3D;

import java.util.List;

public abstract class EditCommand extends Command {

	protected void moveShapes( List<DesignShape> shapes, Point3D anchor, Point3D target ) throws CommandException {
		CadTransform transform = CadTransform.translation( target.subtract( anchor ) );
		try( Txn ignore = Txn.create() ) {
			shapes.forEach( s -> s.apply( transform ) );
		} catch( TxnException exception ) {
			throw new CommandException( "Error moving shapes", exception );
		}
	}

}
