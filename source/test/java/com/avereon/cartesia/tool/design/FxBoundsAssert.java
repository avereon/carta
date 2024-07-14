package com.avereon.cartesia.tool.design;

import javafx.geometry.Bounds;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;

public class FxBoundsAssert extends AbstractAssert<FxBoundsAssert, Bounds> {

	private static final Offset<Double> EXACT = Offset.offset( 0.0 );

	protected FxBoundsAssert( Bounds bounds ) {
		super( bounds, FxBoundsAssert.class );
	}

	public static FxBoundsAssert assertThat( Bounds actual ) {
		return new FxBoundsAssert( actual );
	}

	public FxBoundsAssert isEqualTo( Bounds bounds ) {
		return isEqualTo( bounds, EXACT );
	}

	public FxBoundsAssert isEqualTo( Bounds bounds, Offset<Double> offset ) {
		Assertions.assertThat( actual.getMinX() ).isEqualTo( bounds.getMinX(), offset );
		Assertions.assertThat( actual.getMinY() ).isEqualTo( bounds.getMinY(), offset );
		Assertions.assertThat( actual.getMinZ() ).isEqualTo( bounds.getMinZ(), offset );
		Assertions.assertThat( actual.getWidth() ).isEqualTo( bounds.getWidth(), offset );
		Assertions.assertThat( actual.getHeight() ).isEqualTo( bounds.getHeight(), offset );
		Assertions.assertThat( actual.getMaxX() ).isEqualTo( bounds.getMaxX(), offset );
		Assertions.assertThat( actual.getMaxY() ).isEqualTo( bounds.getMaxY(), offset );
		Assertions.assertThat( actual.getMaxZ() ).isEqualTo( bounds.getMaxZ(), offset );
		return this;
	}

}
