package com.avereon.cartesia.tool;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.data.DesignContext;
import com.avereon.xenon.ProgramProduct;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandContext {

	private final ProgramProduct product;

	private final DesignContext designContext;

	// The incoming command queue
	private final BlockingQueue<CommandExecuteRequest> commandQueue;

	// The current command stack
	private final BlockingDeque<Command> commandStack;

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

	public void handle( MouseEvent event ) {
		// TODO Determine what command to call
	}

	public void handle( ScrollEvent event ) {
		// TODO Determine what command to call
	}

	private static class CommandExecuteRequest {

		private final DesignTool tool;

		private final Command command;

		private final Object[] parameters;

		public CommandExecuteRequest( DesignTool tool, Command command, Object... parameters ) {
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

	}

}
