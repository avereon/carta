package com.avereon.cartesia.command;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.command.draw.DrawPath;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.*;
import com.avereon.cartesia.tool.CommandContext;
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
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Abstract class representing a command that can be executed in a design tool.
 *
 * <p>
 * A Command is responsible for executing a specific action or operation in the
 * design tool. It can perform operations such as creating or modifying design
 * shapes, selecting or deselecting shapes, and handling user input events.
 * </p>
 *
 * <p>
 * The Command class provides methods for managing the execution of the command,
 * such as setting the current step, waiting for a specific condition to be met,
 * and marking a step as executed.
 * </p>
 *
 * <p>
 * The Command class also provides utility methods for converting values to
 * specific data types, prompting for user input, and manipulating design
 * shapes.
 * </p>
 *
 * <h2>Writing Commands</h2>
 * <p>
 * Writing a command class requires understanding the intent of commands. A
 * command class need to handle three main use cases, scripted input,
 * interactive input and interactive events. A user may choose any of these
 * scenarios and the command must be able to interact with the user properly.
 * <p>
 * When a command is executed, it is given any parameters that might be
 * available. If all parameters are available then the command should execute
 * to completion. If all parameters are not available, the command should
 * execute to the point it can and interact with the user for any missing
 * parameters. The user may provide these parameters in the command prompt or
 * through input events such as mouse or gesture events.
 * <p>
 * As new parameters are made available the execute method is called to see
 * if the command can be executed to completion. If so, the command terminates
 * and control is returned to the command context. The execute method may be
 * called as many times as necessary while the user provides input. In the case
 * of some commands, such as the {@link DrawPath} command, the number of
 * parameters is undefined. For most commands, however, there are specific
 * parameters expected.
 *
 * @see DesignShape
 * @see CommandContext
 */
@CustomLog
public abstract class Command {

	public static final Object INCOMPLETE = new Object();

	public static final Object COMPLETE = new Object();

	public static final Object INVALID = new Object();

	public static final Object FAIL = new Object();

	private final Collection<DesignShape> reference;

	private final Collection<DesignShape> preview;

	private final Map<DesignShape, DesignShape> previewMap;

	@Getter
	private int step;

	private boolean stepExecuted;

	protected Command() {
		this.reference = new CopyOnWriteArraySet<>();
		this.preview = new CopyOnWriteArraySet<>();
		this.previewMap = new ConcurrentHashMap<>();
	}

	public Object execute( CommandContext context, Object... parameters ) throws Exception {
		return COMPLETE;
	}

	public void cancel( CommandContext context ) {
		if( context.getTool() != null ) {
			clearReferenceAndPreview( context );
			context.getTool().setCursor( Cursor.DEFAULT );
			context.getTool().clearSelectedShapes();
		}
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

	public void handle( CommandContext context, KeyEvent event ) {}

	public void handle( CommandContext context, MouseEvent event ) {}

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
		return CadShapes.parsePoint( String.valueOf( value ), context.getWorldAnchor() );
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

	protected String asText( CommandContext context, Object value ) throws Exception {
		return String.valueOf( value );
	}

	protected void promptForNumber( CommandContext context, String key ) {
		context.getTool().setCursor( null );
		promptForValue( context, key, CommandContext.Input.NUMBER );
	}

	protected void promptForPoint( CommandContext context, String key ) {
		context.getTool().setCursor( context.getTool().getReticleCursor() );
		promptForValue( context, key, CommandContext.Input.POINT );
	}

	protected void promptForWindow( CommandContext context, String key ) {
		context.getTool().setCursor( context.getTool().getReticleCursor() );
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
		List<DesignShape> shapes = context.getTool().screenPointSyncFindOne( mouse );
		return shapes.isEmpty() ? DesignShape.NONE : shapes.getFirst();
	}

	protected DesignShape findNearestShapeAtPoint( CommandContext context, Point3D point ) {
		List<DesignShape> shapes = context.getTool().worldPointSyncFindOne( point );
		return shapes.isEmpty() ? DesignShape.NONE : shapes.getFirst();
	}

	protected DesignShape selectNearestShapeAtMouse( CommandContext context, Point3D mouse ) {
		List<DesignShape> shapes = context.getTool().screenPointSyncSelect( mouse );
		return shapes.isEmpty() ? DesignShape.NONE : shapes.getFirst();
	}

	protected DesignShape selectNearestShapeAtPoint( CommandContext context, Point3D point ) {
		//return selectNearestShapeAtMouse( context, context.getTool().worldToScreen( point ) );
		List<DesignShape> shapes = context.getTool().worldPointSyncSelect( point );
		return shapes.isEmpty() ? DesignShape.NONE : shapes.getFirst();
	}

	protected Collection<DesignShape> cloneAndAddShapes( Collection<DesignShape> shapes ) {
		return cloneAndAddShapes( shapes, false );
	}

	protected Collection<DesignShape> cloneAndAddReferenceShapes( Collection<DesignShape> shapes ) {
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
			s.setPreview( true );
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
			s.setPreview( true );
			if( s.getLayer() == null ) context.getTool().getCurrentLayer().addShape( s );
		} );
	}

	protected void resetPreviewGeometry() {
		previewMap.keySet().forEach( s -> previewMap.get( s ).updateFrom( s ) );
	}

	protected void removePreview( CommandContext context, DesignShape... shapes ) {
		removePreview( context, Set.of( shapes ) );
	}

	protected void removePreview( CommandContext context, Collection<DesignShape> shapes ) {
		if( shapes == null ) return;
		shapes.stream().filter( s -> s.getLayer() != null ).forEach( s -> s.getLayer().removeShape( s ) );
		preview.removeAll( shapes );
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

	private Collection<DesignShape> cloneAndAddShapes( Collection<DesignShape> shapes, boolean preview ) {
		return shapes.stream().map( s -> {
			DesignShape clone = s.clone().setSelected( false ).setPreview( preview );
			previewMap.put( s, clone );
			// NOTE Reference flag should be set before adding shape to layer, otherwise reference shapes will trigger the modified flag
			if( s.getLayer() != null ) s.getLayer().addShape( clone );
			return clone;
		} ).collect( Collectors.toList() );
	}

}
