package com.avereon.cartesia.tool.design.binding;

import javafx.beans.property.ObjectPropertyBase;

import java.util.function.Function;

public class DesignBinding<T> extends ObjectPropertyBase<T> {

	public <S extends com.avereon.data.Node, R extends T> DesignBinding( S node, String designPropertyName, Function<S, R> consumer ) {
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
