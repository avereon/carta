package com.avereon.cartesia;

import com.avereon.xenos.BaseModTestCase;
import com.avereon.zerra.javafx.Fx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith( MockitoExtension.class )
public class BaseCartesiaUnitTest extends BaseModTestCase<CartesiaMod> {

	protected BaseCartesiaUnitTest() {
		super( CartesiaMod.class );
	}

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		Fx.startup();
	}

	@Override
	protected CartesiaMod getMod() {
		return (CartesiaMod)super.getMod();
	}

}
