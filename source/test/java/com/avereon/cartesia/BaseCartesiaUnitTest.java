package com.avereon.cartesia;

import com.avereon.xenon.ActionLibrary;
import com.avereon.xenon.ActionProxy;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.notice.NoticeManager;
import com.avereon.xenon.task.TaskManager;
import com.avereon.zerra.BaseModTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith( MockitoExtension.class )
public class BaseCartesiaUnitTest extends BaseModTestCase<CartesiaMod> {

	@Mock
	protected Xenon program;

	protected XenonProgramProduct product;

	@Mock
	protected TaskManager taskManager;

	@Mock
	protected ActionLibrary actionLibrary;

	@Mock
	protected ActionProxy other;

	@Mock
	protected NoticeManager noticeManager;

	@Mock
	protected CartesiaMod module;

	protected Map<String, ActionProxy> mockActionMap;

	protected CommandMap commandMap;

	protected BaseCartesiaUnitTest() {
		super( CartesiaMod.class );
	}

	@BeforeEach
	protected void setup() throws Exception {
		product = program;

		lenient().when( product.getProgram() ).thenReturn( program );
		lenient().when( program.getTaskManager() ).thenReturn( taskManager );
		lenient().when( program.getActionLibrary() ).thenReturn( actionLibrary );
		lenient().when( program.getNoticeManager() ).thenReturn( noticeManager );
		lenient().when( module.getProgram() ).thenReturn( program );

		List<String> actions = List.of( "anchor", "select-point", "select-toggle", "select-window-contain", "select-window-intersect", "snap-auto-nearest", "camera-move", "camera-zoom" );

		// Generate mock action proxies for tested actions
		mockActionMap = new HashMap<>();
		for( String command : actions ) {
			ActionProxy action = new ActionProxy();
			action.setName( command );

			if( "select-window-contain".equals( command))action.setCommand( "ws" );

			mockActionMap.put( command, action );
		}

		// Set up the mock when results
		when( other.getName() ).thenReturn( "other" );
		when( actionLibrary.getAction( anyString() ) ).thenAnswer( i -> {
			String name = String.valueOf( i.getArguments()[ 0 ] );
			return mockActionMap.getOrDefault( name, other );
		} );

		// Load the command map for tested actions
		commandMap = new CommandMap().load( product );
		lenient().when( module.getCommandMap() ).thenReturn( commandMap );
	}

	@Override
	protected CartesiaMod getMod() {
		return module;
	}

}
