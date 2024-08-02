package com.avereon.cartesia.command;

import com.avereon.cartesia.tool.CommandTask;

public class SelectByWindowIntersect extends SelectByWindow {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		return execute( task, true );
	}

}
