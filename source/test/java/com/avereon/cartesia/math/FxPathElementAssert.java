package com.avereon.cartesia.math;

import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;

public class FxPathElementAssert extends AbstractAssert<FxPathElementAssert, PathElement> {

	private static final Offset<Double> EXACT = Offset.offset( 0.0 );

	protected FxPathElementAssert( PathElement pathElement ) {
		super( pathElement, FxPathElementAssert.class );
	}

	public static FxPathElementAssert assertThat( PathElement actual ) {
		return new FxPathElementAssert( actual );
	}

	public FxPathElementAssert isEqualTo( MoveTo move ) {
		return isEqualTo( move, EXACT );
	}

	public FxPathElementAssert isEqualTo( MoveTo move, Offset<Double> offset ) {
		isInstanceOf( MoveTo.class );
		Assertions.assertThat( ((MoveTo)actual).getX() ).isEqualTo( move.getX(), offset );
		Assertions.assertThat( ((MoveTo)actual).getY() ).isEqualTo( move.getY(), offset );
		return this;
	}

	public FxPathElementAssert isEqualTo( LineTo move ) {
		return isEqualTo( move, EXACT );
	}

	public FxPathElementAssert isEqualTo( LineTo move, Offset<Double> offset ) {
		isInstanceOf( LineTo.class );
		Assertions.assertThat( ((LineTo)actual).getX() ).isEqualTo( move.getX(), offset );
		Assertions.assertThat( ((LineTo)actual).getY() ).isEqualTo( move.getY(), offset );
		return this;
	}

	// TODO Add remaining PathElement types

	public FxPathElementAssert isEqualTo( ClosePath move ) {
		isInstanceOf( ClosePath.class );
		return this;
	}

}
