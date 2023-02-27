package com.avereon.cartesia.tool;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignPrint;
import com.avereon.marea.fx.FxRenderer2d;
import com.avereon.product.Rb;
import com.avereon.xenon.Program;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.notice.Notice;
import com.avereon.xenon.task.Task;
import javafx.print.*;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Translate;
import lombok.Getter;

@Getter
public class DesignPrintTask extends Task<Void> {

	private final Program program;

	private final Asset asset;

	private final DesignPrint print;

	public DesignPrintTask( Program program, Asset asset, DesignPrint print ) {
		this.program = program;
		this.asset = asset;
		this.print = print;
		setName( Rb.textOr( RbKey.LABEL, "print", "Print" ) + " " + asset.getName() );
	}

	@Override
	public Void call() throws Exception {
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
		JobSettings settings = job.getJobSettings();
		settings.setPaperSource( PaperSource.AUTOMATIC );
		printer.createPageLayout( paper, orientation, leftMargin, rightMargin, topMargin, bottomMargin );

		// NOTE This can be used to give feedback to the user. It can be bound to a text field
		job.jobStatusProperty().asString();

		// NOTE This is a rather Swing looking dialog, maybe handle print properties separately
		//				boolean print = job.showPageSetupDialog( getScene().getWindow() );
		//				boolean print = job.showPrintDialog( getScene().getWindow() );
		//				if( !print ) return;

		final PageLayout layout = job.getJobSettings().getPageLayout();

		// WIDTH and HEIGHT in printer points (1/72)
		double printableWidth = layout.getPrintableWidth();
		double printableHeight = layout.getPrintableHeight();

		// NOTE The print API uses 72 DPI regardless of the printer

		// FIXME Can I use the marea FxRenderer2d renderer for printing?
		final FxRenderer2d renderer = new FxRenderer2d( printableWidth, printableHeight );
		renderer.setDpi( 72, 72 );
		//designPane.setReferenceLayerVisible( false );
		//designPane.setDesign( getDesign() );
		//designPane.setView( getVisibleLayers(), getViewPoint(), getZoom(), getViewRotate() );

		// Create an encapsulating pane to represent the paper
		final Pane paperPane = new Pane();

		// Move the center of the paper pane to the center of the printable area
		paperPane.getTransforms().add( new Translate( 0.5 * printableWidth, 0.5 * printableHeight ) );

		// Add the design pane last
		paperPane.getChildren().add( renderer );

		boolean successful = job.printPage( paperPane ) && job.endJob();
		if( !successful ) getProgram().getNoticeManager().addNotice( new Notice( this.getName(), job.getJobStatus() ) );

		return null;
	}

}
