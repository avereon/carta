package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.DesignArc;
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
public class DesignToolV2ScreenPointSelectArcUIT extends DesignToolV2BaseUIT {

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		useArcLayer();
	}

	@Test
	void screenPointSelectArc() {
		// given
		// Selecting the center of a transparent arc should not select anything
		Point3D point = new Point3D( -1, 1, 0 );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedGeometry();
		assertThat( selected.size() ).isEqualTo( 0 );
	}

	@Test
	void screenPointSelectArc1WithMouseCloseEnough() {
		// given

		// Need to get the selector inside the stroke width of the line
		// 0.02 is just under half the line stroke width

		Point3D offset = new Point3D( 0, 0.02 + getWorldSelectTolerance(), 0 );
		Point3D point = new Point3D( -1, 3, 0 ).add( offset );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedGeometry();
		assertThat( selected.getFirst() ).isInstanceOf( DesignArc.class );
		assertThat( selected.size() ).isEqualTo( 1 );
	}

	@Test
	void screenPointSelectArc1WithMouseTooFarAway() {
		// given

		// Need to get the selector outside the stroke width of the line
		// 0.03 is just over half the line stroke width

		Point3D offset = new Point3D( 0, 0.03 + getWorldSelectTolerance(), 0 );
		Point3D point = new Point3D( -1, 3, 0 ).add( offset );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedGeometry();
		assertThat( selected.size() ).isEqualTo( 0 );
	}

	@Test
	void screenPointSelectArc2WithMouseCloseEnough() {
		// given

		// Need to get the selector inside the stroke width of the line
		// 0.02 is just under half the line stroke width

		Point3D offset = new Point3D( 0, 0.02 + getWorldSelectTolerance(), 0 );
		Point3D point = new Point3D( -1, -1, 0 ).add( offset );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedGeometry();
		assertThat( selected.getFirst() ).isInstanceOf( DesignArc.class );
		assertThat( selected.size() ).isEqualTo( 1 );
	}

	@Test
	void screenPointSelectArc2WithMouseTooFarAway() {
		// given

		// Need to get the selector outside the stroke width of the line
		// 0.03 is just over half the line stroke width

		Point3D offset = new Point3D( 0, 0.03 + getWorldSelectTolerance(), 0 );
		Point3D point = new Point3D( -1, -1, 0 ).add( offset );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedGeometry();
		assertThat( selected.size() ).isEqualTo( 0 );
	}

}
