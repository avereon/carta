package com.avereon.cartesia.tool;

import com.avereon.settings.AbstractSettings;
import com.avereon.settings.Settings;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class StackedSettings extends AbstractSettings {

	private final List<Settings> settingsStack;

	private final Settings base;

	public StackedSettings( Settings base, Settings... others ) {
		this.settingsStack = new CopyOnWriteArrayList<>();
		this.base = base;
		List<Settings> otherList = Arrays.asList( others );
		Collections.reverse( otherList );
		settingsStack.addAll( otherList );
		settingsStack.add( base );
	}

	@Override
	protected String getValue( String key ) {
		return findSettingsForKey( key ).get( key );
	}

	@Override
	protected void setValue( String key, String value ) {
		findSettingsForKey( key ).set( key, value );
	}

	private Settings findSettingsForKey( String key ) {
		return settingsStack.stream().filter( s -> s.exists( key ) ).findFirst().orElse( base );
	}

	@Override
	public String getName() {
		return "SettingsStack" + settingsStack.stream().map( Settings::getName ).collect( Collectors.toList());
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public boolean nodeExists( String path ) {
		return false;
	}

	@Override
	public Settings getNode( String path ) {
		return null;
	}

	@Override
	public Settings getNode( String path, Map<String, String> values ) {
		return null;
	}

	@Override
	public List<String> getNodes() {
		return null;
	}

	@Override
	public Set<String> getKeys() {
		return null;
	}

	@Override
	public Settings flush() {
		return null;
	}

	@Override
	public Settings delete() {
		return null;
	}
}
