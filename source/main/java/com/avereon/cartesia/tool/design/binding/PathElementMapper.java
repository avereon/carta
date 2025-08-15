package com.avereon.cartesia.tool.design.binding;

import com.avereon.cartesia.data.DesignPath;
import javafx.scene.shape.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PathElementMapper {

	@Mapping( target = "" )
	default PathElement map( DesignPath.Step step ) {
		return map( step, 1, 1 );
	}

	@Mapping( target = "" )
	default PathElement map( DesignPath.Step step, double shapeScaleX, double shapeScaleY ) {
		return switch( step.command() ) {
			// step data is: x, y
			case M -> new MoveTo( step.data()[ 0 ] * shapeScaleX, step.data()[ 1 ] * shapeScaleY );
			// step data is: x, y, rx, ry, rotate, largeArc, sweep
			case A -> new ArcTo(
				step.data()[ 2 ] * shapeScaleX,
				step.data()[ 3 ] * shapeScaleY,
				step.data()[ 4 ],
				step.data()[ 0 ] * shapeScaleX,
				step.data()[ 1 ] * shapeScaleY,
				step.data()[ 5 ] != 0,
				step.data()[ 6 ] != 0
			);
			// step data is: bx, by, cx, cy, dx, dy
			case B -> new CubicCurveTo(
				step.data()[ 0 ] * shapeScaleX,
				step.data()[ 1 ] * shapeScaleY,
				step.data()[ 2 ] * shapeScaleX,
				step.data()[ 3 ] * shapeScaleY,
				step.data()[ 4 ] * shapeScaleX,
				step.data()[ 5 ] * shapeScaleY
			);
			// step data is: x, y
			case L -> new LineTo( step.data()[ 0 ] * shapeScaleX, step.data()[ 1 ] * shapeScaleY );
			// step data is: bx, by, cx, cy
			case Q -> new QuadCurveTo( step.data()[ 0 ] * shapeScaleX, step.data()[ 1 ] * shapeScaleY, step.data()[ 2 ] * shapeScaleX, step.data()[ 3 ] * shapeScaleY );
			// no step data
			case Z -> new ClosePath();
		};
	}

}
