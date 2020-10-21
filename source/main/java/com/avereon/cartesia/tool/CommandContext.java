package com.avereon.cartesia.tool;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.CommandMap;
import com.avereon.cartesia.OldCommand;
import com.avereon.util.Log;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.task.Task;
import javafx.event.EventType;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandContext {

	private static final System.Logger log = Log.get();

	private final ProgramProduct product;

	private final DesignContext designContext;

	// The incoming command queue
	private final BlockingQueue<CommandExecuteRequest> commandQueue;

	// The current command stack
	private final BlockingDeque<OldCommand> commandStack;

	public CommandContext( ProgramProduct product, DesignContext designContext ) {
		this.product = product;
		this.designContext = designContext;

		this.commandQueue = new LinkedBlockingQueue<>();
		this.commandStack = new LinkedBlockingDeque<>();
	}

	public ProgramProduct getProduct() {
		return product;
	}

	public DesignContext getDesignContext() {
		return designContext;
	}

	public void handle( KeyEvent event ) {
		// TODO Implement CommandContext.handle( KeyEvent )
	}

	public void handle( MouseEvent event ) {
		// TODO Implement CommandContext.handle( MouseEvent )
	}

	public void handle( MouseDragEvent event ) {
		// TODO Implement CommandContext.handle( MouseDragEvent )
	}

	public void handle( ScrollEvent event ) {
		// Zoom in is the equivalent of moving forward (positive delta y)
		// Zoom out is the equivalent of moving backward (negative delta y)
		double deltaY = event.getDeltaY();

		EventType<ScrollEvent> type = event.getEventType();

		if( type == ScrollEvent.SCROLL && deltaY != 0.0 ) {
			type = deltaY > 0 ? CommandMap.SCROLL_WHEEL_UP : CommandMap.SCROLL_WHEEL_DOWN;
			DesignTool tool = (DesignTool)event.getSource();
			Point3D point = tool.mouseToWorld( event.getX(), event.getY(), 0 );
			Class<? extends Command> command = CommandMap.get( type );
			if( command != null ) submit( tool, command, point.getX(), point.getY() );
		}
	}

	private void submit( DesignTool tool, Class<? extends Command> commandClass, Object... parameters ) {
		Objects.requireNonNull( commandClass, "Command class cannot be null" );
		synchronized( commandQueue ) {
			log.log( Log.TRACE, "Submitted command=" + commandClass.getSimpleName() );
			try {
				boolean trigger = commandQueue.isEmpty();
				Command command = commandClass.getConstructor().newInstance();
				commandQueue.offer( new CommandExecuteRequest( this, tool, command, parameters ) );
				if( trigger ) getProduct().getProgram().getTaskManager().submit( Task.of( "process-commands", this::doProcessCommands ) );
			} catch( Exception exception ) {
				log.log( Log.ERROR, exception );
			}
		}
	}

	private void doProcessCommands() {
		while( !commandQueue.isEmpty() ) {
			try {
				commandQueue.take().execute();
			} catch( Exception exception ) {
				log.log( Log.ERROR, exception );
			}
		}
	}

	private static class CommandExecuteRequest {

		private final CommandContext context;

		private final DesignTool tool;

		private final Command command;

		private final Object[] parameters;

		public CommandExecuteRequest( CommandContext context, DesignTool tool, Command command, Object... parameters ) {
			this.context = context;
			this.tool = tool;
			this.command = command;
			this.parameters = parameters;
		}

		public DesignTool getTool() {
			return tool;
		}

		public Command getCommand() {
			return command;
		}

		public Object[] getParameters() {
			return parameters;
		}

		public void execute() {
			command.execute( context, tool, parameters );
		}

	}

}
