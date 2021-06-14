package com.avereon.cartesia;

import com.avereon.product.ProductCard;
import com.avereon.product.Rb;
import com.avereon.util.Parameters;
import com.avereon.xenon.Profile;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramFlag;
import com.avereon.xenon.ProgramProduct;
import com.avereon.zerra.javafx.Fx;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCartesiaTest {

	private Program program;

	private CartesiaMod mod;

	@BeforeEach
	protected void setup() throws Exception {
		Fx.startup();

		program = new Program().initForTesting(Parameters.parse( getParameterValues() ) );

		mod = new CartesiaMod();
		mod.init( program, ProductCard.card( mod ) );
		Rb.init(mod);
	}

	protected Program getProgram() {
		return program;
	}

	protected ProgramProduct getProduct() {
		return getMod();
	}

	protected CartesiaMod getMod() {
		return mod;
	}

	static List<String> getParameterValues() {
		List<String> values = new ArrayList<>();
		values.add( ProgramFlag.PROFILE );
		values.add( Profile.TEST );
		values.add( ProgramFlag.LOG_LEVEL );
		values.add( ProgramFlag.ERROR );
		values.add( ProgramFlag.DAEMON );
		return values;
	}

}
