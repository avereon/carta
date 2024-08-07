package com.avereon.cartesia.tool;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.command.Command;
import com.avereon.util.ArrayUtil;
import com.avereon.zarra.javafx.Fx;
import javafx.scene.input.InputEvent;
import lombok.CustomLog;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.avereon.cartesia.command.Command.Result.*;

@Getter
@CustomLog
public class CommandTask {

	private static final Set<Command.Result> RESULT_CACHE = new HashSet<>( Arrays.asList( Command.Result.values() ) );

	private final DesignCommandContext context;

	private final DesignTool tool;

	private CommandTrigger trigger;

	private InputEvent event;

	private final Command command;

	private Object[] parameters;

	private Object result;

	public CommandTask( DesignCommandContext context, DesignTool tool, CommandTrigger trigger, InputEvent event, Command command, Object... parameters ) {
		this.context = Objects.requireNonNull( context );
		this.tool = Objects.requireNonNull( tool );
		this.trigger = trigger;
		this.event = event;
		this.command = Objects.requireNonNull( command );
		this.parameters = parameters;
	}

	public int getParameterCount() {
		return parameters.length;
	}

	public boolean hasParameter( int index ) {
		return index < parameters.length;
	}

	public Object getParameter( int index ) {
		return parameters.length > index ? parameters[ index ] : null;
	}

	public void addParameter( Object priorResult ) {
		if( priorResult == null ) log.atWarning().log( "A prior result of null was passed to execute" );
		if( priorResult == INCOMPLETE ) log.atWarning().log( "A prior result of INCOMPLETE was passed to execute" );
		if( priorResult == null || priorResult instanceof Command.Result && RESULT_CACHE.contains( priorResult ) ) return;

		if( priorResult instanceof Object[] priorResultArray ) {
			parameters = concatArrays( parameters, priorResultArray );
		} else {
			parameters = ArrayUtil.append( parameters, priorResult );
		}

		// Clear the trigger and event when a prior result is added
		trigger = null;
		event = null;
	}

	private static <T> T[] concatArrays( T[] array1, T[] array2 ) {
		T[] result = Arrays.copyOf( array1, array1.length + array2.length );
		System.arraycopy( array2, 0, result, array1.length, array2.length );
		return result;
	}

	public Object runTaskStep() throws Exception {
		// NOTE Be judicious adding logic in this method, it is called for every step in a command

		if( Fx.isFxThread() ) {
			log.atError().log( "CommandTask.runTaskStep() called on FX thread" );
		}

		// If this task is already failed, do not run the step
		if( result == FAILURE ) return FAILURE;

		Object result = INVALID;
		try {
			context.setTool( tool );
			result = command.execute( this );
			if( result != INVALID ) command.incrementStep();
		} finally {
			if( result != INCOMPLETE ) doComplete();
			command.setStepExecuted();
		}

		this.result = result;

		return getResult();
	}

	private void doComplete() {
		if( command.clearReferenceAndPreviewWhenComplete() ) command.clearReferenceAndPreview( this );
		if( command.clearSelectionWhenComplete() ) tool.clearSelectedShapes();
		tool.setSelectAperture( null, null );
	}

	public void cancel() {
		try {
			command.cancel( this );
		} catch( Exception exception ) {
			log.atSevere().withCause( exception ).log();
		}
	}

	@Override
	public String toString() {
		String step = String.valueOf( command.getStep() );
		String evnt = event == null ? "null" : event.getEventType().toString();
		String prms = Arrays.toString( parameters );
		return command + "{step=" + step + " evnt=" + evnt + " prms=" + prms + "}";
	}

}
