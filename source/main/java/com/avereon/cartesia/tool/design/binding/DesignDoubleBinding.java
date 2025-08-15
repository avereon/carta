package com.avereon.cartesia.tool.design.binding;

import javafx.beans.property.DoublePropertyBase;

import java.util.function.Function;

public class DesignDoubleBinding extends DoublePropertyBase {

	public <T extends com.avereon.data.Node, R extends Double> DesignDoubleBinding( T node, String designPropertyName, Function<T, R> consumer ) {
		set( consumer.apply( node ) );
		node.register( this, designPropertyName, _ -> set( consumer.apply( node ) ) );
	}

	@Override
	public Object getBean() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}
}
