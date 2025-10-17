package com.avereon.cartesia.data;

import com.avereon.cartesia.tool.DesignCommandContext;
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

	private DesignCommandContext commandContext;

	public Design() {}

	public Design<T> setDataModel( T dataModel ) {
		if( this.dataModel != null ) return this;
		this.dataModel = dataModel;
		this.commandContext = new DesignCommandContext();
		this.designContext = new DesignContext( dataModel, commandContext );
		return this;
	}

}
