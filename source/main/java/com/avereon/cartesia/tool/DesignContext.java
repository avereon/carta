package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.xenon.XenonProgramProduct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.Getter;

/**
 * The DesignContext class is a container for design specific information. It is
 * normally accessed by the design tool and its associated classes.
 * <pre>
 * DesignTool -> Design -> DesignContext -> CommandContext
 * </pre>
 */
@Getter
public class DesignContext {

	private final XenonProgramProduct product;

	private final Design design;

	private final ObservableList<DesignShape> previewShapes;

	private final ObservableList<DesignShape> selectedShapes;

	private final CommandContext commandContext;

	private final CoordinateStatus coordinateStatus;

	public DesignContext( XenonProgramProduct product, Design design ) {
		this.product = product;
		this.design = design;
		this.previewShapes = FXCollections.synchronizedObservableList( FXCollections.observableArrayList() );
		this.selectedShapes = FXCollections.synchronizedObservableList( FXCollections.observableArrayList() );
		this.commandContext = new CommandContext( product );
		this.coordinateStatus = new CoordinateStatus();
	}

	public void setMouse( MouseEvent event ) {
		BaseDesignTool tool = (BaseDesignTool)event.getSource();

		// Set the mouse position in screen coordinates
		Point3D mouse = new Point3D( event.getX(), event.getY(), event.getZ() );
		getCommandContext().setScreenMouse( mouse );

		// Set the mouse position in world coordinates
		Point3D point = tool.screenToWorkplane( mouse );
		getCommandContext().setWorldMouse( point );

		// Update the position
		getCoordinateStatus().updatePosition( point );

		// While we're updating the position, may as well update the zoom
		getCoordinateStatus().updateZoom( tool.getZoom() );
	}
}




