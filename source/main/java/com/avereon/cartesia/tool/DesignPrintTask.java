package com.avereon.cartesia.tool;

import com.avereon.cartesia.RbKey;
import com.avereon.cartesia.data.DesignPrint;
import com.avereon.cartesia.tool.design.DesignRenderer;
import com.avereon.product.Rb;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.notice.Notice;
import com.avereon.xenon.task.Task;
import com.avereon.zerra.color.Colors;
import com.avereon.zerra.color.Paints;
import javafx.print.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import lombok.CustomLog;
import lombok.Getter;

@Getter
@CustomLog
public class DesignPrintTask extends Task<Void> {

	private final Xenon program;

	private final DesignTool tool;

	private final Asset asset;

	private final DesignPrint print;

	public DesignPrintTask( final Xenon program, final DesignTool tool, final Asset asset, final DesignPrint print ) {
		this.program = program;
		this.tool = tool;
		this.asset = asset;
		this.print = print;
		setName( Rb.textOr( RbKey.LABEL, "print", "Print" ) + " " + asset.getName() );
	}

	@Override
	public Void call() throws Exception {
		log.atDebug().log( "Starting design print task..." );

		// User specific properties ------------------------------------------------
		Printer printer = getPrinterByName( "PDF", Printer.getDefaultPrinter() );

		PrintResolution resolution = printer.getPrinterAttributes().getDefaultPrintResolution();
		for( PrintResolution r : printer.getPrinterAttributes().getSupportedPrintResolutions() ) {
			if( r.getFeedResolution() > resolution.getFeedResolution() ) resolution = r;
		}

		// Print setup properties --------------------------------------------------

		// TODO This should come from the DesignPrint setup
		// There is no concept of custom paper sizes in FX.
		// According to FX the authoritative size for paper is from the printer itself
		// However, the Paper class has common sizes that can be used for convenience.
		Paper paper = Paper.NA_LETTER;

		// TODO This should come from the DesignPrint setup
		PageOrientation orientation = PageOrientation.PORTRAIT;

		// TODO These should come from the DesignPrint setup
		double leftMargin = 0.5 * 72;
		double rightMargin = 0.5 * 72;
		double topMargin = 0.5 * 72;
		double bottomMargin = 0.5 * 72;

		PageLayout layout = printer.createPageLayout( paper, orientation, leftMargin, rightMargin, topMargin, bottomMargin );

		// Configure the print job -------------------------------------------------

		PrinterJob job = PrinterJob.createPrinterJob();

		// Link the print job to the selected printer
		job.setPrinter( printer );

		JobSettings settings = job.getJobSettings();
		settings.setPageLayout( layout );
		settings.setPrintResolution( resolution );
		settings.setPrintQuality( PrintQuality.HIGH );
		settings.setPaperSource( PaperSource.AUTOMATIC );

		// NOTE This can be used to give feedback to the user. It can be bound to a text field
		job.jobStatusProperty().asString();

		// NOTE This is a rather Swing looking dialog, maybe handle print properties separately
		//				boolean print = job.showPageSetupDialog( getScene().getWindow() );
		//				boolean print = job.showPrintDialog( getScene().getWindow() );
		//				if( !print ) return;

		// Render the design -------------------------------------------------------

		boolean successful = printWithSingleRenderPane( job );
		//boolean successful = renderWithMultipleRenderPanes( job );

		// Inform the user ---------------------------------------------------------

		Notice notice = new Notice();
		notice.setTitle( successful ? "Print Job Success" : "Print Job Failure" );
		notice.setMessage( DesignPrintTask.this.getName() );
		notice.setType( successful ? Notice.Type.INFO : Notice.Type.WARN );
		getProgram().getNoticeManager().addNotice( notice );

		log.atInfo().log( "Design print task complete." );
		return null;
	}

	private boolean renderWithMultipleRenderPanes( PrinterJob job ) {
		// NEXT Tactic is to try a bunch of small canvases contained in a GridPane
		Canvas renderer = new Canvas();
		return job.printPage( renderer ) && job.endJob();
	}

