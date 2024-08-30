package com.avereon.cartesia;

import com.avereon.zerra.BaseModTestCase;
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
	}

	@Override
	protected CartesiaMod getMod() {
		return (CartesiaMod)super.getMod();
	}

}
