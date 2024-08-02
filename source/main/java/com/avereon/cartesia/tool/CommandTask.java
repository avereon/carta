package com.avereon.cartesia.tool;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.command.Command;
import com.avereon.util.ArrayUtil;
import javafx.scene.input.InputEvent;
import lombok.CustomLog;
import lombok.Getter;

import java.util.Objects;

@Getter
@CustomLog
public class CommandTask {

	private final DesignCommandContext context;

	private final DesignTool tool;

	private final CommandTrigger trigger;

	private final InputEvent event;

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

	public Object executeCommandStep( Object priorResult ) throws Exception {
		// NOTE Be judicious adding logic in this method, it is called for every step in a command

		if( result == Command.FAILURE ) return Command.FAILURE;
		if( priorResult == null ) log.atWarning().log( "A prior result of null was passed to execute" );
		if( priorResult == Command.INCOMPLETE ) log.atWarning().log( "A prior result of INCOMPLETE was passed to execute" );
		if( priorResult != null && priorResult != Command.SUCCESS ) parameters = ArrayUtil.append( parameters, priorResult );

		Object result = Command.INVALID;
		try {
			context.setTool( tool );
			result = command.execute( this );
			if( result != Command.INVALID ) command.incrementStep();
		} catch( Exception exception ) {
			log.atWarn( exception ).log( "Unhandled error executing command=%s", command );
		} finally {
			if( result == Command.SUCCESS || result == Command.INVALID ) doComplete();
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
			command.cancel( this );
		} catch( Exception exception ) {
			log.atSevere().withCause( exception ).log();
		}
	}

	@Override
	public String toString() {
		return command + "{step=" + command.getStep() + " parms=" + parameters.length + "}";
	}

}
