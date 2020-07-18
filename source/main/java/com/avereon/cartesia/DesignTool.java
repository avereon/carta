package com.avereon.cartesia;

import com.avereon.cartesia.cursor.StandardCursor;
import com.avereon.util.Log;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.workpane.ToolException;
import com.avereon.xenon.workspace.Workspace;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public abstract class DesignTool extends ProgramTool {

	private static final System.Logger log = Log.get();

	private static final double SCALE_FACTOR = Math.sqrt( 2 );

	private final CommandPrompt prompt;

	private final CoordinateStatus coordinates;

	private final Pane geometry;

	private Point3D dragAnchor;

	private Point3D translateAnchor;

	public DesignTool( ProgramProduct product, Asset asset ) {
		super( product, asset );

		addStylesheet( CartesiaMod.STYLESHEET );

		this.prompt = new CommandPrompt( this );
		this.coordinates = new CoordinateStatus( this );

		// This pane will "steal" the key events
		geometry = new Pane();
		geometry.setManaged( false );
		getChildren().add( geometry );

		geometry.getChildren().add( new Line( -10, -10, 10, 10 ) );
		geometry.getChildren().add( new Line( 10, -10, -10, 10 ) );

		// Initial values
		geometry.setScaleY( -1 );

		// Initial values from settings
		setCursor( StandardCursor.valueOf( product.getSettings().get( "reticle", StandardCursor.DUPLEX.name() ).toUpperCase() ) );

		// Settings listeners
		product.getSettings().register( "reticle", e -> setCursor( StandardCursor.valueOf( String.valueOf( e.getNewValue() ).toUpperCase() ) ) );

		addEventFilter( KeyEvent.ANY, getCommandPrompt()::update );
		addEventFilter( MouseEvent.MOUSE_MOVED, getCoordinateStatus()::update );
		addEventFilter( MouseEvent.MOUSE_PRESSED, e -> {
			dragAnchor = new Point3D( e.getX(), e.getY(), e.getZ() );
			translateAnchor = new Point3D( geometry.getTranslateX(), geometry.getTranslateY(), 0.0 );
		} );
		addEventFilter( MouseEvent.MOUSE_DRAGGED, this::translate );
		addEventFilter( ScrollEvent.SCROLL, this::rescale );
	}

	protected abstract Point3D mouseToWorld( MouseEvent event );

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

	private void translate( MouseEvent event ) {
		if( event.isPrimaryButtonDown() && event.isShiftDown() ) {
			geometry.setTranslateX( translateAnchor.getX() + ((event.getX() - dragAnchor.getX())) );
			geometry.setTranslateY( translateAnchor.getY() + ((event.getY() - dragAnchor.getY())) );
		}
	}

	private void rescale( ScrollEvent event ) {
		double delta = event.getDeltaY();

		log.log( Log.WARN, "event={0},{1}", event.getX(), event.getY() );

		double dx = event.getX() - geometry.getTranslateX();
		double dy = event.getY() - geometry.getTranslateY();

		// TODO Subtract the event point
		//geometry.setTranslateX( -dx  );
		//geometry.setTranslateY( -dy );

		if( delta > 0 ) {
			// Zoom in
			geometry.setScaleX( geometry.getScaleX() * SCALE_FACTOR );
			geometry.setScaleY( geometry.getScaleY() * SCALE_FACTOR );
		} else if( delta < 0 ) {
			// Zoom out
			geometry.setScaleX( geometry.getScaleX() / SCALE_FACTOR );
			geometry.setScaleY( geometry.getScaleY() / SCALE_FACTOR );
		}

		// TODO Add it back
		//geometry.setTranslateX( dx  );
		//geometry.setTranslateY( dy );
	}

}
