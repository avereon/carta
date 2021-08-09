package com.avereon.cartesia.tool;

import com.avereon.settings.MapSettings;
import com.avereon.settings.Settings;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class StackedSettingsTest {

	@Test
	void testGet() {
		Settings base = new MapSettings();
		base.set( "a", "a-in-base" );
		base.set( "b", "b-in-base" );
		Settings first = new MapSettings();
		first.set( "a", "a-in-first" );

		StackedSettings settings = new StackedSettings( base, first );

		assertThat( settings.get( "a", "default" ), is( "a-in-first" ) );
		assertThat( settings.get( "b", "default" ), is( "b-in-base" ) );
		assertThat( settings.get( "c", "default" ), is( "default" ) );
	}

	@Test
	void testGetAfterSetToNull() {
		Settings base = new MapSettings();
		base.set( "a", "a-in-base" );
		base.set( "b", "b-in-base" );
		Settings first = new MapSettings();
		first.set( "a", "a-in-first" );
		first.set( "b", "b-in-first" );

		StackedSettings settings = new StackedSettings( base, first );

		System.out.println( settings.getName() );

		assertThat( settings.get( "b", "default" ), is( "b-in-first" ) );
		settings.set( "b", null );
		assertThat( settings.get( "b", "default" ), is( "b-in-base" ) );
	}
}
