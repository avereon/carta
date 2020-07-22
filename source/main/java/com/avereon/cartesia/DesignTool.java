package com.avereon.cartesia;

import com.avereon.cartesia.cursor.StandardCursor;
import com.avereon.util.Log;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.xenon.workspace.Workspace;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.NonInvertibleTransformException;

public abstract class DesignTool extends ProgramTool {

	private static final System.Logger log = Log.get();

	private final CommandPrompt prompt;

	private final CoordinateStatus coordinates;

	private final DesignPane geometry;

	private Point2D dragAnchor;

	private Point2D panAnchor;

	public DesignTool( ProgramProduct product, Asset asset ) {
		super( product, asset );

		addStylesheet( CartesiaMod.STYLESHEET );

		this.prompt = new CommandPrompt( product );
		this.coordinates = new CoordinateStatus( product );

		// This pane will "steal" the key events
		geometry = new DesignPane();
		geometry.setManaged( false );
		getChildren().add( geometry );

		// Initial values from settings
		setCursor( StandardCursor.valueOf( product.getSettings().get( "reticle", StandardCursor.DUPLEX.name() ).toUpperCase() ) );

		// Settings listeners
		product.getSettings().register( "reticle", e -> setCursor( StandardCursor.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );

		addEventFilter( KeyEvent.ANY, this::key );
		addEventFilter( MouseEvent.MOUSE_MOVED, this::mouse );
		addEventFilter( MouseEvent.MOUSE_PRESSED, this::anchor );
		addEventFilter( MouseEvent.MOUSE_DRAGGED, this::drag );
		addEventFilter( ScrollEvent.SCROLL, geometry::zoom );

		geometry.zoomProperty().addListener( ( v, o, n ) -> getCoordinateStatus().updateZoom( n.doubleValue() ) );
	}

	private void key( KeyEvent event ) {
		getCommandPrompt().update( this, event );
	}

	private void mouse( MouseEvent event ) {
		Point3D point = mouseToWorld( event.getX(), event.getY(), event.getZ() );
		getCoordinateStatus().updatePosition( point.getX(), point.getY(), point.getZ() );
	}

	private void anchor( MouseEvent event ) {
		if( event.isShiftDown() && event.isPrimaryButtonDown() ) {
			panAnchor = new Point2D( geometry.getTranslateX(), geometry.getTranslateY() );
			dragAnchor = new Point2D( event.getX(), event.getY() );
		}
	}

	private void drag( MouseEvent event ) {
		if( event.isPrimaryButtonDown() && event.isShiftDown() ) geometry.pan( panAnchor, dragAnchor, event.getX(), event.getY() );
	}

	public DesignUnit getDesignUnit() {
		return DesignUnit.CENTIMETER;
	}

	protected Point3D mouseToWorld( double x, double y, double z ) {
		try {
			return geometry.getLocalToParentTransform().inverseTransform( x, y, z );
		} catch( NonInvertibleTransformException exception ) {
			return new Point3D( Double.NaN, Double.NaN, Double.NaN );
		}
	}

	@Override
	protected void activate() throws ToolException {
		super.activate();
		Workspace workspace = getWorkspace();
		if( workspace != null ) {
			workspace.getStatusBar().addLeft( getCommandPrompt() );
			workspace.getStatusBar().addRight( getCoordinateStatus() );
		}
		getCommandPrompt().clear();
		requestFocus();
	}

	@Override
	protected void deactivate() throws ToolException {
		super.conceal();
		Workspace workspace = getWorkspace();
		if( workspace != null ) {
			workspace.getStatusBar().removeRight( getCoordinateStatus() );
			workspace.getStatusBar().removeLeft( getCommandPrompt() );
		}
	}

	private void setCursor( StandardCursor cursor ) {
		setCursor( cursor.get() );
	}

	private CommandPrompt getCommandPrompt() {
		return prompt;
	}

	private CoordinateStatus getCoordinateStatus() {
		return coordinates;
	}

}
