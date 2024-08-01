package com.avereon.cartesia.tool;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.command.Command;
import com.avereon.util.ArrayUtil;
import javafx.scene.input.InputEvent;
import lombok.CustomLog;
import lombok.Getter;

import java.util.Objects;

@CustomLog
class CommandTask {

	private final DesignCommandContext context;

	@Getter
	private final BaseDesignTool tool;

	@Getter
	private final CommandTrigger trigger;

	@Getter
	private final InputEvent event;

	@Getter
	private final Command command;

	@Getter
	private Object[] parameters;

	@Getter
	private Object result;

	public CommandTask( DesignCommandContext context, BaseDesignTool tool, CommandTrigger trigger, InputEvent event, Command command, Object... parameters ) {
		this.context = Objects.requireNonNull( context );
		this.tool = Objects.requireNonNull( tool );
		this.trigger = trigger;
		this.event = event;
		this.command = Objects.requireNonNull( command );
		this.parameters = parameters;
	}

	public Object executeCommandStep( Object priorResult ) throws Exception {
		// NOTE Be judicious adding logic in this method, it is called for every step in a command

		if( result == Command.FAIL ) return Command.FAIL;
		if( priorResult == null ) log.atWarning().log( "A prior result of null was passed to execute" );
		if( priorResult == Command.INCOMPLETE ) log.atWarning().log( "A prior result of INCOMPLETE was passed to execute" );
		if( priorResult != null && priorResult != Command.COMPLETE ) parameters = ArrayUtil.append( parameters, priorResult );

		Object result = Command.INVALID;
		try {
			context.setTool( tool );
			// FIXME  Should I just pass the execute context? I think so.
			result = command.execute( context, trigger, event, parameters );
			if( result != Command.INVALID ) command.incrementStep();
		} catch( Exception exception ) {
			log.atWarn( exception ).log( "Unhandled error executing command=%s", command );
		} finally {
			if( result == Command.COMPLETE || result == Command.INVALID ) doComplete();
			command.setStepExecuted();
		}

		this.result = result;

		return getResult();
	}

	private void doComplete() {
		if( command.clearSelectionWhenComplete() ) tool.clearSelectedShapes();
	}

	public void cancel() {
		try {
			command.cancel( context );
		} catch( Exception exception ) {
			log.atSevere().withCause( exception ).log();
		}
	}

	@Override
	public String toString() {
		return command + "{step=" + command.getStep() + " parms=" + parameters.length + "}";
	}

}
