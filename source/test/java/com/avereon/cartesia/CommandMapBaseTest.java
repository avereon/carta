package com.avereon.cartesia;

import com.avereon.xenon.ActionLibrary;
import com.avereon.xenon.ActionProxy;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.XenonProgramProduct;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith( MockitoExtension.class )
abstract class CommandMapBaseTest extends CommandBaseTest {

	static XenonProgramProduct product = Mockito.mock( XenonProgramProduct.class );

	static Xenon program = Mockito.mock( Xenon.class );

	static ActionLibrary actionLibrary = Mockito.mock( ActionLibrary.class );

	static ActionProxy other = Mockito.mock( ActionProxy.class );

	static Map<String, ActionProxy> mockActionMap = new HashMap<>();

	@BeforeAll
	public static void load() {
		List<String> actions = List.of( "anchor", "select-point", "select-toggle", "select-window-contain", "select-window-intersect", "snap-auto-nearest", "camera-move", "camera-zoom" );

		for( String command : actions ) {
			ActionProxy action = Mockito.mock( ActionProxy.class );
			when( action.getName() ).thenReturn( command );
			mockActionMap.put( command, action );
		}
		when( other.getName() ).thenReturn( "other" );

		when( product.getProgram() ).thenReturn( program );
		when( program.getActionLibrary() ).thenReturn( actionLibrary );
		when( actionLibrary.getAction( anyString() ) ).thenAnswer( i -> {
			String name = String.valueOf( i.getArguments()[ 0 ] );
			return mockActionMap.getOrDefault( name, other );
		} );

		// With this setup, the CommandMap contain mocks for all the tested actions

		CommandMap.load( product );
	}

}
