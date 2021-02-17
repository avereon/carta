package com.avereon.cartesia.tool;

import com.avereon.cartesia.data.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class DesignGeometry {

	private static final Map<Class<? extends DesignShape>, BiFunction<DesignPane, DesignShape, ? extends DesignShapeView>> generatorMap;

	static {
		Map<Class<? extends DesignShape>, BiFunction<DesignPane, DesignShape, ? extends DesignShapeView>> generators = new HashMap<>();

		generators.put( DesignMarker.class, ( pane, shape ) -> from( pane, (DesignMarker)shape ) );
		generators.put( DesignLine.class, ( pane, shape ) -> from( pane, (DesignLine)shape ) );
		generators.put( DesignEllipse.class, ( pane, shape ) -> from( pane, (DesignEllipse)shape ) );
		generators.put( DesignArc.class, ( pane, shape ) -> from( pane, (DesignArc)shape ) );

		generatorMap = Collections.unmodifiableMap( generators );
	}

	private DesignGeometry() {}

	public static DesignShapeView from( DesignPane pane, DesignShape shape ) {
		return generatorMap.get( shape.getClass()).apply( pane, shape );
	}

	private static DesignMarkerView from( DesignPane pane, DesignMarker point ) {
		return new DesignMarkerView( pane, point );
	}

	private static DesignLineView from( DesignPane pane, DesignLine line ) {
		return new DesignLineView( pane, line );
	}

	private static DesignEllipseView from( DesignPane pane, DesignEllipse arc ) {
		return new DesignEllipseView( pane, arc );
	}

	private static DesignArcView from( DesignPane pane, DesignArc arc ) {
		return new DesignArcView( pane, arc );
	}

}
