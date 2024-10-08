package com.avereon.cartesia.command;

import lombok.CustomLog;

@CustomLog
public class SelectToggle extends SelectByPoint {

	@Override
	public Object execute( CommandTask task ) throws Exception {
		return execute( task, true );
	}

}
