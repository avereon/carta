package com.avereon.cartesia;

import com.avereon.product.ProductCard;
import com.avereon.settings.MapSettings;
import com.avereon.xenon.*;
import com.avereon.xenon.asset.AssetManager;
import com.avereon.xenon.index.IndexService;
import com.avereon.xenon.notice.NoticeManager;
import com.avereon.xenon.task.TaskManager;
import com.avereon.zerra.BaseModTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith( MockitoExtension.class )
public class BaseCartesiaUnitTest extends BaseModTestCase<CartesiaMod> {

	@Mock
	protected Xenon program;

	@Mock
	protected TaskManager taskManager;

	@Mock
	protected IconLibrary iconLibrary;

	@Mock
	protected ActionLibrary actionLibrary;

	@Mock
	protected AssetManager assetManager;

	@Mock
	protected SettingsManager settingsManager;

	@Mock
	protected ToolManager toolManager;

	@Mock
	protected IndexService indexService;

	@Mock
	protected NoticeManager noticeManager;

	// Keep this static so it is shared across all tests
	protected static CartesiaMod module;

	protected BaseCartesiaUnitTest() {
		super( CartesiaMod.class );
	}

	@BeforeEach
	protected void setup() throws Exception {
		lenient().when( program.getTaskManager() ).thenReturn( taskManager );
		lenient().when( program.getIconLibrary() ).thenReturn( iconLibrary );
		lenient().when( program.getActionLibrary() ).thenReturn( actionLibrary );
		lenient().when( program.getAssetManager() ).thenReturn( assetManager );
		lenient().when( program.getSettingsManager() ).thenReturn( settingsManager );
		lenient().when( program.getToolManager() ).thenReturn( toolManager );
		lenient().when( program.getIndexService() ).thenReturn( indexService );
		lenient().when( program.getNoticeManager() ).thenReturn( noticeManager );

		lenient().when( actionLibrary.getAction( anyString() ) ).thenAnswer( i -> {
			String name = String.valueOf( i.getArguments()[ 0 ] );
			ActionProxy action = new ActionProxy();
			action.setName( name );
			if( "select-window-contain".equals( name ) ) action.setCommand( "ws" );
			return action;
		} );

		lenient().when( settingsManager.getProductSettings( any( ProductCard.class ) ) ).thenReturn( new MapSettings() );

		if( module == null ) {
			module = new CartesiaMod();
			module.init( program, module.getCard() );
			module.setParent( program );
			module.startup();
		}
	}

	@Override
	protected CartesiaMod getMod() {
		return module;
	}

}
