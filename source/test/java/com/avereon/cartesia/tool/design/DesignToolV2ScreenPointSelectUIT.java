package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.DesignShape;
import javafx.geometry.Point3D;
import lombok.CustomLog;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Getter
@CustomLog
public class DesignToolV2ScreenPointSelectUIT extends DesignToolV2TestUIT {

	@Test
	void screenPointSelect() throws Exception {
		// given
		useLineLayer();
		useEllipseLayer();
		Point3D mouse = getTool().worldToScreen( new Point3D( 0, 0, 0 ) );

		// when - select once
		getTool().screenPointSelect( mouse, false );

		// then - the first line should be selected
		List<DesignShape> selected = getTool().getSelectedGeometry();
		assertThat( selected.size() ).isEqualTo( 1 );
		assertThat( selected.getFirst().getOrigin() ).isEqualTo( new Point3D( -2, 2, 0 ) );
	}

	@Test
	void screenPointSelectWithMultipleSelectsMovingDownVisibleGeometry() throws Exception {
		// given
		useLineLayer();
		useEllipseLayer();
		Point3D mouse = getTool().worldToScreen( new Point3D( 0, 0, 0 ) );

		// when - select once
		getTool().screenPointSelect( mouse, false );

		// then - the first line should be selected
		List<DesignShape> selected = getTool().getSelectedGeometry();
		assertThat( selected.size() ).isEqualTo( 1 );
		assertThat( selected.getFirst().getOrigin() ).isEqualTo( new Point3D( -2, 2, 0 ) );

		// when - select again
		getTool().screenPointSelect( mouse, false );

		// TODO Implement cascading select functionality

		//		// then - the second line should be selected
		//		selected = getTool().getSelectedGeometry();
		//		assertThat( selected.size() ).isEqualTo( 1 );
		//		assertThat( selected.getFirst().getOrigin() ).isEqualTo( new Point3D( -2, -2, 0 ) );
	}

}
