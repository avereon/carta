package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.DesignBox;
import com.avereon.cartesia.data.DesignPath;
import com.avereon.cartesia.data.DesignShape;
import javafx.geometry.Point3D;
import lombok.CustomLog;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Getter
@CustomLog
public class DesignToolV2ScreenWindowSelectUIT extends DesignToolV2BaseUIT {

	@Test
	void screenWindowSelect() throws Exception {
		// given
		useBoxLayer();
		useLineLayer();
		usePathLayer();
		useMarkerLayer();
		Point3D anchor = getTool().worldToScreen( new Point3D( -4.5, 4.5, 0 ) );
		Point3D mouse = getTool().worldToScreen( new Point3D( -1.5, 1.5, 0 ) );

		// when - select once
		getTool().screenWindowSelect( anchor, mouse, false, false );

		// then - the first line should be selected
		List<DesignShape> selected = getTool().getSelectedShapes();
		assertThat( selected.size() ).isEqualTo( 1 );
		assertThat( selected.getFirst() ).isInstanceOf( DesignPath.class );
	}

	@Test
	void screenWindowSelectByIntersect() throws Exception {
		// given
		useBoxLayer();
		useLineLayer();
		usePathLayer();
		useMarkerLayer();
		Point3D anchor = getTool().worldToScreen( new Point3D( -4.5, 4.5, 0 ) );
		Point3D mouse = getTool().worldToScreen( new Point3D( -1.5, 1.5, 0 ) );

		// when - select once
		getTool().screenWindowSelect( anchor, mouse, true, false );

		// then - the first line should be selected
		List<DesignShape> selected = getTool().getSelectedShapes();
		assertThat( selected.size() ).isEqualTo( 2 );
		assertThat( selected.getFirst() ).isInstanceOf( DesignBox.class );
		assertThat( selected.get(1) ).isInstanceOf( DesignPath.class );
	}

	@Test
	void screenWindowSelectNone() throws Exception {
		// given
		useBoxLayer();
		useLineLayer();
		usePathLayer();
		useMarkerLayer();
		Point3D anchor = getTool().worldToScreen( new Point3D( -4.5, 4.5, 0 ) );
		Point3D mouse = getTool().worldToScreen( new Point3D( -1.5, 1.5, 0 ) );

		// when - select once
		getTool().screenWindowSelect( anchor, mouse, false, false );

		// then - the first line should be selected
		List<DesignShape> selected = getTool().getSelectedShapes();
		assertThat( selected.size() ).isEqualTo( 1 );
	}

}
