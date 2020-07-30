package com.avereon.cartesia.el;

import org.nfunk.jep.JEP;

public class CasExpressionParser extends JEP {

	public CasExpressionParser() {
		setAllowUndeclared( false );
		addStandardConstants();
		addStandardFunctions();
		addFunction( "deg", new Deg() );
		addFunction( "rad", new Rad() );
	}

}
