package com.avereon.cartesia.tool.design;

import com.avereon.cartesia.data.DesignMarker;
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
public class DesignToolV2ScreenPointSelectMarkerUIT extends DesignToolV2BaseUIT {

	@BeforeEach
	protected void setup() throws Exception {
		super.setup();
		useMarkerLayer();
	}

	@Test
	void screenPointSelectMarker1WithMouseCloseEnough() {
		// given

		// Need to get the selector inside the path of the marker
		// 0.04 is just inside the marker path

		Point3D offset = new Point3D( 0.04 + getWorldSelectTolerance(), 0, 0 );
		Point3D point = new Point3D( -3, -2.5, 0 ).add( offset );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedGeometry();
		assertThat( selected.size() ).isEqualTo( 1 );
		assertThat( selected.getFirst() ).isInstanceOf( DesignMarker.class );
	}

	@Test
	void screenPointSelectMarker1WithMouseTooFarAway() {
		// given

		// Need to get the selector outside the stroke width of the line
		// 0.03 is just over half the line stroke width

		Point3D offset = new Point3D( 0.06 + getWorldSelectTolerance(), 0, 0 );
		Point3D point = new Point3D( -3, -2.5, 0 ).add( offset );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedGeometry();
		assertThat( selected.size() ).isEqualTo( 0 );
	}

	@Test
	void screenPointSelectMarker2WithMouseCloseEnough() {
		// given

		// Need to get the selector inside the stroke width of the line
		// 0.02 is just under half the line stroke width

		Point3D offset = new Point3D( 0.04 + getWorldSelectTolerance(), 0, 0 );
		Point3D point = new Point3D( 3, -2.5, 0 ).add( offset );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedGeometry();
		assertThat( selected.size() ).isEqualTo( 1 );
		assertThat( selected.getFirst() ).isInstanceOf( DesignMarker.class );
	}

	@Test
	void screenPointSelectMarker2WithMouseTooFarAway() {
		// given

		// Need to get the selector outside the stroke width of the line
		// 0.03 is just over half the line stroke width

		Point3D offset = new Point3D( 0.06 + getWorldSelectTolerance(), 0, 0 );
		Point3D point = new Point3D( 3, -2.5, 0 ).add( offset );
		Point3D mouse = getTool().worldToScreen( point );

		// when
		getTool().screenPointSelect( mouse, false );

		// then
		List<DesignShape> selected = getTool().getSelectedGeometry();
		assertThat( selected.size() ).isEqualTo( 0 );
	}

}
