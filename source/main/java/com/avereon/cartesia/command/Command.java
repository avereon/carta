package com.avereon.cartesia.command;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.*;
import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.view.DesignShapeView;
import com.avereon.product.Rb;
import com.avereon.zarra.color.Paints;
import com.avereon.zarra.javafx.FxUtil;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import lombok.CustomLog;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

@CustomLog
public class Command {

	public static final Object INCOMPLETE = new Object();

	public static final Object COMPLETE = new Object();

	public static final Object INVALID = new Object();

	public static final Object FAIL = new Object();

	private final List<DesignShape> reference;

	private final List<DesignShape> preview;

	private int step;

	private boolean stepExecuted;

	protected Command() {
		this.reference = new CopyOnWriteArrayList<>();
		this.preview = new CopyOnWriteArrayList<>();
	}

	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		return COMPLETE;
	}

	public void cancel( CommandContext context ) {
		if( context.getTool() != null ) {
			clearReferenceAndPreview( context );
			context.getTool().setCursor( Cursor.DEFAULT );
			context.getTool().getDesign().clearSelected();
		}
	}

	public int getStep() {
		return step;
	}

	public void incrementStep() {
		step++;
	}

	public synchronized void waitFor() throws InterruptedException {
		waitFor( 1000 );
	}

	public synchronized void waitFor( long length ) throws InterruptedException {
		while( !stepExecuted ) {
			wait( length );
		}
		this.stepExecuted = false;
	}

	public synchronized void setStepExecuted() {
		this.stepExecuted = true;
		notifyAll();
	}

	public CommandContext.Input getInputMode() {
		return CommandContext.Input.NONE;
	}

	public boolean clearSelectionWhenComplete() {
		return true;
	}

	public void handle( KeyEvent event ) {}

	public void handle( MouseEvent event ) {}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	protected double asDouble( Object value ) throws Exception {
		if( value instanceof Double ) return (Double)value;
		if( value instanceof Point3D ) return ((Point3D)value).distance( Point3D.ZERO );
		return CadMath.eval( String.valueOf( value ) );
	}

	protected double asDouble( Point3D anchor, Object value ) throws Exception {
		if( value instanceof Double ) return (Double)value;
		if( value instanceof Point3D ) return ((Point3D)value).distance( anchor );
		return CadMath.eval( String.valueOf( value ) );
	}

	protected Point3D asPoint( CommandContext context, Object value ) throws Exception {
		if( value instanceof Point3D ) return (Point3D)value;
		return CadShapes.parsePoint( String.valueOf( value ), context.getAnchor() );
	}

	protected Point3D asPoint( Point3D anchor, Object value ) throws Exception {
		if( value instanceof Point3D ) return (Point3D)value;
		return CadShapes.parsePoint( String.valueOf( value ), anchor );
	}

	protected Bounds asBounds( CommandContext context, Object value ) {
		if( value instanceof Bounds ) return (Bounds)value;
		//return CadShapes.parseBounds( String.valueOf( value ), anchor );
		return null;
	}

	protected void promptForNumber( CommandContext context, String key ) {
		context.getTool().setCursor( null );
		promptForValue( context, key, CommandContext.Input.NUMBER );
	}

	protected void promptForPoint( CommandContext context, String key ) {
		context.getTool().setCursor( context.getTool().getReticle().getCursorIcon( context.getProgram() ) );
		promptForValue( context, key, CommandContext.Input.POINT );
	}

	protected void promptForWindow( CommandContext context, String key ) {
		context.getTool().setCursor( context.getTool().getReticle().getCursorIcon( context.getProgram()) );
		promptForValue( context, key, CommandContext.Input.POINT );
	}

	protected void promptForShape( CommandContext context, String key ) {
		context.getTool().setCursor( Cursor.HAND );
		promptForValue( context, key, CommandContext.Input.NONE );
	}

	protected void promptForText( CommandContext context, String key ) {
		context.getTool().setCursor( Cursor.TEXT );
		promptForValue( context, key, CommandContext.Input.TEXT );
	}

	protected DesignShape findNearestShapeAtMouse( CommandContext context, Point3D mouse ) {
		List<Shape> shapes = context.getTool().screenPointFindOneAndWait( mouse );
		return shapes.isEmpty() ? DesignShape.NONE : DesignShapeView.getDesignData( shapes.get( 0 ) );
	}

	protected DesignShape findNearestShapeAtPoint( CommandContext context, Point3D point ) {
		return findNearestShapeAtMouse( context, context.getTool().worldToScreen( point ) );
	}

	protected DesignShape selectNearestShapeAtMouse( CommandContext context, Point3D mouse ) {
		List<Shape> shapes = context.getTool().screenPointSelectAndWait( mouse );
		return shapes.isEmpty() ? DesignShape.NONE : DesignShapeView.getDesignData( shapes.get( 0 ) );
	}

	protected DesignShape selectNearestShapeAtPoint( CommandContext context, Point3D point ) {
		return selectNearestShapeAtMouse( context, context.getTool().worldToScreen( point ) );
	}

	protected List<DesignShape> cloneAndAddShapes( Collection<DesignShape> shapes ) {
		return cloneAndAddShapes( shapes, false );
	}

	protected List<DesignShape> cloneAndAddReferenceShapes( Collection<DesignShape> shapes ) {
		return cloneAndAddShapes( shapes, true );
	}

	protected void setCaptureUndoChanges( CommandContext context, boolean enabled ) {
		context.getTool().getAsset().setCaptureUndoChanges( enabled );
	}

	protected Collection<DesignShape> getReference() {
		return this.reference;
	}

	protected void addReference( CommandContext context, DesignShape... shapes ) {
		addReference( context, List.of( shapes ) );
	}

	protected void addReference( CommandContext context, Collection<DesignShape> shapes ) {
		this.reference.addAll( shapes );
		final String referencePaint = Paints.toString( context.getTool().getSelectedDrawPaint() );
		this.reference.forEach( s -> {
			s.setReference( true );
			s.setDrawPaint( referencePaint );
			// FIXME Should there be a specific reference layer? UX says yes!
			if( s.getLayer() == null ) context.getTool().getCurrentLayer().addShape( s );
		} );
	}

	protected void removeReference( CommandContext context, DesignShape... shapes ) {
		removeReference( context, List.of( shapes ) );
	}

	protected void removeReference( CommandContext context, Collection<DesignShape> shapeList ) {
		shapeList.stream().filter( s -> s.getLayer() != null ).forEach( s -> s.getLayer().removeShape( s ) );
		reference.removeAll( shapeList );
	}

	protected void clearReference( CommandContext context ) {
		// The shapes have to be removed before capturing undo changes again
		removeReference( context, reference );
		reference.clear();
	}

	protected Collection<DesignShape> getPreview() {
		return this.preview;
	}

	protected void addPreview( CommandContext context, DesignShape... shapes ) {
		addPreview( context, List.of( shapes ) );
	}

	protected void addPreview( CommandContext context, Collection<DesignShape> shapes ) {
		this.preview.addAll( shapes );
		this.preview.forEach( s -> {
			s.setReference( true );
			if( s.getLayer() == null ) context.getTool().getCurrentLayer().addShape( s );
		} );
	}

	protected void removePreview( CommandContext context, DesignShape... shapes ) {
		removePreview( context, List.of( shapes ) );
	}

	protected void removePreview( CommandContext context, Collection<DesignShape> shapeList ) {
		shapeList.stream().filter( s -> s.getLayer() != null ).forEach( s -> s.getLayer().removeShape( s ) );
		preview.removeAll( shapeList );
	}

	protected void clearPreview( CommandContext context ) {
		// The shapes have to be removed before capturing undo changes again
		removePreview( context, preview );
		preview.clear();
	}

	protected void clearReferenceAndPreview( CommandContext context ) {
		clearReference( context );
		clearPreview( context );
	}

	/**
	 * Derive a start angle for an ellipse arc.
	 *
	 * @param center The arc center
	 * @param xRadius The arc xRadius
	 * @param yRadius The arc yRadius
	 * @param rotate The arc rotate angle
	 * @param point The point from which to derive the start angle
	 * @return The start angle
	 */
	protected double deriveStart( Point3D center, double xRadius, double yRadius, double rotate, Point3D point ) {
		return deriveRotatedArcAngle( center, xRadius, yRadius, rotate, point );
	}

	/**
	 * Derive an extent angle for an ellipse arc.
	 *
	 * @param center The arc center
	 * @param xRadius The arc xRadius
	 * @param yRadius The arc yRadius
	 * @param rotate The arc rotate angle
	 * @param start The arc start angle
	 * @param point The point from which to derive the extent angle
	 * @param spin The movement spin direction
	 * @return The extent angle
	 */
	protected double deriveExtent( Point3D center, double xRadius, double yRadius, double rotate, double start, Point3D point, double spin ) {
		double angle = deriveRotatedArcAngle( center, xRadius, yRadius, rotate, point ) - start;

		if( angle < 0 && spin > 0 ) angle += 360;
		if( angle > 0 && spin < 0 ) angle -= 360;

		return angle % 360;
	}

	/**
	 * Get the spin from the arc origin, through the last point to the next point.
	 * This will return 1.0 for a left-hand(CCW) spin or -1.0 for right-hand(CW)
	 * spin. If the spin cannot be determined or the points are collinear the
	 * prior spin is returned
	 *
	 * @param center The arc center
	 * @param xRadius The arc xRadius
	 * @param yRadius The arc yRadius
	 * @param rotate The arc rotate angle
	 * @param start The arc start angle
	 * @param lastPoint The last point
	 * @param nextPoint The next point
	 * @param priorSpin The prior spin
	 * @return 1.0 for CCW spin, -1.0 for CW spin or the prior spin
	 */
	protected double getExtentSpin( Point3D center, double xRadius, double yRadius, double rotate, double start, Point3D lastPoint, Point3D nextPoint, double priorSpin ) {
		if( lastPoint == null || nextPoint == null ) return priorSpin;

		// NOTE Rotate does not have eccentricity applied
		// NOTE Start does have eccentricity applied
		// This special transform takes into account the rotation and start angle
		double e = xRadius / yRadius;
		CadTransform transform = CadTransform
			.rotation( Point3D.ZERO, CadPoints.UNIT_Z, -start )
			.combine( CadTransform.scale( 1, e, 1 ) )
			.combine( CadTransform.rotation( Point3D.ZERO, CadPoints.UNIT_Z, -rotate ) )
			.combine( CadTransform.translation( center.multiply( -1 ) ) );

		Point3D lp = transform.apply( lastPoint );
		Point3D np = transform.apply( nextPoint );

		double spin = priorSpin;
		if( lp.getX() > 0 & np.getX() > 0 ) {
			if( np.getY() > 0 & (lp.getY() <= 0 || priorSpin == 0) ) spin = 1.0;
			if( np.getY() < 0 & (lp.getY() >= 0 || priorSpin == 0) ) spin = -1.0;
		}

		return spin;
	}

	protected Bounds getLocalShapeBounds( Collection<Shape> shapes ) {
		return getShapeBounds( shapes, Node::getBoundsInLocal );
	}

	protected Bounds getParentShapeBounds( Collection<Shape> shapes, Node target ) {
		return getShapeBounds( shapes, s -> FxUtil.localToParent( s, target ) );
	}

	private Bounds getShapeBounds( Collection<Shape> shapes, Function<Shape, Bounds> operator ) {
		if( shapes.isEmpty() ) return new BoundingBox( 0, 0, 0, 0 );

		Bounds shapeBounds = null;
		for( Shape s : shapes ) {
			shapeBounds = FxUtil.merge( shapeBounds, operator.apply( s ) );
		}

		// WORKAROUND for JDK-8145499: https://bugs.openjdk.java.net/browse/JDK-8145499
		shapeBounds = new BoundingBox( shapeBounds.getMinX() + 0.5, shapeBounds.getMinY() + 0.5, shapeBounds.getWidth() - 1, shapeBounds.getHeight() - 1 );

		return shapeBounds;
	}

	private void promptForValue( CommandContext context, String key, CommandContext.Input mode ) {
		String text = Rb.text( RbKey.PROMPT, key );
		context.submit( context.getTool(), new Prompt( text, mode ) );
	}

	private double deriveRotatedArcAngle( Point3D center, double xRadius, double yRadius, double rotate, Point3D point ) {
		CadTransform t = DesignEllipse.calcLocalTransform( center, xRadius, yRadius, rotate );

		double angle = CadGeometry.angle360( t.apply( point ) );
		if( angle <= -180 ) angle += 360;
		if( angle > 180 ) angle -= 360;

		return angle;
	}

	private List<DesignShape> cloneShapes( Collection<DesignShape> shapes, boolean reference ) {
		return shapes.stream().map( s -> {
			DesignShape clone = s.clone().setSelected( false ).setReference( reference );
			// NOTE Reference flag should be set before adding shape to layer, otherwise reference shapes will trigger the modified flag
			//if( s.getLayer() != null ) s.getLayer().addShape( clone );
			return clone;
		} ).collect( Collectors.toList() );
	}

	private List<DesignShape> cloneAndAddShapes( Collection<DesignShape> shapes, boolean reference ) {
		return shapes.stream().map( s -> {
			DesignShape clone = s.clone().setSelected( false ).setReference( reference );
			// NOTE Reference flag should be set before adding shape to layer, otherwise reference shapes will trigger the modified flag
			if( s.getLayer() != null ) s.getLayer().addShape( clone );
			return clone;
		} ).collect( Collectors.toList() );
	}

}
