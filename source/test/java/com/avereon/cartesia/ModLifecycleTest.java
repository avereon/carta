package com.avereon.cartesia;

import com.avereon.xenon.Module;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ModLifecycleTest extends BaseCartesiaUiTest {

	@Test
	void testLifecycle() {
		assertThat( getProgram().getProductManager().isModEnabled( getMod() ) ).isTrue();
		assertThat( getMod().getStatus() ).isEqualTo( Module.Status.STARTED );

		getProgram().getProductManager().setModEnabled( getMod().getCard(), false );

		assertThat( getProgram().getProductManager().isModEnabled( getMod() ) ).isFalse();
		assertThat( getMod().getStatus() ).isEqualTo( Module.Status.STOPPED );
	}

}
