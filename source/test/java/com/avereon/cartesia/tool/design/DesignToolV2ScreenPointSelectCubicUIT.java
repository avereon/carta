package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.DesignCubic;
import com.avereon.cartesia.data.DesignShape;
import javafx.geometry.Point3D;
import lombok.CustomLog;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Getter
@CustomLog
public class DesignToolV2ScreenPointSelectCubicUIT extends DesignToolV2BaseUIT {

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		useCubicLayer();
	}

	@Test
	void screenPointSelectCubic() {
		// given
		// Selecting the center of a transparent cubic should not select anything
		Point3D point = new Point3D( 1, -2, 0 );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedShapes();
		assertThat( selected.size() ).isEqualTo( 0 );
	}

	@Test
	void screenPointSelectCubic1WithMouseCloseEnough() {
		// given

		// Need to get the selector inside the stroke width of the line
		// 0.02 is just under half the line stroke width

		Point3D offset = new Point3D( 0, 0.02 + getWorldSelectTolerance(), 0 );
		Point3D point = new Point3D( 0.25, -2, 0 ).add( offset );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedShapes();
		assertThat( selected.size() ).isEqualTo( 1 );
		assertThat( selected.getFirst() ).isInstanceOf( DesignCubic.class );
	}

	@Test
	void screenPointSelectCubic1WithMouseTooFarAway() {
		// given

		// Need to get the selector outside the stroke width of the line
		// 0.03 is just over half the line stroke width

		Point3D offset = new Point3D( 0.03 + getWorldSelectTolerance(), 0, 0 );
		Point3D point = new Point3D( 0.25, -2, 0 ).add( offset );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedShapes();
		assertThat( selected.size() ).isEqualTo( 0 );
	}

	@Test
	void screenPointSelectCubic2WithMouseCloseEnough() {
		// given

		// Need to get the selector inside the stroke width of the line
		// 0.02 is just under half the line stroke width

		Point3D offset = new Point3D( 0, 0.02 + getWorldSelectTolerance(), 0 );
		Point3D point = new Point3D( 2, -2, 0 ).add( offset );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedShapes();
		assertThat( selected.size() ).isEqualTo( 1 );
		assertThat( selected.getFirst() ).isInstanceOf( DesignCubic.class );
	}

	@Test
	void screenPointSelectCubic2WithMouseTooFarAway() {
		// given

		// Need to get the selector outside the stroke width of the line
		// 0.03 is just over half the line stroke width

		Point3D offset = new Point3D( 0.03 - getWorldSelectTolerance(), 0.03 + getWorldSelectTolerance(), 0 );
		Point3D point = new Point3D( 2, -2, 0 ).add( offset );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedShapes();
		assertThat( selected.size() ).isEqualTo( 0 );
	}

}
