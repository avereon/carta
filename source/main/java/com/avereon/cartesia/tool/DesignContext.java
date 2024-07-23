package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.xenon.XenonProgramProduct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
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

	private final ObservableSet<DesignShape> previewShapes;

	// Strategies for handling selected shapes:
	//
	// Desired outcome: The user can select shapes by clicking on them. Shapes
	// show that they are selected in all views. This requires that the selected
	// state be store somewhere that is accessible to all views:
	//
	// 1. Store the selected state in the DesignShape class. This is the most direct
	// 	approach, but it requires that the DesignShape class be modified to support
	// 	the selected state.
	// 2. Store the selected state in the DesignContext class. This is a more indirect
	// 	approach, but it does not require that the DesignShape class be modified.
	// 3. Store the selected state in the DesignRenderer class. This is a more indirect
	// 	approach, but it requires that each view maintain a set of selected shapes.

	// If we choose the DesignContext approach, we need to add an observable
	// collection to that class for the selected shapes. This collection would
	// be updated by the DesignRenderer when shapes are selected or deselected.
	// Listeners would need to be added to the collection to update the selected
	// flag (used for optimization) on the shapes before rendering the views.
	// The selected flag on the DesignShape class would be converted to a simple
	// boolean property since it does not participate in model events.

	private final ObservableSet<DesignShape> selectedShapes;

	private final CommandContext commandContext;

	private final CoordinateStatus coordinateStatus;

	public DesignContext( XenonProgramProduct product, Design design ) {
		this.product = product;
		this.design = design;
		this.previewShapes = FXCollections.synchronizedObservableSet( FXCollections.observableSet() );
		this.selectedShapes = FXCollections.synchronizedObservableSet( FXCollections.observableSet() );
		this.commandContext = new CommandContext( product );
		this.coordinateStatus = new CoordinateStatus();
	}

	public void setMouse( MouseEvent event ) {
		BaseDesignTool tool = (BaseDesignTool)event.getSource();
		Point3D screenMouse = new Point3D( event.getX(), event.getY(), event.getZ() );
		getCommandContext().setScreenMouse( screenMouse );

		Point3D worldMouse = tool.mouseToWorkplane( screenMouse );
		getCommandContext().setWorldMouse( worldMouse );
		getCoordinateStatus().updatePosition( worldMouse );
		getCoordinateStatus().updateZoom( tool.getZoom() );
	}
}




