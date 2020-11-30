package com.avereon.cartesia.command;

import com.avereon.cartesia.Command;
import com.avereon.cartesia.data.DesignShape;
import com.avereon.cartesia.tool.DesignTool;

public abstract class DrawCommand extends Command {

	private DesignShape preview;

	@Override
	public void cancel( DesignTool tool ) throws Exception {
		tool.getCurrentLayer().removeShape( preview );
		super.cancel( tool );
	}

	protected void setPreview( DesignTool tool, DesignShape preview ) {
		this.preview = preview;
		tool.getAsset().setCaptureUndoChanges( false );
		tool.getCurrentLayer().addShape( preview );
	}

	@SuppressWarnings( "unchecked" )
	protected <T extends DesignShape> T getPreview() {
		return (T)preview;
	}

	protected Object commitPreview(DesignTool tool) {
		tool.getCurrentLayer().removeShape( preview );
		tool.getAsset().setCaptureUndoChanges( true );
		tool.getCurrentLayer().addShape( preview );
		return complete();
	}

}
