package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignShape;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.Getter;

/**
 * The DesignContext is for sharing design-related information between design
 * tools. It should only be accessed through the design tool and its associated
 * classes.
 * <pre>
 * DesignTool -> Design -> DesignContext -> CommandContext
 * </pre>
 */
@Getter
public class DesignContext {

	/**
	 * The design that this context belongs to.
	 */
	private final Design design;

	private final DesignCommandContext designCommandContext;

	private final ObservableList<DesignShape> previewShapes;

	private final ObservableList<DesignShape> selectedShapes;

	private final CoordinateStatus coordinateStatus;

	public DesignContext( Design design, DesignCommandContext commandContext ) {
		this.design = design;

		// In theory, commands are executed against a tool but scoped by the design,
		// so there should only be one command context per design, across all the
		// tools involved.
		this.designCommandContext = commandContext;

		this.previewShapes = FXCollections.synchronizedObservableList( FXCollections.observableArrayList() );
		this.selectedShapes = FXCollections.synchronizedObservableList( FXCollections.observableArrayList() );
		this.coordinateStatus = new CoordinateStatus();
	}

	public void setMouse( MouseEvent event ) {
		DesignCommandContext commandContext = getDesignCommandContext();
		CoordinateStatus coordinateStatus = getCoordinateStatus();
		BaseDesignTool tool = (BaseDesignTool)event.getSource();

		// Set the mouse position in screen coordinates
		Point2D screenMouse = new Point2D( event.getScreenX(), event.getScreenY() );
		commandContext.setScreenMouse( screenMouse );

		// Set the mouse position in local coordinates
		Point3D mouse = new Point3D( event.getX(), event.getY(), event.getZ() );
		commandContext.setLocalMouse( mouse );

		// Set the mouse position in world coordinates
		Point3D point = tool.screenToWorkplane( mouse );
		commandContext.setWorldMouse( point );

		// Update the position
		coordinateStatus.updatePosition( point );

		// While we're updating the position, may as well update the zoom
		coordinateStatus.updateZoom( tool.getViewZoom() );
	}
}




