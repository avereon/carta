package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.Design;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.xenon.XenonProgramProduct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import lombok.Getter;

import java.util.concurrent.CopyOnWriteArraySet;

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

	private final ObservableSet<DesignShape> selectedShapes;

	private final CommandContext commandContext;

	private final CoordinateStatus coordinateStatus;

	public DesignContext( XenonProgramProduct product, Design design ) {
		this.product = product;
		this.design = design;
		this.previewShapes = FXCollections.observableSet( new CopyOnWriteArraySet<>() );
		this.selectedShapes = FXCollections.observableSet( new CopyOnWriteArraySet<>() );
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




