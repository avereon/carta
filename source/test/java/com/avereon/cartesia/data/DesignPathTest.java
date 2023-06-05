package com.avereon.cartesia.data;

import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DesignPathTest {

	@Test
	void constructor() {
		DesignPath path = new DesignPath();
		assertThat( path.isModified() ).isTrue();
		assertThat( path.getOrigin() ).isNull();
	}

	@Test
	void constructorWithOrigin() {
		DesignPath path = new DesignPath( Point3D.ZERO );
		assertThat( path.isModified() ).isTrue();
		assertThat( path.getOrigin() ).isEqualTo( Point3D.ZERO );
	}

	@Test
	void modifyFlag() {
		DesignPath path = new DesignPath( Point3D.ZERO );
		assertThat( path.isModified() ).isTrue();
		path.setModified( false );
		assertThat( path.isModified() ).isFalse();
	}

}
