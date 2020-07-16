package com.avereon.cartesia;

public abstract class Command<T> {

	public abstract void evaluate(Object... parameters);

	public abstract T getResult() throws CommandException;

}
