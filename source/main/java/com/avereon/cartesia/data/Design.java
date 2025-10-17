package com.avereon.cartesia.data;

import com.avereon.cartesia.tool.DesignCommandContext;
import com.avereon.cartesia.tool.DesignContext;
import lombok.Getter;

/**
 * The Design class contains the context and data for a design. This is the main
 * bridge between the data model and the UI.
 */
@Getter
public class Design {

	private DesignModel model;

	private DesignContext context;

	private DesignCommandContext commandContext;

	public Design() {
		this.model = new DesignModel(){};
		this.context = new DesignContext(model, new DesignCommandContext());
		this.commandContext = new DesignCommandContext();
	}

}
