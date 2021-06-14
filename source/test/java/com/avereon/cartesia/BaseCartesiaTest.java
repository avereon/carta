package com.avereon.cartesia;

import com.avereon.product.ProductCard;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.zerra.javafx.Fx;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseCartesiaTest {

	private CartesiaMod mod;

	@BeforeEach
	protected void setup() throws Exception {
		Fx.startup();

		Program program = new Program();
		program.init();
		Fx.run( () -> program.start( new Stage() ) );

		mod = new CartesiaMod();
		mod.init( program, ProductCard.card( mod ) );
	}

	public ProgramProduct getProduct() {
		return getMod();
	}

	public CartesiaMod getMod() {
		return mod;
	}

}
