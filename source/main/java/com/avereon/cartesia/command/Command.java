package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.command.draw.DrawPath;
import com.avereon.cartesia.data.DesignEllipse;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.math.*;
import com.avereon.cartesia.tool.CommandTask;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.product.Rb;
import com.avereon.zarra.color.Paints;
import com.avereon.zarra.javafx.FxUtil;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.InputEvent;
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

import static com.avereon.cartesia.command.Command.Result.SUCCESS;

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
 * @see DesignCommandContext
 */
@CustomLog
public abstract class Command {

	public enum Result {
		// Indicates that the command need more parameters
		INCOMPLETE,
		// Indicates that the command executed successfully
		SUCCESS,
		// Indicates an invalid parameter was submitted
		INVALID,
		// Indicates that the command failed to execute
		FAILURE
	}

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

	/**
	 * Execute the command.
	 * <p>
	 * The result of the command execution is one of the following:
	 * </p>
	 *   <ul>
	 *     <li>An object - The result of the successful command execution</li>
	 *     <li>{@link Result#SUCCESS} - The command executed successfully, but doesn't have a return value</li>
	 *     <li>{@link Result#INCOMPLETE} - The command needs more parameters</li>
	 *     <li>{@link Result#INVALID} - The command received an invalid parameter</li>
	 *     <li>{@link Result#FAILURE} - The command failed to execute</li>
	 *     <li>Exception - The command failed to execute</li>
	 *    </ul>
	 *
	 * @param task The command task to execute
	 * @return The result of the command execution
	 * @throws Exception If the command execution fails
	 */
	public Object execute( CommandTask task ) throws Exception {
		return execute( task.getContext(), task.getTrigger(), task.getEvent(), task.getParameters() );
	}

