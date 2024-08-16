package com.avereon.cartesia.command;

public class SelectByWindowContain extends SelectByWindow{

	@Override
	public Object execute( CommandTask task ) throws Exception {
		return execute( task, false );
	}

}
