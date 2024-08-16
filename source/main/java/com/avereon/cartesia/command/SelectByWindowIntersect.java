package com.avereon.cartesia.command;

public class SelectByWindowIntersect extends SelectByWindow {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		return execute( task, true );
	}

}
