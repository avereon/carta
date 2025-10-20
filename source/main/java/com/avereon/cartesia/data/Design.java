package com.avereon.cartesia.data;

import com.avereon.cartesia.tool.CommandContext;
import com.avereon.cartesia.tool.DesignContext;
import lombok.Getter;

/**
 * The Design class contains the context and data for a design. This is the main
 * bridge between the data model and the UI.
 */
@Getter
public class Design<T extends DesignModel> {

	private T dataModel;

	private DesignContext designContext;

	private CommandContext commandContext;

	public Design() {}

	public Design<T> setDataModel( T model ) {
		if( this.dataModel != null ) return this;
		this.dataModel = model;
		this.commandContext = new CommandContext();
		this.designContext = new DesignContext();
		return this;
	}

}