	@Deprecated
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		return SUCCESS;
	}

	public void cancel( CommandTask task ) {
		if( task.getTool() != null ) {
			task.getTool().setCursor( Cursor.DEFAULT );
			task.getTool().clearSelectedShapes();
			clearReferenceAndPreview( task );
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

	public DesignCommandContext.Input getInputMode() {
		return DesignCommandContext.Input.NONE;
	}

	public boolean clearSelectionWhenComplete() {
		return true;
	}

	public boolean clearReferenceAndPreviewWhenComplete() {
		return true;
	}

	public void handle( DesignCommandContext context, KeyEvent event ) {}

	public void handle( DesignCommandContext context, MouseEvent event ) {}

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

	@Deprecated
	protected Point3D asPoint( DesignCommandContext context, Object value ) throws Exception {
		return asPoint( context.getWorldAnchor(), value );
	}

	protected Point3D asPoint( CommandTask task, int index ) throws Exception {
		return asPoint( task, task.getParameter( index ) );
	}

	protected Point3D asPoint( CommandTask task, Object value ) throws Exception {
		return asPoint( task.getContext().getWorldAnchor(), value );
	}

	protected Point3D asPoint( CommandTask task ) {
		if( task.getEvent() instanceof MouseEvent mouseEvent ) return task.getTool().screenToWorld( new Point3D( mouseEvent.getX(), mouseEvent.getY(), 0 ) );
		return null;
	}

	protected Point3D asPointFromEventOrParameter( CommandTask task, InputEvent event, Object value ) throws Exception {
		if( task.getEvent() instanceof MouseEvent mouseEvent ) return task.getTool().screenToWorld( new Point3D( mouseEvent.getX(), mouseEvent.getY(), 0 ) );
		return asPoint( task.getContext().getWorldAnchor(), value );
	}

	protected Point3D asPoint( Point3D anchor, Object value ) throws Exception {
		if( value instanceof Point3D ) return (Point3D)value;
		return CadShapes.parsePoint( String.valueOf( value ), anchor );
	}

	@Deprecated
	protected Bounds asBounds( DesignCommandContext context, Object value ) {
		// NOTE Users cannot input bounds by hand so this method may not be necessary
		if( value instanceof Bounds ) return (Bounds)value;
		//Point3D anchor = context.getScreenMouse();
		//return CadShapes.parseBounds( String.valueOf( value ), anchor );
		return null;
	}

	protected String asText( DesignCommandContext context, Object value ) throws Exception {
		return String.valueOf( value );
	}

	protected void promptForNumber( DesignCommandContext context, String key ) {
		context.getTool().setCursor( null );
		promptForValue( context, key, DesignCommandContext.Input.NUMBER );
	}

	protected void promptForPoint( CommandTask task, String key ) {
		task.getTool().setCursor( task.getTool().getReticleCursor() );
		promptForValue( task, key, DesignCommandContext.Input.POINT );
	}

	@Deprecated
	protected void promptForPoint( DesignCommandContext context, String key ) {
		context.getTool().setCursor( context.getTool().getReticleCursor() );
		promptForValue( context, key, DesignCommandContext.Input.POINT );
	}

	protected void promptForWindow( CommandTask task, String key ) {
		task.getTool().setCursor( task.getTool().getReticleCursor() );
		promptForValue( task, key, DesignCommandContext.Input.POINT );
	}

	protected void promptForShape( DesignCommandContext context, String key ) {
		context.getTool().setCursor( Cursor.HAND );
		promptForValue( context, key, DesignCommandContext.Input.NONE );
	}

	protected void promptForText( CommandTask task, String key ) {
		task.getTool().setCursor( Cursor.TEXT );
		promptForValue( task, key, DesignCommandContext.Input.TEXT );
	}

	@Deprecated
	protected void promptForText( DesignCommandContext context, String key ) {
		context.getTool().setCursor( Cursor.TEXT );
		promptForValue( context, key, DesignCommandContext.Input.TEXT );
	}

	protected DesignShape findNearestShapeAtMouse( DesignCommandContext context, Point3D mouse ) {
		List<DesignShape> shapes = context.getTool().screenPointSyncFindOne( mouse );
		return shapes.isEmpty() ? DesignShape.NONE : shapes.getFirst();
	}

	protected DesignShape findNearestShapeAtPoint( DesignCommandContext context, Point3D point ) {
		List<DesignShape> shapes = context.getTool().worldPointSyncFindOne( point );
		return shapes.isEmpty() ? DesignShape.NONE : shapes.getFirst();
	}

	protected DesignShape selectNearestShapeAtMouse( CommandTask task, Point3D mouse ) {
		List<DesignShape> shapes = task.getTool().screenPointSyncSelect( mouse );
		return shapes.isEmpty() ? DesignShape.NONE : shapes.getFirst();
	}

	@Deprecated
	protected DesignShape selectNearestShapeAtMouse( DesignCommandContext context, Point3D mouse ) {
		List<DesignShape> shapes = context.getTool().screenPointSyncSelect( mouse );
		return shapes.isEmpty() ? DesignShape.NONE : shapes.getFirst();
	}

	protected DesignShape selectNearestShapeAtPoint( CommandTask task, Point3D point ) {
		List<DesignShape> shapes = task.getTool().worldPointSyncSelect( point );
		return shapes.isEmpty() ? DesignShape.NONE : shapes.getFirst();
	}

	@Deprecated
	protected DesignShape selectNearestShapeAtPoint( DesignCommandContext context, Point3D point ) {
		List<DesignShape> shapes = context.getTool().worldPointSyncSelect( point );
		return shapes.isEmpty() ? DesignShape.NONE : shapes.getFirst();
	}

	protected Collection<DesignShape> cloneAndAddShapes( Collection<DesignShape> shapes ) {
		return cloneAndAddShapes( shapes, false );
	}

	protected Collection<DesignShape> cloneAndAddReferenceShapes( Collection<DesignShape> shapes ) {
		return cloneAndAddShapes( shapes, true );
	}

	protected void setCaptureUndoChanges( DesignCommandContext context, boolean enabled ) {
		context.getTool().getAsset().setCaptureUndoChanges( enabled );
	}

	protected Collection<DesignShape> getReference() {
		return this.reference;
	}

	protected void addReference( DesignCommandContext context, DesignShape... shapes ) {
		addReference( context, List.of( shapes ) );
	}

	protected void addReference( DesignCommandContext context, Collection<DesignShape> shapes ) {
		this.reference.addAll( shapes );
		final String referencePaint = Paints.toString( context.getTool().getSelectedDrawPaint() );
		this.reference.forEach( s -> {
			s.setPreview( true );
			s.setDrawPaint( referencePaint );
			// FIXME Should there be a specific reference layer? UX says yes!
			if( s.getLayer() == null ) context.getTool().getCurrentLayer().addShape( s );
		} );
	}

	protected void removeReference( CommandTask task, DesignShape... shapes ) {
		removeReference( task, List.of( shapes ) );
	}

	@Deprecated
	protected void removeReference( DesignCommandContext context, DesignShape... shapes ) {
		removeReference( context, List.of( shapes ) );
	}

	protected void removeReference( CommandTask task, Collection<DesignShape> shapes ) {
		shapes.stream().filter( s -> s.getLayer() != null ).forEach( s -> s.getLayer().removeShape( s ) );
		reference.removeAll( shapes );
	}

	@Deprecated
	protected void removeReference( DesignCommandContext context, Collection<DesignShape> shapes ) {
		shapes.stream().filter( s -> s.getLayer() != null ).forEach( s -> s.getLayer().removeShape( s ) );
		reference.removeAll( shapes );
	}

	protected void clearReference( CommandTask task ) {
		// The shapes have to be removed before capturing undo changes again
		removeReference( task, reference );
		reference.clear();
	}

	@Deprecated
	protected void clearReference( DesignCommandContext context ) {
		// The shapes have to be removed before capturing undo changes again
		removeReference( context, reference );
		reference.clear();
	}

	protected Collection<DesignShape> getPreview() {
		return this.preview;
	}

	protected void addPreview( DesignCommandContext context, DesignShape... shapes ) {
		addPreview( context, List.of( shapes ) );
	}

	protected void addPreview( DesignCommandContext context, Collection<DesignShape> shapes ) {
		this.preview.addAll( shapes );
		this.preview.forEach( s -> {
			s.setPreview( true );
			if( s.getLayer() == null ) context.getTool().getCurrentLayer().addShape( s );
		} );
	}

	protected void resetPreviewGeometry() {
		previewMap.keySet().forEach( s -> previewMap.get( s ).updateFrom( s ) );
	}

	protected void removePreview( CommandTask task, DesignShape... shapes ) {
		removePreview( task.getContext(), Set.of( shapes ) );
	}

	@Deprecated
	protected void removePreview( DesignCommandContext context, DesignShape... shapes ) {
		removePreview( context, Set.of( shapes ) );
	}

	protected void removePreview( CommandTask task, Collection<DesignShape> shapes ) {
		if( shapes == null ) return;
		shapes.stream().filter( s -> s.getLayer() != null ).forEach( s -> s.getLayer().removeShape( s ) );
		preview.removeAll( shapes );
	}

	@Deprecated
	protected void removePreview( DesignCommandContext context, Collection<DesignShape> shapes ) {
		if( shapes == null ) return;
		shapes.stream().filter( s -> s.getLayer() != null ).forEach( s -> s.getLayer().removeShape( s ) );
		preview.removeAll( shapes );
	}

	protected void clearPreview( CommandTask task ) {
		// The shapes have to be removed before capturing undo changes again
		removePreview( task, preview );
		preview.clear();
	}

	@Deprecated
	protected void clearPreview( DesignCommandContext context ) {
		// The shapes have to be removed before capturing undo changes again
		removePreview( context, preview );
		preview.clear();
	}

	public void clearReferenceAndPreview( CommandTask task ) {
		clearReference( task );
		clearPreview( task );
	}

	@Deprecated
	public void clearReferenceAndPreview( DesignCommandContext context ) {
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
	protected static double deriveStart( Point3D center, double xRadius, double yRadius, double rotate, Point3D point ) {
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
	protected static double deriveExtent( Point3D center, double xRadius, double yRadius, double rotate, double start, Point3D point, double spin ) {
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
	protected static double getExtentSpin( Point3D center, double xRadius, double yRadius, double rotate, double start, Point3D lastPoint, Point3D nextPoint, double priorSpin ) {
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

	@Deprecated
	protected Bounds getParentFxShapeBounds( Collection<Shape> shapes, Node target ) {
		return getFxShapeBounds( shapes, s -> FxUtil.localToParent( s, target ) );
	}

	@Deprecated
	private Bounds getFxShapeBounds( Collection<Shape> shapes, Function<Shape, Bounds> operator ) {
		if( shapes.isEmpty() ) return new BoundingBox( 0, 0, 0, 0 );

		Bounds shapeBounds = null;
		for( Shape s : shapes ) {
			shapeBounds = FxUtil.merge( shapeBounds, operator.apply( s ) );
		}

		// WORKAROUND for JDK-8145499: https://bugs.openjdk.java.net/browse/JDK-8145499
		shapeBounds = new BoundingBox( shapeBounds.getMinX() + 0.5, shapeBounds.getMinY() + 0.5, shapeBounds.getWidth() - 1, shapeBounds.getHeight() - 1 );

		return shapeBounds;
	}

	private void promptForValue( CommandTask task, String key, DesignCommandContext.Input mode ) {
		String text = Rb.text( RbKey.PROMPT, key );
		task.getContext().submit( task.getTool(), new Prompt( text, mode ) );
	}

	@Deprecated
	private void promptForValue( DesignCommandContext context, String key, DesignCommandContext.Input mode ) {
		String text = Rb.text( RbKey.PROMPT, key );
		context.submit( context.getTool(), new Prompt( text, mode ) );
	}

	private static double deriveRotatedArcAngle( Point3D center, double xRadius, double yRadius, double rotate, Point3D point ) {
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
