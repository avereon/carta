package com.avereon.cartesia.command.measure;

import com.avereon.cartesia.CommandBaseTest;
import com.avereon.cartesia.command.CommandTask;
import com.avereon.xenon.notice.Notice;
import javafx.geometry.Point3D;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MeasurePointTest extends CommandBaseTest {

	private final MeasurePoint command = new MeasurePoint();

	// Script Tests --------------------------------------------------------------

	/**
	 * Measure point with all parameters should calculate the location of the
	 * selected point and display it as a notice. The result should be success.
	 *
	 * @throws Exception If an error occurs during the test
	 */
	@Test
	void testExecuteWithAllParameters() throws Exception {
		// given
		CommandTask task = new CommandTask( commandContext, tool, null, null, command, "1.001,1.001" );
		when( commandContext.isInteractive() ).thenReturn( true );
		// Make this call lenient to prove it is not called
		lenient().when( tool.snapToWorkplane( eq( new Point3D( 1.001, 1.001, 0 ) ) ) ).thenReturn( new Point3D( 1, 1, 0 ) );

		// when
		Object result = task.runTaskStep();

		// then
		verify( noticeManager, times( 1 ) ).addNotice( any( Notice.class ) );
		verify( tool, times( 0 ) ).snapToWorkplane( any( Point3D.class ) );
		assertThat( result ).isEqualTo( new Point3D( 1.001, 1.001, 0 ) );
		assertThat( command.getReference() ).hasSize( 0 );
		assertThat( command.getPreview() ).hasSize( 0 );
	}

	// Interactive Tests ---------------------------------------------------------

}
