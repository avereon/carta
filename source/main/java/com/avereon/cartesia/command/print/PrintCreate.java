package com.avereon.cartesia.command.print;

import com.avereon.cartesia.CommandTrigger;
import com.avereon.cartesia.data.DesignPrint;
import com.avereon.cartesia.tool.DesignCommandContext;
import javafx.scene.input.InputEvent;
import static com.avereon.cartesia.command.Command.Result.*;

public class PrintCreate extends PrintCommand {

	@Override
	public Object execute( DesignCommandContext context, CommandTrigger trigger, InputEvent triggerEvent, Object... parameters ) throws Exception {
		if( parameters.length < 1 ) {
			promptForText( context, "print-name" );
			return INCOMPLETE;
		}

		DesignPrint print = new DesignPrint().setName( String.valueOf( parameters[ 0 ] ) );
		print.setOrigin( context.getTool().getViewPoint() );
		print.setZoom( context.getTool().getZoom() );
		print.setRotate( context.getTool().getViewRotate() );
		print.setLayers( context.getTool().getVisibleLayers() );

		context.getTool().getDesign().addPrint( print );
		//context.getTool().setCurrentPrint( print );

		return print;
	}

}
