package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.command.draw.DrawPath;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.math.*;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
		// In favor of InvalidInputException
		@Deprecated INVALID,
		// Indicates that the command failed to execute
		FAILURE
	}

	@Getter
	private final List<DesignShape> reference;

	@Getter
	private final List<DesignShape> preview;

	private final Map<DesignShape, DesignShape> previewMap;

	@Getter
	private int step;

	protected Command() {
		this.reference = new CopyOnWriteArrayList<>();
		this.preview = new CopyOnWriteArrayList<>();
		this.previewMap = new ConcurrentHashMap<>();
	}

	/**
	 * Execute the command.
	 * <p>
	 * The result of the command execution is one of the following:
	 * </p>
	 * <ul>
	 *   <li>An object - The result of the successful command execution</li>
	 *   <li>{@link Result#INCOMPLETE} - The command needs more parameters</li>
	 *   <li>{@link Result#SUCCESS} - The command executed successfully, but doesn't have a return value</li>
	 *   <li>{@link Result#FAILURE} - The command failed to execute</li>
	 *   <li>Exception - The command failed to execute</li>
	 *  </ul>
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

	public DesignCommandContext.Input getInputMode() {
		return DesignCommandContext.Input.NONE;
	}

	public boolean clearSelectionWhenComplete() {
		return true;
	}

	public boolean clearReferenceAndPreviewWhenComplete() {
		return true;
	}

	public void handle( CommandTask task, KeyEvent event ) {
		handle( task.getContext(), event );
	}

	public void handle( CommandTask task, MouseEvent event ) {
		handle( task.getContext(), event );
	}

	@Deprecated
	public void handle( DesignCommandContext context, KeyEvent event ) {}

	@Deprecated
	public void handle( DesignCommandContext context, MouseEvent event ) {}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	protected double asDouble( CommandTask task, String rbKey, int index ) throws Exception {
		return asDouble( task, rbKey, Point3D.ZERO, index );
	}

	protected double asDouble( CommandTask task, String rbKey, Point3D anchor, int index ) throws Exception {
		return asDouble( task, rbKey, anchor, task.getParameter( index ) );
	}

	protected double asDouble( CommandTask task, String rbKey, Point3D anchor, Object value ) throws Exception {
		// The value may already be a number
		if( value instanceof Double ) return (Double)value;
		// The value may already be a point
		if( value instanceof Point3D ) return ((Point3D)value).distance( anchor );

		// The value may parse to be a point
		Point3D point = CadShapes.parsePoint( String.valueOf( value ), anchor );
		// If it is a point, return the distance from the anchor
		if( point != null ) return point.distance( anchor );

		// The value may parse to be a number
		try {
			return CadMath.eval( String.valueOf( value ) );
		} catch( CadMathExpressionException exception ) {
			throw new InvalidInputException( task.getCommand(), rbKey, value );
		}
	}

	@Deprecated
	protected double asDouble( Object value ) throws Exception {
		if( value instanceof Double ) return (Double)value;
		if( value instanceof Point3D ) return ((Point3D)value).distance( Point3D.ZERO );
		return CadMath.eval( String.valueOf( value ) );
	}

	@Deprecated
	protected double asDouble( Point3D anchor, Object value ) throws Exception {
		if( value instanceof Double ) return (Double)value;
		if( value instanceof Point3D ) return ((Point3D)value).distance( anchor );
		return CadMath.eval( String.valueOf( value ) );
	}

	protected Point3D asPoint( CommandTask task, String rbKey, int index ) throws Exception {
		return asPoint( task, rbKey, task.getParameter( index ) );
	}

	protected Point3D asPoint( CommandTask task, String rbKey, Object value ) throws InvalidInputException {
		return asPoint( task, task.getContext().getWorldAnchor(), rbKey, value );
	}

	protected Point3D asPoint( CommandTask task, Point3D anchor, String rbKey, Object value ) throws InvalidInputException {
		return asPoint( task, anchor, rbKey, value, true );
	}

	protected Point3D asPoint( CommandTask task, Point3D anchor, String rbKey, Object value, boolean snap ) throws InvalidInputException {
		Point3D point = null;

		if( value instanceof Point3D ) {
			point = (Point3D)value;
		} else if( value instanceof String ) {
			point = CadShapes.parsePoint( (String)value, anchor );
			if( point == null ) throw new InvalidInputException( task.getCommand(), rbKey, value );
		}

		return snap ? task.getTool().snapToWorkplane( point ) : point;
	}

	protected Point3D asPoint( CommandTask task, String rbKey, InputEvent event ) throws InvalidInputException {
		Point3D point = null;
		if( event instanceof MouseEvent mouseEvent ) {
			point = task.getTool().screenToWorld( new Point3D( mouseEvent.getX(), mouseEvent.getY(), 0 ) );
		}
		if( point == null ) throw new InvalidInputException( task.getCommand(), rbKey, event );
		return asPoint( task, task.getContext().getWorldAnchor(), rbKey, point );
	}

	protected Point3D asPointWithoutSnap( CommandTask task, String rbKey, int index ) throws Exception {
		return asPoint( task, task.getContext().getWorldAnchor(), rbKey, task.getParameter( index ), false );
	}

	@Deprecated
	protected Point3D asPointFromEvent( CommandTask task ) {
		if( task.getEvent() instanceof MouseEvent mouseEvent ) return task.getTool().screenToWorld( new Point3D( mouseEvent.getX(), mouseEvent.getY(), 0 ) );
		return null;
	}

	@Deprecated
	protected Point3D asPoint( Point3D anchor, Object value ) throws Exception {
		if( value instanceof Point3D ) return (Point3D)value;
		return CadShapes.parsePoint( String.valueOf( value ), anchor );
	}

	@Deprecated
	protected Point3D asPoint( DesignCommandContext context, Object value ) throws Exception {
		return asPoint( context.getWorldAnchor(), value );
	}

	@Deprecated
	@SuppressWarnings( "unused" )
	protected Bounds asBounds( DesignCommandContext context, Object value ) {
		// NOTE Users cannot input bounds by hand so this method may not be necessary
		if( value instanceof Bounds ) return (Bounds)value;
		//Point3D anchor = context.getScreenMouse();
		//return CadShapes.parseBounds( String.valueOf( value ), anchor );
		return null;
	}

	protected String asText( CommandTask task, String rbKey, int index ) throws Exception {
		Object value = task.getParameter( index );
		if( value == null ) throw new InvalidInputException( task.getCommand(), rbKey, value );
		return String.valueOf( value );
	}

	@Deprecated
	@SuppressWarnings( "unused" )
	protected String asText( DesignCommandContext context, Object value ) {
		return String.valueOf( value );
	}

	protected void promptForNumber( CommandTask task, String key ) {
		task.getTool().setCursor( null );
		promptForValue( task.getContext(), key, DesignCommandContext.Input.NUMBER );
	}

	@Deprecated
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

	protected void promptForShape( CommandTask task, String key ) {
		task.getTool().setCursor( Cursor.HAND );
		promptForValue( task, key, DesignCommandContext.Input.NONE );
	}

	@Deprecated
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

	@Deprecated
	protected DesignShape findNearestShapeAtMouse( DesignCommandContext context, Point3D mouse ) {
		List<DesignShape> shapes = context.getTool().screenPointSyncFindOne( mouse );
		return shapes.isEmpty() ? DesignShape.NONE : shapes.getFirst();
	}

	protected DesignShape findNearestShapeAtPoint( CommandTask task, Point3D point ) {
		List<DesignShape> shapes = task.getTool().worldPointSyncFindOne( point );
		return shapes.isEmpty() ? DesignShape.NONE : shapes.getFirst();
	}

	@Deprecated
	protected DesignShape findNearestShapeAtPoint( DesignCommandContext context, Point3D point ) {
		List<DesignShape> shapes = context.getTool().worldPointSyncFindOne( point );
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

	protected void doNotCaptureUndoChanges( CommandTask task ) {
		setCaptureUndoChanges( task, false );
	}

	protected void setCaptureUndoChanges( CommandTask task, boolean enabled ) {
		task.getTool().getAsset().setCaptureUndoChanges( enabled );
	}

	@Deprecated
	protected void setCaptureUndoChanges( DesignCommandContext context, boolean enabled ) {
		context.getTool().getAsset().setCaptureUndoChanges( enabled );
	}

	protected void addReference( CommandTask task, DesignShape... shapes ) {
		addReference( task, Arrays.stream( shapes ).filter( Objects::nonNull ).collect( Collectors.toList() ) );
	}

	private void addReference( CommandTask task, Collection<DesignShape> shapes ) {
		task.getTool().getReferenceLayer().addShapes( shapes );
		this.reference.addAll( shapes );
	}

	protected void removeReference( CommandTask task, DesignShape... shapes ) {
		removeReference( task, Arrays.stream( shapes ).filter( Objects::nonNull ).collect( Collectors.toList() ) );
	}

	@Deprecated
	protected void addReference( DesignCommandContext context, DesignShape... shapes ) {
		addReference( context, Arrays.stream( shapes ).filter( Objects::nonNull ).collect( Collectors.toList() ) );
	}

	@Deprecated
	private void addReference( DesignCommandContext context, Collection<DesignShape> shapes ) {
		this.reference.addAll( shapes );
		final String referencePaint = Paints.toString( context.getTool().getSelectedDrawPaint() );
		this.reference.forEach( s -> {
			s.setPreview( true );
			s.setDrawPaint( referencePaint );
			// FIXME Should there be a specific reference layer? UX says yes!
			if( s.getLayer() == null ) context.getTool().getCurrentLayer().addShape( s );
		} );
	}

	@Deprecated
	protected void removeReference( DesignCommandContext context, DesignShape... shapes ) {
		removeReference( context, Arrays.stream( shapes ).filter( Objects::nonNull ).collect( Collectors.toList() ) );
	}

	@SuppressWarnings( "unused" )
	protected void removeReference( CommandTask task, Collection<DesignShape> shapes ) {
		task.getTool().getReferenceLayer().removeShapes( shapes );
		reference.removeAll( shapes );
	}

	@Deprecated
	@SuppressWarnings( "unused" )
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

	protected void addPreview( CommandTask task, DesignShape... shapes ) {
		addPreview( task, Arrays.stream( shapes ).filter( Objects::nonNull ).collect( Collectors.toList() ) );
	}

	@Deprecated
	protected void addPreview( DesignCommandContext context, DesignShape... shapes ) {
		addPreview( context, Arrays.stream( shapes ).filter( Objects::nonNull ).collect( Collectors.toList() ) );
	}

	protected DesignShape setAttributesFromLayer( DesignShape shape, DesignLayer layer ) {
		shape.setFillPaint( layer.getFillPaint() );
		shape.setDrawPaint( layer.getDrawPaint() );
		shape.setDrawWidth( layer.getDrawWidth() );
		shape.setDrawPattern( layer.getDrawPattern() );
		shape.setDrawCap( layer.getDrawCap() );
		shape.setDrawJoin( layer.getDrawJoin() );
		return shape;
	}

	protected DesignText setTextAttributesFromLayer( DesignText text, DesignLayer layer ) {
		text.setFillPaint( layer.getTextFillPaint() );
		text.setDrawPaint( layer.getTextDrawPaint() );
		text.setDrawWidth( layer.getTextDrawWidth() );
		text.setDrawPattern( layer.getTextDrawPattern() );

		text.setTextSize( layer.getTextSize() );
		text.setFontName( layer.getFontName() );
		text.setFontWeight( layer.getFontWeight() );
		text.setFontPosture( layer.getFontPosture() );
		text.setFontUnderline( layer.getFontUnderline() );
		text.setFontStrikethrough( layer.getFontStrikethrough() );

		return text;
	}

	protected void addPreview( CommandTask task, List<DesignShape> shapes ) {
		task.getTool().getPreviewLayer().addShapes( shapes );
		this.preview.addAll( shapes );
	}

	@Deprecated
	protected void addPreview( DesignCommandContext context, Collection<DesignShape> shapes ) {
		shapes.forEach( s -> s.setPreview( true ) );
		context.getTool().getPreviewLayer().addShapes( shapes );
		this.preview.addAll( shapes );
	}

	protected void resetPreviewGeometry() {
		previewMap.keySet().forEach( s -> previewMap.get( s ).updateFrom( s ) );
	}

	protected void removePreview( CommandTask task, DesignShape... shapes ) {
		removePreview( task.getContext(), Arrays.stream( shapes ).filter( Objects::nonNull ).collect( Collectors.toList() ) );
	}

	@Deprecated
	protected void removePreview( DesignCommandContext context, DesignShape... shapes ) {
		removePreview( context, Arrays.stream( shapes ).filter( Objects::nonNull ).collect( Collectors.toList() ) );
	}

	protected void removePreview( CommandTask task, Collection<DesignShape> shapes ) {
		if( shapes == null ) return;
		task.getTool().getPreviewLayer().removeShapes( shapes );
		preview.removeAll( shapes );
	}

	@Deprecated
	protected void removePreview( DesignCommandContext context, Collection<DesignShape> shapes ) {
		if( shapes == null ) return;
		context.getTool().getPreviewLayer().removeShapes( shapes );
		preview.removeAll( shapes );
	}

	protected void clearPreview( CommandTask task ) {
		// The shapes have to be removed before capturing undo changes again
		task.getTool().getPreviewLayer().clearShapes();
		preview.clear();
	}

	@Deprecated
	protected void clearPreview( DesignCommandContext context ) {
		// The shapes have to be removed before capturing undo changes again
		context.getTool().getPreviewLayer().clearShapes();
		preview.clear();
	}

	// NOTE Only for use by CommandTask
	void clearReferenceAndPreview( CommandTask task ) {
		clearReference( task );
		clearPreview( task );
	}

	@Deprecated
	public void clearReferenceAndPreview( DesignCommandContext context ) {
		clearReference( context );
		clearPreview( context );
	}

	protected DesignLine createReferenceLine( CommandTask task ) {
		DesignLine line = new DesignLine( task.getContext().getWorldMouse(), task.getContext().getWorldMouse() );
		addReference( task, line );
		return line;
	}

	protected DesignArc createReferenceArc( CommandTask task, Point3D origin ) {
		DesignArc arc = new DesignArc( origin, 0.0, 0.0, 360.0, DesignArc.Type.OPEN );
		addReference( task, arc );
		return arc;
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
