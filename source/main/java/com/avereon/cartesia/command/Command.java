package com.avereon.cartesia.command;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.command.base.Prompt;
import com.avereon.cartesia.command.draw.DrawPath;
import com.avereon.cartesia.data.*;
import com.avereon.cartesia.math.*;
import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.product.Rb;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lombok.CustomLog;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
		// Indicates that the command failed to execute
		FAILURE
	}

	@Getter
	private final List<DesignShape> reference;

	@Getter
	private final List<DesignShape> preview;

	// The preview map is used to store the original geometry of preview shapes
	// so that they can be reset when requested, usually when the user is
	// interacting with preview geometry.
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

	public void handle( CommandTask task, KeyEvent event ) {}

	public void handle( CommandTask task, MouseEvent event ) {}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	protected double asDoubleOrNan( CommandTask task, int index ) {
		return asDoubleOrNan( task, task.getParameter( index ) );
	}

	protected double asDoubleOrNan( CommandTask task, Object value ) {
		if( value instanceof Double ) return (Double)value;
		try {
			return CadMath.eval( String.valueOf( value ) );
		} catch( CadMathExpressionException exception ) {
			return Double.NaN;
		}
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

		try {
			return CadMath.eval( String.valueOf( value ) );
		} catch( CadMathExpressionException ignored ) {}

		// If it is a point, return the distance from the anchor
		Point3D point = CadShapes.parsePoint( String.valueOf( value ), anchor );
		if( point != null ) return point.distance( anchor );

		throw new InvalidInputException( task.getCommand(), rbKey, value );
	}

	protected Point3D asPoint( CommandTask task, String rbKey, int index ) throws Exception {
		return asPoint( task, rbKey, task.getParameter( index ), true );
	}

	protected Point3D asPoint( CommandTask task, String rbKey, Object value ) throws InvalidInputException {
		return asPoint( task, rbKey, value, true );
	}

	protected Point3D asPoint( CommandTask task, String rbKey, Object value, boolean snap ) throws InvalidInputException {
		Point3D point = null;

		if( value instanceof Point3D ) {
			point = (Point3D)value;
		} else if( value instanceof String ) {
			point = CadShapes.parsePoint( (String)value, task.getContext().getWorldAnchor() );
			if( point == null ) throw new InvalidInputException( task.getCommand(), rbKey, value );
		}

		task.getContext().setWorldAnchor( point );

		return point;
	}

	protected Point3D asPoint( CommandTask task, String rbKey, InputEvent event ) throws InvalidInputException {
		Point3D point = null;
		if( event instanceof MouseEvent mouseEvent ) {
			point = task.getTool().screenToWorld( new Point3D( mouseEvent.getX(), mouseEvent.getY(), 0 ) );
		}
		if( point == null ) throw new InvalidInputException( task.getCommand(), rbKey, event );
		return asPoint( task, rbKey, point, true );
	}

	protected String asText( CommandTask task, String rbKey, int index ) throws Exception {
		Object value = task.getParameter( index );
		if( value == null ) throw new InvalidInputException( task.getCommand(), rbKey, null );
		return String.valueOf( value );
	}

	protected void promptForNumber( CommandTask task, String key ) {
		task.getTool().setCursor( null );
		promptForValue( task, key, DesignCommandContext.Input.NUMBER );
	}

	protected void promptForPoint( CommandTask task, String key ) {
		task.getTool().setCursor( task.getTool().getReticleCursor() );
		promptForValue( task, key, DesignCommandContext.Input.POINT );
	}

	protected void promptForWindow( CommandTask task, String key ) {
		task.getTool().setCursor( task.getTool().getReticleCursor() );
		promptForValue( task, key, DesignCommandContext.Input.POINT );
	}

	protected void promptForShape( CommandTask task, String key ) {
		task.getTool().setCursor( Cursor.HAND );
		promptForValue( task, key, DesignCommandContext.Input.SHAPE );
	}

	protected void promptForText( CommandTask task, String key ) {
		task.getTool().setCursor( Cursor.TEXT );
		promptForValue( task, key, DesignCommandContext.Input.TEXT );
	}

	protected DesignShape findNearestShapeAtPoint( CommandTask task, Point3D point ) {
		List<DesignShape> shapes = task.getTool().worldPointSyncFindOne( point );
		return shapes.isEmpty() ? DesignShape.NONE : shapes.getFirst();
	}

	protected DesignShape selectNearestShapeAtPoint( CommandTask task, Point3D point ) {
		List<DesignShape> shapes = task.getTool().worldPointSyncSelect( point );
		return shapes.isEmpty() ? DesignShape.NONE : shapes.getFirst();
	}

	protected void doNotCaptureUndoChanges( CommandTask task ) {
		setCaptureUndoChanges( task, false );
	}

	protected void setCaptureUndoChanges( CommandTask task, boolean enabled ) {
		task.getTool().getAsset().setCaptureUndoChanges( enabled );
	}

	protected void addReference( CommandTask task, DesignShape... shapes ) {
		addReference( task, Arrays.stream( shapes ).filter( Objects::nonNull ).collect( Collectors.toList() ) );
	}

	private void addReference( CommandTask task, Collection<DesignShape> shapes ) {
		DesignLayer referenceLayer = task.getTool().getReferenceLayer();
		if( referenceLayer != null ) referenceLayer.addShapes( shapes );
		this.reference.addAll( shapes );
	}

	protected void removeReference( CommandTask task, DesignShape... shapes ) {
		removeReference( task, Arrays.stream( shapes ).filter( Objects::nonNull ).collect( Collectors.toList() ) );
	}

	@SuppressWarnings( "unused" )
	protected void removeReference( CommandTask task, Collection<DesignShape> shapes ) {
		DesignLayer referenceLayer = task.getTool().getReferenceLayer();
		if( referenceLayer != null ) referenceLayer.removeShapes( shapes );
		reference.removeAll( shapes );
	}

	protected void clearReference( CommandTask task ) {
		// The shapes have to be removed before capturing undo changes again
		removeReference( task, reference );
		reference.clear();
	}

	protected DesignShape setAttributesFromLayer( DesignShape shape, DesignLayer layer ) {
		if( layer == null ) return shape;

		shape.setFillPaint( layer.getFillPaint() );
		shape.setDrawPaint( layer.getDrawPaint() );
		shape.setDrawWidth( layer.getDrawWidth() );
		shape.setDashPattern( layer.getDashPattern() );
		shape.setDrawCap( layer.getDrawCap() );
		shape.setDrawJoin( layer.getDrawJoin() );

		if( shape instanceof DesignText text ) {
			text.setFillPaint( layer.getTextFillPaint() );
			text.setDrawPaint( layer.getTextDrawPaint() );
			text.setDrawWidth( layer.getTextDrawWidth() );
			text.setDashPattern( layer.getTextDrawPattern() );

			text.setTextSize( layer.getTextSize() );
			text.setFontName( layer.getFontName() );
			text.setFontWeight( layer.getFontWeight() );
			text.setFontPosture( layer.getFontPosture() );
			text.setFontUnderline( layer.getFontUnderline() );
			text.setFontStrikethrough( layer.getFontStrikethrough() );
		}

		return shape;
	}

	protected DesignLine createPreviewLine( CommandTask task ) {
		DesignLine line = new DesignLine( task.getContext().getWorldMouse(), task.getContext().getWorldMouse() );
		addPreview( task, setAttributesFromLayer( line, task.getTool().getCurrentLayer() ) );
		return line;
	}

	protected DesignBox createPreviewBox( CommandTask task ) {
		DesignBox box = new DesignBox( task.getContext().getWorldMouse(), Point3D.ZERO );
		addPreview( task, setAttributesFromLayer( box, task.getTool().getCurrentLayer() ) );
		return box;
	}

	protected DesignArc createPreviewArc( CommandTask task, Point3D origin ) {
		DesignArc arc = new DesignArc( origin, 0.0, 0.0, 360.0, DesignArc.Type.OPEN );
		addPreview( task, setAttributesFromLayer( arc, task.getTool().getCurrentLayer() ) );
		return arc;
	}

	protected DesignArc createPreviewArc3( CommandTask task, Point3D start, Point3D mid ) {
		DesignArc arc = CadGeometry.arcFromThreePoints( start, mid, mid );
		addPreview( task, setAttributesFromLayer( arc, task.getTool().getCurrentLayer() ) );
		return arc;
	}

	protected DesignEllipse createPreviewEllipse( CommandTask task, Point3D origin ) {
		DesignEllipse ellipse = new DesignEllipse( origin, 0.0, 0.0 );
		addPreview( task, setAttributesFromLayer( ellipse, task.getTool().getCurrentLayer() ) );
		return ellipse;
	}

	protected DesignEllipse createPreviewEllipse3( CommandTask task, Point3D start, Point3D mid ) {
		DesignArc arc = CadGeometry.arcFromThreePoints( start, mid, mid );
		DesignEllipse ellipse = new DesignEllipse( arc.getOrigin(), arc.getXRadius(), arc.getYRadius() );
		addPreview( task, setAttributesFromLayer( ellipse, task.getTool().getCurrentLayer() ) );
		return ellipse;
	}

	protected DesignCubic createPreviewCubic( CommandTask task ) {
		Point3D worldMouse = task.getContext().getWorldMouse();
		DesignCubic cubic = new DesignCubic( worldMouse, worldMouse, worldMouse, worldMouse );
		addPreview( task, setAttributesFromLayer( cubic, task.getTool().getCurrentLayer() ) );
		return cubic;
	}

	protected DesignMarker createPreviewMarker( CommandTask task ) {
		DesignMarker marker = new DesignMarker( task.getContext().getWorldMouse() );
		addPreview( task, setAttributesFromLayer( marker, task.getTool().getCurrentLayer() ) );
		return marker;
	}

	protected DesignText createPreviewText( CommandTask task ) {
		DesignText text = new DesignText( task.getContext().getWorldMouse() );
		addPreview( task, setAttributesFromLayer( text, task.getTool().getCurrentLayer() ) );
		return text;
	}

	protected void createPreviewShapes( CommandTask task, List<DesignShape> shapes ) {
		List<DesignShape> preview = shapes.stream().map( shape -> {
			DesignShape clone = setAttributesFromLayer( shape.clone(), shape.getLayer() );
			previewMap.put( shape, clone );
			return clone;
		} ).toList();
		addPreview( task, preview );
	}

	private void addPreview( CommandTask task, DesignShape... shapes ) {
		addPreview( task, Arrays.stream( shapes ).filter( Objects::nonNull ).collect( Collectors.toList() ) );
	}

	private void addPreview( CommandTask task, List<DesignShape> shapes ) {
		DesignLayer previewLayer = task.getTool().getPreviewLayer();
		if( previewLayer != null) previewLayer.addShapes( shapes );
		preview.addAll( shapes );
	}

	protected void resetPreviewGeometry() {
		previewMap.forEach( ( k, v ) -> v.updateFrom( k ) );
	}

	protected void removePreview( CommandTask task, DesignShape... shapes ) {
		removePreview( task, Arrays.stream( shapes ).filter( Objects::nonNull ).collect( Collectors.toList() ) );
	}

	protected void removePreview( CommandTask task, Collection<DesignShape> shapes ) {
		if( shapes == null ) return;
		DesignLayer previewLayer = task.getTool().getPreviewLayer();
		if( previewLayer != null) previewLayer.removeShapes( shapes );
		preview.removeAll( shapes );
	}

	protected void clearPreview( CommandTask task ) {
		// The shapes have to be removed before capturing undo changes again
		DesignLayer previewLayer = task.getTool().getPreviewLayer();
		if(previewLayer != null) previewLayer.clearShapes();
		preview.clear();
	}

	void clearReferenceAndPreview( CommandTask task ) {
		clearReference( task );
		clearPreview( task );
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

	private void promptForValue( CommandTask task, String key, DesignCommandContext.Input mode ) {
		String text = Rb.text( RbKey.PROMPT, key );
		task.getContext().submit( task.getTool(), new Prompt( text, mode ) );
	}

	private static double deriveRotatedArcAngle( Point3D center, double xRadius, double yRadius, double rotate, Point3D point ) {
		CadTransform t = DesignEllipse.calcLocalTransform( center, xRadius, yRadius, rotate );

		double angle = CadGeometry.angle360( t.apply( point ) );
		if( angle <= -180 ) angle += 360;
		if( angle > 180 ) angle -= 360;

		return angle;
	}

}