	private boolean printWithSingleRenderPane( PrinterJob job ) throws Exception {
		PageLayout layout = job.getJobSettings().getPageLayout();

		double factor = 1;

		// The NEW way
		Class<? extends DesignRenderer> rendererClass = tool.getPrintDesignRenderer();
		final DesignRenderer renderer = rendererClass.getDeclaredConstructor().newInstance();
		//renderer.setBackground( Background.fill( Color.LIGHTGRAY ) );
		renderer.setDesign( asset.getModel() );
		renderer.setVisibleLayers( tool.getVisibleLayers() );

		renderer.setDpi( factor * 72 );
		renderer.setPrefWidth( layout.getPrintableWidth() );
		renderer.setPrefHeight( layout.getPrintableHeight() );
		renderer.setViewCenter( tool.getViewCenter() );
		renderer.setViewRotate( tool.getViewRotate() );
		renderer.setViewZoom( tool.getViewZoom() );

		//renderer.setReferenceLayerVisible( false );

		// FIXME This is changing the actual geometry colors...not just copying them to the print
		// Invert the colors if using a dark theme
		// TODO This should eventually be a user preference
		//		if( getProgram().getWorkspaceManager().getThemeMetadata().isDark() ) {
		//			Fx.run( () -> renderer.getVisibleShapes().forEach( s -> {
		//				s.setDrawPaint( invertLuminance( s.calcDrawPaint() ) );
		//				s.setFillPaint( invertLuminance( s.calcFillPaint() ) );
		//			} ) );
		//		}

		// Do the actual rendering
		// It is NOT required to do this on the FX thread
		renderer.print( factor );

		//		// FIXME Can't seem to get the factor above 3.2, about 230 dpi
		//		//  ...and it inconsistently prints nothing
		//		// The goal is to match the DPI requested by the printer/user
		//		// 300 dpi is a factor of 4.166666666666667
		//		// 2024 Aug 23 - Been as high as 13 today
		//		// Well, I think this is the issue: https://bugs.openjdk.org/browse/JDK-8090178
		//		// Which eventually leads to this:  https://bugs.openjdk.org/browse/JDK-8090822
		//
		//		double factor = 1; // 720 dpi
		//
		//		Canvas renderer = new Canvas();
		//		//		renderer.setScaleX( 1.0 / factor ); // 0.1
		//		//		renderer.setScaleY( 1.0 / factor ); // 0.1
		//		//
		//		//		double vFactor = 36;
		//		//
		//		//		// Changing the width and height also caused it to be moved
		//		//		renderer.setWidth( factor * layout.getPrintableWidth() ); // 1440
		//		//		renderer.setHeight( factor * 2*vFactor ); // 1440
		//		//		renderer.setLayoutX( -((factor - 1) * (0.5*layout.getPrintableWidth())) ); // -648
		//		//		renderer.setLayoutY( -((factor - 1) * vFactor) ); // -648
		//
		//		//		renderer.setTranslateX( -((factor - 1) * 72) );
		//		//		renderer.setTranslateY( -((factor - 1) * 72) );
		//		//		renderer.getGraphicsContext2D().setFill( Color.BLACK );
		//		//		renderer.getGraphicsContext2D().fillRect( 0, 0, factor * layout.getPrintableWidth(), factor * layout.getPrintableHeight() );
		//		//		renderer.getGraphicsContext2D().setFill( Color.YELLOW );
		//		//		renderer.getGraphicsContext2D().fillOval( 0, 0, factor * layout.getPrintableWidth(), factor * layout.getPrintableHeight() );
		//
		//		renderer.getGraphicsContext2D().setFill( Color.BLACK );
		//		renderer.getGraphicsContext2D().fillRect( 0, 0, factor * 144, factor * 144 );
		//		renderer.getGraphicsContext2D().beginPath();
		//		renderer.getGraphicsContext2D().moveTo( 0, 0 );
		//		renderer.getGraphicsContext2D().lineTo( factor * 72, 0 );
		//		renderer.getGraphicsContext2D().lineTo( 0, factor * 72 );
		//		renderer.getGraphicsContext2D().closePath();
		//		renderer.getGraphicsContext2D().setFill( Color.YELLOW );
		//		renderer.getGraphicsContext2D().fill();
		//		log.atConfig().log( "Job size: " + layout.getPrintableWidth() + "x" + layout.getPrintableHeight() );
		//		log.atConfig().log( "Print size: " + renderer.getWidth() + "x" + renderer.getHeight() + " = " + (renderer.getWidth() * renderer.getHeight()) );

		return job.printPage( layout, renderer ) && job.endJob();
	}

	private static Printer getPrinterByName( String name, Printer orElse ) {
		for( Printer p : Printer.getAllPrinters() ) {
			if( p.getName().equalsIgnoreCase( name ) ) return p;
		}
		return orElse;
	}

	private String invertLuminance( Paint paint ) {
		if( paint instanceof Color color ) return Paints.toString( Colors.invertLuminance( color ) );
		return Paints.toString( paint );
	}

}
