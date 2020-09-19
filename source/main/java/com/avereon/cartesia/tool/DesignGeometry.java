package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.DesignLine;
import com.avereon.cartesia.data.DesignPoint;
import com.avereon.cartesia.data.DesignShape;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class DesignGeometry {

	private static Map<Class<? extends DesignShape>, BiFunction<DesignPane, DesignShape, ? extends DesignShapeView>> generatorMap;

	static {
		Map<Class<? extends DesignShape>, BiFunction<DesignPane, DesignShape, ? extends DesignShapeView>> generators = new HashMap<>();

		generators.put( DesignPoint.class, ( pane, shape ) -> from( pane, (DesignPoint)shape ) );
		generators.put( DesignLine.class, ( pane, shape ) -> from( pane, (DesignLine)shape ) );

		generatorMap = Collections.unmodifiableMap( generators );
	}

	private DesignGeometry() {}

	public static DesignShapeView from( DesignPane pane, DesignShape shape ) {
		return generatorMap.get( shape.getClass()).apply( pane, shape );
	}

	private static DesignPointView from( DesignPane pane, DesignPoint point ) {
		return new DesignPointView( pane, point );
	}

	private static DesignLineView from( DesignPane pane, DesignLine line ) {
		return new DesignLineView( pane, line );
	}

}
