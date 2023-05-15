package com.avereon.cartesia.tool;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignPrint;
import com.avereon.cartesia.tool.view.DesignPane;
import com.avereon.product.Rb;
import com.avereon.xenon.Program;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.notice.Notice;
import com.avereon.xenon.task.Task;
import com.avereon.zarra.javafx.Fx;
import javafx.print.*;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Translate;
import lombok.CustomLog;
import lombok.Getter;

@Getter
@CustomLog
public class DesignPrintTask extends Task<Void> {

	private final Program program;

	private final DesignTool tool;

	private final Asset asset;

	private final DesignPrint print;

	public DesignPrintTask( Program program, DesignTool tool, Asset asset, DesignPrint print ) {
		this.program = program;
		this.tool = tool;
		this.asset = asset;
		this.print = print;
		setName( Rb.textOr( RbKey.LABEL, "print", "Print" ) + " " + asset.getName() );
	}

	@Override
	public Void call() throws Exception {
		log.atConfig().log( "Starting design print task..." );
		// TODO This should come from the DesignPrint setup
		// According to FX the authoritative size for paper is from the printer itself
		// However, the Paper class has common sizes that can be used for convenience.
		// There is no concept of custom paper sizes in FX.
		Paper paper = Paper.NA_LETTER;

		// TODO This should come from the DesignPrint setup
		PageOrientation orientation = PageOrientation.PORTRAIT;

		// TODO These should come from the DesignPrint setup
		double leftMargin = 0;
		double rightMargin = 0;
		double topMargin = 0;
		double bottomMargin = 0;

		final PrinterJob job = PrinterJob.createPrinterJob();
		final Printer printer = job.getPrinter();
		final PageLayout layout = printer.createPageLayout( paper, orientation, leftMargin, rightMargin, topMargin, bottomMargin );

		// WIDTH and HEIGHT in printer points (1/72)
		double printableWidth = layout.getPrintableWidth();
		double printableHeight = layout.getPrintableHeight();

		JobSettings settings = job.getJobSettings();
		settings.setPageLayout( layout );
		settings.setPaperSource( PaperSource.AUTOMATIC );

		// NOTE This can be used to give feedback to the user. It can be bound to a text field
		job.jobStatusProperty().asString();

		// NOTE This is a rather Swing looking dialog, maybe handle print properties separately
		//				boolean print = job.showPageSetupDialog( getScene().getWindow() );
		//				boolean print = job.showPrintDialog( getScene().getWindow() );
		//				if( !print ) return;

		//final PageLayout layout = job.getJobSettings().getPageLayout();

		// FIXME Can I use the marea FxRenderer2d renderer for printing?
		// NOTE The print API uses 72 DPI regardless of the printer
		//		final FxRenderer2d renderer = new FxRenderer2d( printableWidth, printableHeight );
		//		renderer.setDpi( 72, 72 );

		final DesignPane designPane = new DesignPane();
		designPane.setDesign( asset.getModel() );
		designPane.setDpi( 72 );
		designPane.setReferenceLayerVisible( false );
		designPane.setView( tool.getVisibleLayers(), tool.getViewPoint(), tool.getZoom(), tool.getViewRotate() );

		// Create an encapsulating pane to represent the paper
		final Pane paperPane = new Pane();

		// Move the center of the paper pane to the center of the printable area
		paperPane.getTransforms().add( new Translate( 0.5 * printableWidth, 0.5 * printableHeight ) );

		// Last, add the design pane to the paper pane to render the geometry
		paperPane.getChildren().add( designPane );
		// Wait for all the geometry to be rendered
		Fx.waitFor( 10000 );

		boolean successful = job.printPage( paperPane ) && job.endJob();
		if( !successful ) getProgram().getNoticeManager().addNotice( new Notice( this.getName(), job.getJobStatus() ) );

		log.atInfo().log( "Design print task complete." );

		return null;
	}

}
